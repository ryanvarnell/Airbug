import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * Class to handle user commands.
 * @author Ryan Varnell
 */
class CommandHandler {
    static String rootCommand;
    static String[] modifiers;
    static Message message;

    /**
     * Single-parameter constructor.
     * @param message Message with command to be processed.
     */
    CommandHandler(Message message) {
        CommandHandler.message = message;
        parseCommand(message);
    }

    /**
     * Parses the command into more easily controllable formats.
     * @param message The message to be parsed.
     */
    private void parseCommand(Message message) {
        String[] tokenizedString = message.getContent().split("\\s+");
        rootCommand = tokenizedString[0].substring(1);
        modifiers = Arrays.copyOfRange(tokenizedString, 1, tokenizedString.length);
    }

    /**
     * Chooses which command to run based on the root command.
     * @return The result of the command ran.
     */
    public static Mono<Message> process() {
        switch (rootCommand) {
            case "ping" -> { return ping(); }
            case "bing" -> {
                if (modifiers[0].equalsIgnoreCase("chilling"))
                    return bingChilling();
            }
            case "img" -> { return img(); }
            case "gif" -> { return gif(); }
        }
        return null;
    }

    /**
     * Simple ping command.
     * @return Pong!
     */
    private static Mono<Message> ping() {
        return message.getChannel().flatMap(channel -> channel.createMessage("pong!"));
    }

    /**
     * Bing Chilling
     * @return cold_face emoji
     */
    private static Mono<Message> bingChilling() {
        return message.getChannel().flatMap(channel -> channel.createMessage(":cold_face:"));
    }

    /**
     * Image search powered by Bing
     * @return Image related to user's query
     */
    private static Mono<Message> img() {
        String searchQuery = message.getContent().replaceFirst(rootCommand, "");
        WebSearch webSearch = new WebSearch(searchQuery);
        String result = webSearch.getImageUrl();
        return message.getChannel().flatMap(channel -> channel.createMessage(result));
    }

    /**
     * Gif search powered by Giphy
     * @return Gif related to user's query
     */
    private static Mono<Message> gif() {
        String searchQuery = message.getContent().substring(rootCommand.length() + 2);
        return message.getChannel().flatMap(channel -> channel.createMessage(GiphySearch.getGif(searchQuery)));
    }
}
