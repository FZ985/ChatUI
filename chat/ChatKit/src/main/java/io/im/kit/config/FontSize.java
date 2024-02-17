package io.im.kit.config;

/**
 * author : JFZ
 * date : 2024/1/31 13:41
 * description :
 */
public enum FontSize {

    None(1f),

    Small(0.85f),

    Large(1.5f),

    MaxLarge(2f);

    private final float type;

    FontSize(float type) {
        this.type = type;
    }

    public float getType() {
        return type;
    }
}
