package uk.ac.kcl.SEG_Project_2.data;

import org.json.JSONObject;

import java.util.List;

public interface WorldBankApiRequest {

	/*
	Request builders
	 */

	public void setIndicator(String indicator);

	public void setCountries(String... countries);

	public void setDateRange(int startMonth, int startYear, int endMonth, int endYear);

	public void setFrequency(Frequency frequency);

	public void setForceFresh(boolean forceFresh);

	/*
	Request running behaviour modifiers
	 */

	public void setOnComplete(OnCompleteListener onComplete);

	public void setOnFail(OnFailListener onFail);

	public void setOnCancel(OnCancelListener onCancel);

	public void setOnProgressUpdate(OnProgressUpdateListener onStatusUpdate);

	/*
	Request execution methods
	 */

	public void execute();

	public void cancel();

	/*
	Request result accessors
	 */

	public Status getStatus();

	public List<JSONObject> getResult();

	public Integer getResponseCode();

	/*
	Request status/settings
	 */

	public enum Frequency {
		MONTHLY,
		QUARTERLY,
		YEARLY
	}

	public enum Status {
		WAITING,
		EXECUTING,
		COMPLETED,
		FAILED,
		CANCELLED
	}

	/*
	Listener classes
	 */

	public interface OnCompleteListener {
		public void onComplete();
	}

	public interface OnFailListener {
		public void onFail();
	}

	public interface OnCancelListener {
		public void onCancel();
	}

	public interface OnProgressUpdateListener {
		public void onProgressUpdate(float progress);
	}

}
