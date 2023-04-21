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
    private static final String discordToken = "token here";
    private static final DiscordClient client = DiscordClient.create(discordToken);
    // Anything here will work as a prompt.
    public static final String commandPrompt = "!";

    /**
     * Main loop
     */
    public static void main(String[] args) {
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            Mono<User> airbug = gateway.getUserById(Snowflake.of("placeholder"));

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

                // Ignore the message if it's from another bot.
                if (message.getAuthor().isPresent() && message.getAuthor().get().isBot()) {
                    return Mono.empty();
                }

                // If the message mentions Airbug respond with an Ai generated message.
                else if (message.getUserMentions().contains(airbug.block())) {
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

                return Mono.empty();
            }).then();

            // Checks for new free games on Epic, it occurs whenever someone starts typing which is busted and way too
            // frequent, but it works for now.
            Flux<Void> checkEpic = gateway.on(TypingStartEvent.class, event -> {
                // Checks if there's new free games.
                if (EpicGames.hasNewFreeGames()) {
                    // Loads up the channels to post in.
                    MessageChannel deals = gateway
                            .getChannelById(Snowflake.of("placeholder"))
                            .ofType(MessageChannel.class).block();
                    MessageChannel freeStuff = gateway
                            .getChannelById(Snowflake.of("placeholder"))
                            .ofType(MessageChannel.class).block();
                    MessageChannel wallaNetFreeStuff = gateway
                            .getChannelById(Snowflake.of("placeholder"))
                            .ofType(MessageChannel.class).block();
                    MessageChannel disfunktGaming = gateway.getChannelById(Snowflake.of("placeholder"))
                            .ofType(MessageChannel.class).block();

                    // Posts the new games in the channels
                    ArrayList<String> newGames = EpicGames.getNewGames();
                    for (String game : newGames) {
                        assert deals != null;
                        deals.createMessage(EpicGames.getStorePage(game)).block();
                        assert freeStuff != null;
                        freeStuff.createMessage(EpicGames.getStorePage(game)).block();
                        assert wallaNetFreeStuff != null;
                        wallaNetFreeStuff.createMessage(EpicGames.getStorePage(game)).block();
                        assert disfunktGaming != null;
                        disfunktGaming.createMessage(EpicGames.getStorePage(game)).block();
                    }
                }

                return Mono.empty();
            });

            return printOnLogin
                    .and(messageEvent)
                    .and(checkEpic)
                    .and(gateway.updatePresence(ClientPresence.online(
                            ClientActivity.playing("trying my best"))));
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

    /**
     * Method to call OpenAI's api and receive a randomly generated string of text in response to a user's message.
     * @param message The user's message to respond to.
     * @return An AI-generated string.
     */
    public static Mono<Message> getAIMessage(Message message) {
        String token = "placeholder";
        OpenAiService service = new OpenAiService(token);

        // Create a new StringBuilder with the message content, removing each mention to the bot and dropping the white
        // space from the beginning and end of the message.
        // We grab the first word for later processing.
        StringBuilder aiPrompt = new StringBuilder(message.getContent()
                .replaceAll("placeholder", "")
                .replaceAll("<", "")
                .replaceAll("@", "")
                .replaceAll(">", "")
                .trim());
        String firstWord;
        if (aiPrompt.toString().contains(" ")) {
            firstWord = aiPrompt.substring(0, aiPrompt.indexOf(" ")).toLowerCase();
        } else {
            firstWord = aiPrompt.toString();
        }

        // Grabs the AI response from the OpenAI API
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(aiPrompt.toString())
                .maxTokens(64)
                .temperature(0.7)
                .echo(true)
                .build();
        CompletionChoice completionChoice = service.createCompletion("ada", completionRequest)
                .getChoices().get(0);
        String response = completionChoice.getText();

        // If we think the message contains a question and not a direct prompt, we want to remove it from the response.
        if ((firstWord.equals("who")
                || firstWord.equals("what")
                || firstWord.equals("when")
                || firstWord.equals("where")
                || firstWord.equals("why")
                || firstWord.equals("how")
                || firstWord.equals("did")
                || firstWord.equals("does")
                || firstWord.equals("do")
                || firstWord.equals("is")
                || firstWord.equals("will")
                || firstWord.equals("can")
                || firstWord.equals("has")
                || firstWord.equals("have")
                || firstWord.equals("are")
                || firstWord.equals("was"))) {
            response = response.replace(aiPrompt, "");
        }
        if (aiPrompt.toString().contains("?"))
            response = response.substring(response.indexOf("?") + 1).trim();

        // Makes sure the response starts with a letter or quotation mark, for neat formatting.
        while (!(Character.isAlphabetic(response.charAt(0))
                || Character.isDigit(response.charAt(0))
                || (response.charAt(0) == '\"'))) {
            response = response.substring(1);
        }

        // Even out quotation marks if need be. This seems super inefficient but who cares.
        int quotationCount = 0;
        for (char c : response.toCharArray()) {
            if (c =='"')
                quotationCount++;
        }
        if (quotationCount % 2 != 0)
            response = "\"".concat(response);

        return respondTo(message, response);
    }
}
