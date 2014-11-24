package uk.ac.kcl.SEG_Project_2.data;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.kcl.SEG_Project_2.constants.C;
import uk.ac.kcl.SEG_Project_2.constants.Utils;

import java.io.IOException;
import java.util.*;

public class ApiRequest implements WorldBankApiRequest {

	private boolean finished = false;
	private Status status = Status.WAITING;
	private int timeout = 20000;

	// request structure fields
	private String indicator = "";
	private List<String> countries = null;
	private int startMonth = 0;
	private int startYear = 0;
	private int endMonth = 0;
	private int endYear = 0;
	private Frequency frequency = Frequency.YEARLY;
	private boolean forceFresh = false;

	// threading fields
	private OnCompleteListener onComplete;
	private OnFailListener onFail;
	private OnCancelListener onCancel;
	private OnProgressUpdateListener onProgressUpdate;
	private Activity activity;
	private Context context;

	// request fields
	private List<JSONObject> result = new ArrayList<JSONObject>();
	private HttpUriRequest requestBase;

	// create a new request and handler
	public ApiRequest(Activity activity) {
		this.activity = activity;
		this.context = activity.getBaseContext();
	}

	// REQUEST-BUILDING METHODS

	@Override
	public void setIndicator(String indicator) {
		this.indicator = indicator;
	}

	@Override
	public void setCountries(String... countries) {
		this.countries = Arrays.asList(countries);
	}

	@Override
	public void setDateRange(int startMonth, int startYear, int endMonth, int endYear) {
		this.startMonth = startMonth;
		this.startYear = startYear;
		this.endMonth = endMonth;
		this.endYear = endYear;
	}

	@Override
	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	@Override
	public void setForceFresh(boolean forceFresh) {
		this.forceFresh = forceFresh;
	}

    @Override
    public String createHash() {
        // sanitise inputs for creating hash
        String countrySegment;
        if (countries == null || countries.size() == 0) {
            countrySegment = "all";
        } else {
            String[] sorted = (String[]) countries.toArray();
            Arrays.sort(sorted);
            countrySegment = TextUtils.join(";", sorted);
        }

        // create hash
        return Utils.createSHA256(indicator + countrySegment + startMonth + startYear + endMonth + endYear + frequency.toString());
    }

    // set handlers

	@Override
	public void setOnComplete(OnCompleteListener onComplete) {
		this.onComplete = onComplete;
	}

	@Override
	public void setOnFail(OnFailListener onFail) {
		this.onFail = onFail;
	}

	@Override
	public void setOnCancel(OnCancelListener onCancel) {
		this.onCancel = onCancel;
	}

	@Override
	public void setOnProgressUpdate(OnProgressUpdateListener onProgressUpdate) {
		this.onProgressUpdate = onProgressUpdate;
	}

