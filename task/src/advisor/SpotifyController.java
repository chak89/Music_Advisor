package advisor;

public class SpotifyController {

    private SpotifyModel model;
    private SpotifyView view;

    public SpotifyController(SpotifyModel model, SpotifyView view) {
        this.model = model;
        this.view = view;
    }

    public boolean getSpotifyIsAuthenticated() {
        return model.isAuthenticated();
    }

    public boolean getSpotifyIsExitAccount() {
        return model.isExitAccount();
    }

    public void setSpotifyAuthCode(String authCode) {
        model.setAuthCode(authCode);
    }

    public String getSpotifyAuthCode() {
        return model.getAuthCode();
    }

    public String getSpotifyAuthLink(){
        return model.getAuthLink();
    }

    public void spotifySwitchCases(String command) {
        model.switchCases(command);
    }

    public boolean spotifySwitchNavigation(String navigate) {
        return model.switchNavigation(navigate);
    }

    public void setSpotifyAccessToken() {
        model.setAccessToken();
    }

    public String getSpotifyServerPoint() {
        return model.getSpotifyServerPoint();
    }

    public int getSpotifyRedirect_uri_Port() {
        return model.getRedirect_uri_Port();
    }

    public boolean isSpotifyInsideDisplayPages () {
        return model.isInsideDisplayPages();
    }


    public void updateView(){
        this.view.setResponse(this.model.getResponse());
        view.displayResponse(model);
    }

}