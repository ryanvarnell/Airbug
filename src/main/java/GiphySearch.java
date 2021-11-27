import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to search Giphy
 * @author Ryan Varnell
 */
public class GiphySearch {
    public static String getGif(String searchQuery) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://giphy.p.rapidapi.com/v1/gifs/search?q="
                        .concat(URLEncoder.encode(searchQuery, StandardCharsets.UTF_8))
                        .concat("&api_key=rcIYj6JOvJ5igGL2Gr0uCLtDOaHtBp9p")
                        .concat("&limit=1")))
                .header("x-rapidapi-host", "giphy.p.rapidapi.com")
                .header("x-rapidapi-key", "b639b892ebmshefa33b885f89941p1efb9fjsn848fbf270c1d")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Something went wrong";
        }
        ObjectMapper mapper = new ObjectMapper();
        MapType type = mapper
                .getTypeFactory()
                .constructMapType(Map.class, String.class, Object.class);
        try {
            HashMap<String, Object> map = mapper.readValue(response.body(), HashMap.class);
            return (String) map.get("embed_url");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
