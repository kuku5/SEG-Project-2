package uk.ac.kcl.SEG_Project_2.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.adapters.MetricListAdapter;
import uk.ac.kcl.SEG_Project_2.data.MetricList;

public class SelectMetric extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setTitle("Select a Metric");
		setContentView(R.layout.metrics);
		GridView gridView = (GridView) findViewById(R.id.metric_grid_view);

		// set up adapter for grid view
		MetricListAdapter adapter = new MetricListAdapter(getBaseContext(), MetricList.getMetrics());
		gridView.setAdapter(adapter);
	}

}
