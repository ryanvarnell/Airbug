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
public class BingSearch {
    private final static String subscriptionKey = System.getenv("BING_SEARCH_KEY");
    private final static String host = "https://api.bing.microsoft.com";

    /**
     * Gets a webpage relevant to the user's search query.
     * @param searchQuery The user's search query.
     * @return Top-most relevant webpage.
     */
    public static JsonObject getWebPage(String searchQuery) {
        String bingSearchPath = "/v7.0/search";
        SearchResults results;
        try {
            results = search(searchQuery, bingSearchPath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // Parses the results into a JsonObject, parses the "webPages" variable in that object into its own child
        // object, stores the members of the "webPages" object into an array, and returns the first member of the array.
        JsonObject resultsJson = (JsonObject) JsonParser.parseString(results.jsonResponse);
        JsonObject webPagesJson = resultsJson.get("webPages").getAsJsonObject();
        JsonArray webPagesArray = webPagesJson.getAsJsonArray("value");
        return (JsonObject) webPagesArray.get(0);
    }

    /**
     * Gets an image relevant to the user's search query.
     * @param searchQuery The user's search query.
     * @return Top-most relevant image.
     */
    public static String getImage(String searchQuery) {
        String bingImagePath = "/v7.0/images/search";
        SearchResults results;
        try {
            results = search(searchQuery, bingImagePath);
        } catch (IOException e) {
            e.printStackTrace();
            return "Something went wrong";
        }
        // Parses the results into a JsonObject, stores the "value" of that object in an array (in this case the
        // "value" is the resulting image data, and return the first image's thumbnailUrl.
        JsonObject resultsJson = JsonParser.parseString(results.jsonResponse).getAsJsonObject();
        JsonArray imagesJson = resultsJson.getAsJsonArray("value");
        JsonObject searchResult = (JsonObject) imagesJson.get(0);
        return searchResult.get("thumbnailUrl").getAsString();
    }

    /**
     * Searches the web based on a query.
     * @param searchQuery The query used for searching.
     * @param path the path used to specify what type of result we want from Bing's API.
     * @return Search Results.
     * @throws IOException If something goes wrong I guess, I don't know.
     */
    public static SearchResults search(String searchQuery, String path) throws IOException {
        // Construct the search request URL (in the form of endpoint + query string)
        URL url = new URL(host + path + "?q="
                + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8));
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
        // Receive JSON body
        InputStream stream = connection.getInputStream();
        String response = new Scanner(stream).useDelimiter("\\A").next();
        // Construct result object for return
        SearchResults results = new SearchResults(new HashMap<>(), response);
        // Extract Bing-related HTTP headers
        Map<String, List<String>> headers = connection.getHeaderFields();
        for (String header : headers.keySet()) {
            if (header == null) continue;      // may have null key
            if (header.startsWith("BingAPIs-") || header.startsWith("X-MSEdge-")) {
                results.relevantHeaders.put(header, headers.get(header).get(0));
            }
        }
        stream.close();
        return results;
    }
}

/**
 * Class to contain the search results from using Bing's API.
 */
class SearchResults {
    HashMap<String, String> relevantHeaders;
    String jsonResponse;
    SearchResults(HashMap<String, String> headers, String json) {
        relevantHeaders = headers;
        jsonResponse = json;
    }
}
