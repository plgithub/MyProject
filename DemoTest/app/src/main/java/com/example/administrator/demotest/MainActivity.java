package com.example.administrator.demotest;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.administrator.view.TopBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.topbar)
    TopBar topbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        topbar.setOnTopBarClick(new TopBar.topbarClickListener() {
            @Override
            public void leftClick() {
                Snackbar.make(toolbar, "点击了左边的按钮", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void rightClick() {
                Snackbar.make(toolbar, "点击了右边的的按钮", Snackbar.LENGTH_LONG).show();
            }
        });


    }


}
