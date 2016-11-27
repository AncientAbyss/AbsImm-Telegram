import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        initTelegram();
        initWebServer();
    }

    private static void initWebServer() {
        HttpServer server = null;
        try {
            int port = Integer.parseInt(Util.readFromPropertyOrEnv("port"));
            System.out.println("Starting server on " + port + "...");
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace(); // TODO: use proper logging
        }
        server.createContext("/", new MyHttpHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private static void initTelegram() {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new TextAdventuresBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static class MyHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Handling web request...");
            String response = "Welcome to AbsImm.";
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
