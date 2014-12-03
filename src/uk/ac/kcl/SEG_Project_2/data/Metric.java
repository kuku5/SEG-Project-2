package uk.ac.kcl.SEG_Project_2.data;

public class Metric {

	private String name;
	private String info;
	private int iconId;
	private String[] indicators;
	private int graphType;

	public Metric(String name, String info, int iconId, String[] indicators, int graphType) {
		this.name = name;
		this.info = info;
		this.iconId = iconId;
		this.indicators = indicators;
		this.graphType = graphType;
	}

	public String getName() {
		return name;
	}

	public String getInfo() {
		return info;
	}

	public int getIconId() {
		return iconId;
	}

	public String[] getIndicators() {
		return indicators;
	}

	public int getGraphType() {
		return graphType;
	}
}
