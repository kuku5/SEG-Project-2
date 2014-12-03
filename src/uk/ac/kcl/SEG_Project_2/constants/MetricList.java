package uk.ac.kcl.SEG_Project_2.constants;

import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.data.Metric;

import java.util.ArrayList;

public class MetricList {

	static private ArrayList<Metric> metrics = new ArrayList<Metric>(1);

	static {
		metrics.add(0, new Metric(
				"Gas Emissions",
				"This metric will display the greenhouse gas emissions for each country, grouped into CO2, methane, nitrous oxides and others.",
				R.drawable.gases,
				new String[]{
						"EN.ATM.CO2E.KT",
						"EN.ATM.METH.KT.CE",
						"EN.ATM.NOXE.KT.CE",
						"EN.ATM.GHGO.KT.CE"
				}
		));
		metrics.add(1, new Metric(
				"Population",
				"This metric will display the population sizes for each country selected.",
				R.drawable.population,
				new String[]{
						"SP.POP.TOTL"
				}));
		metrics.add(2, new Metric(
				"Deforestation",
				"This metric will display the sum of forested areas that each country has.",
				R.drawable.tree,
				new String[]{
						"AG.LND.FRST.K2"
				}));
		metrics.add(3, new Metric(
				"Elec. Use", "This metric will display the electrical consumption of each country selected.",
				R.drawable.electricity,
				new String[]{
						"EG.USE.ELEC.KH.PC"
				}));
	}

	public static ArrayList<Metric> getMetrics() {
		return metrics;
	}
}

