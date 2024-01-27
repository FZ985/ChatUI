package io.im.kit.config;

/**
 * author : JFZ
 * date : 2024/1/26 17:25
 * description :
 */
public enum InputStyle {

    All(0);

    private int type;

    InputStyle(int type) {
        this.type = type;
    }

    public static InputStyle setType(int type) {
        InputStyle[] var1 = values();
        int var2 = var1.length;
        for (int var3 = 0; var3 < var2; ++var3) {
            InputStyle c = var1[var3];
            if (type == c.getType()) {
                return c;
            }
        }
        return All;
    }

    public int getType() {
        return type;
    }
}
