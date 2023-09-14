package path;

public class Paths {

    public static final String ASSET_PATH = "assets/";

    public static String getImagePath(char piece){
        return Character.isUpperCase(piece)? ASSET_PATH+"w"+Character.toString(piece).toLowerCase()+".png":ASSET_PATH+"b"+Character.toString(piece).toLowerCase()+".png";
    }

}
