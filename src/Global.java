import java.awt.*;

public class Global {
    private static Global gbl = new Global();

    public static Global getgbl() {
        return gbl;
    }

    private Global() {
    }

    public enum osEnum{
        WINDOWS,
        MAC,
        UNIX,
        SOLARIS,
        NOT_FOUND       //default
    }

    public osEnum os=osEnum.NOT_FOUND;
    public int nWindows = 0;
    public boolean debug = false;

    public int gridDim=10;
    public double bombRate=0.1;

    public enum themes{
        light,
        dark
    }

    ///////COLORS///////

    //LIGHT THEME//
    private Color lightShownBoxColor = new Color(224, 224, 224);
    private Color lightHiddenBoxColor = new Color(184, 184, 184);
    private Color lightBoxFontColor = new Color(0, 0, 0);
    private Color lightBombColor = new Color(255, 172, 172);
    private Color lightBorderColor = new Color(255, 255, 255);

    //DARK THEME//
    private Color darkShownBoxColor = new Color(57, 57, 57);
    private Color darkHiddenBoxColor = new Color(88, 88, 88);
    private Color darkBoxFontColor = new Color(172, 172, 172);
    private Color darkBombColor = new Color(117, 41, 41);
    private Color darkBorderColor = new Color(33, 33, 33);

    ///////COLORS///////

    public MinesTheme light = new MinesTheme(lightShownBoxColor, lightHiddenBoxColor, lightBoxFontColor, lightBombColor, lightBorderColor);
    public MinesTheme dark = new MinesTheme(darkShownBoxColor, darkHiddenBoxColor, darkBoxFontColor, darkBombColor, darkBorderColor);

}
