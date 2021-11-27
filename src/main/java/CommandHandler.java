import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Arrays;

class CommandHandler {
    static String rootCommand;
    static String[] modifiers;
    static Message message;

    CommandHandler(Message message) {
        CommandHandler.message = message;
        parseCommand(message);
    }

    private void parseCommand(Message message) {
        String[] tokenizedString = message.getContent().split("\\s+");
        rootCommand = tokenizedString[0].substring(1);
        modifiers = Arrays.copyOfRange(tokenizedString, 1, tokenizedString.length);
    }

    public static Mono<Message> process() {
        switch (rootCommand) {
            case "ping" -> { return ping(); }
            case "bing" -> {
                if (modifiers[0].equalsIgnoreCase("chilling"))
                    return bingChilling();
            }
            case "img" -> { return img(); }
        }
        return null;
    }

    private static Mono<Message> ping() {
        return message.getChannel().flatMap(channel -> channel.createMessage("pong!"));
    }

    private static Mono<Message> bingChilling() {
        return message.getChannel().flatMap(channel -> channel.createMessage(":cold_face:"));
    }

    private static Mono<Message> img() {
        String searchQuery = message.getContent().replaceFirst(rootCommand, "");
        return message.getChannel().flatMap(channel -> channel.createMessage(ImageSearch.getImage(searchQuery)));
    }
}
