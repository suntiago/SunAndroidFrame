package com.suntiago.sunandroidframe;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
        initDb();
//        insert();
        query();
        RequestQueue mQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest("http://www.baidu.com",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG","response:" + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", "response:" + error.getMessage(), error);
                    }
                });
        mQueue.add(stringRequest);
        mQueue.start();
    }
    void initDb() {
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
    }
    void insert() {
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
