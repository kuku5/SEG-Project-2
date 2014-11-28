package uk.ac.kcl.SEG_Project_2.data;

public class Metric {

	private String name;
	private String info;
	private int iconId;

	public Metric(String name, String info, int iconId) {
		this.name = name;
		this.info = info;
		this.iconId = iconId;
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
}
