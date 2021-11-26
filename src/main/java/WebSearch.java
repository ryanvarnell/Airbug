import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import discord4j.core.object.entity.Message;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

public class WebSearch {
    private static final String bingSubscriptionKey = System.getenv("BING_SEARCH_KEY");
    private static final String bingEndpoint = System.getenv("BING_SEARCH_ENDPOINT" + "/v7.0/search");
    static SearchResults results;

    WebSearch(Message search) throws IOException {
        String[] splitString = search.getContent().split("\\s+");
        String searchTerm = splitString[1];
        test(searchTerm);
    }

    public static void test(String searchTerm) {
        try {
            System.out.println("Searching the Web for: " + searchTerm);

            SearchResults result = searchWeb(searchTerm);

            System.out.println(" Relevant HTTP Headers: ");
            for (String header : result.relevantHeaders.keySet())
                System.out.println(header + ": " + result.relevantHeaders.get(header));

            System.out.println(" JSON Response: ");
            System.out.println(prettify(result.jsonResponse));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }

    public String getImage() {
        System.out.println(results.relevantHeaders.get("contentUrl"));
        return results.relevantHeaders.get("contentUrl");
    }

    public static SearchResults searchWeb(String searchTerm) throws IOException {
        URL url = new URL(bingEndpoint + "?q=" + URLEncoder.encode(searchTerm, StandardCharsets.UTF_8));
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", bingSubscriptionKey);

        InputStream inputStream = connection.getInputStream();
        Scanner scanner = new Scanner (inputStream);
        String response = scanner.useDelimiter("\\A").next();

        results = new SearchResults(new HashMap<>(), response);

        Map<String, List<String>> headers = connection.getHeaderFields();
        for (String header : headers.keySet()) {
            if (header == null) continue;
            if (header.startsWith("BingAPIs-") || header.startsWith("X-MSEdge-")) {
                results.relevantHeaders.put(header, headers.get(header).get(0));
            }
        }
        inputStream.close();
        scanner.close();

        return results;
    }

    public static String prettify (String json_text) {
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }
}
