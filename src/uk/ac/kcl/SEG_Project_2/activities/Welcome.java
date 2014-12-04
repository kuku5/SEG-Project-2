package uk.ac.kcl.SEG_Project_2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import uk.ac.kcl.SEG_Project_2.R;

public class Welcome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		Button continueButton = (Button) findViewById(R.id.welcome_continue);
		continueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SelectCountry.class));
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}
