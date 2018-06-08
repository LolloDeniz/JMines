import java.awt.*;

public class MinesTheme {

    private Color shownBoxColor;
    private Color hiddenBoxColor;
    private Color boxFontColor;
    private Color bombColor;
    private Color borderColor;

    public MinesTheme(Color shownBoxColor, Color hiddenBoxColor, Color boxFontColor, Color bombColor, Color borderColor) {
        this.shownBoxColor = shownBoxColor;
        this.hiddenBoxColor = hiddenBoxColor;
        this.boxFontColor = boxFontColor;
        this.bombColor = bombColor;
        this.borderColor = borderColor;
    }

    public MinesTheme(MinesTheme template) {
        this.shownBoxColor = template.shownBoxColor;
        this.hiddenBoxColor = template.hiddenBoxColor;
        this.boxFontColor = template.boxFontColor;
        this.bombColor = template.bombColor;
        this.borderColor = template.borderColor;
    }

    public Color getShownBoxColor() {
        return shownBoxColor;
    }

    public void setShownBoxColor(Color shownBoxColor) {
        this.shownBoxColor = shownBoxColor;
    }

    public Color getHiddenBoxColor() {
        return hiddenBoxColor;
    }

    public void setHiddenBoxColor(Color hiddenBoxColor) {
        this.hiddenBoxColor = hiddenBoxColor;
    }

    public Color getBoxFontColor() {
        return boxFontColor;
    }

    public void setBoxFontColor(Color boxFontColor) {
        this.boxFontColor = boxFontColor;
    }

    public Color getBombColor() {
        return bombColor;
    }

    public void setBombColor(Color bombColor) {
        this.bombColor = bombColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }
}
