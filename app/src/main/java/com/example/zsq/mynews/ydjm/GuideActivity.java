package com.example.zsq.mynews.ydjm;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.zsq.mynews.MainActivity;
import com.example.zsq.mynews.R;
import com.example.zsq.mynews.transforms.StackTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZSQ on 2016/10/28.
 */
public class GuideActivity extends Activity{
    private ViewPager viewPager;
    private List<View> views;
    private int[] images;
    private Button btn_kqxw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initViews();
    }
    /**
     * 初始化View Pager
     * */
    private void initViews(){
        btn_kqxw = (Button) findViewById(R.id.btn_kqxw);
        viewPager = (ViewPager) findViewById(R.id.viewPage);
        images = new int[]{R.mipmap.xwyd1,R.mipmap.xwyd2,
                R.mipmap.xwyd3,R.mipmap.xwyd4};
        views = new ArrayList<>();
        for (int i=0;i<images.length;i++){
            ImageView imageView = new ImageView(this);
            if (i!=images.length){
                imageView.setImageResource(images[i]);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            views.add(imageView);
        }
        //创建适配器，初始化数据
        final GuidePagerAdapter adapter = new GuidePagerAdapter(views);
        viewPager.setAdapter(adapter);
        //给View Pager设置动画
        viewPager.setPageTransformer(true,new StackTransformer());
        //监听View Pager的页面改变
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //如果View Pager选中最后一个界面，就出现按钮点击可跳转到Activity
                if (position==adapter.getCount()-1){
                    btn_kqxw.setVisibility(View.VISIBLE);
                    btn_kqxw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(GuideActivity.this,MainActivity.class));
                            finish();
                            overridePendingTransition(R.anim.activity_enter,R.anim.ativity_exit);
                        }
                    });
                }else {
                    btn_kqxw.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
