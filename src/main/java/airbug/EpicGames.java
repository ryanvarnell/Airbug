package airbug;

import com.google.gson.JsonObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Using modified python script from https://github.com/d4rckh/epicgamesfree
 */
public class EpicGames {
    private static final String knownFreeGamesFile = "src/resources/freeGames.txt";

    /**
     * Checks to see if Epic has any new free games listed.
     * @return True if there's new free games, false if not.
     */
    public static boolean hasNewFreeGames() {
        try {
            ArrayList<String> freeGames = getFreeGames();
            ArrayList<String> alreadyKnownGames = loadKnownGames();

            // Check the current free games at Epic for the games we already know are free, if there are any new free
            // games, return true.
            for (String game : freeGames) {
                assert alreadyKnownGames != null;
                if (!alreadyKnownGames.contains(game)) {
                    return true;
                }
            }
        } catch (IOException ignored) {}

        return false;
    }

    /**
     * Gets all the 100% discounted games from Epic Games store.
     * @return ArrayList containing all 100% discounted games.
     * @throws IOException If there's an error or something.
     */
    private static ArrayList<String> getFreeGames() throws IOException {
        // Runs a Python script that does the heavy lifting and stores the discounted games in freeGames.
        ArrayList<String> freeGames = new ArrayList<>();
        Process p = Runtime.getRuntime().exec("python3 libs/EpicGamesFreeQuery.py");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String game;
        while ((game = bufferedReader.readLine()) != null) {
            freeGames.add(game);
        }

        return freeGames;
    }

    /**
     * Get all the new free games from Epic.
     * @return An ArrayList containing the titles of all the new 100% discounted games.
     */
    public static ArrayList<String> getNewGames() {
        ArrayList<String> currentFreeGames = null;
        try {
            currentFreeGames = getFreeGames();
        } catch (IOException ignored) {}
        ArrayList<String> knownFreeGames = loadKnownGames();
        ArrayList<String> newFreeGames = new ArrayList<>();
        assert currentFreeGames != null;
        for (String game: currentFreeGames) {
            assert knownFreeGames != null;
            if (!knownFreeGames.contains(game)) {
                newFreeGames.add(game);
            }
        }
        updateKnownFreeGames();
        return newFreeGames;
    }

    /**
     * Updates the known free games list.
     */
    private static void updateKnownFreeGames() {
        ArrayList<String> knownFreeGames;
        try {
            knownFreeGames = getFreeGames();
            FileWriter fileWriter;
            fileWriter = new FileWriter(knownFreeGamesFile);
            for (String game : knownFreeGames) {
                fileWriter.write(game + "\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception ignored) {}
    }

    /**
     * Loads the games from the text file that we know to have been free recently.
     * @return ArrayList containing names of free games.
     */
    private static ArrayList<String> loadKnownGames() {
        ArrayList<String> knownGames = new ArrayList<>();
        Scanner scanner;
        try {
            scanner = new Scanner(new File(knownFreeGamesFile));
        } catch (FileNotFoundException e) {
            return null;
        }
        while (scanner.hasNextLine()) {
            knownGames.add(scanner.nextLine());
        }
        return knownGames;
    }

    /**
     * Gets a store page for a game on Epic store
     * @param query Name of the game to search.
     * @return URL of store page.
     */
    public static String getStorePage(String query) {
        JsonObject webpage = BingSearch.getWebPage(query + " epic games store");
        if (webpage != null && webpage.get("url").getAsString().contains("epicgames")) {
            return webpage.get("url").getAsString();
        } else {
            return null;
        }
    }
}
