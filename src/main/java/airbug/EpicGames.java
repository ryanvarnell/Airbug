package airbug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Using modified python script from https://github.com/d4rckh/epicgamesfree
 */
public class EpicGames {
    public static void main(String[] args) {
        try {
            getFreeGames();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void getFreeGames() throws IOException {
        String s = null;
        Process p = Runtime.getRuntime().exec("python3 libs/EpicGamesFreeQuery.py");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((s = bufferedReader.readLine()) != null) {
            System.out.println(s);
        }
    }
}
