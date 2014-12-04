package uk.ac.kcl.SEG_Project_2.constants;

public class C {

	/* NETWORKING */
	public static final String API_URI_FORMAT = "http://api.worldbank.org/countries/%s/indicators/%s?format=json&frequency=%s&per_page=100%s&page=::PAGE::";
	public static final int MIN_YEAR = 1900;
	public static final int MAX_YEAR = 2014;// TODO: (new Date()).getYear();

	/* CACHE */
	public static final int CACHE_EXPIRY = 6 * 60 * 60;
	public static final String CACHE_DELIM = "###cachedelim###";

	/* DEBUGGING */
	public static final String LOG_TAG = "++ SEG2 ++";

	/* OPTIONS */
	public static final int MAX_COUNTRIES = 4;

	/* GRAPHING */
	public static int[] GRAPH_COLOURS = new int[]{
			0x99ff0000,
			0x99ffff00,
			0x9900ff00,
			0x9900ffff,
			0x990000ff,
			0x99ff00ff,
			0x99aa0000,
			0x99aaaa00,
			0x9900aa00,
			0x9900aaaa,
			0x990000aa,
			0x99aa00aa
	};

}
