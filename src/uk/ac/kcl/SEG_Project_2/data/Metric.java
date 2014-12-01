package uk.ac.kcl.SEG_Project_2.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Metric implements Parcelable{

	private String name;
	private String info;
	private int iconId;

	public Metric(String name, String info, int iconId) {
		this.name = name;
		this.info = info;
		this.iconId = iconId;
	}

	public Metric(Parcel in) {
		this.name = in.readString();
		this.info = in.readString();
		this.iconId = in.readInt();
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(info);
		dest.writeInt(iconId);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Metric createFromParcel(Parcel in) {
			return new Metric(in);
		}

		public Metric[] newArray(int size) {
			return new Metric[size];
		}
	};
}
