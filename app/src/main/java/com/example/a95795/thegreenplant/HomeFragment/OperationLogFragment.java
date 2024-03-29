package com.example.a95795.thegreenplant.HomeFragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a95795.thegreenplant.R;
import com.example.a95795.thegreenplant.adapter.LogAdapter;
import com.example.a95795.thegreenplant.custom.Log;
import com.example.a95795.thegreenplant.custom.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;

import org.angmarch.views.NiceSpinner;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.yokeyword.fragmentation.SupportFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class OperationLogFragment extends SupportFragment {
    private NiceSpinner niceSpinner,niceSpinner1;
    private SearchView mSearchView;
    private ListView mListView;
    private int ID = 1;
    SpringView springView;


    public static OperationLogFragment newInstance() {
        return new OperationLogFragment();
    }
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //结束刷新动作
                    springView.onFinishFreshAndLoad();
                    //更新adapter
                    list();
                    Toast.makeText(OperationLogFragment.this.getActivity(),"刷新成功",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String url;
            if (msg.what == 3) {
                url = getString(R.string.ip) + "user/LogDateLike?id=" + msg.obj;
            } else if (msg.what == 2) {
                url = getString(R.string.ip) + "user/LogNameLike?name=" + msg.obj;
            } else {
                url = getString(R.string.ip) + "user/LogUseridLike?id=" + msg.obj;
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    "",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Gson gson = new Gson();
                                final List<Log> subjectList = gson.fromJson(response.getJSONArray("Log").toString(), new TypeToken<List<Log>>() {
                                }.getType());
                                LogAdapter logAdapter = new LogAdapter(OperationLogFragment.this.getActivity(), R.layout.log, subjectList);
                                mListView = (ListView) getView().findViewById(R.id.feedlistview);
                                mListView.setAdapter(logAdapter);
                                new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setContentText("查询到  "+subjectList.size()+"  条操作日志")
                                        .setConfirmText("确定")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );
            MyApplication.addRequest(jsonObjectRequest, "MainActivity");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        mSearchView = (SearchView) view.findViewById(R.id.searchView);
        //设置输入框提示语
        mSearchView.setQueryHint("支持模糊搜索");
        list();
        niceSpinner = view.findViewById(R.id.nice_spinner);
        niceSpinner1 = view.findViewById(R.id.nice_spinner2);
        List<String> spinnerData = new LinkedList<>(Arrays.asList("工号查询", "姓名查询", "时间查询"));
        List<String> spinnerData1 = new LinkedList<>(Arrays.asList("登录", "注册", "设备"));
        niceSpinner.attachDataSource(spinnerData);
        niceSpinner1.attachDataSource(spinnerData1);
        mSearchView.onActionViewExpanded();

        niceSpinner.setTextSize(13);
        niceSpinner.setArrowDrawable(R.drawable.jiantou);
        niceSpinner1.setArrowDrawable(R.drawable.jiantou);
        niceSpinner1.setTextSize(13);
        //监听下拉列表
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ID = position + 1;
                mSearchView.setQuery("",false);
                list();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //监听下拉列表
        niceSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String url = getString(R.string.ip) + "user/LogDateType?id="+(position+1);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        "",
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Gson gson = new Gson();
                                    final List<Log> subjectList = gson.fromJson(response.getJSONArray("Log").toString(),new TypeToken<List<Log>>(){}.getType());
                                    LogAdapter logAdapter = new LogAdapter(OperationLogFragment.this.getActivity(),R.layout.log,subjectList);
                                    mListView = (ListView) getView().findViewById(R.id.feedlistview);
                                    mListView.setAdapter(logAdapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );
                MyApplication.addRequest(jsonObjectRequest,"MainActivity");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(final String query) {
                mSearchView.clearFocus();  //可以收起键盘

                switch (ID) {
                    case 1:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //通过Handler发送一个消息切换回主线程（mHandler所在的线程）
                                Message msg = new Message();
                                msg.what = 1;
                                msg.obj = query;
                                mHandler.sendMessage(msg);

                            }
                        }).start();

                        break;
                    case 2:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //通过Handler发送一个消息切换回主线程（mHandler所在的线程）
                                Message msg = new Message();
                                msg.what = 2;
                                msg.obj = query;
                                mHandler.sendMessage(msg);
                            }
                        }).start();

                        break;
                    case 3:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //通过Handler发送一个消息切换回主线程（mHandler所在的线程）
                                Message msg = new Message();
                                msg.what = 3;
                                msg.obj = query;
                                mHandler.sendMessage(msg);
                            }
                        }).start();
                        break;

                    default:
                        break;
                }
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    mListView.setFilterText(newText);
                } else {
                    mListView.clearTextFilter();
                }
                return false;
            }
        });
        return view;
    }

    public void list() {
        String url = getString(R.string.ip) + "user/LogAll";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                "",
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Gson gson = new Gson();
                            final List<Log> subjectList = gson.fromJson(response.getJSONArray("Log").toString(),new TypeToken<List<Log>>(){}.getType());
                            LogAdapter logAdapter = new LogAdapter(OperationLogFragment.this.getActivity(),R.layout.log,subjectList);
                            mListView = (ListView) getView().findViewById(R.id.feedlistview);
                            mListView.setAdapter(logAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        MyApplication.addRequest(jsonObjectRequest,"MainActivity");
    }
    private void initData() {

        springView.setHeader(new DefaultHeader(this.getActivity()));

    }

    private void initEvnet() {

        springView.setType(SpringView.Type.FOLLOW);

//        刷新和载入很多其它的事件处理
//        假设须要处理的话，仅仅需在代码中加入监听：
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(0,1000);
            }

            @Override
            public void onLoadmore() {

            }
        });

    }
}
