package uk.ac.kcl.SEG_Project_2.data;

public class Metric {

	private String name;
	private String info;
	private int iconId;
	private String[] indicators;

	public Metric(String name, String info, int iconId, String[] indicators) {
		this.name = name;
		this.info = info;
		this.iconId = iconId;
		this.indicators = indicators;
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

}
