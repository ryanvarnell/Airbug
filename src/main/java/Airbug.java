import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class Airbug {
    private static final DiscordClient client = DiscordClient.create(System.getenv("AIRBUG_TOKEN"));

    public static void main(String[] args) {
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            // Check messages for command prompt
            return gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().charAt(0) == '-') {
                    new CommandHandler(message);
                    message.delete();
                    return CommandHandler.process();
                }
                return Mono.empty();
            });
        });
        login.block();
    }
}
