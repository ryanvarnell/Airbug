import com.github.lalyos.jfiglet.FigletFont;
import com.github.ricksbrown.cowsay.Cowsay;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
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
            case "bing", "b", "google", "g", "duckduckgo", "ddg", "askjeeves", "aj", "search" -> {return bing();}
            case "image", "img" -> {return img();}
            case "giphy", "gif" -> {return gif();}
            case "wiki", "w" -> {return wiki();}
            case "anime", "a" -> {return anime();}
            case "manga", "m" -> {return manga();}
            case "cowsay", "cs" -> {return cowsay();}
            case "cowthink", "ct" -> {return cowthink();}
            case "figlet" -> {return figlet();}
            case "choose" -> {return choose();}
            case "8ball" -> {return eightBall();}
            case "youtube", "yt" -> {return youtube();}
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
        if (response == null || response.isEmpty())
            return message.getChannel().flatMap(channel -> channel.createMessage("something went wrong."));
        else
            return message.getChannel().flatMap(channel -> channel.createMessage(response));
    }
    private Mono<Message> respondWith(EmbedCreateSpec response) {
        if (response == null)
            return message.getChannel().flatMap(channel -> channel.createMessage("something went wrong."));
        else
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
        if (commands.get(0).equalsIgnoreCase("bing")
                && query.toString().equalsIgnoreCase("chilling"))
            return bingChilling();
        return respondWith(BingSearch.getWebpageEmbed(query.toString()));
    }

    /**
     * This a JANK wiki command, but it'll work for what I'm using it for.
     * @return Embedded wiki result.
     */
    private Mono<Message> wiki() {
        parse();
        return respondWith(WikiSearch.getWikiEmbed(query + " wikipedia"));
    }

    /**
     * MyAnimeList anime search
     * @return Embedded anime related to query.
     */
    private Mono<Message> anime() {
        parse();
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

    /**
     * Chooses a random result from options given by the user.
     * @return Result of given options.
     */
    private Mono<Message> choose() {
        parse();
        String[] choices = query.toString().split(",");
        Random random = new Random();
        int randInt = random.nextInt(choices.length);
        return respondWith(choices[randInt].trim());
    }

    /**
     * 8ball command
     * @return A positive, neutral, or pessimistic response.
     */
    private Mono<Message> eightBall() {
        Random random = new Random();
        int num = random.nextInt(3);
        String s = "";
        switch(num) {
            case 0 -> {
                num = random.nextInt(10);
                switch (num) {
                    case 0 -> s = "yeah probably";
                    case 1 -> s = "def";
                    case 2 -> s = "for SURE";
                    case 3 -> s = "yeah";
                    case 4 -> s = "of course :)";
                    case 5 -> s = "I don't see why not";
                    case 6 -> s = "most likely";
                    case 7 -> s = "8-ball says... :flushed:";
                    case 8 -> s = "sure";
                    case 9 -> s = ":triumph::triumph::triumph::triumph::triumph: yea";
                }
            }
            case 1 -> {
                num = random.nextInt(5);
                switch (num) {
                    case 0 -> s = "idk";
                    case 1 -> s = "sky looks a little cloudy, hard 2 say";
                    case 2 -> s = "can't say for sure";
                    case 3 -> s = "im sorry what";
                    case 4 -> s = "best not said out loud i think";
                }
            }
            case 2 -> {
                num = random.nextInt(5);
                switch (num) {
                    case 0 -> s = "ain't lookin too hot buddy";
                    case 1 -> s = "yikes";
                    case 2 -> s = "big no";
                    case 3 -> s = "absolutely not";
                    case 4 -> s = "lol no";
                }
            }
            default -> s = "something went wrong";
        }
        return respondWith(s);
    }

    /**
     * YouTube search
     * @return YouTube video link
     */
    private Mono<Message> youtube() {
        parse();
        return respondWith(YouTubeSearch.getVideo(query.toString()));
    }
}
