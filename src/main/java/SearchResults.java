import java.util.HashMap;

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