package uk.ac.kcl.SEG_Project_2.data;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.kcl.SEG_Project_2.constants.C;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Cache {

	public static List<JSONObject> getData(Context context, ApiRequest request) {
		// find cache file
		File cacheFile = findFile(context, request);
		if (cacheFile == null) return null;

		// read file contents
		StringBuilder fileContents = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(cacheFile));
			String line;
			while ((line = reader.readLine()) != null) fileContents.append(line);
			reader.close();
		} catch (FileNotFoundException e) {
			Log.d(C.LOG_TAG, "Failed to open " + cacheFile.getName() + " to read for publishing");
			return null;
		} catch (IOException e) {
			Log.d(C.LOG_TAG, "Failed to read from " + cacheFile.getName() + " for publishing");
			return null;
		}

		// parse result
		List<JSONObject> output = new ArrayList<JSONObject>();
		String[] fileParts = fileContents.toString().split(C.CACHE_DELIM);
		for (String fP : fileParts) {
			try {
				output.add(new JSONObject(fP));
			} catch (JSONException e) {
				return null;
			}
		}

		// base case
		return output;
	}

	public static void saveData(Context context, ApiRequest request, List<JSONObject> data) {
		// compile a string to save to the file
		StringBuilder toSaveBuilder = new StringBuilder();
		for (JSONObject o : data) {
			toSaveBuilder.append(o.toString());
			toSaveBuilder.append(C.CACHE_DELIM);
		}
		String toSave = toSaveBuilder.substring(0, toSaveBuilder.length() - C.CACHE_DELIM.length());

		// create a file to save to
		String hash = request.createHash();
		long currentTime = System.currentTimeMillis() / 1000;
		File toSaveTo = new File(context.getFilesDir(), "cache." + hash + "." + currentTime);

		// write to file
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(toSaveTo, true));
			writer.append(toSave);
			writer.close();
		} catch (Exception e) {
			// well, at least we tried - things will still work, just sans-case
		}
	}

	public static boolean hasData(Context context, ApiRequest request) {
		File cacheFile = findFile(context, request);
		return !(cacheFile == null);
	}

	public static File findFile(Context context, ApiRequest request) {
		// create hash for request
		String hash = request.createHash();

		// loop through files, looking for match
		File cacheFolder = context.getFilesDir();
		File[] cacheFiles = cacheFolder.listFiles();
		long currentTime = System.currentTimeMillis() / 1000;
		for (File f : cacheFiles) {
			// check file format
			String name = f.getName();
			if (name.startsWith("cache." + hash)) {
				String[] nameParts = name.split("\\.");
				if (nameParts.length != 3) continue;
				long timeStamp;
				try {
					timeStamp = Long.parseLong(nameParts[2]);
				} catch (NumberFormatException e) {
					continue;
				}
				// check if file is within expiry time
				if (currentTime - timeStamp <= C.CACHE_EXPIRY) {
					return f;
				} else {
					// delete if not
					f.delete();
				}
			}
		}
		return null;
	}
}