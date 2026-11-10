package com.example.web.enums;

public enum RoleTypeEnum {
    /**
     * 管理员
     */
    管理员(1),

    /**
     * 用户
     */
    用户(2);

    private final int index;

    RoleTypeEnum(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }

    public static RoleTypeEnum getEnum(Integer v) {
        if (v == null) {
            return values()[0];
        }
        for (RoleTypeEnum myEnum : values()) {
            if (myEnum.index() == v) {
                return myEnum;
            }
        }
        return values()[0];
    }

    public static RoleTypeEnum fromIndex(Integer index) {
        return getEnum(index);
    }
}
