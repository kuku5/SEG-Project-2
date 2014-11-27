package uk.ac.kcl.SEG_Project_2.data;

public class Country {

	private String id;
	private String name;
	private boolean selected;

	public Country(String id, String name) {
		this.id = id;
		this.name = name;
		selected = false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
