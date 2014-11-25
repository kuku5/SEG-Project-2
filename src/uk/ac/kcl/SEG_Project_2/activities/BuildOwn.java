package uk.ac.kcl.SEG_Project_2.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import uk.ac.kcl.SEG_Project_2.R;

public class BuildOwn extends Activity implements OnClickListener {
	Spinner spSelect;
	String graphList[] = { "Graph type 1", "Graph type 2", "Graph type 3" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selection);
		initialise();
		populateSpinner();
		populateGraphList();
	}

	private void initialise() {
		spSelect = (Spinner) findViewById(R.id.spSelect);

	}

	private void populateSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.list_metric_type,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spSelect.setAdapter(adapter);
	}

	private void populateGraphList() {
		final LinearLayout lm = (LinearLayout) findViewById(R.id.llSelection);
		ScrollView sv = new ScrollView(this);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);

		for (int i = 0; i < graphList.length; i++) {
			TextView tv = new TextView(this);
			tv.setTextSize(getResources().getDimension(R.dimen.textsize));
			tv.setOnClickListener(this);
			tv.setTag(graphList[i]);
			tv.setText(graphList[i]);
			ll.addView(tv);

		}

		lm.addView(sv);

	}

	@Override
	public void onClick(View v) {
		if (v.getTag().equals("Graph type 1")) {
		}
	}
}
