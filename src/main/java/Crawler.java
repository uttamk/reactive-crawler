import io.reactivex.Observable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Crawler {
    private int maxLevels;

    Crawler(int maxLevels) {

        this.maxLevels = maxLevels;
    }

    public Observable<Link> crawl(String urlString) {
        return crawlUrl(urlString, 1);
    }

    private Observable<Link> crawlUrl(String urlString, int level) {

        Observable<Link> currentLevelLinks = Observable
                .just(urlString)
                .flatMap(url -> {
                    Document document = Jsoup.connect(urlString).get();
                    Elements aTags = document.select("a");
                    return Observable.fromIterable(aTags).map(tag -> new Link(tag.select("a").attr("href")));
                });

        Observable<Link> nextLevelLinks = level < maxLevels
                ? currentLevelLinks.flatMap(link -> crawlUrl(link.getHref(), level + 1))
                : Observable.empty();

        return Observable.merge(currentLevelLinks, nextLevelLinks);
    }

}