	// execute the request
	@Override
	public void execute() {
		// requests can be executed only once
		if (status != Status.WAITING) {
			Log.d(C.LOG_TAG, "Error: trying to re-use an ApiRequest");
			status = Status.FAILED;
			finish();
			return;
		}

		// did we specify some indicators?
		if (indicator == null) {
			Log.d(C.LOG_TAG, "Error: did not set any indicators for ApiRequest");
			status = Status.FAILED;
			finish();
			return;
		}

		// update status
		status = Status.EXECUTING;
		sendProgressUpdate(0, 0);

		// build the URI
		String countriesSegment = countries.size() > 0 ? TextUtils.join(";", countries) : "all";
		String frequencySegment = "Y";
		if (frequency == Frequency.QUARTERLY) frequencySegment = "Q";
		if (frequency == Frequency.MONTHLY) frequencySegment = "M";
		String dateSegment = "";
		if (startYear != 0 && endYear != 0) {
			if (startMonth != 0 && endMonth != 0) {
				dateSegment = "&date=" + startYear + "M" + startMonth + ":" + endYear + "M" + endMonth;
			} else {
				dateSegment = "&date=" + startYear + ":" + endYear;
			}
		}
		final String compiledUri = String.format(C.API_URI_FORMAT, countriesSegment, indicator, frequencySegment, dateSegment);

		// build and execute the request on a thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				// start tracking pages
				int lastPageLoaded = 0;
				int totalPages = 1;

				// did the loop below fail at any point?
				int responseCode = 0;
				boolean failed = false;

				// loop to get all pages
				while (lastPageLoaded < totalPages) {
					// create client
					HttpParams params = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(params, timeout);
					HttpConnectionParams.setSoTimeout(params, timeout);
					HttpClient httpClient = new DefaultHttpClient(params);

					// execute the request
					try {
						// build request
						requestBase = new HttpGet(compiledUri.replace("::PAGE::", "" + (lastPageLoaded + 1)));
						requestBase.addHeader("Content-type", "application/json");

						// load response
						HttpResponse httpResponse = httpClient.execute(requestBase);
						responseCode = httpResponse.getStatusLine().getStatusCode();
						HttpEntity httpEntity = httpResponse.getEntity();
						String serverReply = new Scanner(httpEntity.getContent()).useDelimiter("\\A").next();
						JSONArray responseArray = new JSONArray(serverReply);

						// split into sections
						JSONObject paging = responseArray.getJSONObject(0);
						JSONArray data = responseArray.getJSONArray(1);

						// update paging info
						lastPageLoaded = paging.getInt("page");
						totalPages = paging.getInt("pages");
						sendProgressUpdate(lastPageLoaded, totalPages);

						// add data to result
						for (int i = 0; i < data.length(); ++i) {
							result.add(data.getJSONObject(i));
						}
					} catch (NoSuchElementException e) {
						Log.d(C.LOG_TAG, "Error: Empty response when executing '" + compiledUri + "'");
						failed = true;
						break;
					} catch (NullPointerException e) {
						Log.d(C.LOG_TAG, "Error: NPE when executing '" + compiledUri + "'");
						failed = true;
						break;
					} catch (IOException e) {
						// report the error
						Log.d(C.LOG_TAG, "Error: IO error (usually a timeout) when executing '" + compiledUri + "'; " + e.getMessage());
						failed = true;
						break;
					} catch (JSONException e) {
						// report the error
						Log.d(C.LOG_TAG, "Error: JSON exception when executing '" + compiledUri + "'; " + e.getMessage());
						failed = true;
						break;
					}
				}

				// did the above code fail?
				if (failed || !(responseCode >= 200 && responseCode < 300)) {
					// abandon ship!
					status = Status.FAILED;
					finish();
					return;
				}

				status = Status.COMPLETED;
				finish();
			}
		}).start();
	}

	// cancel the request
	@Override
	public void cancel() {
		status = Status.CANCELLED;
		finish();
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					requestBase.abort();
				}
			}).start();
		} catch (UnsupportedOperationException e) {
			// don't worry about it!
		}
	}

	// finish the request and execute one of the result handlers
	private void finish() {
		// this is run-once
		if (finished) return;
		finished = true;

		// run the correct method
		if (status == Status.COMPLETED && onComplete != null) activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onComplete.onComplete();
			}
		});
		if (status == Status.FAILED && onFail != null) activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onFail.onFail();
			}
		});
		if (status == Status.CANCELLED && onCancel != null) activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onCancel.onCancel();
			}
		});
	}

	// send a progress update notice to the caller
	private void sendProgressUpdate(final int currentProgress, final int totalProgress) {
		if (onProgressUpdate == null) return;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onProgressUpdate.onProgressUpdate(totalProgress > 0 ? (float) currentProgress / totalProgress : 0);
			}
		});
	}

	// get the creating activity (nb. accessible within a result handling Runnable)
	public Activity getActivity() {
		return activity;
	}

	// get the creating context (nb. accessible within a result handling Runnable)
	public Context getContext() {
		return context;
	}

	// get the status of the request
	@Override
	public Status getStatus() {
		return status;
	}

	// get the resulting list of JSONObjects (nb. accessible within a result handling Runnable)
	@Override
	public List<JSONObject> getResult() {
		return result;
	}

}