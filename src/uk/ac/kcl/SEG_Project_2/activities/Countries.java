package uk.ac.kcl.SEG_Project_2.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.kcl.SEG_Project_2.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import uk.ac.kcl.SEG_Project_2.adapters.CountryListAdapter;
import uk.ac.kcl.SEG_Project_2.constants.C;
import uk.ac.kcl.SEG_Project_2.data.ApiRequest;
import uk.ac.kcl.SEG_Project_2.data.Country;
import uk.ac.kcl.SEG_Project_2.data.WorldBankApiRequest;

public class Countries extends Activity {

	private CheckBox ch;
	private ArrayList selectedCountries = new ArrayList();

	// view components
	private ViewGroup loadingDisplay;
	private ViewGroup mainDisplay;
	private ListView countryListView;
	//private Button btnProceed;

	// data components
	private final ArrayList<Country> countryList = new ArrayList<Country>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.countries);
		initialise();
		populate();
	}

	private void initialise() {
		// collect view components
		loadingDisplay = (ViewGroup) findViewById(R.id.country_loading_group);
		mainDisplay = (ViewGroup) findViewById(R.id.country_main_group);
		countryListView = (ListView) findViewById(R.id.country_list);
		//btnProceed = (Button) findViewById(R.id.btProceed);

		// set up view components
		//btnProceed.setOnClickListener(this);
	}

	private void populate() {
		// load the country list from api/cache
		final WorldBankApiRequest apiRequest = new WorldBankApiRequest(this);
		apiRequest.setGettingCountries(true);
		apiRequest.setOnComplete(new ApiRequest.OnCompleteListener() {
			@Override
			public void onComplete() {
				// parse result
				List<JSONObject> result = apiRequest.getResult();
				for (JSONObject r : result) {
					try {
						countryList.add(new Country(r.getString("id"), r.getString("name")));
					} catch (JSONException e) {
						Log.d(C.LOG_TAG, "JSON Exception");
					}
				}

				// create and apply adapter
				CountryListAdapter adapter = new CountryListAdapter(apiRequest.getContext());
				adapter.setCountryList(countryList);
				countryListView.setAdapter(adapter);

				// swap views
				loadingDisplay.setVisibility(View.GONE);
				mainDisplay.setVisibility(View.VISIBLE);
			}
		});
		apiRequest.setOnFail(new ApiRequest.OnFailListener() {
			@Override
			public void onFail() {
				Toast.makeText(apiRequest.getContext(), "Failed to get country list", Toast.LENGTH_LONG).show();
				finish();
			}
		});
		apiRequest.execute();

		/*final LinearLayout lm = (LinearLayout) findViewById(R.id.llCountries);
		for (int i = 0; i < countries.size(); i++) {
			ch = new CheckBox(this);
			ch.setOnClickListener(this);
			ch.setTag(countries.get(i));
			ch.setText(countries.get(i));
			lm.addView(ch);
		}*/
	}

	/*private void check(Object o) {
		if (selectedCountries.contains(o)) {
			selectedCountries.remove(o);
		} else {
			selectedCountries.add(o);
		}
	}*/

	/*@Override
	public void onClick(View v) {
		check(v.getTag());
		switch (v.getId()) {
			case R.id.btProceed:
				Intent i = new Intent(getBaseContext(), MetricSelection.class);
				i.putCharSequenceArrayListExtra("a", selectedCountries);
				startActivity(i);
				break;
		}
	}*/
}