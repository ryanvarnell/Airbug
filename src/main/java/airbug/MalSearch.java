package airbug;

import com.kttdevelopment.mal4j.MyAnimeList;
import com.kttdevelopment.mal4j.anime.Anime;
import com.kttdevelopment.mal4j.anime.AnimePreview;
import com.kttdevelopment.mal4j.anime.AnimeRecommendation;
import com.kttdevelopment.mal4j.manga.Manga;
import com.kttdevelopment.mal4j.manga.MangaPreview;
import com.kttdevelopment.mal4j.manga.MangaRecommendation;
import discord4j.core.spec.EmbedCreateSpec;

import java.util.List;

/**
 * Class for search MAL.
 */
public class MalSearch {
    private static final String malClientID = "5a62a2915c5c1c475429e7e7cb9b6864";
    private static final MyAnimeList mal = MyAnimeList.withClientID(malClientID);

    /**
     * Searches MAL for anime.
     * @param query User's search query.
     * @return Anime matching search query.
     */
    public static Anime searchAnime(String query) {
        List<AnimePreview> search = mal.getAnime()
                .withQuery(query).includeNSFW(false)
                .search();
        return search.get(0).getAnime();
    }

    /**
     * Searches MAL for manga.
     * @param query User's search query.
     * @return Manga matching search query.
     */
    public static Manga searchManga(String query) {
        List<MangaPreview> search = mal.getManga().withQuery(query)
                .includeNSFW(false)
                .search();
        return search.get(0).getManga();
    }

    /**
     * Creates a nice embed for an anime.
     * @param query Query to search for.
     * @return Embed of the anime.
     */
    public static EmbedCreateSpec getAnimeEmbed(String query) {
        Anime anime = searchAnime(query);
        return EmbedCreateSpec.builder()
                .author("MyAnimeList",
                        "https://myanimelist.net/",
                        "https://image.winudf.com/v2/image/bmV0Lm15YW5pbWVsaXN0X2ljb25fMTUyNjk5MjEwNV8wODE/icon.png?w=170&fakeurl=1&type=.png")
                .thumbnail(anime.getMainPicture().getLargeURL())
                .title(anime.getTitle())
                .description(anime.getSynopsis()
                        .replace("\\n", "")
                        .replace("[Written by MAL Rewrite]", ""))
                .addField("Mean rating:", String.valueOf(anime.getMeanRating()), false)
                .build();
    }

    /**
     * Creates a nice embed for recommendations based on an anime.
     * @param query Query to find recommendations based on.
     * @return Embed of the anime reccomendations.
     */
    public static EmbedCreateSpec getAnimeRecEmbed(String query) {
        Anime anime = searchAnime(query);
        AnimeRecommendation[] recs = anime.getRecommendations();
        Anime rec1 = recs[0].getAnime();
        Anime rec2 = recs[1].getAnime();
        Anime rec3 = recs[2].getAnime();
        return EmbedCreateSpec.builder()
                .author("Recommendations based on:",
                        "https://myanimelist.net/",
                        "https://image.winudf.com/v2/image/bmV0Lm15YW5pbWVsaXN0X2ljb25fMTUyNjk5MjEwNV8wODE/icon.png?w=170&fakeurl=1&type=.png")
                .title(anime.getTitle())
                .thumbnail(anime.getMainPicture().getLargeURL())
                .addField(rec1.getTitle(), "Rating: " + rec1.getMeanRating().toString(), true)
                .addField(rec2.getTitle(), "Rating: " + rec1.getMeanRating().toString(), true)
                .addField(rec3.getTitle(), "Rating: " + rec1.getMeanRating().toString(), true)
                .build();
    }

    /**
     * Creates a nice embed for an manga.
     * @param query Query to search for.
     * @return Embed of the manga.
     */
    public static EmbedCreateSpec getMangaEmbed(String query) {
        Manga manga = searchManga(query);
        return EmbedCreateSpec.builder()
                .author("MyAnimeList",
                        "https://myanimelist.net/",
                        "https://image.winudf.com/v2/image/bmV0Lm15YW5pbWVsaXN0X2ljb25fMTUyNjk5MjEwNV8wODE/icon.png?w=170&fakeurl=1&type=.png")
                .thumbnail(manga.getMainPicture().getLargeURL())
                .title(manga.getTitle())
                .description(manga.getSynopsis()
                        .replace("\\n", "")
                        .replace("[Written by MAL Rewrite]", ""))
                .addField("Mean rating:", String.valueOf(manga.getMeanRating()), false)
                .build();
    }

    /**
     * Creates a nice embed for recommendations based on an manga.
     * @param query Query to find recommendations based on.
     * @return Embed of the manga reccomendations.
     */
    public static EmbedCreateSpec getMangaRecEmbed(String query) {
        Manga manga = searchManga(query);
        MangaRecommendation[] recs = manga.getRecommendations();
        Manga rec1 = recs[0].getManga();
        Manga rec2 = recs[1].getManga();
        Manga rec3 = recs[2].getManga();
        return EmbedCreateSpec.builder()
                .author("Recommendations based on:",
                        "https://myanimelist.net/",
                        "https://image.winudf.com/v2/image/bmV0Lm15YW5pbWVsaXN0X2ljb25fMTUyNjk5MjEwNV8wODE/icon.png?w=170&fakeurl=1&type=.png")
                .title(manga.getTitle())
                .thumbnail(manga.getMainPicture().getLargeURL())
                .addField(rec1.getTitle(), "Rating: " + rec1.getMeanRating().toString(), true)
                .addField(rec2.getTitle(), "Rating: " + rec1.getMeanRating().toString(), true)
                .addField(rec3.getTitle(), "Rating: " + rec1.getMeanRating().toString(), true)
                .build();
    }
}
