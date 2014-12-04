package uk.ac.kcl.SEG_Project_2.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.constants.C;
import uk.ac.kcl.SEG_Project_2.constants.MetricList;
import uk.ac.kcl.SEG_Project_2.data.ApiRequest;
import uk.ac.kcl.SEG_Project_2.data.Country;
import uk.ac.kcl.SEG_Project_2.data.Metric;
import uk.ac.kcl.SEG_Project_2.data.WorldBankApiRequest;

import java.util.*;

public class DataDisplay extends Activity {

	// data collection fields
	private int indicatorsToCollect = 0;
	private int indicatorsCollected = 0;
	private ArrayList<ArrayList<JSONObject>> indicatorData;
	private boolean failed = false;
	private boolean onFailDone = false;
	private ArrayList<String> selectedCountryNames = new ArrayList<String>();
	private List<String> selectedIndicatorCodes;
	private int graphType = 1;
	private HashMap<String, HashMap<String, ArrayList<Pair<String, String>>>> dataset = new HashMap<String, HashMap<String, ArrayList<Pair<String, String>>>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_loading);

		// get info from previous activity
		Bundle extras = getIntent().getExtras();
		ArrayList<Country> selectedCountries = extras.getParcelableArrayList("countries");
		int selectedMetricPosition = extras.getInt("metric_position");
		Metric selectedMetric = MetricList.getMetrics().get(selectedMetricPosition);
		graphType = selectedMetric.getGraphType();
		String[] selectedCountryCodes = new String[selectedCountries.size()];
		for (int i = 0; i < selectedCountries.size(); i++) {
			selectedCountryCodes[i] = selectedCountries.get(i).getId();
			selectedCountryNames.add(selectedCountries.get(i).getName());
		}
		int startYear = extras.getInt("startYear");
		int endYear = extras.getInt("endYear");

		// build request(s)
		selectedIndicatorCodes = Arrays.asList(selectedMetric.getIndicators());
		indicatorsToCollect = selectedIndicatorCodes.size();
		indicatorData = new ArrayList<ArrayList<JSONObject>>();
		for (String i : selectedIndicatorCodes) {
			// build a request
			final WorldBankApiRequest request = new WorldBankApiRequest(DataDisplay.this);
			request.setDateRange(startYear, endYear);
			request.setCountries(selectedCountryCodes);
			request.setIndicator(i);
			request.setOnFail(new ApiRequest.OnFailListener() {
				@Override
				public void onFail() {
					failed = true;
					onDataCollectionFailed();
				}
			});
			request.setOnComplete(new ApiRequest.OnCompleteListener() {
				@Override
				public void onComplete() {
					++indicatorsCollected;
					indicatorData.add((ArrayList<JSONObject>) request.getResult());
					checkAllDataCollected();
				}
			});
			request.execute();
		}
	}

	private void checkAllDataCollected() {
		if (failed) return;
		if (indicatorsCollected == indicatorsToCollect) {
			for (ArrayList<JSONObject> a : indicatorData) {
				for (JSONObject o : a) {
					// parse collected date
					try {
						String value = o.get("value").toString();
						String date = o.get("date").toString();
						String country = o.getJSONObject("country").get("value").toString();
						String indicator = o.getJSONObject("indicator").get("id").toString();

						// create country map
						if (!dataset.containsKey(country)) {
							dataset.put(country, new HashMap<String, ArrayList<Pair<String, String>>>());
						}

						// create indicator map
						if (!dataset.get(country).containsKey(indicator)) {
							dataset.get(country).put(indicator, new ArrayList<Pair<String, String>>());
						}

						// place values
						if (value != null && !value.equals("null")) {
							dataset.get(country).get(indicator).add(new Pair<String, String>(date, value));
						}
					} catch (JSONException e) {
						failed = true;
						onDataCollectionFailed();
						return;
					}
				}
			}

			// finished collection
			onDataCollectionFinished();
		}
	}

	private void onDataCollectionFailed() {
		// only run once
		if (!failed || onFailDone) return;
		onFailDone = true;

		// alert
		AlertDialog.Builder builder = new AlertDialog.Builder(DataDisplay.this);
		builder.setTitle(R.string.data_error_title)
				.setMessage(R.string.data_error_body)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DataDisplay.this.finish();
					}
				});
		builder.create().show();
	}

	private void onDataCollectionFinished() {
		// list of x values (no duplicates)
		HashSet<String> xValuesRaw = new HashSet<String>();

		// loop countries
		for (String c : selectedCountryNames) {
			// loop indicators
			for (String i : selectedIndicatorCodes) {
				// sort values
				ArrayList<Pair<String, String>> values = dataset.get(c).get(i);
				Collections.sort(values, new Comparator<Pair<String, String>>() {
					@Override
					public int compare(Pair<String, String> lhs, Pair<String, String> rhs) {
						return lhs.first.compareTo(rhs.first);
					}
				});

				// loop values
				for (Pair<String, String> r : values) {
					xValuesRaw.add(r.first);
				}
			}
		}

		// sort xValues
		ArrayList<String> xValues = new ArrayList<String>(xValuesRaw.size());
		xValues.addAll(xValuesRaw);
		Collections.sort(xValues);

		// start building datasets
		HashMap<String, ArrayList<Object>> datasets = new HashMap<String, ArrayList<Object>>();

		// loop countries
		for (String c : selectedCountryNames) {
			// loop indicators
			for (String i : selectedIndicatorCodes) {
				// create an array to store
				datasets.put(c + "##" + i, new ArrayList<Object>());

				// loop xValues, and insert values where they exist
				for (int x = 0; x < xValues.size(); x++) {
					String xVal = xValues.get(x);
					// loop values
					ArrayList<Pair<String, String>> values = dataset.get(c).get(i);
					for (Pair<String, String> r : values) {
						if (r.first.equals(xVal)) {
							try {
								// insert into dataset
								switch (graphType) {
									case MetricList.BAR_CHART:
										datasets.get(c + "##" + i).add(new BarEntry(Float.parseFloat(r.second), x));
										break;
									default:
										datasets.get(c + "##" + i).add(new Entry(Float.parseFloat(r.second), x));
										break;
								}
							} catch (NumberFormatException e) {
								// don't add
							}
						}
					}
				}
			}
		}

		// switch to data display
		setContentView(R.layout.data_display);

		// turn on right type of graph
		View chart;
		switch (graphType) {
			case MetricList.BAR_CHART:
				chart = findViewById(R.id.barChart);
				break;
			default:
				chart = findViewById(R.id.lineChart);
				break;
		}
		chart.setVisibility(View.VISIBLE);

		// for colouring
		int colour = 0;
		int colours = C.GRAPH_COLOURS.length;

		// create data sets
		ArrayList<Object> sets = new ArrayList<Object>();
		for (Map.Entry<String, ArrayList<Object>> e : datasets.entrySet()) {
			Object individualSet;
			switch (graphType) {
				case MetricList.BAR_CHART:
					ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
					for (Object o : e.getValue()) {
						barEntries.add((BarEntry) o);
					}
					individualSet = new BarDataSet(barEntries, e.getKey());

					// format the bars
					((BarDataSet) individualSet).setColor(C.GRAPH_COLOURS[colour % colours]);
					sets.add(individualSet);
					break;
				default:
					ArrayList<Entry> lineEntries = new ArrayList<Entry>();
					for (Object o : e.getValue()) {
						lineEntries.add((Entry) o);
					}
					individualSet = new LineDataSet(lineEntries, e.getKey());

					// format the lines
					((LineDataSet) individualSet).setColor(C.GRAPH_COLOURS[colour % colours]);
					((LineDataSet) individualSet).setDrawCircles(false);
					((LineDataSet) individualSet).setLineWidth(2);
					sets.add(individualSet);
					break;
			}

			// next colour!
			++colour;
		}

		// final setup on the graph
		switch (graphType) {
			case MetricList.BAR_CHART:
				ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
				for (Object o : sets) barDataSets.add((BarDataSet) o);
				BarData barData = new BarData(xValues, barDataSets);
				((BarChart) chart).setDrawYValues(false);
				((BarChart) chart).setDescription("");
				((BarChart) chart).setData(barData);
				break;
			default:
				ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
				for (Object o : sets) lineDataSets.add((LineDataSet) o);
				LineData lineData = new LineData(xValues, lineDataSets);
				((LineChart) chart).setDrawYValues(false);
				((LineChart) chart).setDescription("");
				((LineChart) chart).setData(lineData);
				break;
		}
	}

}
