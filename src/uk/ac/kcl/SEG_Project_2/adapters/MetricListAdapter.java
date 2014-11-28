package uk.ac.kcl.SEG_Project_2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.data.Metric;

import java.util.ArrayList;

public class MetricListAdapter extends BaseAdapter {

	private ArrayList<Metric> metrics;
	private Context context;
	private LayoutInflater inflater;

	public MetricListAdapter(Context context, ArrayList<Metric> metrics) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.metrics = metrics;
	}

	@Override
	public int getCount() {
		return metrics.size();
	}

	@Override
	public Object getItem(int position) {
		return metrics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// children in each cell
		Button b;

		// get object to work with
		Metric m = metrics.get(position);

		// recycle/create view
		View view;
		if (convertView == null) {
			// inflate new view
			view = inflater.inflate(R.layout.metrics_cell, parent, false);

			// get views
			b = (Button) view.findViewById(R.id.metric_cell_button);

			// create view holder
			MetricListCellHolder holder = new MetricListCellHolder();
			holder.b = b;
			view.setTag(holder);
		} else {
			// fast recycling with view holder
			view = convertView;
			MetricListCellHolder holder = (MetricListCellHolder) view.getTag();
			b = holder.b;
		}

		// set text of the button
		b.setText(m.getName());

		// set the button
		if (m.getIconId() > 0) {
			// set an icon
			b.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.flag_unknown, 0, 0);
		} else {
			// default icon
			b.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.flag_unknown, 0, 0);
		}

		// return view
		return view;
	}

	// view holder pattern
	private class MetricListCellHolder {

		public Button b;
	}
}
