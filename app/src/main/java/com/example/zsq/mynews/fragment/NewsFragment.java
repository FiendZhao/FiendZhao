package com.example.zsq.mynews.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.zsq.mynews.adapter.LvItemAdapter;
import com.example.zsq.mynews.R;
import com.example.zsq.mynews.bean.DataEntity;
import com.example.zsq.mynews.tool.Url;
import com.example.zsq.mynews.view.Webpage;
import com.google.gson.Gson;
import com.grumoon.pulllistview.PullListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adamlambert on 2016/10/19.
 */
public class NewsFragment extends Fragment{

    private String text;
    private LvItemAdapter myAdapter;
    private List<DataEntity> datas;
    private List<DataEntity> olddatas = new ArrayList<>();
    private int index = 20;
    private int index2 = 20;
    private boolean On_Off =true;
    private PullListView plv;
    private Context context;

    @Override
    public void onAttach(Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        text = args != null ?args.getString("text"):"";
        Log.e("textttttt","===========>"+text);
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment,null);
        plv = (PullListView) view.findViewById(R.id.pull_down_view);

        if (On_Off==true) {
            loadData();
            //执行刷新
            plv.performRefresh();
            On_Off=false;
        }
        iniview();
        myAdapter = new LvItemAdapter(olddatas,context);
        plv.setAdapter(myAdapter);

        listViewListener();
        return view;
    }

    //刷新新闻内容
    public void loadData(){
        if (network()) {
            volleyGetJson(index);
        }else {
            Toast.makeText(context,"网络有问题！请检查网络",Toast.LENGTH_SHORT).show();
        }
        plv.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (datas!=null) {
                    olddatas.addAll(datas);
                }
                myAdapter.notifyDataSetChanged();
                plv.refreshComplete();
                plv.getMoreComplete();
                //判断数据是否为空，不为空才调用此方法清理数据
                if (datas!=null){
                 datas.clear();
                }
            }
        },2000);
    }
    private int Index_more;
    private boolean Index_flush;

    public void iniview(){
        //下拉刷新
        plv.setOnRefreshListener(new PullListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (network()) {
                    volleyGetJson(index);
                }else {
                    Toast.makeText(context,"网络有问题！请检查网络",Toast.LENGTH_SHORT).show();
                }
                plv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                         //防止断网重连后无显示
                        if (Index_flush==true&&olddatas.size()==0){
                            olddatas.addAll(datas);
                        }
                        myAdapter.notifyDataSetChanged();
                        plv.refreshComplete();
                        plv.getMoreComplete();
                        if (datas!=null) {
                            datas.clear();
                        }
                    }
                },2000);
            }
        });

        //上拉加载
        plv.setOnGetMoreListener(new PullListView.OnGetMoreListener() {
            @Override
            public void onGetMore() {
                //获取新数据，通过累加index而得到新数据
                if (network()) {
                    index+=index2;
                    volleyGetJson(index);
                }else {
                    Toast.makeText(context,"网络有问题！请检查网络",Toast.LENGTH_SHORT).show();
                }
                plv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //判断第二次或更多次加载时中间某个时刻断网而造成的网址index重复添加了相同的内容
                        if ((Index_more+20)!=index) {
                            olddatas.addAll(datas);
                        }
                        myAdapter.notifyDataSetChanged();
                        plv.refreshComplete();
                        plv.getMoreComplete();
                        //清除上一次集合里的内容
                        datas.clear();
                    }
                },2000);
            }
        });
    }

    //传递内容
    private void volleyGetJson(int indexs) {
        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jor = new JsonObjectRequest(Url.TopUrl+text+"/"+indexs+Url.endUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                gson(response);
                Index_flush=true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAGTTT", "" + error);
                Toast.makeText(context,
                        "网络有问题！请检查网络",
                        Toast.LENGTH_SHORT).show();
                //防止当次获取网络失败时，下次重新获取到网络时会有重复
                if (index>(index-20)) {
                    index -= index2;
                    Index_more=index;
                }
                Index_flush=false;
                plv.refreshComplete();
                plv.getMoreComplete();
            }
        });
        mQueue.add(jor);
    }

    //用Gson解析字符串
    private void gson(JSONObject jsonObject){
        datas = new ArrayList<>();
        Gson gson=new Gson();
        try {
            JSONArray ja = jsonObject.getJSONArray(text);
            for (int i=0;i<ja.length();i++){
                datas.add(gson.fromJson(ja.getJSONObject(i).toString(),
                        DataEntity.class));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //跳转新闻详情界面
    public void listViewListener(){
        plv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(getActivity(),Webpage.class);
                intent.putExtra("webpage",olddatas.get(position-1).getUrl_3w());
                intent.putExtra("webpage2",olddatas.get(position-1).getTitle());
                Log.e("TAGG1", "onItemClick: "+olddatas.get(position-1).getUrl_3w());
                if (olddatas.get(position-1).getUrl_3w()==null){
                    Toast.makeText(context,"该新闻手机网址不存在！",Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(intent);
                }
            }
        });
    }
    //检查网络
    private boolean network(){
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info==null){
            return false;
        }
        return info.isAvailable();
    }
}
