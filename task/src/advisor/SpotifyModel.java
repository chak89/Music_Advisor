package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SpotifyModel {

    private String clientId = "13dd0144eb2d4c89b66faf748a822976";
    private String clientSecret = "ec2984a02cf047c291d7d1fcf1e81b05";
    private String spotifyServerPoint = "https://accounts.spotify.com";
    private String redirect_uri = "http://localhost:8080";
    private String authLink = "";
    private String authCode = "";
    private int redirect_uri_Port;
    private String spotifyApiPath = "";
    private boolean authenticated;
    private boolean exitAccount;
    private String accessToken;
    private HttpJavaClient httpClientApiRequest = null;
    private HttpResponse<String> response;
    private String responseType = "";
    private PageNavigation pageNavigation;
    private boolean insideDisplayPages = true;
    private int limit;

    public class PageNavigation {
        private int limit;
        private String next = " ";
        private int offset;
        private String previous = " ";
        private int total;
        private int currentPage;
        private int totalPages;
        private String selectLimitOffset;

        public PageNavigation(int limit, int offset) {
            this.limit = limit;
            this.offset = offset;
        }

        public String getSelectLimitOffset() {
            return selectLimitOffset;
        }

        public void setSelectLimitOffset(String selectLimitOffset) {
            this.selectLimitOffset = selectLimitOffset;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }

    public PageNavigation getPageNavigation() {
        return this.pageNavigation;
    }

    public SpotifyModel(String spotifyServerPoint, String spotifyApiPath, int limit) {
        this.spotifyServerPoint = spotifyServerPoint;
        this.spotifyApiPath = spotifyApiPath;

        String[] port = this.redirect_uri.split(":");
        this.redirect_uri_Port = Integer.parseInt(port[2]);

        initializeAccount(this.clientId, this.clientSecret);
        this.limit = limit;
    }

    private void initializeAccount(String clientId, String clientSecret) {
        this.authenticated = false;
        this.exitAccount = false;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authCode = "";
        this.accessToken = "";

        this.authLink = this.spotifyServerPoint +
                "/" +
                "authorize?client_id=" + this.clientId +
                "&redirect_uri=" + this.redirect_uri +
                "&response_type=code";
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public boolean isExitAccount() {
        return this.exitAccount;
    }

    public String getAuthCode() {
        return this.authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getAuthLink() {
        return this.authLink;
    }

    public String getSpotifyServerPoint() {
        return this.spotifyServerPoint;
    }

    public int getRedirect_uri_Port() {
        return this.redirect_uri_Port;
    }

    public HttpResponse<String> getResponse() {
        return this.response;
    }

    public String getResponseType() {
        return this.responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public boolean isInsideDisplayPages() {
        return this.insideDisplayPages;
    }

    public void switchCases(String command) {
        this.pageNavigation = new PageNavigation(this.limit, 0);

        String[] split = command.split(" ", 2);

        switch (split[0]) {
            case "new":
                pageNavigation.setSelectLimitOffset("/v1/browse/new-releases?limit=" + this.limit + "&offset=0");
                caseNew();
                break;
            case "featured":
                pageNavigation.setSelectLimitOffset("/v1/browse/featured-playlists?limit=" + this.limit + "&offset=0");
                caseFeatured();
                break;
            case "categories":
                pageNavigation.setSelectLimitOffset("/v1/browse/categories?limit=" + this.limit + "&offset=0");
                caseCategories();
                break;
            case "playlists":
                pageNavigation.setSelectLimitOffset("/v1/browse/categories?limit=" + 50 + "&offset=0");
                String returnedID = casePlaylistsForACategories(split);

                if (returnedID.equals("")) {
                    break;
                }

                pageNavigation.setSelectLimitOffset("/v1/browse/categories/" + returnedID + "/playlists?limit=" + this.limit + "&offset=0");
                casePlaylists(returnedID);
                break;
            case "exit":
                System.out.println("---GOODBYE!---");
                this.exitAccount = true;
                break;
            default:
                break;
        }
    }

    public boolean switchNavigation(String navigate) {
        switch (navigate) {
            case "next":
                if (pageNavigation.getNext() != null) {
                    pageNavigation.setSelectLimitOffset(pageNavigation.getNext());
                    pageNavigation.setOffset(pageNavigation.getOffset() + 1);
                    spotifyTypes(this.responseType);
                    return true;
                } else {
                    System.out.println("No more pages.");
                    return false;
                }
            case "prev":
                if (pageNavigation.getPrevious() != null) {
                    pageNavigation.setSelectLimitOffset(pageNavigation.getPrevious());
                    pageNavigation.setOffset(pageNavigation.getOffset() - 1);
                    spotifyTypes(this.responseType);
                    return true;
                } else {
                    System.out.println("No more pages.");
                    return false;
                }
            case "exit":
                this.insideDisplayPages = false;
                return false;
        }
        return false;
    }

    public void spotifyTypes(String types) {

        String[] split = types.split(" ", 2);

        switch (split[0]) {
            case "categories":
                caseCategories();
                break;
            case "albums":
                caseNew();
                break;
            case "featured":
                caseFeatured();
                break;
            case "playlists":
                casePlaylists(split[1]);
                break;
            default:
                break;
        }
    }

    public void browsePages(HttpResponse<String> response, String responseType) {

        String temp;
        if (responseType.equals("featured")) {
            temp = "playlists";
        } else if (responseType.contains("playlists")) {
            temp = "playlists";
        } else {
            temp = responseType;
        }

        JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject obj = jo.getAsJsonObject(temp);

        //int limit = obj.get("limit").getAsInt();
        int limit = pageNavigation.getLimit();
        int total = obj.get("total").getAsInt();
        int offset = pageNavigation.getOffset();
        String tempAPI = pageNavigation.getSelectLimitOffset();

//        String next = obj.get("next").toString();
//        if (next != null) {
//            next = next.substring(next.indexOf(".com") + 4, next.length() - 1);
//        }

        String next;
        if (pageNavigation.getLimit() + pageNavigation.getOffset() < total) {
            int offsetNext = pageNavigation.offset + pageNavigation.limit;
            next = tempAPI.substring(0, tempAPI.indexOf("offset=") + 7);
            next = next + offsetNext;
        } else {
            next = null;
        }


        //int offset = obj.get("offset").getAsInt();

//        String previous = obj.get("previous").toString();
//        if (previous != null) {
//            previous = previous.substring(previous.indexOf(".com") + 4, previous.length() - 1);
//        }

        String previous;
        if (pageNavigation.getOffset() > 0) {
            int offsetBack = pageNavigation.offset - pageNavigation.limit;
            previous = tempAPI.substring(0, tempAPI.indexOf("offset=") + 7);
            previous = previous + offsetBack;
        } else {
            previous = null;
        }


        int currentPage = (offset / limit) + 1;
        int totalPages = (int) Math.ceil(total / (double) limit);

        pageNavigation.setNext(next);
        pageNavigation.setOffset(offset);
        pageNavigation.setPrevious(previous);
        pageNavigation.setCurrentPage(currentPage);
        pageNavigation.setTotalPages(totalPages);

//        System.out.println("pageNavigation.getNext() : " + pageNavigation.getNext());
//        System.out.println("pageNavigation.getOffset() :" + pageNavigation.getOffset());
//        System.out.println("pageNavigation.getPrevious() :" + pageNavigation.getPrevious());
//        System.out.println("browsePages() -> this.responseType : " + this.responseType);
    }


    private void caseCategories() {
        this.httpClientApiRequest.newHttpRequestToSpotify(this.accessToken, this.spotifyApiPath + pageNavigation.getSelectLimitOffset());
        this.response = httpClientApiRequest.sendHttpRequestToSpotifyCategories();
        this.responseType = "categories";

        browsePages(this.response, this.responseType);
    }

    private void caseNew() {
        this.httpClientApiRequest.newHttpRequestToSpotify(this.accessToken, this.spotifyApiPath + pageNavigation.getSelectLimitOffset());
        this.response = httpClientApiRequest.sendHttpRequestToSpotifyNew();
        this.responseType = "albums";
        browsePages(this.response, this.responseType);
    }

    private void caseFeatured() {
        this.httpClientApiRequest.newHttpRequestToSpotify(this.accessToken, this.spotifyApiPath + pageNavigation.getSelectLimitOffset());
        this.response = httpClientApiRequest.sendHttpRequestToSpotifyFeatured();
        this.responseType = "featured";
        browsePages(this.response, this.responseType);
    }

    private String casePlaylistsForACategories(String[] command) {
        this.httpClientApiRequest.newHttpRequestToSpotify(this.accessToken, this.spotifyApiPath + pageNavigation.getSelectLimitOffset());
        String returnedID = httpClientApiRequest.sendHttpRequestToSpotifyPlaylistAllCategoriesMatch(command[1], this.spotifyApiPath);
        return returnedID;
    }

    private void casePlaylists(String returnedID) {
        System.out.println("casePlaylists() -> command[1] : " + returnedID);

        this.httpClientApiRequest.newHttpRequestToSpotify(this.accessToken, this.spotifyApiPath + pageNavigation.getSelectLimitOffset());
        this.response = httpClientApiRequest.sendHttpRequestToSpotifyPlaylist();
        this.responseType = "playlists" + " " + returnedID;
        browsePages(this.response, this.responseType);
    }

    public void setAccessToken() {
        String encoded64Authorization = BaseEncode64.encodeToString(this.clientId, this.clientSecret);

        System.out.println("making http request for access_token...");
        this.httpClientApiRequest = new HttpJavaClient();
        this.httpClientApiRequest.createClient();
        this.httpClientApiRequest.newHttpRequestAccessToken(this.spotifyServerPoint, this.authCode, this.redirect_uri, encoded64Authorization);
        this.authenticated = this.httpClientApiRequest.sendHttpRequest();
        this.accessToken = this.httpClientApiRequest.access_token;
    }

    static class BaseEncode64 {
        public static String encodeToString(String clientId, String clientSecret) {
            return Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        }

    }
}