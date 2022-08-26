package infcon.armeria;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.ServiceRequestContext;

import java.util.concurrent.CompletableFuture;

public final class MyService implements HttpService {

    private final WebClient fooClient;
    private final WebClient barClient;

    public MyService(WebClient fooClient, WebClient barClient) {
        this.fooClient = fooClient;
        this.barClient = barClient;
    }

    @Override
    public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
        // 껍데기
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        HttpResponse httpResponse = fooClient.get("/foo");
        // backend로 요청을 보내고, future라는 껍데기를 받아서 thenAccept 라는 콜백을 붙임.
        // 3초 후에 응답을 다 받은 후에 실행된다.
        httpResponse.aggregate().thenAccept(aggregatedHttpResponse -> {
            // An event loop
            System.err.println(aggregatedHttpResponse.contentUtf8());
            // 껍데기지만, 알맹이가 있는 껍데기
            final HttpResponse response = aggregatedHttpResponse.toHttpResponse();
            future.complete(response);
        });

        // 이번에는 반대로 future 라는 껍데기에서 HttpResponse 라는 껍데기를 만들었다.
        // 그래서 실제 알맹이를 만들어서 original client 에게 응답을 보낸다.
        HttpResponse response = HttpResponse.from(future);
        return response;
    }
}
