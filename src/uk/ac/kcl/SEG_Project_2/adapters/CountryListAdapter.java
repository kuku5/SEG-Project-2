package uk.ac.kcl.SEG_Project_2.adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.constants.C;
import uk.ac.kcl.SEG_Project_2.data.Country;

import java.util.ArrayList;
import java.util.List;

public class CountryListAdapter extends BaseAdapter implements Filterable {

	private ArrayList<Country> originalCountryList;
	private ArrayList<Country> countryList;
	private ArrayList<Country> selectedCountries = new ArrayList<Country>();
	private Context context;
	private LayoutInflater inflater;
	private String filterString = "";

	public CountryListAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	public void setCountryList(ArrayList<Country> countryList, boolean setOriginal) {
		if (setOriginal) this.originalCountryList = countryList;
		this.countryList = countryList;
	}

	@Override
	public int getCount() {
		return countryList.size();
	}

	@Override
	public Object getItem(int position) {
		return countryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// children in each row
		final CheckBox cb;
		TextView tv;
		ImageView iv;

		// get object to work with
		Country c = countryList.get(position);

		// recycle/create view
		View view;
		if (convertView == null) {
			// inflate new view
			view = inflater.inflate(R.layout.country_row, parent, false);

			// get views
			cb = (CheckBox) view.findViewById(R.id.country_row_checkbox);
			tv = (TextView) view.findViewById(R.id.country_row_textview);
			iv = (ImageView) view.findViewById(R.id.country_row_imageview);

			// create view holder
			CountryRowViewHolder holder = new CountryRowViewHolder();
			holder.cb = cb;
			holder.tv = tv;
			holder.iv = iv;
			view.setTag(holder);

			// set action listeners on the checkbox
			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					CheckBox cb = (CheckBox) buttonView;
					if (isChecked) {
						if (!selectedCountries.contains((Country) cb.getTag())) {
							// enforce a maximum number of countries
							if (selectedCountries.size() >= C.MAX_COUNTRIES) {
								Toast.makeText(
										context,
										context.getResources().getString(R.string.country_too_many, C.MAX_COUNTRIES),
										Toast.LENGTH_LONG
								).show();
								cb.toggle();
								return;
							}
							selectedCountries.add((Country) cb.getTag());
						}
					} else {
						selectedCountries.remove((Country) cb.getTag());
					}
				}
			});
		} else {
			// fast recycling with view holder
			view = convertView;
			CountryRowViewHolder holder = (CountryRowViewHolder) view.getTag();
			cb = holder.cb;
			tv = holder.tv;
			iv = holder.iv;
		}

		// set the position of this checkbox
		cb.setTag(c);

		// set up checkbox
		cb.setChecked(selectedCountries.contains(c));

		// set bold/underline filter
		SpannableStringBuilder ssb = new SpannableStringBuilder(c.getName());
		if (!filterString.equals("")) {
			int index = c.getName().toLowerCase().indexOf(filterString.toLowerCase());
			while (index >= 0) {
				ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + filterString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.setSpan(new UnderlineSpan(), index, index + filterString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				index = c.getName().toLowerCase().indexOf(filterString.toLowerCase(), index + 1);
			}
		}
		tv.setText(ssb);

		// set up flag
		int flagId = context.getResources().getIdentifier("flag_" + c.getId().toLowerCase(), "drawable", context.getApplicationContext().getPackageName());
		if (flagId != 0) {
			iv.setImageResource(flagId);
		} else {
			iv.setImageResource(R.drawable.flag_unknown);
		}

		// set a click listener
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cb.toggle();
			}
		});

		// finish
		return view;
	}

	public List<Country> getSelectedCountries() {
		return selectedCountries;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				filterString = constraint == null ? "" : constraint.toString();
				FilterResults result = new FilterResults();
				if (constraint == null || constraint.equals("")) {
					result.values = originalCountryList;
					result.count = originalCountryList.size();
				} else {
					ArrayList<Country> filteredDataset = new ArrayList<Country>();
					for (Country c : originalCountryList) {
						if (c.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
							filteredDataset.add(c);
						}
					}
					result.values = filteredDataset;
					result.count = filteredDataset.size();
				}
				return result;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results.count == 0) {
					setCountryList(new ArrayList<Country>(), false);
					notifyDataSetChanged();
				} else {
					try {
						setCountryList((ArrayList<Country>) results.values, false);
						notifyDataSetChanged();
					} catch (ClassCastException e) {
						setCountryList(new ArrayList<Country>(), false);
						notifyDataSetChanged();
					}
				}
			}
		};
	}

	private class CountryRowViewHolder {

		public CheckBox cb;
		public TextView tv;
		public ImageView iv;
	}
}
