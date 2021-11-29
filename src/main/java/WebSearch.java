import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Class to search the web using Bing's API that's almost 100% identical to Bing's documentation on how to use their
 * API lmao
 * @author Ryan Varnell.
 */
public class WebSearch {
    private final static String subscriptionKey = System.getenv("BING_SEARCH_KEY");
    private final static String host = "https://api.bing.microsoft.com";
    private final static String path = "/v7.0/images/search";
    private static JsonObject searchResult;

    /**
     * Single parameter constructor.
     * @param searchQuery The term/string to be searched.
     */
    WebSearch(String searchQuery) {
        try {
            Search(searchQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the image from the first relevant search result relevant to the search query.
     * @return Image url.
     */
    public String getImageUrl() {
        return searchResult.get("thumbnailUrl").getAsString();
    }

    /**
     * Gets an image based on the user's query.
     * @param searchQuery The user's query.
     */
    public void Search(String searchQuery) throws IOException {
        // construct the search request URL (in the form of endpoint + query string)
        URL url;
        url = new URL(host + path + "?q=" +  URLEncoder.encode(searchQuery, StandardCharsets.UTF_8));

        HttpsURLConnection connection;
        connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

        // receive JSON body
        InputStream stream;
        stream = connection.getInputStream();
        String response = new Scanner(stream).useDelimiter("\\A").next();
        // construct result object for return
        SearchResults results = new SearchResults(new HashMap<>(), response);

        // extract Bing-related HTTP headers
        Map<String, List<String>> headers = connection.getHeaderFields();
        for (String header : headers.keySet()) {
            if (header == null) continue;      // may have null key
            if (header.startsWith("BingAPIs-") || header.startsWith("X-MSEdge-")) {
                results.relevantHeaders.put(header, headers.get(header).get(0));
            }
        }

        stream.close();

        JsonObject json = JsonParser.parseString(results.jsonResponse).getAsJsonObject();
        //get the first image result from the JSON object
        JsonArray jsonResults = json.getAsJsonArray("value");
        // Store the first (i.e. most relevant) result in searchResult.
        searchResult = (JsonObject)jsonResults.get(0);
    }
}
