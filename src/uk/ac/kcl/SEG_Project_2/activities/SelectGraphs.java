package uk.ac.kcl.SEG_Project_2.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.constants.MetricList;
import uk.ac.kcl.SEG_Project_2.data.ApiRequest;
import uk.ac.kcl.SEG_Project_2.data.Country;
import uk.ac.kcl.SEG_Project_2.data.Metric;
import uk.ac.kcl.SEG_Project_2.data.WorldBankApiRequest;

import java.util.*;

public class SelectGraphs extends Activity {

	/*public ArrayList<String> xVals;
	ArrayList<LineDataSet> lineDataSets;
	ArrayList<BarDataSet> barDataSets;
	ArrayList<PieDataSet> pieDataSets;

	Spinner spinner;
	BarChart barchart;*/

	// data collection fields
	private int indicatorsToCollect = 0;
	private int indicatorsCollected = 0;
	private ArrayList<ArrayList<JSONObject>> indicatorData;
	private boolean failed = false;
	private boolean onFailDone = false;

	// useful data set
	private Metric selectedMetric;
	private int selectedMetricPosition;
	private ArrayList<String> selectedCountryNames = new ArrayList<String>();
	private List<String> selectedIndicatorCodes;
	private HashMap<String, HashMap<String, ArrayList<Pair<String, String>>>> dataset = new HashMap<String, HashMap<String, ArrayList<Pair<String, String>>>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: initially setContentView() to a view with a loading message

		// get info from previous activity
		Bundle extras = getIntent().getExtras();
		ArrayList<Country> selectedCountries = extras.getParcelableArrayList("countries");
		selectedMetricPosition = extras.getInt("metric_position");
		selectedMetric = MetricList.getMetrics().get(selectedMetricPosition);
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
			final WorldBankApiRequest request = new WorldBankApiRequest(SelectGraphs.this);
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

		/*if (selectedMetric.equals("Gas Emissions")) {
			setContentView(R.layout.activity_main_barchart);
			//setContentView(R.layout.activity_main_piechart);
		}
		if (selectedMetric.equals("Population")) {
			setContentView(R.layout.activity_main_linechart);
		}
		if (selectedMetric.equals("Deforestation")) {
			setContentView(R.layout.activity_main_linechart);
		}
		if (selectedMetric.equals("Elec. Use")) {
			setContentView(R.layout.activity_main_piechart);
		}

		barchart = (BarChart) findViewById(R.id.barChart);
		spinner = (Spinner) findViewById(R.id.countries_spinner);

		createLineChart();
		createBarChart();
		createPieChart();*/
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

		// TODO: use setContentView() to a view with an error message and button to go back
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
		HashMap<String, ArrayList<Entry>> datasets = new HashMap<String, ArrayList<Entry>>();

		// loop countries
		for (String c : selectedCountryNames) {
			// loop indicators
			for (String i : selectedIndicatorCodes) {
				// create an array to store
				datasets.put(c + "##" + i, new ArrayList<Entry>());

				// loop xValues, and insert values where they exist
				for (int x = 0; x < xValues.size(); x++) {
					String xVal = xValues.get(x);
					// loop values
					ArrayList<Pair<String, String>> values = dataset.get(c).get(i);
					for (Pair<String, String> r : values) {
						if (r.first.equals(xVal)) {
							try {
								// insert into dataset
								datasets.get(c + "##" + i).add(new Entry(Float.parseFloat(r.second), x));
							} catch (NumberFormatException e) {
								// don't add
							}
						}
					}
				}
			}
		}

