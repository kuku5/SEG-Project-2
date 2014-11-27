package uk.ac.kcl.SEG_Project_2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.data.Country;

import java.util.ArrayList;
import java.util.List;

public class CountryListAdapter extends BaseAdapter {

	private ArrayList<Country> countryList;
	private Context context;
	private LayoutInflater inflater;

	public CountryListAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	public void setCountryList(ArrayList<Country> countryList) {
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
		CheckBox cb;
		TextView tv;

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

			// create view holder
			CountryRowViewHolder holder = new CountryRowViewHolder();
			holder.cb = cb;
			holder.tv = tv;
			view.setTag(holder);

			// set action listeners on the checkbox
			cb.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					Integer position = (Integer) cb.getTag();
					countryList.get(position).setSelected(true);
				}
			});
		} else {
			// fast recycling with view holder
			view = convertView;
			CountryRowViewHolder holder = (CountryRowViewHolder) view.getTag();
			cb = holder.cb;
			tv = holder.tv;
		}

		// set the position of this checkbox
		cb.setTag(new Integer(position));

		// set up view
		cb.setChecked(c.isSelected());
		tv.setText(c.getName());

		// finish
		return view;
	}

	public List<Country> getSelectedCountries() {
		ArrayList<Country> selected = new ArrayList<Country>();
		for (Country c : countryList) {
			if (c.isSelected()) selected.add(c);
		}
		return selected;
	}

	private class CountryRowViewHolder {
		public CheckBox cb;
		public TextView tv;
	}
}
