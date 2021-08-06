package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        if (args.length == 0) {
            System.out.println("Incorrect command line");
            System.exit(1);
        }

        SpotifyModel model;
        if (args.length <= 5) {
            model = new SpotifyModel(args[1], args[3], 5);
        } else {
            model = new SpotifyModel(args[1], args[3], Integer.parseInt(args[5]));
        }


        SpotifyView view = new SpotifyView();
        SpotifyController controller = new SpotifyController(model, view);


        boolean mainLoop = true;
        while (mainLoop) {
            String command = sc.nextLine();

            if (controller.getSpotifyIsAuthenticated()) {

                controller.spotifySwitchCases(command);
                controller.updateView();

                while (controller.isSpotifyInsideDisplayPages()) {
                    String navigate = sc.nextLine();
                    if (controller.spotifySwitchNavigation(navigate)) {
                        controller.updateView();
                    }

                }


                if (controller.getSpotifyIsExitAccount()) {
                    mainLoop = false;
                }
            } else {
                switch (command) {
                    case "auth":
                        HttpJavaServer httpServer1 = new HttpJavaServer();
                        httpServer1.createServer(controller.getSpotifyRedirect_uri_Port());
                        httpServer1.handleContextRequest(controller);
                        httpServer1.startServer();

                        System.out.println("use this link to request the access code:");
                        System.out.println(controller.getSpotifyAuthLink());
                        System.out.println("waiting for code...");

                        while (true) {
                            if (httpServer1.isStopServer()) {
                                httpServer1.stopServer();
                                controller.setSpotifyAccessToken();
                                break;
                            }
                        }
                        break;
                    case "exit":
                        System.out.println("---GOODBYE!---");
                        mainLoop = false;
                        break;
                    default:
                        System.out.println("Please, provide access for application.");
                        break;
                }
            }
        }
    }
}

class HttpJavaClient {
    HttpClient client;
    HttpRequest request;
    String access_token;

    HttpResponse<String> response;

    public void createClient() {
        client = HttpClient.newBuilder().build();
    }

    public void newHttpRequestAccessToken(String spotifyServerPoint, String code, String redirect_uri, String encoded64Authorization) {
        String grant_type = "authorization_code";
        //System.out.println("code : " + code);

        request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .headers("Authorization", "Basic " + encoded64Authorization)
                .uri(URI.create(spotifyServerPoint + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=" + grant_type
                        + "&code=" + code
                        + "&redirect_uri=" + redirect_uri
                ))
                .build();
    }

    public void newHttpRequestToSpotify(String accessToken, String apiPath) {
        this.access_token = accessToken;
        newHttpRequestToSpotify(apiPath);
    }

    public void newHttpRequestToSpotify(String apiPath) {
        request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + this.access_token)
                .uri(URI.create(apiPath))
                .GET()
                .build();
    }


    public HttpResponse<String> sendHttpRequestToSpotifyCategories() {
        try {
            response = this.client.send(this.request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("response:\n" + response.body());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return response;
    }

    public HttpResponse<String> sendHttpRequestToSpotifyNew() {
        try {
            response = this.client.send(this.request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return response;
    }


    public HttpResponse<String> sendHttpRequestToSpotifyFeatured() {
        try {
            response = this.client.send(this.request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return response;
    }

    public HttpResponse<String> sendHttpRequestToSpotifyPlaylist() {
        try {
            response = this.client.send(this.request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return response;
    }

    public String sendHttpRequestToSpotifyPlaylistAllCategoriesMatch(String category, String spotifyApiPath) {

        String returnId = "";
        try {
            response = this.client.send(this.request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("response:\n" + response.body());

            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject categoriesObj = jo.getAsJsonObject("categories");

            //Map<String, String> playlists = new HashMap<>();
            Map<String, String> playlists = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            for (JsonElement item : categoriesObj.getAsJsonArray("items")) {
                JsonObject itemsObj = JsonParser.parseString(String.valueOf(item)).getAsJsonObject();
                String name = itemsObj.get("name").getAsString();
                String id = itemsObj.get("id").getAsString();
                playlists.put(name, id);
            }

            if (playlists.containsKey(category)) {
                returnId = playlists.get(category);
                //System.out.println("playlists contains Key");
            } else {
                System.out.println("Unknown category name.");
                return "";
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return returnId;
    }


    public boolean sendHttpRequest() {
        try {
            response = this.client.send(this.request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("response:\n" + response.body());

            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            String accessToken = jo.get("access_token").getAsString();
            //System.out.println(accessToken);
            this.access_token = accessToken;

            if (response.body().contains("access_token")) {
                System.out.println("Success!");
                return true;
            }

        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}

class HttpJavaServer {
    HttpServer server;
    boolean stopServer;

    public void createServer(int spotifyRedirect_uri_port) {
        try {
            this.server = HttpServer.create();
            this.server.bind(new InetSocketAddress(spotifyRedirect_uri_port), 0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleContextRequest(SpotifyController spotifyController) {
        this.server.createContext("/",
                new HttpHandler() {
                    public void handle(HttpExchange exchange) throws IOException {
                        String query = exchange.getRequestURI().getQuery();

                        if (query == null || query.contains("error=")) {
                            String authFailed = "Authorization code not found. Try again.";
                            exchange.sendResponseHeaders(200, authFailed.length());
                            exchange.getResponseBody().write(authFailed.getBytes());
                            exchange.getResponseBody().close();
                        } else if (query.contains("code=")) {
                            spotifyController.setSpotifyAuthCode(query.replace("code=", ""));
                            String authSuccess = "Got the code. Return back to your program.";
                            exchange.sendResponseHeaders(200, authSuccess.length());
                            exchange.getResponseBody().write(authSuccess.getBytes());
                            exchange.getResponseBody().close();
                            System.out.println("code received");
                            setStopServer(true);
                        }
                    }
                }
        );

    }

    public boolean isStopServer() {
        return stopServer;
    }

    public void setStopServer(boolean stopServer) {
        this.stopServer = stopServer;
    }

    public void startServer() {
        this.server.start();
    }

    public void stopServer() {
        this.server.stop(1);
    }
}
