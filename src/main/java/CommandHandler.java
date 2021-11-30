import com.github.lalyos.jfiglet.FigletFont;
import com.github.ricksbrown.cowsay.Cowsay;
import com.google.gson.JsonObject;
import com.kttdevelopment.mal4j.anime.Anime;
import com.kttdevelopment.mal4j.anime.AnimeRecommendation;
import com.kttdevelopment.mal4j.manga.Manga;
import com.kttdevelopment.mal4j.manga.MangaRecommendation;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class to handle user commands.
 * @author Ryan Varnell
 */
public class CommandHandler {
    private static final String prompt = Airbug.commandPrompt;
    Message message;
    StringBuilder query = new StringBuilder();
    ArrayList<String> commands = new ArrayList<>();

    /**
     * Chooses which command to run based on the root command.
     * @return The result of the command ran.
     */
    public Mono<Message> process(Message message) {
        this.message = message;
        String[] split = message.getContent().split("\\s+");
        String rootCommand = split[0].replace(prompt, "");
        switch (rootCommand.toLowerCase()) {
            case "ping", "p" -> {return ping();}
            case "help", "h" -> {return help();}
            case "bing", "b", "google", "g", "duckduckgo", "ddg", "askjeeves", "aj", "search" -> {
                if (commands.get(0).equalsIgnoreCase("bing")
                        && query.toString().equalsIgnoreCase("chilling"))
                    return bingChilling();
                else {return bing();}
            }
            case "image", "img" -> {return img();}
            case "giphy", "gif" -> {return gif();}
            case "wiki", "w" -> {return wiki();}
            case "anime", "a" -> {return anime();}
            case "manga", "m" -> {return manga();}
            case "cowsay", "cs" -> {return cowsay();}
            case "cowthink", "ct" -> {return cowthink();}
            case "figlet" -> {return figlet();}
            default -> { return respondWith("unrecognized command"); }
        }
    }

    /**
     * Parses the command into more easily controllable formats.
     */
    private void parse() {
        String[] tokenizedMessage = this.message.getContent().toLowerCase().split("\\s+");
        for (String s : tokenizedMessage) {
            if (s.startsWith(prompt)) {
                if (commands.isEmpty() || s.equals(prompt + "rec")) {
                    s = s.replace(prompt, "");
                    commands.add(s);
                } else
                    query.append(s).append(" ");
            } else {
                query.append(s).append(" ");
            }
        }
        query = new StringBuilder(query.toString().trim());
    }

    /**
     * This line of code was getting repeated a lot, so I threw it in its own method.
     * @param response The content to be included in the method
     * @return Message to be posted in Discord.
     */
    private Mono<Message> respondWith(String response) {
        return message.getChannel().flatMap(channel -> channel.createMessage(response));
    }
    private Mono<Message> respondWith(EmbedCreateSpec response) {
        return message.getChannel().flatMap(channel -> channel.createMessage(response));
    }

    /**
     * Simple ping command.
     * @return Pong!
     */
    private Mono<Message> ping() {
        return respondWith("pong!");
    }

