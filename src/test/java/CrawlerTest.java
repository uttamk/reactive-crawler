import com.google.common.io.Resources;
import io.reactivex.subscribers.TestSubscriber;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

class CrawlerTest {
    @Test
    void shouldCrawlWhenThereIsOneLevel() throws IOException {
        TestSubscriber<Page> testSubscriber = crawl(1, "http://localhost:3000/level1.html");

        testSubscriber.assertValueCount(1);
        assertPage(testSubscriber, "level1.html", new String[]{"level21.html", "level22.html"});

    }

    @Test
    void shouldCrawlWhenThereAreTwoLevels() throws IOException {
        TestSubscriber<Page> testSubscriber = crawl(2, "http://localhost:3000/level1.html");


        testSubscriber.assertValueCount(3);
        assertPage(testSubscriber, "level1.html", new String[]{"level21.html", "level22.html"});
        assertPage(testSubscriber, "level21.html", new String[]{"level31.html", "level32.html", "level33.html"});
        assertPage(testSubscriber, "level22.html", new String[]{"level34.html", "level35.html", "level36.html"});
    }

    @Test
    void shouldCrawlWhenThereAreThreeLevels() throws IOException {
        TestSubscriber<Page> testSubscriber = crawl(3, "http://localhost:3000/level1.html");


        testSubscriber.assertValueCount(9);
        assertPage(testSubscriber, "level1.html", new String[]{"level21.html", "level22.html"});
        assertPage(testSubscriber, "level21.html", new String[]{"level31.html", "level32.html", "level33.html"});
        assertPage(testSubscriber, "level22.html", new String[]{"level34.html", "level35.html", "level36.html"});
        assertPage(testSubscriber, "level31.html", new String[]{"level41.html"});
        assertPage(testSubscriber, "level32.html", new String[]{"level41.html"});
        assertPage(testSubscriber, "level33.html", new String[]{"level41.html"});
        assertPage(testSubscriber, "level34.html", new String[]{"level41.html"});
        assertPage(testSubscriber, "level35.html", new String[]{"level41.html"});
        assertPage(testSubscriber, "level36.html", new String[]{"level41.html"});
    }

    @Test
    void shouldResumeWithOtherLinksIfOneLinkIsBroken() throws IOException {
        TestSubscriber<Page> testSubscriber = crawl(2, "http://localhost:3000/broken.html");

        testSubscriber.assertValueCount(3);
        assertPage(testSubscriber, "level1.html", new String[]{"level21.html", "level22.html"});
        assertPage(testSubscriber, "level21.html", new String[]{"level31.html", "level32.html", "level33.html"});
        assertPage(testSubscriber, "broken.html", new String[]{"level1.html", "level21.html", "nonexistent.html"});
    }

    private TestSubscriber<Page> crawl(int levelLimit, String url) {
        TestSubscriber<Page> testSubscriber = new TestSubscriber<>();

        new Crawler(levelLimit)
                .crawl(url)
                .subscribe(testSubscriber);
        return testSubscriber;
    }


    private String fileContent(String fileName) throws IOException {

        String content = Resources.toString(Resources.getResource(fileName), Charset.forName("UTF-8"));
        return Jsoup.parse(content).html();
    }

    private void assertPage(TestSubscriber<Page> testSubscriber, String fileName, String[] linkFileNames) throws IOException {
        Page page = testSubscriber.values().stream().filter(p -> p.getUrl()
                .equals("http://localhost:3000/" + fileName)).findFirst().orElse(null);

        Assertions.assertNotNull(page);
        Assertions.assertEquals(fileContent(fileName), page.getContent());
        Arrays.stream(linkFileNames).forEach(url -> Assertions.assertTrue(
                page.getLinks()
                        .stream()
                        .anyMatch(link -> link.getUrl().equals("http://localhost:3000/" + url))));
    }
}