		// show data
		setContentView(R.layout.activity_main_linechart);
		LineChart lineChart = (LineChart) findViewById(R.id.lineChart);
		ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
		int col = 0;
		int[] cols = new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA};
		for (Map.Entry<String, ArrayList<Entry>> e : datasets.entrySet()) {
			if (e == null || e.getValue() == null || e.getKey() == null) continue;
			LineDataSet l = new LineDataSet(e.getValue(), e.getKey());
			l.setColor(cols[col % 4]);
			l.setLineWidth(2f);
			sets.add(l);
			++col;
		}
		LineData data = new LineData(xValues, sets);
		lineChart.setDrawYValues(false);
		lineChart.setData(data);
	}

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void createLineChart() {

		Spinner spinner = (Spinner) findViewById(R.id.countries_spinner);
		//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		//                R.array.countries_array, android.R.layout.simple_spinner_item);
		//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//        spinner.setAdapter(adapter);

		LineChart chart = (LineChart) findViewById(R.id.lineChart);
		ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
		ArrayList<Entry> valsComp2 = new ArrayList<Entry>();

		for (int i = 0; i <= 5; i++) {
			Random random = new Random();
			float entry = random.nextFloat() * 100;
			Entry countryOne = new Entry(entry, i);
			valsComp1.add(countryOne);

		}

		for (int j = 0; j <= 5; j++) {
			Random random = new Random();
			float entry = random.nextFloat() * 100;
			Entry countryTwo = new Entry(entry, j);
			valsComp2.add(countryTwo);

		}

		LineDataSet setComp1 = new LineDataSet(valsComp1, "Country 1");
		setComp1.setColor(Color.BLUE);

		LineDataSet setComp2 = new LineDataSet(valsComp2, "Country 2");
		setComp2.setColor(Color.RED);

		lineDataSets = new ArrayList<LineDataSet>();
		lineDataSets.add(setComp1);
		lineDataSets.add(setComp2);

		xVals = new ArrayList<String>();
		xVals.add("2009");
		xVals.add("2010");
		xVals.add("2011");
		xVals.add("2012");
		xVals.add("2013");
		xVals.add("2014");

		LineData data = new LineData(xVals, lineDataSets);
		chart.setData(data);

	}

	public void createBarChart() {
		if (spinner != null) {
			//            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			//                    R.array.gases_array, android.R.layout.simple_spinner_item);
			//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//            spinner.setAdapter(adapter);
			barchart.fitScreen();
		}
		ArrayList<BarEntry> barVals = new ArrayList<BarEntry>();
		ArrayList<BarEntry> barVals2 = new ArrayList<BarEntry>();
		ArrayList<BarEntry> barVals3 = new ArrayList<BarEntry>();

		for (int i = 0; i <= 5; i++) {
			Random random = new Random();
			float entry = random.nextFloat() * 100;
			BarEntry countryOne = new BarEntry(entry, i);
			barVals.add(countryOne);

		}

		for (int j = 0; j <= 5; j++) {
			Random random = new Random();
			float entry = random.nextFloat() * 100;
			BarEntry countryTwo = new BarEntry(entry, j);
			barVals2.add(countryTwo);

		}

		for (int x = 0; x <= 5; x++) {
			Random random = new Random();
			float entry = random.nextFloat() * 100;
			BarEntry countryThree = new BarEntry(entry, x);
			barVals3.add(countryThree);

		}

		BarDataSet setComp1 = new BarDataSet(barVals, "Country 1");
		setComp1.setBarSpacePercent(75f);
		setComp1.setColor(Color.BLUE);

		BarDataSet setComp2 = new BarDataSet(barVals2, "Country 2");
		setComp2.setBarSpacePercent(75f);
		setComp2.setColor(Color.YELLOW);

		BarDataSet setComp3 = new BarDataSet(barVals2, "Country 3");
		setComp3.setBarSpacePercent(75f);
		setComp3.setColor(Color.MAGENTA);

		barDataSets = new ArrayList<BarDataSet>();
		barDataSets.add(setComp1);
		barDataSets.add(setComp2);
		barDataSets.add(setComp3);

		xVals = new ArrayList<String>();
		xVals.add("2009");
		xVals.add("2010");
		xVals.add("2011");
		xVals.add("2012");
		xVals.add("2013");
		xVals.add("2014");

		BarData data = new BarData(xVals, barDataSets);
		barchart.setData(data);
		barchart.fitScreen();
	}

	public void createPieChart() {
		Spinner spinner = (Spinner) findViewById(R.id.countries_spinner);
		//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		//                R.array.gases_array, android.R.layout.simple_spinner_item);
		//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//        spinner.setAdapter(adapter);

		PieChart pieChart = (PieChart) findViewById(R.id.pieChart);
		ArrayList<Entry> pieVals = new ArrayList<Entry>();
		ArrayList<Entry> pieVals2 = new ArrayList<Entry>();

		for (int i = 0; i <= 5; i++) {
			Random random = new Random();
			float entry = random.nextFloat() * 100;
			Entry countryOne = new Entry(entry, i);
			pieVals.add(countryOne);

		}

		for (int j = 0; j <= 5; j++) {
			Random random = new Random();
			float entry = random.nextFloat() * 100;
			Entry countryTwo = new Entry(entry, j);
			pieVals2.add(countryTwo);

		}

		PieDataSet setComp1 = new PieDataSet(pieVals, "Country 1");
		setComp1.setSliceSpace(3f);
		PieDataSet setComp2 = new PieDataSet(pieVals, "Country 2");
		setComp1.setSliceSpace(3f);

		pieDataSets = new ArrayList<PieDataSet>();
		pieDataSets.add(setComp1);
		//dataSets.add(setComp2);

		xVals = new ArrayList<String>();
		xVals.add("2009");
		xVals.add("2010");
		xVals.add("2011");
		xVals.add("2012");
		xVals.add("2013");
		xVals.add("2014");

		ArrayList<Integer> colors = new ArrayList<Integer>();

		for (int c : ColorTemplate.JOYFUL_COLORS) {
			colors.add(c);
		}

		setComp1.setColors(colors);

		PieData data = new PieData(xVals, setComp1);
		pieChart.setData(data);

		pieChart.highlightValues(null);

		pieChart.invalidate();
	}*/

}
