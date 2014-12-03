package uk.ac.kcl.SEG_Project_2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.adapters.MetricListAdapter;
import uk.ac.kcl.SEG_Project_2.constants.MetricList;
import uk.ac.kcl.SEG_Project_2.data.Country;

import java.util.ArrayList;

public class SelectMetric extends Activity {

	private ArrayList<Country> selectedCountries;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTitle(R.string.select_a_metric);
		setContentView(R.layout.metrics);
		GridView gridView = (GridView) findViewById(R.id.metric_grid_view);

		// get selected countries
		Bundle bundle = getIntent().getExtras();
		selectedCountries = bundle.getParcelableArrayList("countries");

		// set up adapter for grid view
		MetricListAdapter adapter = new MetricListAdapter(SelectMetric.this, MetricList.getMetrics());
		gridView.setAdapter(adapter);
	}

	public void onMetricSelect(int position) {
		Intent sendToData = new Intent(getBaseContext(), DataDisplay.class);
		sendToData.putParcelableArrayListExtra("countries", selectedCountries);
		sendToData.putExtra("metric_position", position);
		// TODO: Replace with real info from user input
		sendToData.putExtra("startYear", 1990);
		sendToData.putExtra("endYear", 2014);
		startActivity(sendToData);
	}

}
