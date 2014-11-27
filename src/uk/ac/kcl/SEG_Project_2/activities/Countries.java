package uk.ac.kcl.SEG_Project_2.activities;

import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.kcl.SEG_Project_2.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Countries extends Activity implements OnClickListener {

	private String countryList[] = { "Lithuania", "United Kingdom",
			"Wonderland" };
	private ArrayList<String> countries = new ArrayList<String>(
			Arrays.asList(countryList));
	private Button btnProceed;
	private TextView text;
	private CheckBox ch;
	private ArrayList selectedCountries = new ArrayList();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.countries);

		initialise();
		populate();

	}

	private void initialise() {

		btnProceed = (Button) findViewById(R.id.btProceed);
		btnProceed.setOnClickListener(this);

	}

	private void populate() {
		final LinearLayout lm = (LinearLayout) findViewById(R.id.llCountries);
		for (int i = 0; i < countries.size(); i++) {
			ch = new CheckBox(this);
			ch.setOnClickListener(this);
			ch.setTag(countries.get(i));
			ch.setText(countries.get(i));
			lm.addView(ch);

		}
	}

	private void check(Object o) {
		if (selectedCountries.contains(o)) {
			selectedCountries.remove(o);
		} else {

			selectedCountries.add(o);

		}
	}

	@Override
	public void onClick(View v) {
		check(v.getTag());
		switch (v.getId()) {
		case R.id.btProceed:

			Intent i = new Intent(getBaseContext(), MetricSelection.class);
			i.putCharSequenceArrayListExtra("a", selectedCountries);
			startActivity(i);
			break;
		}

	}
}
