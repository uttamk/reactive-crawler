import io.reactivex.Observable;
import org.jsoup.Jsoup;

public class Crawler {
    private int levelLimit;

    Crawler(int levelLimit) {

        this.levelLimit = levelLimit;
    }

    public Observable<Link> crawl(String url) {
        return crawl(url, 1);
    }

    private Observable<Link> crawl(String url, int level) {
        return Observable.just(url)
                .map(u -> Jsoup.connect(u).get())
                .flatMap(doc -> Observable.fromIterable(doc.select("a")))
                .onErrorResumeNext(Observable.empty())
                .flatMap(a -> crawl(new Link(a.attr("href")), level));
    }

    private Observable<Link> crawl(Link link, int level) {
        Observable<Link> singleLink = Observable.just(link);

        if (level == levelLimit) {
            return singleLink;
        }
        return crawl(link.getUrl(), level + 1)
                .flatMap(c -> Observable.concat(singleLink, Observable.just(c)))
                .distinct();

    }

}
