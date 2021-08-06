package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpotifyView {
    HttpResponse<String> response;

    public void setResponse(HttpResponse<String> response) {
        this.response = response;
    }

    public void displayResponse(SpotifyModel spotifyModel) {
        if (response == null) {
            return;
        }

        String[] split = spotifyModel.getResponseType().split(" ", 2);

        switch (split[0]) {
            case "albums":
                displayNew(spotifyModel);
                break;
            case "categories":
                displayCategories(spotifyModel);
                break;
            case "featured":
                displayFeatured(spotifyModel);
                break;
            case "playlists":
                displayPlaylists(spotifyModel);
                break;
        }
    }


    public void displayCategories(SpotifyModel spotifyModel) {
        JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject categoriesObj = jo.getAsJsonObject("categories");

        int page = spotifyModel.getPageNavigation().getCurrentPage();
        int limit = spotifyModel.getPageNavigation().getLimit();

        ArrayList<String> arrayList = new ArrayList<>();

        for (JsonElement item : categoriesObj.getAsJsonArray("items")) {
            JsonObject itemsObj = JsonParser.parseString(String.valueOf(item)).getAsJsonObject();
            String name = itemsObj.get("name").getAsString();
            arrayList.add(name);
        }

        for (int i = page; i <= page / limit; i++) {
            System.out.println(arrayList.get(i - 1));
        }

        System.out.println("---PAGE " + spotifyModel.getPageNavigation().getCurrentPage() + " OF "
                + spotifyModel.getPageNavigation().getTotalPages() + "---");
    }

    public void displayNew(SpotifyModel spotifyModel) {
        JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject albumsObj = jo.getAsJsonObject("albums");

        int page = spotifyModel.getPageNavigation().getCurrentPage();
        int limit = spotifyModel.getPageNavigation().getLimit();

        ArrayList<JsonObject> arrayList = new ArrayList<>();

        for (JsonElement item : albumsObj.getAsJsonArray("items")) {
            JsonObject itemsObj = JsonParser.parseString(String.valueOf(item)).getAsJsonObject();
            arrayList.add(itemsObj);

//            String name = itemsObj.get("name").getAsString();
//            System.out.println(name);
//
//            List<String> artistsNames = new ArrayList<>();
//
//            for (JsonElement artist : itemsObj.getAsJsonArray("artists")) {
//                JsonObject artistsObj = JsonParser.parseString(String.valueOf(artist)).getAsJsonObject();
//                String artistName = artistsObj.get("name").getAsString();
//                artistsNames.add(artistName);
//            }
//
//            System.out.print("[");
//            for (int i = 0; i < artistsNames.size(); i++) {
//                System.out.print(artistsNames.get(i));
//                if (i != artistsNames.size() - 1) {
//                    System.out.print(", ");
//                }
//            }
//            System.out.println("]");
//
//            String spotifyUrl = itemsObj.getAsJsonObject("external_urls").get("spotify").getAsString();
//            System.out.println(spotifyUrl);
//            System.out.println();
        }

        for (int i = page; i <= page / limit; i++) {

            String name = arrayList.get(i-1).get("name").getAsString();
            System.out.println(name);

            List<String> artistsNames = new ArrayList<>();

            for (JsonElement artist : arrayList.get(i-1).getAsJsonArray("artists")) {
                JsonObject artistsObj = JsonParser.parseString(String.valueOf(artist)).getAsJsonObject();
                String artistName = artistsObj.get("name").getAsString();
                artistsNames.add(artistName);
            }

            System.out.print("[");
            for (int j = 0; j < artistsNames.size(); j++) {
                System.out.print(artistsNames.get(j));
                if (j != artistsNames.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");

            String spotifyUrl = arrayList.get(i-1).getAsJsonObject("external_urls").get("spotify").getAsString();
            System.out.println(spotifyUrl);
        }

        System.out.println("---PAGE " + spotifyModel.getPageNavigation().getCurrentPage() + " OF "
                + spotifyModel.getPageNavigation().getTotalPages() + "---");
    }

    public void displayFeatured(SpotifyModel spotifyModel) {
        System.out.println("FEATURED PLAYLISTS");
        JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject playlistsObj = jo.getAsJsonObject("playlists");

        int page = spotifyModel.getPageNavigation().getCurrentPage();
        int limit = spotifyModel.getPageNavigation().getLimit();

        ArrayList<JsonObject> arrayList = new ArrayList<>();

        for (JsonElement item : playlistsObj.getAsJsonArray("items")) {
            JsonObject itemsObj = JsonParser.parseString(String.valueOf(item)).getAsJsonObject();
            arrayList.add(itemsObj);
//            String name = itemsObj.get("name").getAsString();
//            System.out.println(name);
//            String spotifyUrl = itemsObj.getAsJsonObject("external_urls").get("spotify").getAsString();
//            System.out.println(spotifyUrl);
//            System.out.println();
        }

        for (int i = page; i <= page / limit; i++) {

            String name = arrayList.get(i-1).get("name").getAsString();
            System.out.println(name);
            String spotifyUrl = arrayList.get(i-1).getAsJsonObject("external_urls").get("spotify").getAsString();
            System.out.println(spotifyUrl);
        }

        System.out.println("---PAGE " + spotifyModel.getPageNavigation().getCurrentPage() + " OF "
                + spotifyModel.getPageNavigation().getTotalPages() + "---");
    }

    public void displayPlaylists(SpotifyModel spotifyModel) {
        if (response.body().contains("error")) {
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject error = json.getAsJsonObject("error");
            System.out.println(error.get("message").getAsString());
            return;
        }

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject playlistsObj = jsonObject.getAsJsonObject("playlists");

        int page = spotifyModel.getPageNavigation().getCurrentPage();
        int limit = spotifyModel.getPageNavigation().getLimit();

        ArrayList<JsonObject> arrayList = new ArrayList<>();

        for (JsonElement item : playlistsObj.getAsJsonArray("items")) {
            JsonObject itemsObj = JsonParser.parseString(String.valueOf(item)).getAsJsonObject();
            arrayList.add(itemsObj);

//            String name = itemsObj.get("name").getAsString();
//            System.out.println(name);
//            String spotifyUrl = itemsObj.getAsJsonObject("external_urls").get("spotify").getAsString();
//            System.out.println(spotifyUrl);
//            System.out.println();
        }

        for (int i = page; i <= page / limit; i++) {

            String name = arrayList.get(i-1).get("name").getAsString();
            System.out.println(name);
            String spotifyUrl = arrayList.get(i-1).getAsJsonObject("external_urls").get("spotify").getAsString();
            System.out.println(spotifyUrl);
        }

        System.out.println("---PAGE " + spotifyModel.getPageNavigation().getCurrentPage() + " OF "
                + spotifyModel.getPageNavigation().getTotalPages() + "---");
    }
}
