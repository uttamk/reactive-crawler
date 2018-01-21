import io.reactivex.observers.TestObserver;
import org.junit.jupiter.api.Test;

class CrawlerTest {
    @Test
    void shouldCrawlWhenThereIsOneLevel() {
        TestObserver<Link> testObserver = crawl(1, "http://localhost:3000/level1.html");

        testObserver.assertValueCount(2);
    }

    @Test
    void shouldCrawlWhenThereAreTwoLevels() {
        TestObserver<Link> testObserver = crawl(2, "http://localhost:3000/level1.html");


        testObserver.assertValueCount(8);
    }

    @Test
    void shouldCrawlWhenThereAreThreeLevels() {
        TestObserver<Link> testObserver = crawl(3, "http://localhost:3000/level1.html");


        testObserver.assertValueCount(14);
    }

    @Test
    void shouldResumeWithOtherLinksIfOneLinkIsBroken() {
        TestObserver<Link> testObserver = crawl(2, "http://localhost:3000/broken.html");

        testObserver.assertValueCount(3);
    }

    private TestObserver<Link> crawl(int levelLimit, String url) {
        TestObserver<Link> testObserver = new TestObserver<>();

        new Crawler(levelLimit)
                .crawl(url)
                .subscribe(testObserver);
        return testObserver;
    }
}

