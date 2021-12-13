package airbug;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Using modified python script from https://github.com/d4rckh/epicgamesfree
 */
public class EpicGames {
    public static ArrayList<String> getFreeGame() throws IOException {
        // Grab List of current free games from Epic Games store and store them in arrayList.
        ArrayList<String> freeGames = new ArrayList<>();
        Process p = Runtime.getRuntime().exec("python3 libs/EpicGamesFreeQuery.py");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String game;
        while ((game = bufferedReader.readLine()) != null) {
            freeGames.add(game);
        }

        // Check freeGames for any duplicates (games we already knew were free and have been posted) and return any new
        // ones
        ArrayList<String> newFreeGames = getNewGames(freeGames);
        if (newFreeGames.isEmpty())
            return null;
        return newFreeGames;
    }

    private static ArrayList<String> getNewGames(ArrayList<String> currentFreeGames) throws IOException {
        // Get all the currently known free games stored in freeGames.txt and store them in knownFreeGames
        String knownFreeGamesFile = "src/resources/freeGames.txt";
        Scanner scanner = new Scanner(new File(knownFreeGamesFile));
        ArrayList<String> knownFreeGames = new ArrayList<>();
        while (scanner.hasNextLine()) {
            knownFreeGames.add(scanner.nextLine());
        }
        scanner.close();

        // Iterate through the current free games, and if any of them are new, add them to the newFreeGames ArrayList
        // and store them in the known free games text file
        ArrayList<String> newFreeGames = new ArrayList<>();
        for (String freeGame : currentFreeGames) {
            if (!knownFreeGames.contains(freeGame)) {
                FileWriter fileWriter = new FileWriter(knownFreeGamesFile);
                fileWriter.append(freeGame);
                fileWriter.close();
                newFreeGames.add(freeGame);
            }
        }

        return newFreeGames;
    }
}
