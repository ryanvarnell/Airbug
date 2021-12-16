package airbug;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.channel.TypingStartEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            Mono<User> airbug = gateway.getUserById(Snowflake.of("499795214387380237"));

            // Confirm Airbug's login
            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                    Mono.fromRunnable(() -> {
                        final User self = event.getSelf();
                        System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
                    }))
                    .then();

            // Handle commands, parse messages, etc. Anything that requires reading a user's input.
            Mono<Void> messageEvent = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                String messageString = message.getContent().toLowerCase();

                if (message.getAuthor().isPresent() && message.getAuthor().get().isBot()) {
                    return Mono.empty();
                }

                else if (message.getUserMentions().contains(airbug.block())) {
                    message.;
                    return getAIMessage(message);
                }

                // If the user's message begins with the command prompt, open a new airbug.CommandHandler and send it
                // the message to be processed.
                else if (messageString.startsWith(commandPrompt)) {
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

                else {
                    Random random = new Random();
                    int randomNum = random.nextInt(50);
                    if (randomNum == 24) {
                        return getAIMessage(message);
                    }
                }

                return Mono.empty();
            }).then();

            // Checks for new free games on Epic, it occurs whenever someone starts typing which is busted and way too
            // frequent, but it works for now.
            Flux<Void> checkEpic = gateway.on(TypingStartEvent.class, event -> {
                if (EpicGames.hasNewFreeGames()) {
                    MessageChannel deals = gateway
                            .getChannelById(Snowflake.of("659258143108235275"))
                            .ofType(MessageChannel.class).block();
                    MessageChannel freeStuff = gateway
                            .getChannelById(Snowflake.of("694979462592331907"))
                            .ofType(MessageChannel.class).block();
                    MessageChannel wallaNetFreeStuff = gateway
                            .getChannelById(Snowflake.of("791754448741072906"))
                            .ofType(MessageChannel.class).block();

                    ArrayList<String> newGames = EpicGames.getNewGames();
                    for (String game : newGames) {
                        assert deals != null;
                        deals.createMessage(EpicGames.getStorePage(game)).block();
                        assert freeStuff != null;
                        freeStuff.createMessage(EpicGames.getStorePage(game)).block();
                        assert wallaNetFreeStuff != null;
                        wallaNetFreeStuff.createMessage(EpicGames.getStorePage(game)).block();
                    }
                }
                return Mono.empty();
            });

            return printOnLogin
                    .and(messageEvent)
                    .and(checkEpic)
                    .and(gateway.updatePresence(ClientPresence.online(
                            ClientActivity.playing("hey"))));
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

    public static Mono<Message> getAIMessage(Message message) {
        String token = "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";
        OpenAiService service = new OpenAiService(token);
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(message.getContent())
                .echo(true)
                .build();
        CompletionChoice completionChoice = service.createCompletion("davinci", completionRequest)
                .getChoices().get(0);
        String response = completionChoice.getText();
        return respondTo(message, response);
    }
}
