package uk.ac.kcl.SEG_Project_2.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.GridView;
import android.widget.Toast;
import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.adapters.MetricListAdapter;
import uk.ac.kcl.SEG_Project_2.data.Country;
import uk.ac.kcl.SEG_Project_2.constants.MetricList;

import java.util.ArrayList;

public class SelectMetric extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTitle(R.string.select_a_metric);
		setContentView(R.layout.metrics);
		GridView gridView = (GridView) findViewById(R.id.metric_grid_view);

		// get selected countries
		Bundle bundle = getIntent().getExtras();
		ArrayList<Country> selectedCountries = bundle.getParcelableArrayList("countries");

		// TODO: remove this when these are implemented properly
		ArrayList<String> selectedNames = new ArrayList<String>();
		for (Country c : selectedCountries) {
			selectedNames.add(c.getName());
		}
		Toast.makeText(getBaseContext(), "Selected: " + TextUtils.join(", ", selectedNames), Toast.LENGTH_LONG).show();

		// set up adapter for grid view
		MetricListAdapter adapter = new MetricListAdapter(SelectMetric.this, MetricList.getMetrics());
		gridView.setAdapter(adapter);
	}

}
