package uk.ac.kcl.SEG_Project_2.constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

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
}
