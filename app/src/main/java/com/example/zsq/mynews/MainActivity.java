package com.example.zsq.mynews;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.zsq.mynews.adapter.NewsFragmentPagerAdpter;
import com.example.zsq.mynews.bean.NewsClassify;
import com.example.zsq.mynews.fragment.NewsFragment;
import com.example.zsq.mynews.tool.BaseTools;
import com.example.zsq.mynews.tool.Constans;
import com.example.zsq.mynews.transforms.RotateUpTransformer;
import com.example.zsq.mynews.view.AddChannel;
import com.example.zsq.mynews.view.CircleImageView;
import com.example.zsq.mynews.view.ColumnHorizontalScrollView;
import com.example.zsq.mynews.view.GuanYu;
import com.example.zsq.mynews.water.WaterScreenView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

public class MainActivity extends AppCompatActivity{
    private ArrayList<NewsClassify> adddatas = new ArrayList<>();
    //判断是否是第一次进入界面
    public boolean dyc = true;
    //自定义的视图
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    LinearLayout mRadioGroup_content;
    LinearLayout ll_more_columns;
    RelativeLayout rl_column;
    private ViewPager mViewPager;
    private ImageView button_more_colums,leftImg,rightImg;
    //新闻的分类列表
    private ArrayList<NewsClassify> newsClassify = new ArrayList<>();
    //当前选中的栏目
    private int columnSelectIndex = 0;
    //左阴影部分
    private ImageView shade_left;
    //右阴影部分
    private ImageView shade_right;
    //屏幕的宽度
    private int mScreenWidth = 0;
    //item宽度
    private int mItemWidth = 0;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    //添加标题栏
    private Toolbar toolBar;
    //侧滑栏
    private DrawerLayout drawerLayout;
    //添加侧滑动画
    private ActionBarDrawerToggle drawerToggle;

    //添加夜间模式
    private TextView tv_yjms,tv_sz;
    private boolean tag=true;
    private LinearLayout dl_menu,ll_zt,ll_pdl;
    private WaterScreenView waterScreenView;

    //添加关于
    private TextView tv_gy;

    //登录头像
    private CircleImageView civ;
    private TextView tv_djdl,tv_zxdl;
    //声明第三方平台引用
    private Platform qq;
    //// 声明用户名和用户头像地址引用
    private String userName,userIconUrl;
    // Handler消息类型
    private static final int MSG_AUTH_CANCEL = 0;
    private static final int MSG_AUTH_ERROR = 1;
    private static final int MSG_AUTH_COMPLETE = 2;

    private RequestQueue rq;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取屏幕宽度
        mScreenWidth = BaseTools.getWindowsWidth(this);
        //一个item宽度为屏幕的1/7
        mItemWidth = mScreenWidth/7;

        rq= Volley.newRequestQueue(this);

