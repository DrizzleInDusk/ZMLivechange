package com.android.zmlive.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.zmlive.R;
import com.android.zmlive.adapter.GuideViewPagerAdapter;
import com.android.zmlive.tool.utils.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {
    private ViewPager viewPager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<ImageView> imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        viewPager = (ViewPager) findViewById(R.id.guide_viewpage);
        imageViews = new ArrayList<>();

        sharedPreferences = getSharedPreferences("isFirst", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("first", true)) {
            editor.putBoolean("first", false);
            editor.commit();

            //// TODO: 2017/2/9  liuzai benye
        } else {

            //// TODO: 2017/2/9  tiaodao xiayiye
            startActivity(new Intent(GuideActivity.this,WelcomeActivity.class));
            finish();

        }




        ImageView imageView1 = new ImageView(this);
        imageView1.setImageResource(R.mipmap.yindao1);
        ImageView imageView2 = new ImageView(this);
        imageView2.setImageResource(R.mipmap.yindao2);
        ImageView imageView3 = new ImageView(this);
        imageView3.setImageResource(R.mipmap.yindao3);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this,WelcomeActivity.class));
                finish();
            }
        });
        imageViews.add(imageView1);
        imageViews.add(imageView2);
        imageViews.add(imageView3);




        GuideViewPagerAdapter guideViewPagerAdapter=new GuideViewPagerAdapter(imageViews);
        viewPager.setAdapter(guideViewPagerAdapter);








    }


}
