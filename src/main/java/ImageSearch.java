import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ImageSearch {
    static String subscriptionKey = System.getenv("BING_SEARCH_KEY");
    static String host = "https://api.bing.microsoft.com";
    static String path = "/v7.0/images/search";

    public static String getImage(String searchQuery) {
        // construct the search request URL (in the form of endpoint + query string)
        URL url;
        try {
            url = new URL(host + path + "?q=" +  URLEncoder.encode(searchQuery, StandardCharsets.UTF_8));
        } catch (MalformedURLException e) {
            return "URL was borked";
        }
        HttpsURLConnection connection;
        try {
            connection = (HttpsURLConnection)url.openConnection();
        } catch (IOException e) {
            return "Couldn't open a connection to bing servers.";
        }
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

        // receive JSON body
        InputStream stream;
        try {
            stream = connection.getInputStream();
        } catch (IOException e) {
            return "Failure to open input stream";
        }
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

        try {
            stream.close();
        } catch (IOException e) {
            return "Couldn't close the stream???? what";
        }
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(results.jsonResponse).getAsJsonObject();
        //get the first image result from the JSON object
        JsonArray jsonResults = json.getAsJsonArray("value");
        JsonObject first_result = (JsonObject)jsonResults.get(0);
        return first_result.get("thumbnailUrl").getAsString();
    }
}
