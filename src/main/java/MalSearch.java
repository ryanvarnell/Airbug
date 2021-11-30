import com.kttdevelopment.mal4j.MyAnimeList;
import com.kttdevelopment.mal4j.anime.Anime;
import com.kttdevelopment.mal4j.anime.AnimePreview;
import com.kttdevelopment.mal4j.anime.AnimeRecommendation;
import com.kttdevelopment.mal4j.manga.Manga;
import com.kttdevelopment.mal4j.manga.MangaPreview;
import com.kttdevelopment.mal4j.manga.MangaRecommendation;
import discord4j.core.spec.EmbedCreateSpec;

import java.util.List;

public class MalSearch {
    private static final MyAnimeList mal = MyAnimeList.withClientID("5a62a2915c5c1c475429e7e7cb9b6864");

    public static Anime searchAnime(String query) {
        List<AnimePreview> search = mal.getAnime()
                .withQuery(query).includeNSFW(false)
                .search();
        return search.get(0).getAnime();
    }

    public static Manga searchManga(String query) {
        List<MangaPreview> search = mal.getManga().withQuery(query)
                .includeNSFW(false)
                .search();
        return search.get(0).getManga();
    }

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
