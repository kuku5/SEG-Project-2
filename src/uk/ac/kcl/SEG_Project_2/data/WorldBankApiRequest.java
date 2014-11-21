package uk.ac.kcl.SEG_Project_2.data;

import java.util.HashMap;
import java.util.List;

public interface WorldBankApiRequest {

	/*
	Request builders
	 */

	public void setIndicators(List<String> indicators);

	public void setCountries(List<String> countries);

	public void setDateRange(int startMonth, int startYear, int endMonth, int endYear);

	public void setFrequency(Frequency frequency);

	public void setForceFresh(boolean forceFresh);

	/*
	Request running behaviour modifiers
	 */

	public void setOnComplete(Runnable onComplete);

	public void setOnFail(Runnable onFail);

	public void setOnCancel(Runnable onCancel);

	/*
	Request execution methods
	 */

	public void execute();

	public void cancel();

	/*
	Request result accessors
	 */

	public Status getStatus();

	public HashMap<String, Object> getResult();

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
		PENDING,
		OKAY,
		FAILED,
		CANCELLED
	}

}
