package path;

public class Paths {

    public static final String ASSET_PATH = "assets/";

    public static String getImagePath(char piece){
        return ASSET_PATH+Character.toString(piece)+".png";
    }

}
