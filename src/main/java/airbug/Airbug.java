package airbug;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Random;

/**
 * Simple, small-scale Discord bot using Discord4J
 * @author Ryan Varnell
 */
public class Airbug {
    // Instantiate a DiscordClient using bot token.
    private static final String discordToken = System.getenv("AIRBUG_TOKEN");
    private static final DiscordClient client = DiscordClient.create(discordToken);
    // Anything here will work as a prompt.
    public static final String commandPrompt = "-";

    /**
     * Main loop
     */
    public static void main(String[] args) {
        // Check messages for command prompt
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) ->
                gateway.on(MessageCreateEvent.class, event -> {
                    Message message = event.getMessage();
                    // If the user's message begins with the command prompt, open a new airbug.CommandHandler and send it the
                    // message to be processed.
                    String messageString = message.getContent().toLowerCase();
                    if (messageString.startsWith(commandPrompt)) {
                        CommandHandler commandHandler = new CommandHandler();
                        return commandHandler.process(message);
                    }
                    // Will respond cutely if you say airbug-chan with random emoticon
                    else if (messageString.contains("airbug-chan")) {
                        Random random = new Random();
                        int num = random.nextInt(10);
                        String s;
                        switch(num) {
                            case 0 -> s = "★~(◠‿◕✿)";
                            case 1 -> s = "★~(◡ω◕✿)";
                            case 2 -> s = "★~(◡‿◡✿)";
                            case 3 -> s = "(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧";
                            case 4 -> s = "(´｡• ω •｡`)";
                            case 5 -> s = "(✿◠‿◠)";
                            case 6 -> s = "ﾟ+.(*ﾉｪﾉ)ﾟ+";
                            case 7 -> s = "( ͡°⁄ ⁄ ͜⁄ ⁄ʖ⁄ ⁄ ͡°)";
                            case 8 -> s = "(´ε｀ )♡☆κｉss мё☆ﾟ";
                            case 9 -> s = "ayo look at this mf lmao *\"airbug-chan\"* ass";
                            default -> s = "huh";
                        }
                        return respondWith(message, s);
                    }
                    // If someone's message has a :) in it there's a 10% chance airbug will also respond with a :)
                    else if (messageString.contains(":)")) {
                        Random random = new Random();
                        int num = random.nextInt(10);
                        if (num == 4)
                            return respondWith(message, ":)");
                    }
                    return Mono.empty();
                }));
        login.block();
    }

    public static Mono<Message> respondWith(Message message, String s) {
        return message.getChannel().flatMap(channel -> channel.createMessage(s));
    }
}
