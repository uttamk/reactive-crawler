import com.google.common.io.Resources;
import io.reactivex.observers.TestObserver;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

class CrawlerTest {
    @Test
    void shouldCrawlWhenThereIsOneLevel() throws IOException {
        TestObserver<Page> testObserver = crawl(1, "http://localhost:3000/level1.html");

        testObserver.assertValueCount(1);
        assertPage(testObserver, "level1.html", new String[]{"level21.html", "level22.html"});

    }

    @Test
    void shouldCrawlWhenThereAreTwoLevels() throws IOException {
        TestObserver<Page> testObserver = crawl(2, "http://localhost:3000/level1.html");


        testObserver.assertValueCount(3);
        assertPage(testObserver, "level1.html", new String[]{"level21.html", "level22.html"});
        assertPage(testObserver, "level21.html", new String[]{"level31.html", "level32.html", "level33.html"});
        assertPage(testObserver, "level22.html", new String[]{"level34.html", "level35.html", "level36.html"});
    }

    @Test
    void shouldCrawlWhenThereAreThreeLevels() throws IOException {
        TestObserver<Page> testObserver = crawl(3, "http://localhost:3000/level1.html");


        testObserver.assertValueCount(9);
        assertPage(testObserver, "level1.html", new String[]{"level21.html", "level22.html"});
        assertPage(testObserver, "level21.html", new String[]{"level31.html", "level32.html", "level33.html"});
        assertPage(testObserver, "level22.html", new String[]{"level34.html", "level35.html", "level36.html"});
        assertPage(testObserver, "level31.html", new String[]{"level41.html"});
        assertPage(testObserver, "level32.html", new String[]{"level41.html"});
        assertPage(testObserver, "level33.html", new String[]{"level41.html"});
        assertPage(testObserver, "level34.html", new String[]{"level41.html"});
        assertPage(testObserver, "level35.html", new String[]{"level41.html"});
        assertPage(testObserver, "level36.html", new String[]{"level41.html"});
    }

    @Test
    void shouldResumeWithOtherLinksIfOneLinkIsBroken() throws IOException {
        TestObserver<Page> testObserver = crawl(2, "http://localhost:3000/broken.html");

        testObserver.assertValueCount(3);
        assertPage(testObserver, "level1.html", new String[]{"level21.html", "level22.html"});
        assertPage(testObserver, "level21.html", new String[]{"level31.html", "level32.html", "level33.html"});
        assertPage(testObserver, "broken.html", new String[]{"level1.html", "level21.html", "nonexistent.html"});
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

    private void assertPage(TestObserver<Page> testObserver, String fileName, String[] linkFileNames) throws IOException {
        Page page = testObserver.values().stream().filter(p -> p.getUrl()
                .equals("http://localhost:3000/" + fileName)).findFirst().orElse(null);

        Assertions.assertNotNull(page);
        Assertions.assertEquals(fileContent(fileName), page.getContent());
        Arrays.stream(linkFileNames).forEach(url -> Assertions.assertTrue(
                page.getLinks()
                        .stream()
                        .anyMatch(link -> link.getUrl().equals("http://localhost:3000/" + url))));
    }
}

