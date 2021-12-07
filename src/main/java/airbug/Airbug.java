package airbug;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.rest.entity.RestChannel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.util.Calendar.*;

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
        // Variable setup for timed events.
        Timer timer = new Timer();
        Calendar date = getInstance();
        date.set(HOUR, 0);
        date.set(MINUTE, 0);
        date.set(SECOND, 0);
        date.set(MILLISECOND, 0);
        timer.schedule(new EpicGames(), date.getTime(), 1000 * 60 * 60 * 24 * 7);

        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            // Confirm Airbug's login
            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                    Mono.fromRunnable(() -> {
                        final User self = event.getSelf();
                        System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
                    }))
                    .then();

            // Handle commands, parse messages, etc. Anything that requires reading a user's input.
            Mono<Void> parseMessage = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                String messageString = message.getContent().toLowerCase();

                // If the user's message begins with the command prompt, open a new airbug.CommandHandler and send it
                // the message to be processed.
                if (messageString.startsWith(commandPrompt)) {
                    CommandHandler commandHandler = new CommandHandler();
                    return commandHandler.process(message);
                }

                // Airbug will respond with a cute emoticon if you say airbug-chan
                else if (messageString.contains("airbug-chan")) {
                    return respondTo(message, airbugChan());
                }

                // If someone's message has a :) in it there's a 10% chance airbug will also respond with a :)
                else if (messageString.contains(":)")) {
                    Random random = new Random();
                    int num = random.nextInt(10);
                    if (num == 4)
                        return respondTo(message, ":)");
                }

                else if (messageString.contains("testepic")) {
                    return respondTo(message, checkFreeGameDuplicates());
                }
                return Mono.empty();
            }).then();

            // Checks for free games on the Epic store every 12 hours.
            Flux<Object> checkEpic = gateway.on(MessageCreateEvent.class, event -> {
                String gameUrl = checkFreeGameDuplicates();
                if (gameUrl != null) {
                    RestChannel deals = gateway.getRestClient().getChannelById(Snowflake.of("659258143108235275"));
                    deals.createMessage(gameUrl);
                }
                return Mono.empty();
            });

            return printOnLogin.and(parseMessage).and(checkEpic);
        });

        login.block();
    }

    /**
     * Uses a message to create an appropriate response.
     * @param message Message to determine correct server, channel, etc.
     * @param s String to respond with.
     * @return The Mono Message object.
     */
    public static Mono<Message> respondTo(Message message, String s) {
        return message.getChannel().flatMap(channel -> channel.createMessage(s));
    }

    /**
     * Chooses a random cute emoticon. to respond for airbug-chan.
     * @return Cute emoticon.
     */
    public static String airbugChan() {
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
        return s;
    }

    public static String checkFreeGameDuplicates() {
        try {
            ArrayList<String> currentFreeGames = EpicGames.getFreeGames();
            System.out.println(currentFreeGames.get(0));
            String filePath = "src/resources/freeGames.txt";
            Scanner scanner = new Scanner(new File("src/resources/freeGames.txt"));

            // Compile the last wave of free games into a string
            StringBuilder freeGamesString = new StringBuilder();
            while (scanner.hasNextLine()) {
                freeGamesString.append(scanner.nextLine()).append("\n");
            }
            scanner.close();

            // Test the current free games against the last wave of free games to know whether to post them.
            for (String game : currentFreeGames) {
                if (!freeGamesString.toString().contains(game)) {
                    FileWriter fileWriter = new FileWriter(filePath);
                    fileWriter.write(game);
                    fileWriter.close();
                    return EpicSearch.getStorePage(game);
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
