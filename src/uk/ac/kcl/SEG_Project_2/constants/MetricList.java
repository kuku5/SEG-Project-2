package uk.ac.kcl.SEG_Project_2.constants;

import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.data.Metric;

import java.util.ArrayList;

public class MetricList {

	public static final int LINE_GRAPH = 1;
	public static final int BAR_CHART = 2;

	static private ArrayList<Metric> metrics = new ArrayList<Metric>();

	static {
		metrics.add(0, new Metric(
				"Gas Emissions",
				"This metric will display the greenhouse gas emissions for each country, grouped into CO2, methane, nitrous oxides and others.",
				R.drawable.gases,
				new String[][]{
						new String[]{"EN.ATM.CO2E.KT", "CO2, kT"},
						new String[]{"EN.ATM.METH.KT.CE", "Methane, kT"},
						new String[]{"EN.ATM.NOXE.KT.CE", "N-Oxides, kT"},
						new String[]{"EN.ATM.GHGO.KT.CE", "Other, kT"}
				},
				LINE_GRAPH
		));
		metrics.add(1, new Metric(
				"Population",
				"This metric will display the population sizes for each country selected.",
				R.drawable.population,
				new String[][]{
						new String[]{"SP.POP.TOTL", "Population"}
				},
				BAR_CHART
		));
		metrics.add(2, new Metric(
				"Forestation",
				"This metric will display the sum of forested areas that each country has.",
				R.drawable.tree,
				new String[][]{
						new String[]{"AG.LND.FRST.K2", "Forest Area, KM sq."}
				},
				LINE_GRAPH
		));
		metrics.add(3, new Metric(
				"Elec. Use", "This metric will display the electrical consumption of each country selected.",
				R.drawable.electricity,
				new String[][]{
						new String[]{"EG.USE.ELEC.KH.PC", "Elec. Use, kW"}
				},
				LINE_GRAPH
		));
	}

	public static ArrayList<Metric> getMetrics() {
		return metrics;
	}
}
