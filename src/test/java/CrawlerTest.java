import io.reactivex.observers.TestObserver;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class CrawlerTest {
    @Test
    void shouldCrawlWhenThereIsOneLevel() throws IOException {
        TestObserver<Link> testObserver = new TestObserver<>();

        new Crawler(1)
                .crawl("https://google.com")
                .subscribe(testObserver);


        testObserver.assertValueCount(29);
    }

    @Test
    void shouldCrawlWhenThereAreTwoLevels() throws IOException {
        TestObserver<Link> testObserver = new TestObserver<>();

        new Crawler(2)
                .crawl("https://google.com")
                .subscribe(testObserver);


        testObserver.assertValueCount(10);
    }
}

