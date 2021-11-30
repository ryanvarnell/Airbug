import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.checkerframework.checker.units.qual.C;
import reactor.core.publisher.Mono;

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
                    // If the user's message begins with the command prompt, open a new CommandHandler and send it the
                    // message to be processed.
                    if (message.getContent().startsWith(commandPrompt)) {
                        CommandHandler commandHandler = new CommandHandler();
                        return commandHandler.process(message);
                    }
                    return Mono.empty();
                }));
        login.block();
    }
}
