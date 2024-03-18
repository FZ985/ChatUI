package io.im.kit.config.enums;

/**
 * author : JFZ
 * date : 2024/1/31 13:41
 * description :
 */
public enum FontSize {

    None(1f),

    Small(0.85f),

    Large(1.3f),

    Largest(1.6f);

    private final float type;

    FontSize(float type) {
        this.type = type;
    }

    public float getType() {
        return type;
    }

    public static FontSize setType(float type) {
        FontSize[] var1 = values();
        for (FontSize c : var1) {
            if (type == c.getType()) {
                return c;
            }
        }
        return None;
    }
}
