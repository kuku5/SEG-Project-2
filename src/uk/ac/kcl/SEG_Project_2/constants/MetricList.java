package uk.ac.kcl.SEG_Project_2.constants;

import uk.ac.kcl.SEG_Project_2.R;
import uk.ac.kcl.SEG_Project_2.data.Metric;

import java.util.ArrayList;

public class MetricList {

	static private ArrayList<Metric> metrics = new ArrayList<Metric>(1);

	static {
		metrics.add(0, new Metric(
				"Gas Emissions",
				"Some info about gas here...",
				R.drawable.gases,
				new String[] {
						"EN.ATM.CO2E.KT",
						"EN.ATM.METH.KT.CE",
						"EN.ATM.NOXE.KT.CE",
						"EN.ATM.GHGO.KT.CE"
				}
		));
		//metrics.add(1, new Metric("Population", "Some info about population here...", R.drawable.population));
		//metrics.add(2, new Metric("Deforestation", "Some info about tree hugging here...", R.drawable.tree));
		//metrics.add(3, new Metric("Elec. Use", "Some info about electricity here...", R.drawable.electricity));
	}

	public static ArrayList<Metric> getMetrics() {
		return metrics;
	}
}

