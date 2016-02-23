package com.suntiago.sunandroidframe.login;

/**
 * Created by yu.zai on 2016/2/22.
 */
public enum AccessTokenSource  {
    PHONE(0),
    THIRD_PARTY(1),
    BBS(2),
    TEST(3),
    GESTURE(4),
    SOUND(5),
    FINGER_PRINT(6),
    FACE(7);
    private int source;
    AccessTokenSource(int source) {
        this.source = source;
    }
}
