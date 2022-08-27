package infcon.armeria;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.Server;

public class AuthServer {

    private final Server server;

    public static AuthServer of(int port) {
        return new AuthServer(port);
    }

    private AuthServer(int port) {
        server = Server.builder()
                .http(port)
                .service(
                        "/auth",
                        (ctx, req) -> {
                            System.err.println(this.getClass() + ": 요청 처리");
                            return HttpResponse.of("authorized");
                        })
                .build();
    }

    public void start() {
        server.start().join();
    }

}
