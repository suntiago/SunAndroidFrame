package com.suntiago.sunandroidframe;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.suntiago.sunandroidframe.entity.User;

import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.database.DaoConfig;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    KJDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        insert();
        query();
    }

    void insert() {
        DaoConfig d =new DaoConfig();
        d.setContext(this);
        d.setDbName("sun");
        d.setDbUpdateListener(new KJDB.DbUpdateListener() {
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        });
        d.setDbVersion(1);
        d.setDebug(true);
        db= KJDB.create(d);
        User user = new User(); //warn: The ugc must have id field or @ID annotate
        user.setUserId("1231312");
        db.save(user);
    }
    void query(){
        String where = "user_id = \"1231312\"";
        List<User> l = db.findAllByWhere(User.class, where);
        for (User u: l) {
            Log.e(TAG, u.toString());
        }
    }
}
