package com.example.zsq.mynews.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zsq.mynews.R;
import com.example.zsq.mynews.adapter.StaggeredAdapter;
import com.example.zsq.mynews.bean.NewsClassify;
import com.example.zsq.mynews.tool.Constans;

import java.util.ArrayList;

/**
 * Created by ZSQ on 2016/10/26.
 */
public class AddChannel extends AppCompatActivity{
    //拿栏目数据的集合
    private ArrayList<NewsClassify> listDatas;
    //返回按钮
    private TextView tv_addchannelfh;
    //添加内容
    private RecyclerView recyclerViewAdd,recyclerViewRemove;
    private ArrayList<NewsClassify> mdatasAdd,mdatasRemove;
    private StaggeredAdapter mStaggeredAdapterAdd,mStaggeredAdapterRemove;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addchannel);
        init();
        //点击返回按钮关闭本界面
        tv_addchannelfh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //添加内容
        initData();
        mStaggeredAdapterRemove = new StaggeredAdapter(this,mdatasRemove);
        mStaggeredAdapterAdd = new StaggeredAdapter(this,mdatasAdd);

        recyclerViewAdd.setLayoutManager(new StaggeredGridLayoutManager(5,
                StaggeredGridLayoutManager.VERTICAL));
        recyclerViewRemove.setLayoutManager(new StaggeredGridLayoutManager(5,
                StaggeredGridLayoutManager.VERTICAL));
        recyclerViewAdd.setAdapter(mStaggeredAdapterAdd);
        recyclerViewRemove.setAdapter(mStaggeredAdapterRemove);
        //设置动画
        recyclerViewAdd.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRemove.setItemAnimator(new DefaultItemAnimator());

        //点击事件
        initEvent();

    }


    public void init(){
        //返回按钮
        tv_addchannelfh = (TextView) findViewById(R.id.tv_addchannelfh);
        recyclerViewAdd = (RecyclerView) findViewById(R.id.recyclerViewAdd);
        recyclerViewRemove = (RecyclerView) findViewById(R.id.recyclerViewRemove);
    }

    //点击事件
    private void initEvent() {
        mStaggeredAdapterAdd.setOnItemClickLitener(new StaggeredAdapter.OnItemClickLitener() {
            //点击事件
            @Override
            public void onItemClick(View view, int position) {
                //先加后删
                mStaggeredAdapterRemove.addData(mdatasAdd.get(position));
                mStaggeredAdapterAdd.removeData(position);
                Intent intent = new Intent();
                intent.putExtra("addChannel",mdatasAdd);
                setResult(2,intent);
            }

            //长按事件
            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(AddChannel.this,"长按没有用哦！",Toast.LENGTH_SHORT).show();
            }
        });
        mStaggeredAdapterRemove.setOnItemClickLitener(new StaggeredAdapter.OnItemClickLitener() {
            //点击事件
            @Override
            public void onItemClick(View view, int position) {
                //先加后删
                mStaggeredAdapterAdd.addData(mdatasRemove.get(position));
                mStaggeredAdapterRemove.removeData(position);
                Intent intent = new Intent();
                intent.putExtra("addChannel",mdatasAdd);
                setResult(2,intent);
            }

            //长按事件
            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(AddChannel.this,"长按没有用哦！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    
    private void initData() {
        //拿到新闻类所有的频道
        ArrayList<NewsClassify> allNews = Constans.getData();
        mdatasAdd = new ArrayList<>();
        mdatasRemove = new ArrayList<>();
        //给添加频道集合传值
        Intent intent = getIntent();
        listDatas = (ArrayList<NewsClassify>) intent.getSerializableExtra("datas");
        mdatasAdd = listDatas;
        //给删除频道集合传值
        for (int i=0;i<allNews.size();i++){
            for (int j=0;j<mdatasAdd.size();j++){
                if (allNews.get(i).getTitle().equals(mdatasAdd.get(j).getTitle())){
                    allNews.remove(i);
                }
            }
        }
        mdatasRemove = allNews;
    }
}
