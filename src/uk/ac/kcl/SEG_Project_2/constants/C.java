package uk.ac.kcl.SEG_Project_2.constants;

public class C {

	/* NETWORKING */
	public static final String API_URI_FORMAT = "http://api.worldbank.org/countries/%s/indicators/%s?format=json&frequency=%s&per_page=100%s&page=::PAGE::";

    /* CACHE */
    public static final int CACHE_EXPIRY = 6*60*60;
	public static final String CACHE_DELIM = "###cachedelim###";

	/* DEBUGGING */
	public static final String LOG_TAG = "++ SEG2 ++";

	/* OPTIONS */
	public static final int MAX_COUNTRIES = 4;

}
