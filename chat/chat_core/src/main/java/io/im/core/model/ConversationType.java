package io.im.core.model;

/**
 * author : JFZ
 * date : 2024/1/26 14:49
 * description :
 */
public enum ConversationType {
    //未知
    TYPE_UNKNOWN(0, "UNKNOWN"),
    //单聊
    TYPE_P2P(1, "P2P"),
    //群聊
    TYPE_GROUP(2, "GROUP");

    private final int value;
    private final String name;

    ConversationType(int value, String name) {
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

        return TYPE_P2P;
    }
}
