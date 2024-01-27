package io.im.lib.model;

/**
 * author : JFZ
 * date : 2024/1/26 14:49
 * description :
 */
public enum ConversationType {
    PRIVATE(1, "private"),
    GROUP(2, "group");

    private final int value;
    private final String name;

    private ConversationType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static ConversationType setValue(int code) {
        ConversationType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ConversationType c = var1[var3];
            if (code == c.getValue()) {
                return c;
            }
        }

        return PRIVATE;
    }
}
