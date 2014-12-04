package uk.ac.kcl.SEG_Project_2.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.constants.C;
import uk.ac.kcl.SEG_Project_2.constants.MetricList;
import uk.ac.kcl.SEG_Project_2.constants.Utils;
import uk.ac.kcl.SEG_Project_2.data.ApiRequest;
import uk.ac.kcl.SEG_Project_2.data.Country;
import uk.ac.kcl.SEG_Project_2.data.Metric;
import uk.ac.kcl.SEG_Project_2.data.WorldBankApiRequest;

import java.util.*;

public class DataDisplay extends Activity {

	// views
	private GridLayout legendDisplay;

	// data collection fields
	private int indicatorsToCollect = 0;
	private int indicatorsCollected = 0;
	private ArrayList<ArrayList<JSONObject>> indicatorData;
	private boolean failed = false;
	private boolean onFailDone = false;
	private int fromYear;
	private int toYear;
	private ArrayList<Country> selectedCountries;
	private ArrayList<String> selectedCountryNames = new ArrayList<String>();
	private List<String[]> selectedIndicatorCodes;
	private int selectedMetricPosition;
	private Metric selectedMetric;
	private int graphType = 1;
	private HashMap<String, HashMap<String, ArrayList<Pair<String, String>>>> dataset = new HashMap<String, HashMap<String, ArrayList<Pair<String, String>>>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_loading);

		// get info from previous activity
		Bundle extras = getIntent().getExtras();
		selectedCountries = extras.getParcelableArrayList("countries");
		selectedMetricPosition = extras.getInt("metric_position");
		selectedMetric = MetricList.getMetrics().get(selectedMetricPosition);
		graphType = selectedMetric.getGraphType();
		String[] selectedCountryCodes = new String[selectedCountries.size()];
		for (int i = 0; i < selectedCountries.size(); i++) {
			selectedCountryCodes[i] = selectedCountries.get(i).getId();
			selectedCountryNames.add(selectedCountries.get(i).getName());
		}
		fromYear = extras.getInt("startYear");
		toYear = extras.getInt("endYear");

		// build request(s)
		selectedIndicatorCodes = Arrays.asList(selectedMetric.getIndicators());
		indicatorsToCollect = selectedIndicatorCodes.size();
		indicatorData = new ArrayList<ArrayList<JSONObject>>();
		for (String[] i : selectedIndicatorCodes) {
			// build a request
			final WorldBankApiRequest request = new WorldBankApiRequest(DataDisplay.this);
			request.setDateRange(fromYear, toYear);
			request.setCountries(selectedCountryCodes);
			request.setIndicator(i[0]);
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
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}

	private void onDataCollectionFinished() {
		// list of x values (no duplicates)
		HashSet<String> xValuesRaw = new HashSet<String>();

		// loop countries
		for (String c : selectedCountryNames) {
			// loop indicators
			for (String[] i : selectedIndicatorCodes) {
				// sort values
				ArrayList<Pair<String, String>> values = dataset.get(c).get(i[0]);
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
			for (String[] i : selectedIndicatorCodes) {
				// create an array to store
				datasets.put(c + ", " + i[1], new ArrayList<Object>());

				// loop xValues, and insert values where they exist
				for (int x = 0; x < xValues.size(); x++) {
					String xVal = xValues.get(x);
					// loop values
					ArrayList<Pair<String, String>> values = dataset.get(c).get(i[0]);
					for (Pair<String, String> r : values) {
						if (r.first.equals(xVal)) {
							try {
								// insert into dataset
								switch (graphType) {
									case MetricList.BAR_CHART:
										datasets.get(c + ", " + i[1]).add(new BarEntry(Float.parseFloat(r.second), x));
										break;
									default:
										datasets.get(c + ", " + i[1]).add(new Entry(Float.parseFloat(r.second), x));
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

		// turn on right type of graph
		setDataView();
		View chart;
		switch (graphType) {
			case MetricList.BAR_CHART:
				chart = findViewById(R.id.data_bar_chart);
				break;
			default:
				chart = findViewById(R.id.data_line_chart);
				break;
		}
		chart.setVisibility(View.VISIBLE);

		// for colouring
		int colour = 0;
		int colours = C.GRAPH_COLOURS.length;

		// for legends
		ArrayList<Pair<String, Integer>> legends = new ArrayList<Pair<String, Integer>>();

		// create data sets
		ArrayList<Object> sets = new ArrayList<Object>();
		for (Map.Entry<String, ArrayList<Object>> e : datasets.entrySet()) {
			// pick a colour
			int c = C.GRAPH_COLOURS[colour % colours];

			// create legend
			legends.add(new Pair<String, Integer>(e.getKey(), c));

			// force data set into correct type for graph
			Object individualSet;
			switch (graphType) {
				case MetricList.BAR_CHART:
					ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
					for (Object o : e.getValue()) {
						barEntries.add((BarEntry) o);
					}
					individualSet = new BarDataSet(barEntries, e.getKey());

					// format the bars
					((BarDataSet) individualSet).setColor(c);
					sets.add(individualSet);
					break;
				default:
					ArrayList<Entry> lineEntries = new ArrayList<Entry>();
					for (Object o : e.getValue()) {
						lineEntries.add((Entry) o);
					}
					individualSet = new LineDataSet(lineEntries, e.getKey());

					// format the lines
					((LineDataSet) individualSet).setColor(c);
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

		// create our own legend display
		((Chart) chart).setDrawLegend(false);
		Collections.sort(legends, new Comparator<Pair<String, Integer>>() {
			@Override
			public int compare(Pair<String, Integer> lhs, Pair<String, Integer> rhs) {
				return lhs.first.compareTo(rhs.first);
			}
		});
		if (legendDisplay != null) {
			legendDisplay.removeAllViews();
			for (Pair<String, Integer> l : legends) {
				// add colour box
				TextView boxToAdd = new TextView(getBaseContext());
				boxToAdd.setText("■ ");
				boxToAdd.setTextColor(l.second);
				legendDisplay.addView(boxToAdd);

				// add text view
				TextView tvToAdd = new TextView(getBaseContext());
				tvToAdd.setText(" " + l.first);
				tvToAdd.setTextColor(Color.BLACK);
				legendDisplay.addView(tvToAdd);
			}
			legendDisplay.setVisibility(View.VISIBLE);
		}
	}

	private void setDataView() {
		// switch to data display
		setContentView(R.layout.data_display);

		// create Font Awesome typeface
		Typeface fontAwesome = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Fontawesome-Webfont.ttf");

		// get options button
		Button optionsButton = (Button) findViewById(R.id.data_options_button);
		if (optionsButton != null) {
			optionsButton.setTypeface(fontAwesome);
			optionsButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onOptionsButtonClick();
				}
			});
		}

		// get legends display
		legendDisplay = (GridLayout) findViewById(R.id.data_legends);

		// get legend button
		final Button legendButton = (Button) findViewById(R.id.data_legend_button);
		if (legendButton != null) {
			legendButton.setTypeface(fontAwesome);
			legendButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (legendDisplay != null) {
						if (legendDisplay.getVisibility() == View.VISIBLE) {
							legendButton.setText(R.string.data_legend_show);
							legendDisplay.setVisibility(View.GONE);
						} else {
							legendDisplay.setVisibility(View.VISIBLE);
							legendButton.setText(R.string.data_legend_hide);
						}
					}
				}
			});
		}
	}

	private void onOptionsButtonClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(DataDisplay.this);
		builder.setTitle(R.string.data_options_button);
		builder.setItems(getResources().getStringArray(R.array.data_options), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						Utils.createInfoDialog(DataDisplay.this, selectedMetric.getName(), selectedMetric.getInfo());
						break;
					case 1:
						Utils.createDatePickerDialog(DataDisplay.this, new Utils.OnDatePickerDone() {
							@Override
							public void onDone(boolean cancelled, int fromYear, int toYear) {
								if (!cancelled) {
									final Intent sendToData = new Intent(getBaseContext(), DataDisplay.class);
									sendToData.putParcelableArrayListExtra("countries", selectedCountries);
									sendToData.putExtra("metric_position", selectedMetricPosition);
									sendToData.putExtra("startYear", fromYear);
									sendToData.putExtra("endYear", toYear);
									DataDisplay.this.startActivity(sendToData);
									DataDisplay.this.finish();
								}
							}
						}, fromYear, toYear);
						break;
				}
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

}
