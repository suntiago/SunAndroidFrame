package com.suntiago.sunandroidframe.entity;
import android.support.annotation.NonNull;

import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;

/**
 * Created by yu.zai on 2016/2/22.
 */
@Table(name = "user")
public class User {
    @Id
    @Property(column = "user_id")
    @NonNull
    private String userId;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return this.userId;
    }
}
