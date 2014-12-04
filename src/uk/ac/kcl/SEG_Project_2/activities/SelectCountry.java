package uk.ac.kcl.SEG_Project_2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.adapters.CountryListAdapter;
import uk.ac.kcl.SEG_Project_2.constants.C;
import uk.ac.kcl.SEG_Project_2.data.ApiRequest;
import uk.ac.kcl.SEG_Project_2.data.Country;
import uk.ac.kcl.SEG_Project_2.data.WorldBankApiRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectCountry extends Activity {

	// data components
	private final ArrayList<Country> countryList = new ArrayList<Country>();

	// view components
	private ViewGroup loadingDisplay;
	private ViewGroup mainDisplay;
	private EditText filterTextView;
	private ImageView filterClearButton;
	private ListView countryListView;
	private Button continueButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.select_a_country);
		setContentView(R.layout.countries);
		initialise();
		populate();
	}

	private void initialise() {
		// collect view components
		loadingDisplay = (ViewGroup) findViewById(R.id.country_loading_group);
		mainDisplay = (ViewGroup) findViewById(R.id.country_main_group);
		filterTextView = (EditText) findViewById(R.id.country_list_filter);
		filterClearButton = (ImageView) findViewById(R.id.country_list_filter_clear);
		countryListView = (ListView) findViewById(R.id.country_list);
		continueButton = (Button) findViewById(R.id.country_list_continue);

		// set action listeners
		filterTextView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterClearButton.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
				SelectCountry.this.setFilter(s);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		filterClearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				filterTextView.setText("");
			}
		});
		continueButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CountryListAdapter adapter = (CountryListAdapter) countryListView.getAdapter();
				if (adapter != null && !adapter.getSelectedCountries().isEmpty()) {
					// collect countries
					ArrayList<Country> selectedCountries = (ArrayList<Country>) adapter.getSelectedCountries();

					// send to next activity
					Intent sendToSelectMetric = new Intent(getBaseContext(), SelectMetric.class);
					sendToSelectMetric.putParcelableArrayListExtra("countries", selectedCountries);
					startActivity(sendToSelectMetric);
				} else {
					Toast.makeText(getBaseContext(), "You did not select any countries", Toast.LENGTH_LONG).show();
				}
			}
		});
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
						// check if this is a country, not just a grouping from the API
						if (r.getString("longitude").equals("") || r.getString("latitude").equals("")) continue;

						// add to list
						countryList.add(new Country(r.getString("iso2Code"), r.getString("name")));
					} catch (JSONException e) {
						Log.d(C.LOG_TAG, "JSON Exception");
					}
				}

				// sort the country list
				Collections.sort(countryList, new Comparator<Country>() {
					@Override
					public int compare(Country lhs, Country rhs) {
						return lhs.getName().compareTo(rhs.getName());
					}
				});

				// create and apply adapter
				CountryListAdapter adapter = new CountryListAdapter(apiRequest.getContext());
				adapter.setCountryList(countryList, true);
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
				startActivity(new Intent(getBaseContext(), Welcome.class));
				finish();
			}
		});
		apiRequest.execute();
	}

	public void setFilter(CharSequence filter) {
		CountryListAdapter adapter = (CountryListAdapter) countryListView.getAdapter();
		if (adapter != null) {
			adapter.getFilter().filter(filter);
		}
	}

}