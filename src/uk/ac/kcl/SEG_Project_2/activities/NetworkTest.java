package uk.ac.kcl.SEG_Project_2.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import uk.ac.kcl.SEG_Project_2.constants.C;
import uk.ac.kcl.SEG_Project_2.data.ApiRequest;
import uk.ac.kcl.SEG_Project_2.data.WorldBankApiRequest;

public class NetworkTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ApiRequest request = new ApiRequest(this);
		request.setIndicator("SP.POP.TOTL");
		request.setCountries("bra", "gbr");
		request.setDateRange(0, 2000, 0, 2014);
		request.setFrequency(WorldBankApiRequest.Frequency.YEARLY);
		request.setOnProgressUpdate(new WorldBankApiRequest.OnProgressUpdateListener() {
			@Override
			public void onProgressUpdate(float progressDone) {
				Log.d(C.LOG_TAG, "Done " + Math.round(progressDone * 100) + "%");
			}
		});
		request.setOnComplete(new WorldBankApiRequest.OnCompleteListener() {
			@Override
			public void onComplete() {
				Log.d(C.LOG_TAG, "Done!");
				Log.d(C.LOG_TAG, "Found " + request.getResult().length() + " records");
			}
		});
		request.setOnFail(new WorldBankApiRequest.OnFailListener() {
			@Override
			public void onFail() {
				Log.d(C.LOG_TAG, "Failed!");
			}
		});

		request.execute();
	}
}
