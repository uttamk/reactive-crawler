import io.reactivex.Observable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Crawler {
    private int maxLevels;

    Crawler(int maxLevels) {

        this.maxLevels = maxLevels;
    }

    public Observable<Link> crawl(String urlString) throws IOException {
        return crawlUrl(urlString, 1);
    }

    private Observable<Link> crawlUrl(String urlString, int level) throws IOException {
        Document document = Jsoup.connect(urlString).get();

        return Observable
                .just(urlString)
                .flatMap(url -> {
                    Elements aTags = document.select("a");
                    return Observable.fromIterable(aTags).map(tag -> new Link(tag.select("a").attr("href")));
                }).compose(o -> level < maxLevels
                        ? o.flatMap(link -> crawlUrl(link.getHref(), level + 1))
                        : o.map(v -> v));
    }

}
