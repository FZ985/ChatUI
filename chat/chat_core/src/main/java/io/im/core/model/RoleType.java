package io.im.core.model;

/**
 * author : JFZ
 * date : 2026/9/20 11:01
 * description :权限类型
 */
public enum RoleType {
    //普通人
    TOLE_NORMAL(0, "NORMAL"),
    //创建者
    TOLE_CREATOR(1, "CREATOR"),
    //管理员
    TOLE_MANAGER(2, "MANAGER");

    private final int value;
    private final String name;

    RoleType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static RoleType setValue(int code) {
        RoleType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            RoleType c = var1[var3];
            if (code == c.getValue()) {
                return c;
            }
        }

        return TOLE_NORMAL;
    }
}
