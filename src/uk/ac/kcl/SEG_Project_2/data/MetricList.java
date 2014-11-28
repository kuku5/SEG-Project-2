package uk.ac.kcl.SEG_Project_2.data;

import java.util.ArrayList;

public class MetricList {

	static private ArrayList<Metric> metrics = new ArrayList<Metric>(4);

	static {
		metrics.add(0, new Metric("Gas Emissions", "", 0));
		metrics.add(1, new Metric("Population", "", 0));
		metrics.add(2, new Metric("Deforestation", "", 0));
		metrics.add(3, new Metric("Elec. Use", "", 0));
	}

	public static ArrayList<Metric> getMetrics() {
		return metrics;
	}
}

