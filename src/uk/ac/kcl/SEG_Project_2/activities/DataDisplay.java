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
	private View chart;
	private ArrayList<Boolean> legendVisibility = new ArrayList<Boolean>();

	// collected data
	private int indicatorsToCollect = 0;
	private int indicatorsCollected = 0;
	private ArrayList<ArrayList<JSONObject>> indicatorData;
	private boolean failed = false;
	private boolean onFailDone = false;
	private HashMap<String, HashMap<String, ArrayList<Pair<String, String>>>> dataset = new HashMap<String, HashMap<String, ArrayList<Pair<String, String>>>>();
	ArrayList<Object> individualDatasets;
	private ArrayList<String> xValues;

	// data to collect
	private int fromYear;
	private int toYear;
	private ArrayList<Country> selectedCountries;
	private ArrayList<String> selectedCountryNames = new ArrayList<String>();
	private List<String[]> selectedIndicatorCodes;
	private int selectedMetricPosition;
	private Metric selectedMetric;
	private int graphType = MetricList.LINE_GRAPH;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_loading);

		// get info from previous activity
		Bundle extras = getIntent().getExtras();
		selectedCountries = extras.getParcelableArrayList("countries");
		selectedMetricPosition = extras.getInt("metric_position");
		selectedMetric = MetricList.getMetrics().get(selectedMetricPosition);
		graphType = extras.getInt("graphType");
		String[] selectedCountryCodes = new String[selectedCountries.size()];
		for (int i = 0; i < selectedCountries.size(); i++) {
			selectedCountryCodes[i] = selectedCountries.get(i).getId();
			selectedCountryNames.add(selectedCountries.get(i).getName());
		}
		fromYear = extras.getInt("startYear");
		toYear = extras.getInt("endYear");

		// set title
		setTitle(selectedMetric.getName());

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
				.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
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
		// list of x values (hashset, so no duplicates)
		HashSet<String> xValuesRaw = new HashSet<String>();

		// loop all data to build an unsorted lit of X values
		for (String c : selectedCountryNames) {
			for (String[] i : selectedIndicatorCodes) {
				for (Pair<String, String> r : dataset.get(c).get(i[0])) {
					xValuesRaw.add(r.first);
				}
			}
		}

		// sort xValues
		xValues = new ArrayList<String>(xValuesRaw.size());
		xValues.addAll(xValuesRaw);
		Collections.sort(xValues);

		// start building individualDatasets (uses a treemap for inbuilt sorting)
		Map<String, ArrayList<Object>> datasets = new TreeMap<String, ArrayList<Object>>();

		// loop countries
		for (String c : selectedCountryNames) {
			// loop indicators
			for (String[] i : selectedIndicatorCodes) {
				// create an array to store the data in
				datasets.put(c + C.LEGEND_DELIM + i[1], new ArrayList<Object>());

				// loop xValues, and insert values into the above array where they exist
				for (int x = 0; x < xValues.size(); x++) {
					String xVal = xValues.get(x);
					// loop values
					for (Pair<String, String> r : dataset.get(c).get(i[0])) {
						if (r.first.equals(xVal)) {
							try {
								// insert into dataset
								switch (graphType) {
									case MetricList.BAR_CHART:
										datasets.get(c + C.LEGEND_DELIM + i[1]).add(new BarEntry(Float.parseFloat(r.second), x));
										break;
									default:
										datasets.get(c + C.LEGEND_DELIM + i[1]).add(new Entry(Float.parseFloat(r.second), x));
										break;
								}
							} catch (NumberFormatException e) {
								// I don't trust data from APIs ¬.¬
							}
						}
					}
				}
			}
		}

		// turn on the right type of graph
		switchToDataView();
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

		// create data individualDatasets for the graph
		individualDatasets = new ArrayList<Object>();
		for (Map.Entry<String, ArrayList<Object>> e : datasets.entrySet()) {
			// pick a colour
			int c = C.GRAPH_COLOURS[colour % colours];

			// create legend
			legends.add(new Pair<String, Integer>(e.getKey(), c));
			legendVisibility.add(true);

			// force data set into correct type for graph
			Object individualSet;
			switch (graphType) {
				case MetricList.BAR_CHART:
					// create bar entries
					ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
					for (Object o : e.getValue()) barEntries.add((BarEntry) o);
					individualSet = new BarDataSet(barEntries, e.getKey());

					// format the bars
					((BarDataSet) individualSet).setColor(c);
					((BarDataSet) individualSet).setBarShadowColor(Color.TRANSPARENT);
					individualDatasets.add(individualSet);
					break;
				default:
					// create line entries
					ArrayList<Entry> lineEntries = new ArrayList<Entry>();
					for (Object o : e.getValue()) lineEntries.add((Entry) o);
					individualSet = new LineDataSet(lineEntries, e.getKey());

					// format the lines
					((LineDataSet) individualSet).setColor(c);
					((LineDataSet) individualSet).setDrawCircles(false);
					((LineDataSet) individualSet).setLineWidth(2);
					individualDatasets.add(individualSet);
					break;
			}

			// next colour!
			++colour;
		}

		// final setup on the graph
		updateChartData();

		// generic options
		((Chart) chart).setDrawYValues(false);
		((Chart) chart).setDescription("");
		((Chart) chart).setDrawLegend(false);

		// create our own legend display
		if (legendDisplay != null) {
			legendDisplay.removeAllViews();
			for (int legendNo = 0; legendNo < legends.size(); ++legendNo) {
				Pair<String, Integer> l = legends.get(legendNo);
				// add colour box
				TextView boxToAdd = new TextView(getBaseContext());
				boxToAdd.setText("■ ");
				boxToAdd.setTextColor(l.second);
				boxToAdd.setTypeface(null, Typeface.BOLD);
				boxToAdd.setTag(true); // tag: visible?
				legendDisplay.addView(boxToAdd);

				// add text view
				TextView tvToAdd = new TextView(getBaseContext());
				tvToAdd.setText(" " + l.first);
				tvToAdd.setTextColor(Color.BLACK);
				tvToAdd.setTag(legendNo); // tag: legend number
				legendDisplay.addView(tvToAdd);

				// click actions
				tvToAdd.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int position = (Integer) v.getTag();
						onLegendClicked(position);
					}
				});
			}
			legendDisplay.setVisibility(View.VISIBLE);
		}
	}

	private void switchToDataView() {
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
							public void onDone(boolean cancelled, int fromYear, int toYear, int graphType) {
								if (!cancelled) {
									final Intent sendToData = new Intent(getBaseContext(), DataDisplay.class);
									sendToData.putParcelableArrayListExtra("countries", selectedCountries);
									sendToData.putExtra("metric_position", selectedMetricPosition);
									sendToData.putExtra("startYear", fromYear);
									sendToData.putExtra("endYear", toYear);
                                    sendToData.putExtra("graphType", graphType);

                                    DataDisplay.this.startActivity(sendToData);
									DataDisplay.this.finish();
								}
							}
						}, fromYear, toYear, graphType);
						break;
				}
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private void onLegendClicked(int position) {
		if (legendDisplay == null) return;

		// get text view parts
		TextView part1 = (TextView) legendDisplay.getChildAt(position * 2);
		TextView part2 = (TextView) legendDisplay.getChildAt(position * 2 + 1);

		// visible at the moment?
		Boolean visible = (Boolean) part1.getTag();

		if (visible) {
			// hide
			part1.setAlpha(0.3f);
			part2.setAlpha(0.3f);
			legendVisibility.remove(position);
			legendVisibility.add(position, false);
		} else {
			// show
			part1.setAlpha(1.0f);
			part2.setAlpha(1.0f);
			legendVisibility.remove(position);
			legendVisibility.add(position, true);
		}

		// update statue
		part1.setTag(!visible);

		// update graph
		updateChartData();
	}

	private void updateChartData() {
		switch (graphType) {

			case MetricList.BAR_CHART:
				ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
				for (int i = 0; i < individualDatasets.size(); ++i) {
					if (legendVisibility.get(i)) {
						barDataSets.add((BarDataSet) individualDatasets.get(i));
					}
				}
				BarData barData = new BarData(xValues, barDataSets);
				((BarChart) chart).setData(barData);
				break;
			default:
				ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
				for (int i = 0; i < individualDatasets.size(); ++i) {
					if (legendVisibility.get(i)) {
						lineDataSets.add((LineDataSet) individualDatasets.get(i));
					}
				}
				LineData lineData = new LineData(xValues, lineDataSets);
				((LineChart) chart).setData(lineData);
				break;
		}

		chart.invalidate();
	}

}
