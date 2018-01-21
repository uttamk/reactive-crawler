import java.util.List;

public class Page {
    private String url;
    private List<Link> links;
    private String content;

    Page(String url, List<Link> links, String content) {

        this.url = url;
        this.links = links;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public List<Link> getLinks() {
        return links;
    }

    public String getContent() {
        return content;
    }
}
