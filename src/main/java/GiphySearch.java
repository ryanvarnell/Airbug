import com.trievosoftware.giphy4j.Giphy;
import com.trievosoftware.giphy4j.entity.search.SearchFeed;
import com.trievosoftware.giphy4j.exception.GiphyException;

/**
 * Class to search Giphy and grab the url to a relevant gif.
 * @author Ryan Varnell
 */
public class GiphySearch {
    private static final String API_KEY = "rcIYj6JOvJ5igGL2Gr0uCLtDOaHtBp9p";
    public static String getGif(String searchQuery) {
        Giphy giphy = new Giphy(API_KEY);

        SearchFeed feed = null;
        try {
            feed = giphy.search(searchQuery, 1, 0);
        } catch (GiphyException e) {
            e.printStackTrace();
        }

        assert feed != null;
        return feed.getDataList().get(0).getImages().getOriginal().getUrl();
    }
}
