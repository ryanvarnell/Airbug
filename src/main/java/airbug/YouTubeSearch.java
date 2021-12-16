package airbug;

import YouTubeSearchApi.YouTubeSearchClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Searches YouTube. Uses https://github.com/Shiroechi/YouTubeSearchApi-Java
 */
public class YouTubeSearch {
    private static final String key = System.getenv("YOUTUBE_API_KEY");
    public static String getVideo(String query) {
        YouTubeSearchClient client = new YouTubeSearchClient(key);
        JsonObject video = JsonParser.parseString(client.Search(query, "snippet", "video", 1)).getAsJsonObject();
        System.out.println("jsonvideo" + video);
        JsonArray items = video.getAsJsonArray("items");
        JsonObject item = items.get(0).getAsJsonObject();
        JsonObject id = item.get("id").getAsJsonObject();
        String videoId = id.get("videoId").getAsString();
        return "https://www.youtube.com/watch?v=" + videoId;
    }
}
