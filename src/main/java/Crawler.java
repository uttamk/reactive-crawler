import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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
                .onErrorResumeNext(e -> e instanceof IllegalArgumentException ? Flowable.empty() : Flowable.error(e))
                .onErrorResumeNext(e -> e instanceof SSLProtocolException ? Flowable.empty() : Flowable.error(e))
                .onErrorResumeNext(e -> e instanceof UnsupportedMimeTypeException ? Flowable.empty() : Flowable.error(e))
                .onErrorResumeNext(e -> e instanceof MalformedURLException ? Flowable.empty() : Flowable.error(e))
                .onErrorResumeNext(e -> e instanceof HttpStatusException ? Flowable.empty() : Flowable.error(e))
                .onErrorResumeNext(e -> e instanceof SocketTimeoutException ? Flowable.empty() : Flowable.error(e))
                .onErrorResumeNext(e -> e instanceof SocketException ? Flowable.empty() : Flowable.error(e))
                .onErrorResumeNext(e -> e instanceof SSLHandshakeException ? Flowable.empty() : Flowable.error(e))
                .onErrorResumeNext(e -> e instanceof UnknownHostException ? Flowable.empty() : Flowable.error(e))
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

