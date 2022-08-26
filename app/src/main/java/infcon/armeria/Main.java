package infcon.armeria;

import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;

import java.util.concurrent.CompletableFuture;

public final class Main {

    public static void main(String[] args) {
        ServerBuilder serverBuilder = Server.builder();
        Server server = serverBuilder.http(8080)
                .service("/infcon", new MyService())
                .build();

        CompletableFuture<Void> future = server.start();
        future.join();
    }
}
