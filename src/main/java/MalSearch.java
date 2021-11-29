import com.kttdevelopment.mal4j.MyAnimeList;
import com.kttdevelopment.mal4j.anime.Anime;
import com.kttdevelopment.mal4j.anime.AnimePreview;
import com.kttdevelopment.mal4j.manga.Manga;
import com.kttdevelopment.mal4j.manga.MangaPreview;

import java.util.List;

public class MalSearch {
    private static final MyAnimeList mal = MyAnimeList.withClientID("");

    public static Anime searchAnime(String query) {
        List<AnimePreview> search = mal.getAnime()
                .withQuery(query).includeNSFW(false)
                .search();
        return (Anime) search.get(0);
    }

    public static Manga searchManga(String query) {
        List<MangaPreview> search = mal.getManga().withQuery(query)
                .includeNSFW(false)
                .search();
        return (Manga) search.get(0);
    }
}
