package uk.ac.kcl.SEG_Project_2.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Country implements Parcelable {

	private String id;
	private String name;

	public Country(Parcel in) {
		this.id = in.readString();
		this.name = in.readString();
	}

	public Country(String id, String name) {
		this.id = id;
		this.name = name;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Country createFromParcel(Parcel in) {
			return new Country(in);
		}

		public Country[] newArray(int size) {
			return new Country[size];
		}
	};
}
