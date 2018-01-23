import io.reactivex.schedulers.Schedulers;

public class Main {
    public static void main(String args[]) throws InterruptedException {
        String url = args[0];
        int levelLimit = Integer.parseInt(args[1]);
        final PageCounter counter = new PageCounter();
        new Crawler(levelLimit)
                .crawl(url)
                .doOnComplete(() -> System.exit(0))
                .doOnError((err) -> System.exit(1))
                .subscribeOn(Schedulers.newThread())
                .subscribe(page -> {
                    counter.increment();
                });
        while (true) {
            Thread.sleep(60000);
            System.out.println(counter.getCount());
            System.exit(0);
        }
    }
}

class PageCounter {
    private int count;

    public int increment() {
        count += 1;
        return count;
    }

    public int getCount() {
        return count;
    }

}
