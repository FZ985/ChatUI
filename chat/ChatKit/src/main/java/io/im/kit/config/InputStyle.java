package io.im.kit.config;

/**
 * author : JFZ
 * date : 2024/1/26 17:25
 * description :
 */
public enum InputStyle {

    All(0),

    Add(1),

    Voice(2),

    Emoji(3),

    Voice_Emoji(4),

    Voice_Add(5),

    Emoji_Add(6);


    private final int type;

    InputStyle(int type) {
        this.type = type;
    }

    public static InputStyle setType(int type) {
        InputStyle[] var1 = values();
        for (InputStyle c : var1) {
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
