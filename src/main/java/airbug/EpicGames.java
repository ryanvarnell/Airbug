package airbug;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class EpicGames {
    private static final String url = "https://www.epicgames.com/store/en-US/free-games";

    public static void getFreeGames() throws IOException {
        Document document = Jsoup.connect(url).get();
    }
}
