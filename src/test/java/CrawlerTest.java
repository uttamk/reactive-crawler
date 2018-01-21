import com.google.common.io.Resources;
import io.reactivex.observers.TestObserver;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;

class CrawlerTest {
    @Test
    void shouldCrawlWhenThereIsOneLevel() throws IOException {
        TestObserver<Page> testObserver = crawl(1, "http://localhost:3000/level1.html");

        testObserver.assertValueCount(1);
        Page page = testObserver.values().get(0);
        Assertions.assertEquals("http://localhost:3000/level1.html", page.getUrl());
        Assertions.assertEquals(fileContent("level1.html"), page.getContent());
        Assertions.assertEquals(2, page.getLinks().size());
    }

    @Test
    void shouldCrawlWhenThereAreTwoLevels() {
        TestObserver<Page> testObserver = crawl(2, "http://localhost:3000/level1.html");


        testObserver.assertValueCount(3);
    }

    @Test
    void shouldCrawlWhenThereAreThreeLevels() {
        TestObserver<Page> testObserver = crawl(3, "http://localhost:3000/level1.html");


        testObserver.assertValueCount(9);
    }

    @Test
    void shouldResumeWithOtherLinksIfOneLinkIsBroken() {
        TestObserver<Page> testObserver = crawl(2, "http://localhost:3000/broken.html");

        testObserver.assertValueCount(3);
    }

    private TestObserver<Page> crawl(int levelLimit, String url) {
        TestObserver<Page> testObserver = new TestObserver<>();

        new Crawler(levelLimit)
                .crawl(url)
                .subscribe(testObserver);
        return testObserver;
    }


    private String fileContent(String fileName) throws IOException {

        String content = Resources.toString(Resources.getResource(fileName), Charset.forName("UTF-8"));
        return Jsoup.parse(content).html();
    }
}

