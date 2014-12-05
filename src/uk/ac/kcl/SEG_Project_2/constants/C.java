package uk.ac.kcl.SEG_Project_2.constants;

import java.util.Calendar;

public class C {

	/* NETWORKING */
	public static final String API_URI_FORMAT = "http://api.worldbank.org/countries/%s/indicators/%s?format=json&frequency=%s&per_page=100%s&page=::PAGE::";
	public static final int MIN_YEAR = 1960;
	public static final int MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR);

	/* CACHE */
	public static final int CACHE_EXPIRY = 6 * 60 * 60;
	public static final String CACHE_DELIM = "###cachedelim###";

	/* DEBUGGING */
	public static final String LOG_TAG = "++ SEG2 ++";

	/* OPTIONS */
	public static final int MAX_COUNTRIES = 4;

	/* GRAPHING */
	public static final String LEGEND_DELIM = " - ";
	public static final int[] GRAPH_COLOURS = new int[]{
			0x998b0000,
			0x99ff0000,
			0x99ff8c00,
			0x99ffa500,
			0x99ffff00,
			0x9900ff00,
			0x99008000,
			0x99000080,
			0x990000ff,
			0x99800080,
			0x99ff1493,
			0x99a52a2a,
			0x995c4033
	};

}
