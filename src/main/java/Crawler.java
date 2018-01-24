import io.reactivex.Observable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.stream.Collectors;

public class Crawler {
    private int levelLimit;

    Crawler(int levelLimit) {

        this.levelLimit = levelLimit;
    }

    public Observable<Page> crawl(String url) {
        return crawl(url, 1);
    }

    private Observable<Page> crawl(String url, int level) {
        if(level > levelLimit)
            return Observable.empty();
        return Observable.just(url)
                .map(u -> Jsoup.connect(u).get())
                .onErrorResumeNext(Observable.empty())
                .map(doc -> new Page(url, getLinks(doc), doc.html()))
                .flatMap(page -> Observable.concat(Observable.just(page), crawl(page.getLinks(), level))).distinct();
    }

    private Observable<Page> crawl(List<Link> links, int level) {
        return Observable.fromIterable(links).flatMap(link -> crawl(link.getUrl(), level + 1));


    }

    private List<Link> getLinks(Document doc) {
        return doc.select("a")
                .stream()
                .map(e -> new Link(e.attr("href")))
                .collect(Collectors.toList());
    }

}

