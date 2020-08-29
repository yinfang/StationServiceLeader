package com.kunbo.app.activitys;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.kunbo.app.C;
import com.kunbo.app.R;
import com.kunbo.app.adapter.ElectStatisticAdapter;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.net.bean.Result;
import com.kunbo.app.net.response.OnSuccessAndFaultListener;
import com.kunbo.app.net.response.OnSuccessAndFaultSub;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kunbo.app.net.api.ApiSubscribe.getStationList;
import static com.kunbo.app.net.api.ApiSubscribe.getStatistics;

/**
 * 机电综合统计
 */
public class ElectStatisticAnalyzeActivity extends BaseActivity implements OnSuccessAndFaultListener {
    @BindView(R.id.header_title)
    TextView title;
    @BindView(R.id.station_elect_list)
    RecyclerView electList;
    @BindView(R.id.pie_chart)
    PieChart pieChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elect_statistic_analyze);
        ButterKnife.bind(this);
        title.setText("机电综合统计");
        getStatistics(C.DEPT_ID, new OnSuccessAndFaultSub(this, "getSelectStatistics", this));
    }

    @Override
    public void onSuccess(Result result, String method) {
        super.onSuccess(result, method);
        switch (method) {
            case "getSelectStatistics":
                MyRow row = (MyRow) result.obj;
                initPieChart(row);
                MyData datas = (MyData) row.get("list");
                ElectStatisticAdapter adapter = new ElectStatisticAdapter(this, datas);
                electList.setAdapter(adapter);
                break;
        }
    }

    private void initPieChart(MyRow row) {
        MyData pieList = (MyData) row.get("pieList");
        List<PieEntry> yvals = new ArrayList<>();
        for (MyRow ro : pieList) {
            yvals.add(new PieEntry(ro.getFloat("num"), ""));
        }
        List<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.theme_color2));
        colors.add(getResources().getColor(R.color.theme_color));
        colors.add(getResources().getColor(R.color.search_opaque));

        //显示为圆环
     /*   pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleRadius(120f);//设置中间洞的大小
        pieChart.setHoleColor(Color.TRANSPARENT);
        // 半透明圈
        pieChart.setTransparentCircleRadius(100f);
        pieChart.setTransparentCircleColor(Color.WHITE); //设置半透明圆圈的颜色
        pieChart.setTransparentCircleAlpha(125); //设置半透明圆圈的透明度*/

        //饼状图中间可以添加文字
       /* pieChart.setDrawCenterText(true);
        pieChart.setCenterText("60%"); //设置中间文字
        pieChart.setCenterTextColor(Color.WHITE); //中间问题的颜色
        pieChart.setCenterTextSizePixels(20);  //中间文字的大小px
        pieChart.setCenterTextRadiusPercent(100f);
        pieChart.setCenterTextOffset(0, 0); //中间文字的偏移量*/

        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationAngle(90);// 初始旋转角度
        pieChart.setRotationEnabled(true);// 可以手动旋转
        pieChart.setUsePercentValues(true);//显示成百分比

        //图相对于上下左右的偏移
        pieChart.setExtraOffsets(10, 10, 10, 10);
        //图标的背景色
        pieChart.setBackgroundColor(Color.TRANSPARENT);
        //设置pieChart图表转动阻力摩擦系数[0,1]
        pieChart.setDragDecelerationFrictionCoef(0.75f);

        //获取图例
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        //数据集合
        PieDataSet dataset = new PieDataSet(yvals, "");
        //填充每个区域的颜色
        dataset.setColors(colors);
        //是否在图上显示数值
        dataset.setDrawValues(false);
        //文字的大小
        dataset.setValueTextSize(10);
        //文字的颜色
        dataset.setValueTextColor(getResources().getColor(R.color.white));
        pieChart.setContentDescription("");
        //设置Y值的位置是在圆内还是圆外
        dataset.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);

        //设置饼状Item被选中时变化的距离
        dataset.setSelectionShift(5f);

        //填充数据
        PieData pieData = new PieData(dataset);
        //格式化显示的数据为%百分比
        pieData.setValueFormatter(new PercentFormatter());
        //显示试图
        pieChart.setData(pieData);
    }
}
