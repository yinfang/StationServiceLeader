package com.kunbo.app.activitys;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kunbo.app.C;
import com.kunbo.app.R;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.net.bean.Result;
import com.kunbo.app.net.response.OnSuccessAndFaultListener;
import com.kunbo.app.net.response.OnSuccessAndFaultSub;
import com.kunbo.app.utils.AppUtil;
import com.kunbo.app.utils.Utils;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kunbo.app.net.api.ApiSubscribe.getStationList;
import static com.kunbo.app.net.api.ApiSubscribe.getStatistics;

/**
 * 综合统计
 */
public class StatisticAnalyzeActivity extends BaseActivity implements OnSuccessAndFaultListener {
    @BindView(R.id.header_title)
    TextView title;
    private String stationId;
    private MyData datas = new MyData();//所有站点
    private String[] stations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_analyze);
        ButterKnife.bind(this);
        title.setText("综合统计");

        getStationList(C.DEPT_ID, new OnSuccessAndFaultSub(this, "getStationList", "", true, false, this));
    }

    private void initData(String stationId) {
        getStatistics(stationId, new OnSuccessAndFaultSub(this, "getStatistics", this));
    }

    @OnClick(R.id.tv_station_selecte)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_station_selecte:
                showListDialog(view, 0, stations);
                break;
        }
    }

    @Override
    public void listSelected(View view, int index) {
        super.listSelected(view, index);
        setEText(R.id.tv_station_selecte, datas.get(index).getString("deptName"));
        stationId = datas.get(index).getString("deptId");
        initData(stationId);
    }

    @Override
    public void onSuccess(Result result, String method) {
        super.onSuccess(result, method);
        switch (method) {
            case "getStationList":
                datas = (MyData) result.obj;
                setEText(R.id.tv_station_selecte, datas.get(0).getString("deptName"));
                stationId = datas.get(0).getString("deptId");
                stations = new String[datas.size()];
                for (int i = 0; i < datas.size(); i++) {
                    stations[i] = datas.get(i).getString("deptName");
                }
                initData(stationId);
                break;
            case "getStatistics":
                MyRow row = (MyRow) result.obj;
                //收费额
                setText(R.id.month_total_fee, R.string.month_total_fee, row.getFloat("drafted_by"));
                setText(R.id.last_year_total_fee, R.string.last_year_total_fee, row.getFloat("last_year_drafted_by"));
                setText(R.id.guest_month_total_fee, R.string.guest_month_total_fee, row.getFloat("Coach_during_the_month"));
                setText(R.id.goods_month_total_fee, R.string.goods_month_total_fee, row.getFloat("Van_that_month"));

                //当月实收额占比
                LinearLayout pieLl = findViewById(R.id.pie_ll);
                TextView cashLl = findViewById(R.id.cash_percent);
                pieLl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cashLl.getWidth()));

                float total = row.getFloat("cashPayment") + row.getFloat("mobilePayment") + row.getFloat("paymentCard");
                setHtmlText(R.id.cash_percent, String.format(getString(R.string.percent),
                        getBrText(C.nf_a.format(row.getFloat("cashPayment") / total * 100))) + "<br>现金支付");
                setHtmlText(R.id.moblie_percent, String.format(getString(R.string.percent),
                        getBrText(C.nf_a.format(row.getFloat("mobilePayment") / total * 100))) + "<br>移动支付");
                setHtmlText(R.id.card_percent, String.format(getString(R.string.percent),
                        getBrText(C.nf_a.format(row.getFloat("paymentCard") / total * 100))) + "<br>支付卡支付");

                //车流量
                setHtmlText(R.id.carin_month, String.format(getString(R.string.car_num),
                        getHtmlText(row.getFloat("entrance"))) + "<br>当月车流量");
                setHtmlText(R.id.carin_last_year, String.format(getString(R.string.car_num),
                        getHtmlText(row.getFloat("last_year_entrance"))) + "<br>上年同比车流量");
                setHtmlText(R.id.carout_month, String.format(getString(R.string.car_num),
                        getHtmlText(row.getFloat("exit"))) + "<br>当月车流量");
                setHtmlText(R.id.carout_last_year, String.format(getString(R.string.car_num),
                        getHtmlText(row.getFloat("last_year_exit"))) + "<br>上年同比车流量");
                setHtmlText(R.id.car_guest_month, String.format(getString(R.string.car_num),
                        getHtmlText(row.getFloat("passenger"))) + "<br>客车");
                setHtmlText(R.id.car_goods_month, String.format(getString(R.string.car_num),
                        getHtmlText(row.getFloat("trucks"))) + "<br>货车");

                //堵漏增收
                setText(R.id.dulou_num_month, R.string.num_tip, C.nf_i.format(row.getFloat("this_month_car")));
                setText(R.id.dulou_fee_month, R.string.fee_tip, row.getFloat("this_month_money"));
                setText(R.id.dulou_num_year, R.string.num_tip, C.nf_i.format(row.getFloat("licount")));
                setText(R.id.dulou_fee_year, R.string.fee_tip, row.getFloat("limoney"));

                break;
        }
    }

    public void setHtmlText(int resId, String value) {
        if (value != null) {
            TextView tv = this.findViewById(resId);
            if (tv != null) {
                tv.setText(Html.fromHtml(value));
            }
        }
    }

    public void setText(int viewId, int resId, Object content) {
        String text = "<font color=\"#232323\" size=\"28\"><b>" + content + "</b></font>"; //粗体效果<b>
        text = text.replace("\n", "<br>");
        TextView tv = findViewById(viewId);
        tv.setText(Html.fromHtml(String.format(getString(resId), text)));
    }

    public String getHtmlText(Object content) {
        return "<font color=\"#232323\" size=\"28\"><b>" + content + "</b></font>"; //粗体效果<b>
    }

    public String getBrText(Object content) {
        return "<font color=\"#232323\" size=\"28\"><b>" + content + "%" + "</b></font>"; //粗体效果<b>
    }
}
