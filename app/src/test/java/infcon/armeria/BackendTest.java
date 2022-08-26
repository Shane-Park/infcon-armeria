package infcon.armeria;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

class BackendTest {

    @Test
    void backend() {
        final Backend foo = Backend.of("foo", 9000);
        foo.start();

        final WebClient webClient = WebClient.of("http://127.0.0.1:9000");

        // 이 시점에 httpResponse는 응답을 가지고 있지 않음. 3초 후에 응답을 return 하게끔 했기 때문에
        // 응답을 가지고 있지 않은 껍데기.
        HttpResponse httpResponse = webClient.get("/foo");
        System.err.println("Thread name: " + Thread.currentThread().getName());
        // 동기서버는 3초를 기다리지만, 비동기서버에서는 기다리지 않음.

        // response는 한번에 오지 않는다.
        // aggregate 를 이용 하면 header 와 body가 따로따로 오는걸 잘 모아서 하나의 aggregated된 Response로 만들어준다.
        CompletableFuture<AggregatedHttpResponse> future = httpResponse.aggregate();
        // 지금 이 future는 body를 가지고 있지 않음. 또 다른 껍데기. 껍데기에서 껍데기를 만든 상태.

        // 비동기서버에서는 join을 절대로 사용 하면 안됨.
        AggregatedHttpResponse aggregatedHttpResponse = future.join();
        String content = aggregatedHttpResponse.contentUtf8();
        System.err.println(content);


    }
}
