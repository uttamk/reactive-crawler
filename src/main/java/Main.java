import io.reactivex.schedulers.Schedulers;

public class Main {
    public static void main(String args[]) throws InterruptedException {
        String url = args[0];
        int levelLimit = Integer.parseInt(args[1]);
        final PageCounter status = new PageCounter();
        long startTime = System.currentTimeMillis();

        new Crawler(levelLimit)
                .crawl(url)
                .doOnComplete(() -> {
                    long endTime = System.currentTimeMillis();
                    System.out.println(status.getCount() + " links crawled in " + (endTime - startTime) / 1000 + " seconds");
                    System.exit(0);
                })
                .doOnError(err -> {
                    System.out.println(err);
                    System.exit(1);
                }).subscribeOn(Schedulers.single())
                .subscribe(page -> {
                    status.incrementCount();
                    if (status.getCount() % 100 == 0) {
                        long endTime = System.currentTimeMillis();
                        System.out.println(status.getCount() + " links crawled in " + (endTime - startTime) / 1000 + " seconds");
                        System.out.println(page.getUrl());
                    }
                });

        while (true) {
            Thread.sleep(1000);
        }
    }
}

class PageCounter {
    private volatile int count;

    public int incrementCount() {
        count += 1;
        return count;
    }

    public int getCount() {
        return count;
    }
}
