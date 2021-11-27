import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

/**
 * Simple, small-scale Discord bot using Discord4J
 * @author Ryan Varnell
 */
public class Airbug {
    // Instantiate a DiscordClient using bot token.
    private static final String discordToken = System.getenv("AIRBUG_TOKEN");
    private static final DiscordClient client = DiscordClient.create(discordToken);

    public static void main(String[] args) {
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            // Check messages for command prompt
            return gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();

                // If the user's message begins with the command prompt, open a new CommandHandler and send it the
                // message to be processed.
                if (message.getContent().charAt(0) == '-') {
                    new CommandHandler(message);
                    return CommandHandler.process();
                }
                return Mono.empty();
            });
        });
        login.block();
    }
}
