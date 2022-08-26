package infcon.armeria;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

class BackendTest {

    @Test
    void backend() throws InterruptedException {
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

        // join 이 아닌 callback 을 사용 해야 한다.
        // future에 callback을 등록 해서 3초 후에 응답이 발생하면 알맹이가 채워지고, 오리지널 클라이언트에게 요청
        future.thenAccept(aggregatedHttpResponse -> {
            // 이벤트 루프. 요청을 받아서 futre 껍데기 에다가 알맹이를 넣어주는 쓰레드
            System.err.println("In callback. Thread name: " + Thread.currentThread().getName());
            sendBackToTheOriginalClient(aggregatedHttpResponse);
        });

        Thread.sleep(Long.MAX_VALUE);
    }

    private void sendBackToTheOriginalClient(AggregatedHttpResponse aggregatedHttpResponse) {
    }
}
