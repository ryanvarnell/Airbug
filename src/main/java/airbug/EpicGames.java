package airbug;

import java.io.IOException;

public class EpicGames {
    private static final String url = "https://www.epicgames.com/store/en-US/";

    public static void main(String[] args) {
        try {
            getFreeGames();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void getFreeGames() throws IOException {
        Process p = Runtime.getRuntime().exec("python /Users/ryan/IdeaProjects/airbug/libs/epicgamesfree/main.py");
        System.out.println(p);
    }
}
