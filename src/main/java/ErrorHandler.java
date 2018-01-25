import io.reactivex.Flowable;
import io.vavr.collection.List;
import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class ErrorHandler {
    private static List<Class<?>> ignorableExceptionClasses = List.of(
            IllegalArgumentException.class,
            HttpStatusException.class,
            SocketTimeoutException.class,
            SocketException.class,
            SSLProtocolException.class,
            SSLHandshakeException.class,
            UnknownHostException.class,
            UnsupportedMimeTypeException.class,
            MalformedURLException.class,
            HttpStatusException.class
    );

    static Flowable<Document> onError(Throwable error) {
        return ignorableExceptionClasses.contains(error.getClass()) ? Flowable.empty() : Flowable.error(error);
    }
}