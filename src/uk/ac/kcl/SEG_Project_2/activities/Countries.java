package uk.ac.kcl.SEG_Project_2.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import uk.ac.kcl.SEG_Project_2.R;

public class Countries extends Activity implements OnClickListener {

	private String countryList[] = { "Lithuania", "United Kingdom",
			"Wonderland", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test",
			"test", "test", "test", "test", "test", "test", "test", "test" };
	private Button btnProceed;
	private EditText text;
	private CheckBox ch;

	ArrayList<String> arr = new ArrayList<String>();

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

		text = (EditText) findViewById(R.id.etText);

	}

	private void populate() {
		final LinearLayout lm = (LinearLayout) findViewById(R.id.llCountries);
		ScrollView sv = new ScrollView(this);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);

		for (int i = 0; i < countryList.length; i++) {
			ch = new CheckBox(this);
			ch.setOnClickListener(this);
			ch.setTag(countryList[i]);
			ch.setText(countryList[i]);
			ll.addView(ch);

		}

		lm.addView(sv);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btProceed:
			startActivity(new Intent("com.project.seg2.METRICSELECTION"));
			break;
		}

	}
}
