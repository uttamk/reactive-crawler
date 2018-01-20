import java.util.List;

public class CrawlResult {

    private String url;
    private List<Link> links;

    CrawlResult(String url, List<Link> links) {

        this.url = url;
        this.links = links;
    }

    public List<Link> getLinks() {
        return links;
    }

    public String getUrl() {
        return url;
    }
}
