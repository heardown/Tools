package me.winds.demo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import me.winds.demo.widget.CircularProgressView;

import com.sayweee.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    CircleProgressView cpvIndicator;
    CircularProgressView mIndicator;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Logger.f("demo/log").i("aaa");

    }

    private void initView() {
        cpvIndicator = findViewById(R.id.cpv_indicator);
        mIndicator = findViewById(R.id.mIndicator);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    public void click(View view) {
        startActivity(new Intent(this, DemoActivity.class));

        Logger.s("  aaa  ").enable(true).i("Demo", "1", 2, 3, 4, 5, 6, 7);
        Logger.s("  ").i("Demo", "1", 2, 3, 4, 5, 6, 7);
        Logger.f("demo").enable(false).i("Demo", "1", 2, 3, 4, 5, 6, 7);
        Logger.f("demo/log").enable(false).s(" ").i("Demo", "1", 2, 3, 4, 5, 6, 7);
        Logger.f("demo/log").s(" ttt ").i("Demo", "1", 2, 3, 4, 5, 6, 7);
        Logger.f("demo/log/b").enable(true).s("\n").i("Demo", "1", 2, 3, 4, 5, 6, 7);


//        Logger.i(getFileDate("log_-2019-08-22.data"));
    }

    public void start(View view) {
        cpvIndicator.setCurrentValue(88, "+88");
        mIndicator.setCurrentValue(100, "+99", true);
    }


    private long getFileDate(String name) {
        if (!TextUtils.isEmpty(name)) {
            try {
                int i = name.lastIndexOf(".");
                Logger.i("getFileDate", i);
                String s = name.substring(i - 10, i);
                Logger.i("getFileDate", s);
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(s);
                return date.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
