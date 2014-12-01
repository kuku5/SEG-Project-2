package uk.ac.kcl.SEG_Project_2.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.constants.Utils;
import uk.ac.kcl.SEG_Project_2.data.Metric;

import java.util.ArrayList;

public class MetricListAdapter extends BaseAdapter {

	private ArrayList<Metric> metrics;
	private Activity activity;
	private Context context;
	private LayoutInflater inflater;

	public MetricListAdapter(Activity activity, ArrayList<Metric> metrics) {
		this.activity = activity;
		this.context = activity.getBaseContext();
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
		ImageView i;
		TextView t;

		// get object to work with
		final Metric m = metrics.get(position);

		// recycle/create view
		View view;
		if (convertView == null) {
			// inflate new view
			view = inflater.inflate(R.layout.metrics_cell, parent, false);

			// get views
			i = (ImageView) view.findViewById(R.id.metric_cell_image);
			t = (TextView) view.findViewById(R.id.metric_cell_text);

			// create view holder
			MetricListCellHolder holder = new MetricListCellHolder();
			holder.i = i;
			holder.t = t;
			view.setTag(holder);
		} else {
			// fast recycling with view holder
			view = convertView;
			MetricListCellHolder holder = (MetricListCellHolder) view.getTag();
			i = holder.i;
			t = holder.t;
		}

		// set text of the button
		t.setText(m.getName());

		// set the button
		if (m.getIconId() > 0) {
			// set an icon
			i.setImageResource(m.getIconId());
		} else {
			// default icon
			i.setImageResource(R.drawable.unknown);
		}

		// set listeners
		view.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Utils.createInfoDialog(activity, m.getName(), m.getInfo());
				return false;
			}
		});

		// return view
		return view;
	}

	// view holder pattern
	private class MetricListCellHolder {

		public ImageView i;
		public TextView t;
	}
}
