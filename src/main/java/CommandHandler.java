import com.google.gson.JsonObject;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;
import java.util.Arrays;

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
            case "ping" -> { return ping(); }
            case "b", "bing", "g", "google", "ddg", "duckduckgo", "aj", "askjeeves" -> {
                if (rootCommand.equals("bing") && modifiers[0].equalsIgnoreCase("chilling"))
                    return bingChilling();
                else
                    return bing();
            }
            case "img", "image" -> { return img(); }
            case "gif", "giphy" -> { return gif(); }
        }
        return null;
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
}
