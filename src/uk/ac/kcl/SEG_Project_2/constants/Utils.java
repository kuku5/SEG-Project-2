package uk.ac.kcl.SEG_Project_2.constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import uk.ac.kcl.SEG_Project_2.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

	public static String createSHA256(String input) {
		StringBuilder output = new StringBuilder();
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(input.getBytes());
			byte byteData[] = messageDigest.digest();
			for (byte aByteData : byteData) {
				output.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
			}
			return output.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static void createInfoDialog(Activity activity, String title, String info) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(title)
				.setMessage(info)
				.setPositiveButton("Ok", null);
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}

	public static void createDatePickerDialog(final Activity activity, int fromYear, int toYear, final OnDatePickerDone onDone) {
		final Dialog selectDates = new Dialog(activity);
		selectDates.setTitle(R.string.date_picker_title);
		selectDates.setContentView(R.layout.date_picker_dialog);

		final NumberPicker npFrom = (NumberPicker) selectDates.findViewById(R.id.date_picker_from);
		npFrom.setMaxValue(C.MAX_YEAR);
		npFrom.setMinValue(C.MIN_YEAR);
		npFrom.setValue(fromYear);
		npFrom.setWrapSelectorWheel(false);

		final NumberPicker npTo = (NumberPicker) selectDates.findViewById(R.id.date_picker_to);
		npTo.setMaxValue(C.MAX_YEAR);
		npTo.setMinValue(C.MIN_YEAR);
		npTo.setValue(toYear);
		npTo.setWrapSelectorWheel(false);

		Button btSet = (Button) selectDates.findViewById(R.id.date_picker_okay);
		btSet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int from = npFrom.getValue();
				int to = npTo.getValue();
				if (from > to) {
					Toast.makeText(activity.getBaseContext(), R.string.date_picker_invalid, Toast.LENGTH_SHORT).show();
					return;
				}
				onDone.onDone(false, from, to);
				selectDates.cancel();
			}
		});

		final Button btCancel = (Button) selectDates.findViewById(R.id.date_picker_cancel);
		btCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDone.onDone(true, npFrom.getValue(), npTo.getValue());
				selectDates.cancel();
			}
		});

		selectDates.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				onDone.onDone(true, npFrom.getValue(), npTo.getValue());
			}
		});

		selectDates.show();
	}

	public interface OnDatePickerDone {

		public void onDone(boolean cancelled, int fromYear, int toYear);
	}
}
