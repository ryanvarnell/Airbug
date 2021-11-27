import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class Airbug {
    private static final String discordToken = System.getenv("AIRBUG_TOKEN");
    private static final DiscordClient client = DiscordClient.create(discordToken);

    public static void main(String[] args) {
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            // Confirm login
            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                            Mono.fromRunnable(() -> {
                                final User self = event.getSelf();
                                System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
                            }))
                    .then();

            // Check messages for command prompt
            Mono<Void> checkForPrompt = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().charAt(0) == '-') {
                    return handleCommand(message);
                }
                return Mono.empty();
            }).then();

            return printOnLogin.and(checkForPrompt);
        });
        login.block();
    }

    public static Mono<Message> handleCommand(Message message) {
        String[] commandTokens = message.getContent().split("\\s+");
        String rootCommand = commandTokens[0].substring(1);

        switch (rootCommand) {
            case "ping" -> {
                return ping(message);
            }
            case "img" -> {
                return getImage(message, commandTokens[1]);
            }
            case "bing" -> {
                if (commandTokens[1].equalsIgnoreCase("chilling"))
                    return bingChilling(message);
                else
                    return message.getChannel().flatMap(channel -> channel.createMessage("bong"));
            }
        }

        return Mono.empty();
    }

    public static Mono<Message> ping(Message message) {
        return message.getChannel().flatMap(channel -> channel.createMessage("pong!"));
    }

    public static Mono<Message> getImage(Message message, String searchTerm) {
        return null;
    }

    public static void bing(Message message) {
    }

    public static Mono<Message> bingChilling(Message message) {
        return message.getChannel().flatMap(channel -> channel.createMessage("<:cold_face:913836443695198231>"));
    }
}
