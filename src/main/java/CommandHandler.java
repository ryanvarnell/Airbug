import com.google.gson.JsonObject;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Class to handle user commands.
 * @author Ryan Varnell
 */
public class CommandHandler {
    private static final String prompt = Airbug.commandPrompt;
    private static Message message;
    private static String rootCommand;
    private static String query;
    private static String[] modifiers;

    /**
     * Chooses which command to run based on the root command.
     * @return The result of the command ran.
     */
    public static Mono<Message> process(Message message) {
        parseCommand(message);

        switch (rootCommand) {
            case "ping", "p" -> { return ping(); }
            case "help", "h" -> { return help(); }
            case "bing", "b",
                    "google", "g",
                    "duckduckgo", "ddg",
                    "askjeeves", "aj",
                    "search" -> {
                if (rootCommand.equals("bing") && modifiers[0].equalsIgnoreCase("chilling"))
                    return bingChilling();
                else
                    return bing();
            }
            case "image", "img" -> { return img(); }
            case "giphy", "gif" -> { return gif(); }
            case "wiki", "w" -> { return wiki(); }
            default -> { return null; }
        }
    }

    /**
     * Parses the command into more easily controllable formats.
     * @param message The message to be parsed.
     */
    private static void parseCommand(Message message) {
        CommandHandler.message = message;
        String messageString = message.getContent().replaceFirst(prompt, "").toLowerCase();
        String[] tokenizedMessageString = messageString.split("\\s+");
        rootCommand = tokenizedMessageString[0];
        query = messageString.replaceFirst(rootCommand + " ", "");
        modifiers = Arrays.copyOfRange(tokenizedMessageString, 1, tokenizedMessageString.length);
    }

    /**
     * This line of code was getting repeated a lot, so I threw it in its own method.
     * @param response The content to be included in the method
     * @return Message to be posted in Discord.
     */
    public static Mono<Message> respondWith(String response) {
        return message.getChannel().flatMap(channel -> channel.createMessage(response));
    }
    public static Mono<Message> respondWith(EmbedCreateSpec response) {
        return message.getChannel().flatMap(channel -> channel.createMessage(response));
    }

    /**
     * Simple ping command.
     * @return Pong!
     */
    private static Mono<Message> ping() {
        return respondWith("pong!");
    }

    private static Mono<Message> help() {
        Scanner scanner;
        try {
            scanner = new Scanner(new File("help.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return respondWith("no help file means no help");
        }
        StringBuilder help = new StringBuilder("```\n");
        while (scanner.hasNextLine()) {
            help.append(scanner.nextLine()).append("\n");
        }
        help.append("\n```");
        return respondWith(help.toString());
    }

    /**
     * Bing Chilling
     * @return cold_face emoji
     */
    private static Mono<Message> bingChilling() {
        return respondWith(":cold_face:");
    }

    /**
     * Image search powered by Bing
     * @return Image related to user's query
     */
    private static Mono<Message> img() {
        return respondWith(BingSearch.getImage(query));
    }

    /**
     * Gif search powered by Giphy
     * @return Gif related to user's query
     */
    private static Mono<Message> gif() {
        return respondWith(GiphySearch.getGif(query));
    }

    /**
     * Web search command, uses Bing API.
     * @return A message containing an embedded search result.
     */
    private static Mono<Message> bing() {
        JsonObject webpage = BingSearch.getWebPage(query);
        // Builds an embed with properties of the webpage.
        EmbedCreateSpec embed = null;
        if (webpage != null) {
            embed = EmbedCreateSpec.builder()
                    .color(Color.HOKI)
                    .thumbnail(BingSearch.getImage(query))
                    .description(webpage.get("snippet").getAsString())
                    .title(webpage.get("name").getAsString())
                    .url(webpage.get("url").getAsString())
                    .build();
        } else {
            respondWith("Something went wrong");
        }
        return respondWith(embed);
    }

    /**
     * This a JANK wiki command, but it'll work for what I'm using it for.
     * @return Embedded wiki result.
     */
    private static Mono<Message> wiki() {
        JsonObject webpage = BingSearch.getWebPage(query + " wiki");
        // Builds an embed with properties of the webpage.
        EmbedCreateSpec embed = null;
        System.out.println(webpage);
        if (webpage != null) {
            embed = EmbedCreateSpec.builder()
                    .color(Color.DEEP_LILAC).author("Wikipedia, the Free Encyclopedia",
                            "https://en.wikipedia.org/wiki/Main_Page",
                            "https://www.famouslogos.org/wp-content/uploads/2009/04/wikipediafav.png")
                    .thumbnail(BingSearch.getImage(webpage.get("name").getAsString() + " logo"))
                    .description(webpage.get("snippet").getAsString())
                    .title(webpage.get("name").getAsString())
                    .url(webpage.get("url").getAsString())
                    .build();
        } else {
            respondWith("Something went wrong");
        }
        return respondWith(embed);
    }
}
