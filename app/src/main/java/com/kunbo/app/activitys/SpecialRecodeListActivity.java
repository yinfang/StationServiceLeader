package com.kunbo.app.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kunbo.app.C;
import com.kunbo.app.R;
import com.kunbo.app.adapter.SpecialOndutyAdapter;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.net.bean.Result;
import com.kunbo.app.net.response.OnSuccessAndFaultListener;
import com.kunbo.app.net.response.OnSuccessAndFaultSub;
import com.kunbo.app.view.CustomDatePickerDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kunbo.app.net.api.ApiSubscribe.getSpecialOndutyList;

public class SpecialRecodeListActivity extends BaseActivity implements OnSuccessAndFaultListener {
    @BindView(R.id.special_list)
    RecyclerView specialList;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.empty_ll)
    LinearLayout emptyLL;
    @BindView(R.id.header_title)
    TextView title;
    private CustomDatePickerDialog mTimerPicker;
    private SpecialOndutyAdapter adapter;
    private MyData datas = new MyData();
    private int page = C.pageIndex;
    private boolean isRefresh = false;
    private String firstDayOfMonth, lastDayOfMonth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_recode_list);
        ButterKnife.bind(this);
        title.setText("车道特情");

        setEText(R.id.tv_date_selecte, C.df_yM.format(new Date()));
        adapter = new SpecialOndutyAdapter(this, datas);
        specialList.setAdapter(adapter);
        try {
            getFirstAndLastDayOfMonth(C.df_yM.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initData(page);
        initListener();
    }

    private void initData(int page) {
        getSpecialOndutyList(String.valueOf(page), String.valueOf(C.pageSize),
                C.DEPT_ID, firstDayOfMonth, lastDayOfMonth,
                new OnSuccessAndFaultSub(this, "getList", this));
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            if (datas != null) {
                datas.clear();
            }
            isRefresh = true;
            page = C.pageIndex;
            initData(page);
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            page++;
            if (page > 1) {
                initData(page);
            }
        });
    }

    /**
     * 初始化时间日期选择器
     */
    private void initTimerPicker() {
        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePickerDialog(this, (v, timestamp) -> {
            String date = C.df_yM.format(new Date(timestamp));
            setEText(v.getId(), date);
            try {
                getFirstAndLastDayOfMonth(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (datas != null) {
                datas.clear();
            }
            page = C.pageIndex;
            initData(page);
            mTimerPicker.dismissDialog();
        }, "", "");
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示年月日时分
        mTimerPicker.setCanShowMonthDay(true, false);
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
    }

    @OnClick(R.id.tv_date_selecte)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date_selecte:
                initTimerPicker();
                mTimerPicker.show(view, "", C.df_yMd.format(new Date()));
                break;
        }
    }

    @Override
    public void onSuccess(Result result, String method) {
        super.onSuccess(result, method);
        if (method.equals("getList")) {
            MyData data = (MyData) ((MyRow) result.obj).get("records");
            if (data != null && data.size() > 0) {
                datas.addAll(data);
                emptyLL.setVisibility(View.GONE);
            } else {
                if (page == 1)
                    emptyLL.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
            if (isRefresh) {
                refreshLayout.finishRefresh();
                isRefresh = false;
            }
            if (page > 1)
                refreshLayout.finishLoadMore();
        }
    }

    public void getFirstAndLastDayOfMonth(String str) throws Exception {
        Calendar cale = Calendar.getInstance();
        cale.setTime(C.df_yM.parse(str));

        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        firstDayOfMonth = C.df_yMd.format(cale.getTime()); // 当月第一天

        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        lastDayOfMonth = C.df_yMd.format(cale.getTime()); // 当月最后一天
    }

}