        initShareSdk();
        initView();

    }
    // 初始化分享SDK
    private void initShareSdk() {
        ShareSDK.initSDK(this);
        qq = ShareSDK.getPlatform(QQ.NAME);
        // 如果已经授权则先取消授权
        if (qq.isAuthValid())
            qq.removeAccount(true);
    }
    /*初始化Layout控件*/
    private void initView() {
        mColumnHorizontalScrollView = (ColumnHorizontalScrollView)
                findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout)
                findViewById(R.id.mRadioGroup_content);
        ll_more_columns = (LinearLayout) findViewById(R.id.ll_more_colums);
        rl_column = (RelativeLayout) findViewById(R.id.rl_colum);
        button_more_colums = (ImageView) findViewById(R.id.button_more_colums);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mViewPager.setOnPageChangeListener(pageListener);
        shade_left = (ImageView) findViewById(R.id.shade_left);
        shade_right = (ImageView) findViewById(R.id.shade_right);

        //夜间模式
        tv_yjms = (TextView) findViewById(R.id.tv_yjms);
        tv_gy = (TextView) findViewById(R.id.tv_gy);
        dl_menu = (LinearLayout) findViewById(R.id.dl_menu);
        ll_zt = (LinearLayout) findViewById(R.id.ll_zt);
        ll_pdl = (LinearLayout) findViewById(R.id.ll_pdl);
        tv_sz = (TextView) findViewById(R.id.tv_sz);
        waterScreenView = (WaterScreenView) findViewById(R.id.waterScreenView);
        leftImg = (ImageView) findViewById(R.id.shade_left);
        rightImg = (ImageView) findViewById(R.id.shade_right);

        //登录
        civ = (CircleImageView) findViewById(R.id.civ);
        tv_djdl = (TextView) findViewById(R.id.tv_djdl);
        tv_zxdl = (TextView) findViewById(R.id.tv_zxdl);


        //加载更多频道按钮
        button_more_colums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddChannel.class);
                intent.putExtra("datas",newsClassify);
                selectTab(0);
                startActivityForResult(intent,1);
            }
        });
        setChangeView();

        //声明标题栏
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle("MyNews");
        toolBar.setTitleTextColor(getResources().getColor(R.color.white));

        //声明侧滑栏
        drawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout);

        //为侧滑栏标志添加动画
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolBar,0,0);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerView.setClickable(true);
            }
        });
    }

    /**
     * 登录事件
     * */
    //点击头像登录事件
    public void login(View view){
        if (qq.isAuthValid()){
            Toast.makeText(MainActivity.this,"你已经登录！",Toast.LENGTH_SHORT).show();
            return;
        }
        //登录方法
        qq.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                //获取用户名和用户头像地址
                userName = platform.getDb().getUserName();
                userIconUrl = platform.getDb().getUserIcon();
                handler.sendEmptyMessage(MSG_AUTH_COMPLETE);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                handler.sendEmptyMessage(MSG_AUTH_ERROR);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                handler.sendEmptyMessage(MSG_AUTH_CANCEL);
            }
        });
        qq.SSOSetting(false);
        //authorize与showUser单独调用一个即可
        qq.authorize();//单独授权,OnComplete返回的hashmap是空的
    }

    // 注销按钮点击事件
    public void logout(View view) {
        if (qq.isAuthValid()) {
            tv_djdl.setText("点击头像登录");
            qq.removeAccount(true);
            //隐藏注销按钮
            tv_zxdl.setVisibility(View.GONE);
            // 将头像还原
            civ.setImageResource(R.mipmap.morentouxiang);
            Toast.makeText(this, "成功退出登录", Toast.LENGTH_SHORT).show();
        }
    }

    // 登陆后使用Handler更新用户头像和用户名，并更新UI
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTH_COMPLETE:
                    getUserIcon();
                    // 显示用户名
                    tv_djdl.setText(userName);
                    //显示注销按钮
                    tv_zxdl.setVisibility(View.VISIBLE);

                    Toast.makeText(MainActivity.this, "授权成功", Toast.LENGTH_LONG).show();
                    break;
                case MSG_AUTH_ERROR:
                    Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_LONG).show();
                    break;
                case MSG_AUTH_CANCEL:
                    Toast.makeText(MainActivity.this, "授权已经被取消", Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    // 获取头像
    private void getUserIcon(){
        ImageRequest imageRequest = new ImageRequest(userIconUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        civ.setImageBitmap(bitmap);
                    }
                },0,0, Bitmap.Config.RGB_565,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("TAG",volleyError.toString() );
            }
        }
        );
        rq.add(imageRequest);
    }

    // 程序关闭时清理缓存
    @Override
    protected void onDestroy() {
        ShareSDK.stopSDK(this);
        super.onDestroy();
    }

    //重写带返回值的跳转方法
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode==2){
            adddatas = (ArrayList<NewsClassify>) data.getSerializableExtra("addChannel");
            setChangeView();
        }
    }


    /**
     * 当栏目发生变化时调用
     */
    private void setChangeView()
    {
        initColumnData(adddatas);
        initTabColumn();
        initFragment();
    }
    /**
     * 获取Column栏目数据
     */
    private void initColumnData(ArrayList list)
    {
        if (dyc ==true){
            for (int i=0;i<10;i++) {
                newsClassify.add(Constans.getData().get(i));
            }
            dyc=false;
        }else {
            newsClassify = list;
        }
    }
    /**
     * 初始化栏目项
     */
    private void initTabColumn()
    {
        mRadioGroup_content.removeAllViews();
        //刷新界面,清除集合
        fragments.clear();
        int count = newsClassify.size();
        mColumnHorizontalScrollView.setParam(this,mScreenWidth,mRadioGroup_content,
                shade_left,shade_right,ll_more_columns,rl_column);
        for (int i = 0;i<count;i++)
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 10;
            params.rightMargin = 10;
            //创建TextView对象，并对其进行属性设置
            TextView localTextView = new TextView(this);
            localTextView.setTextAppearance(this,R.style.top_category_scroll_view_item_text);

            localTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
            localTextView.setGravity(Gravity.CENTER);
            localTextView.setPadding(5,0,5,0);
            localTextView.setId(i);
            localTextView.setText(newsClassify.get(i).getTitle());
            localTextView.setTextColor(getResources().getColorStateList(
                    R.color.top_category_scroll_text_color_day
            ));
            //给TextView设置选中状态
            if (columnSelectIndex == i)
            {
                localTextView.setSelected(true);
            }
            //添加监听
            localTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0;i<mRadioGroup_content.getChildCount();i++)
                    {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v)
                        {
                            localView.setSelected(false);
                        }else
                        {
                            localView.setSelected(true);
                            mViewPager.setCurrentItem(i);
                        }
                    }
                }
            });
            mRadioGroup_content.addView(localTextView,i,params);
        }
    }

    /**
     * 选择的Column里面的Tab
     */
    private void selectTab(int tab_position)
    {
        columnSelectIndex = tab_position;
        for (int i =0 ;i<mRadioGroup_content.getChildCount();i++)
        {
            //拿到被选中的视图
            View checkView = mRadioGroup_content.getChildAt(tab_position);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l+k/2-mScreenWidth/2;
            //滚动View
            mColumnHorizontalScrollView.smoothScrollTo(i2,0);
        }
        //判断是否选中
        for (int j = 0;j<mRadioGroup_content.getChildCount();j++)
        {
            View checkView = mRadioGroup_content.getChildAt(j);
            boolean isCheck;
            if (j == tab_position)
            {
                isCheck = true;
            }else {
                isCheck = false;
            }
            checkView.setSelected(isCheck);
        }
    }
    /**
     * 初始化Fragment
     */
    private void initFragment()
    {
        int count = newsClassify.size();
        for (int i = 0;i<count;i++)
        {
            Bundle data = new Bundle();
            data.putString("text",newsClassify.get(i).getUrlId());
            //创建Fragment对象
            NewsFragment newFragment = new NewsFragment();
            newFragment.setArguments(data);
            fragments.add(newFragment);
        }
        //创建Fragment适配器
        NewsFragmentPagerAdpter mAdapter = new NewsFragmentPagerAdpter(
                getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(mAdapter);
        //给ViewPager添加动画
        mViewPager.setPageTransformer(true, new RotateUpTransformer());
    }
    /**
     * ViewPager切换监听方法
     */
    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }
        @Override
        public void onPageSelected(int i) {
            //设置选中的View
            mViewPager.setCurrentItem(i);
            selectTab(i);
        }
        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    //设置主题
    public void setZhuTi(View view) {
        if (tag == true) {
            tag = false;
            tv_yjms.setText("白天模式");
            dl_menu.setBackgroundColor(getResources().getColor(R.color.menu_item_text_normal));
            tv_yjms.setTextColor(getResources().getColor(R.color.white));
            tv_gy.setTextColor(getResources().getColor(R.color.white));
            ll_zt.setBackgroundColor(getResources().getColor(R.color.subscribe_item_pressed_stroke_night));
            ll_pdl.setBackgroundColor(getResources().getColor(R.color.subscribe_item_pressed_stroke_night));
            toolBar.setBackgroundColor(getResources().getColor(R.color.feed_user_more_bg_night));
            tv_sz.setTextColor(getResources().getColor(R.color.white));
            tv_djdl.setTextColor(getResources().getColor(R.color.white));
            tv_zxdl.setTextColor(getResources().getColor(R.color.white));
            waterScreenView.setBackgroundResource(R.mipmap.yejianbeijin);
            leftImg.setBackgroundResource(R.drawable.channel_leftblock_night);
            rightImg.setBackgroundResource(R.drawable.channel_rightblock_night);
        } else {
            tag = true;
            tv_yjms.setText("夜间模式");
            dl_menu.setBackgroundColor(getResources().getColor(R.color.white));
            tv_yjms.setTextColor(getResources().getColor(R.color.subscribe_item_pressed_stroke_night));
            tv_gy.setTextColor(getResources().getColor(R.color.subscribe_item_pressed_stroke_night));
            ll_pdl.setBackgroundColor(getResources().getColor(R.color.white));
            ll_zt.setBackgroundColor(getResources().getColor(R.color.white));
            toolBar.setBackgroundColor(getResources().getColor(R.color.toolbar));
            tv_sz.setTextColor(getResources().getColor(R.color.subscribe_item_pressed_stroke_night));
            tv_djdl.setTextColor(getResources().getColor(R.color.subscribe_item_pressed_stroke_night));
            tv_zxdl.setTextColor(getResources().getColor(R.color.subscribe_item_pressed_stroke_night));
            waterScreenView.setBackgroundResource(R.drawable.water_scene_background);
            leftImg.setBackgroundResource(R.drawable.channel_leftblock);
            rightImg.setBackgroundResource(R.drawable.channel_rightblock);
        }
        //关闭drawerLayout
        drawerLayout.closeDrawers();
    }
    public void guanyu(View view){
        //跳转
        Intent intent = new Intent(MainActivity.this, GuanYu.class);
        startActivity(intent);
        //关闭drawerLayout
        drawerLayout.closeDrawers();
    }
}

