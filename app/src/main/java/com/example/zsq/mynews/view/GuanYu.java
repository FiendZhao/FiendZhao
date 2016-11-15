package com.example.zsq.mynews.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.zsq.mynews.R;

/**
 * Created by ZSQ on 2016/10/27.
 */
public class GuanYu extends AppCompatActivity {
    private TextView tv_gyfh;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guanyu);
        tv_gyfh = (TextView) findViewById(R.id.tv_gyfh);
        tv_gyfh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
