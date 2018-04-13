package com.example.y.routeplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.example.y.routeplanner.adapter.TipAdapter;
import com.example.y.routeplanner.util.Test;

import java.util.ArrayList;
import java.util.List;


public class SearchTipActivity extends BaseActivity implements Inputtips.InputtipsListener {
    private EditText search;
    private List<Tip> tipList;
    private InputtipsQuery query;
    private Inputtips inputtips;
    private int requestCode = 0;
    private TipAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tip);
        search = findViewById(R.id.search_edit);
        RecyclerView recyclerView = findViewById(R.id.search_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        tipList = new ArrayList<>();
        adapter = new TipAdapter(tipList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new TipAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("tip", tipList.get(position));
                intent.putExtra("tip", bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
            //点击事件
        });
        requestCode = getIntent().getIntExtra("requestCode", 0);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                query = new InputtipsQuery(search.getText().toString(), Test.getInstance().cityCode);
                query.setCityLimit(true);
                inputtips = new Inputtips(getApplicationContext(), query);
                inputtips.setInputtipsListener(SearchTipActivity.this);
                inputtips.requestInputtipsAsyn();
            }
        });
    }


    @Override
    public void onGetInputtips(final List<Tip> list, int i) {       //在这里判断提示信息的类型  显示对应信息
        if (i == 1000) {
            if (list != null) {
                tipList.clear();
                switch (requestCode) {
                    case SEARCH_BUS_LINT:
                        for (Tip tip : list) {
                            if (tip.getPoiID() != null && tip.getPoint() == null) {
                                tipList.add(tip);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case SEARCH_BUS_STEP:
                        for (Tip tip : list) {
                            if (tip.getName().contains("公交站")) {
                                tipList.add(tip);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        for (Tip tip : list) {
                            if (tip.getPoint()!=null&&tip.getPoiID()!=null) {
                                tipList.add(tip);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        break;

                }
            }
        }
    }

}
