import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.stream.Collectors;

public class Crawler {
    private int levelLimit;

    Crawler(int levelLimit) {

        this.levelLimit = levelLimit;
    }

    public Flowable<Page> crawl(String url) {
        return crawl(url, 1);
    }

    private Flowable<Page> crawl(String url, int level) {
        if (level > levelLimit)
            return Flowable.empty();
        return Flowable.just(url)
                .map(u -> Jsoup.connect(u).get())
                .onErrorResumeNext(ErrorHandler::onError)
                .map(doc -> new Page(url, getLinks(doc), doc.html()))
                .flatMap(page -> Flowable.concat(Flowable.just(page), crawl(page.getLinks(), level))).distinct();
    }

    private Flowable<Page> crawl(List<Link> links, int level) {
        return Flowable.fromIterable(links)
                .parallel()
                .runOn(Schedulers.io())
                .flatMap(link -> crawl(link.getUrl(), level + 1))
                .sequential();


    }

    private List<Link> getLinks(Document doc) {
        return doc.select("a")
                .stream()
                .map(e -> new Link(e.attr("href")))
                .collect(Collectors.toList());
    }

}

