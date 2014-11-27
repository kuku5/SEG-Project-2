package uk.ac.kcl.SEG_Project_2.data;

import org.json.JSONObject;

import java.util.List;

public interface ApiRequest {

	/*
	Request builders
	 */

	/**
	 * Set the indicator that this request will fetch
	 *
	 * @param indicator The indicator to query
	 */
	public void setIndicator(String indicator);

	/**
	 * Set the countries that this request will fetch data for
	 *
	 * @param countries The countries to query (defaults to all)
	 */
	public void setCountries(String... countries);

	/**
	 * Set the date range that this request will fetch data for
	 *
	 * @param startMonth The month at the start of the date range (set to 0 to search year-only)
	 * @param startYear  The year at the start of the date range
	 * @param endMonth   The month at the end of the date range (set to 0 to search year-only)
	 * @param endYear    The year at the end of the date range
	 */
	public void setDateRange(int startMonth, int startYear, int endMonth, int endYear);

	/**
	 * Set the frequency of data that this query should fetch (may be ignored by the API
	 *
	 * @param frequency The frequency of data to collect (yearly, quarterly or monthly)
	 */
	public void setFrequency(Frequency frequency);

	/**
	 * Set whether the request should bypass the cache and forcibly collect fresh data
	 *
	 * @param forceFresh True for forcing fresh data; defaults to false
	 */
	public void setForceFresh(boolean forceFresh);

	/**
	 * Create a hash to represent the request
	 *
	 * @return a hash representing the request
	 */
	public String createHash();

	/*
	Request running behaviour modifiers
	 */

	/**
	 * Set a listener for request completion
	 *
	 * @param onComplete Request completion callback listener
	 */
	public void setOnComplete(OnCompleteListener onComplete);

	/**
	 * Set a listener for request failure
	 *
	 * @param onFail Request failure callback listener
	 */
	public void setOnFail(OnFailListener onFail);

	/**
	 * Set a listener for request cancellation
	 *
	 * @param onCancel Request cancellation callback listener
	 */
	public void setOnCancel(OnCancelListener onCancel);

	/**
	 * Set a listener for request progress update
	 *
	 * @param onStatusUpdate Request progress update callback listener
	 */
	public void setOnProgressUpdate(OnProgressUpdateListener onStatusUpdate);

	/*
	Request execution methods
	 */

	/**
	 * Start execution of the method
	 */
	public void execute();

	/**
	 * Cancel an already-executing method
	 */
	public void cancel();

	/*
	Request result accessors
	 */

	/**
	 * Get the current status of the request
	 *
	 * @return The current status of the request
	 */
	public Status getStatus();

	/**
	 * Get the data returned by the request
	 *
	 * @return The data returned by the request
	 */
	public List<JSONObject> getResult();

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

	/**
	 * Called when the request completes
	 */
	public interface OnCompleteListener {

		public void onComplete();
	}

	/**
	 * Called when the request fails
	 */
	public interface OnFailListener {

		public void onFail();
	}

	/**
	 * Called when the request is cancelled
	 */
	public interface OnCancelListener {

		public void onCancel();
	}

	/**
	 * Called when the request progress updates
	 * progress will be between 0 and 1
	 */
	public interface OnProgressUpdateListener {

		public void onProgressUpdate(float progress);
	}

}
