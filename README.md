# AirBug v1

<img src="source/images/airbugLogo.png" alt="Airbug Logo" style="width: 150px; float: right;">

This was a very early attempt at a bot for Discord using Discord4J.\
The project has since been migrated to a JavaScript/Node.js/Discord.js base, which is currently being kept in a (as of 
writing this) private repository. I can't guarantee this version still
works because of the numerous updates Discord has been through since this was last worked on.

# Features

## AI responses

<img src="source/images/AI.png" alt="Airbug AI Responses" style="box-shadow: 0 0 18px black; border-radius: 10px; display: block; margin-left: auto; margin-right: auto;">
<a href="https://openai.com/"><img src="source/images/openAIlogo.png" alt="OpenAI Logo" style="width: 200px; display: block; margin-left: auto; margin-right: auto; padding-top: 15px; padding-bottom: 15px;"></a>
Built with OpenAI's GPT3 model (pre-ChatGPT) using the "ada" base. "Ada" was great, as it was quick and cheap, but not
nearly as coherent as the "Davinci" base would have been, which was much more costly and slow. As a consequence, these 
responses were far below the mark we expect from ChatGPT today.

## Web Search

Airbug can search the web, given a query. This was implemented using Bing's web search API. This command also has a ton 
of aliases, so you can really use any search engine name as a command if you so please.
```
-bing [query]
```
<img src="source/images/webSearch.png" alt="Web Search" style="box-shadow: 0 0 18px black; border-radius: 10px; display: block; margin-left: auto; margin-right: auto;">

## Wikipedia Search

Airbug can grab a Wikipedia page, given a query.
```
-wiki [query]
```
<img src="source/images/wikiSearch.png" alt="Wiki Search" style="box-shadow: 0 0 18px black; border-radius: 10px; display: block; margin-left: auto; margin-right: auto;">

## MyAnimeList Integration

AirBug is able to search and provide information on any manga or anime on 
<a href="https://myanimelist.net/"><img src="source/images/myAnimeListLogo.png" alt="MyAnimeList Logo" style="height: 1.3em; transform: translate(0, 0.3em);"></a>, as well as
offer recommendations based on a given title.

<h3 style="text-align: center;">Anime/Manga Search</h3>
```
-anime [query]
-manga [query]
```
<img src="source/images/animeSearch.png" alt="Anime/Manga Search" style="box-shadow: 0 0 18px black; border-radius: 10px; display: block; margin-left: auto; margin-right: auto;">

<h3 style="text-align: center;">Anime/Manga Recommendations</h3>
```
-anime -rec [query]
-manga -rec [query]
```
<img src="source/images/mangarec.png" alt="Anime/Manga Recommendations" style="box-shadow: 0 0 18px black; border-radius: 10px; display: block; margin-left: auto; margin-right: auto;">

## Other Commands

```
help                            ⟶  List of commands.
ping                            ⟶  Pong!
img [query]                     ⟶  Image search.
gif [query]                     ⟶  Gif search.
cowsay [text]                   ⟶  Prints an image of a cow saying your text.
cowthink [text]                 ⟶  Same as cowsay, but the cow keeps it to themselves.
figlet [text]                   ⟶  Prints the text in large ASCII-art letters.
choose [thing1, thing2, etc.]   ⟶  Chooses a random thing.
8ball [question]                ⟶  Ask it your deepest desires.
youtube [query]                 ⟶  Searches YouTube.
```