    /**
     * Sends a list of commands stored in help.txt
     * @return A message containing a list of commands.
     */
    private Mono<Message> help() {
        Scanner scanner;
        try {
            scanner = new Scanner(new File("help.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return respondWith("no help file means no help");
        }
        StringBuilder help = new StringBuilder("```\n");
        while (scanner.hasNextLine()) {
            help.append(scanner.nextLine()).append("\n");
        }
        help.append("\n```");
        return respondWith(help.toString());
    }

    /**
     * Bing Chilling
     * @return cold_face emoji
     */
    private Mono<Message> bingChilling() {
        return respondWith(":cold_face:");
    }

    /**
     * Image search powered by Bing
     * @return Image related to user's query
     */
    private Mono<Message> img() {
        parse();
        return respondWith(BingSearch.getImage(query.toString()));
    }

    /**
     * Gif search powered by Giphy
     * @return Gif related to user's query
     */
    private Mono<Message> gif() {
        parse();
        return respondWith(GiphySearch.getGif(query.toString()));
    }

    /**
     * Web search command, uses Bing API.
     * @return A message containing an embedded search result.
     */
    private Mono<Message> bing() {
        parse();
        JsonObject webpage = BingSearch.getWebPage(query.toString());
        // Builds an embed with properties of the webpage.
        EmbedCreateSpec embed;
        if (webpage != null) {
            embed = EmbedCreateSpec.builder()
                    .color(Color.ENDEAVOUR).author("Bing",
                            "https://www.bing.com/",
                            "https://vignette2.wikia.nocookie.net/logopedia/images/0/09/Bing-2.png/revision/latest/scale-to-width-down/220?cb=20160504230420")
                    .thumbnail(BingSearch.getImage(webpage.get("name").getAsString()))
                    .description(webpage.get("snippet").getAsString())
                    .title(webpage.get("name").getAsString())
                    .url(webpage.get("url").getAsString())
                    .build();
        } else {
            return respondWith("Something went wrong");
        }
        return respondWith(embed);
    }

    /**
     * This a JANK wiki command, but it'll work for what I'm using it for.
     * @return Embedded wiki result.
     */
    private Mono<Message> wiki() {
        parse();
        JsonObject webpage = BingSearch.getWebPage(query + " wikipedia");
        // Builds an embed with properties of the webpage.
        EmbedCreateSpec embed;
        System.out.println(webpage);
        if (webpage != null && webpage.get("url").getAsString().contains("wikipedia")) {
            embed = EmbedCreateSpec.builder()
                    .color(Color.WHITE).author("Wikipedia",
                            "https://en.wikipedia.org/wiki/Main_Page",
                            "https://cdn.freebiesupply.com/images/large/2x/wikipedia-logo-transparent.png")
                    .thumbnail(BingSearch.getImage(query + " wikipedia"))
                    .description(webpage.get("snippet").getAsString())
                    .title(webpage.get("name").getAsString())
                    .url(webpage.get("url").getAsString())
                    .build();
        } else {
            return respondWith("No luck");
        }
        return respondWith(embed);
    }

    /**
     * MyAnimeList anime search
     * @return Embedded anime related to query.
     */
    private Mono<Message> anime() {
        parse();
        Anime anime = MalSearch.searchAnime(query.toString());
        EmbedCreateSpec embed;
        if (commands.size() > 1 && commands.get(1).equalsIgnoreCase("rec")) {
            embed = MalSearch.getAnimeRecEmbed(query.toString());
        } else {
            embed = MalSearch.getAnimeEmbed(query.toString());
        }
        return respondWith(embed);
    }

    /**
     * MyAnimeList manga search
     * @return Embedded manga related to query.
     */
    private Mono<Message> manga() {
        parse();
        Manga manga = MalSearch.searchManga(query.toString());
        EmbedCreateSpec embed;
        if (commands.size() > 1 && commands.get(1).equalsIgnoreCase("rec")) {
            embed = MalSearch.getMangaRecEmbed(query.toString());
        } else {
            embed = MalSearch.getMangaEmbed(query.toString());
        }
        return respondWith(embed);
    }

    /**
     * Cowsay
     * @return Cowsay
     */
    private Mono<Message> cowsay() {
        parse();
        return respondWith("```\n" + Cowsay.say(new String[]{query.toString()}) + "\n```");
    }

    /**
     * Cowthink
     * @return Cowthink
     */
    private Mono<Message> cowthink() {
        parse();
        return respondWith("```\n" + Cowsay.think(new String[]{query.toString()}) + "\n```");
    }

    /**
     * Figlet
     * @return Figlet
     */
    private Mono<Message> figlet() {
        parse();
        try {
            return respondWith("```\n" + FigletFont.convertOneLine(query.toString()) + "\n```");
        } catch (IOException e) {
            e.printStackTrace();
            return respondWith("something went wrong");
        }
    }

}
