class MinesBox {


    private boolean bomb;
    private int value;
    private boolean shown;

    MinesBox() {
        this.value = 0;
    }

    static MinesBox[][] matrix(int i, int j) {
        MinesBox[][] m = new MinesBox[i][j];

        for (int x = 0; x < i; x++)
            for (int y = 0; y < j; y++)
                m[x][y] = new MinesBox();

        return m;
    }

    void setBomb(boolean bomb) {
        this.bomb = bomb;
    }

    boolean isBomb() {
        return bomb;
    }

    void addValue() {
        this.value++;
    }

    int getValue() {
        return this.value;
    }

    boolean isShown() {
        return shown;
    }

    void setShown() {
        this.shown = true;
    }
}
