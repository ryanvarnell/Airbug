import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

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
    private final static String imagePath = "/v7.0/images/search";
    private final static String webPath = "/v7.0/search";
    private static SearchResults searchResult;
    private static char searchType;

    /**
     * 2-Parameter constructor.
     * @param type Search type. "I" for image, "W" for web.
     * @param searchQuery The search query.
     */
    WebSearch(char type, String searchQuery) {
        searchType = type;
        try {
            searchResult = search(searchQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets an image based on the user's query.
     * @param searchQuery The user's query.
     */
    public SearchResults search(String searchQuery) throws IOException {
        // Construct the search request URL (in the form of endpoint + query string)
        String path;
        if (searchType == 'I')
            path = imagePath;
        else
            path = webPath;
        URL url = new URL(host + path + "?q="
                + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8)
                + "&"
                + URLEncoder.encode("count=1-", StandardCharsets.UTF_8));

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

    /**
     * Gets the image from the first relevant search result relevant to the search query.
     * @return Image url.
     */
    public String getImageUrl() {
        JsonObject json = JsonParser.parseString(searchResult.jsonResponse).getAsJsonObject();

        // Get the first image result from the JSON object
        JsonArray jsonResults = json.getAsJsonArray("value");
        // Store the first (i.e. most relevant) result in searchResult.
        JsonObject searchResult = (JsonObject) jsonResults.get(0);
        return searchResult.get("thumbnailUrl").getAsString();
    }

    public EmbedCreateSpec getResultsAsEmbedded() {
        JsonObject jsonString = (JsonObject) JsonParser.parseString(searchResult.jsonResponse);
        JsonObject jsonWebPageString = jsonString.get("webPages").getAsJsonObject();
        JsonArray webPages = jsonWebPageString.getAsJsonArray("value");
        JsonObject result = (JsonObject) webPages.get(0);
        System.out.println(result);
        return EmbedCreateSpec.builder()
                .color(Color.HOKI)
                .description(result.get("snippet").getAsString())
                .title(result.get("name").getAsString())
                .url(result.get("url").getAsString())
                .build();
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
