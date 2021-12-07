package airbug;

import org.checkerframework.checker.units.qual.A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Using modified python script from https://github.com/d4rckh/epicgamesfree
 */
public class EpicGames extends TimerTask {
    public static ArrayList<String> getFreeGames() throws IOException {
        ArrayList<String> arrayList = new ArrayList<>();
        String s = null;
        Process p = Runtime.getRuntime().exec("python3 libs/EpicGamesFreeQuery.py");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((s = bufferedReader.readLine()) != null) {
            arrayList.add(s);
        }
        return arrayList;
    }

    @Override
    public void run() {
        System.out.println("Checking Epic for free games...");
    }
}
