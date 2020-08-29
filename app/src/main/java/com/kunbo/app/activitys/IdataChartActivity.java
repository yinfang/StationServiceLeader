package com.kunbo.app.activitys;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.kunbo.app.C;
import com.kunbo.app.R;
import com.kunbo.app.adapter.MiddleBottomListAdapter;
import com.kunbo.app.chartCustom.DayAxisValueFormatter;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.net.bean.Result;
import com.kunbo.app.net.response.OnSuccessAndFaultListener;
import com.kunbo.app.utils.SPUtil;
import com.kunbo.app.utils.Utils;
import com.kunbo.app.view.AutoScrollRecyclerview;
import com.kunbo.app.view.CustomTimeView;
import com.kunbo.app.view.statusBarUtil.StatusBarUtil;
import com.kunbo.app.view.zoomPhotoView.PhotoView;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 大数据可视化
 */
public class IdataChartActivity extends BaseActivity implements OnSuccessAndFaultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idata_chart);
        StatusBarUtil.setStatusBarImageIn(this);
        initLeftChart();
        initMiddleChart();
        initRightChart();
    }

    private void initLeftChart() {
        String str = "<font color:'#fff'><b><big>15763.6</big></b></font><br>收费额";
        String str1 = "<font color:'#fff'><b><big>5763</big></font></b><br>车流量";
        setEText(R.id.num1, Html.fromHtml(str));
        setEText(R.id.num2, Html.fromHtml(str1));
        BarChart barChart = findViewById(R.id.chart_left_bottom);
        initBarChartData(barChart, true);
    }

    private void initMiddleChart() {
        PhotoView img = findViewById(R.id.chart_middle_top);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //img.setMinimumScale(3);
        //img.setMediumScale(4);
        //img.setMaximumScale(6);
        img.setBackgroundColor(Color.TRANSPARENT);

        AutoScrollRecyclerview list = findViewById(R.id.list_middle_bottom);
        MyData datas = new MyData();
        String[] staions = {
                "临潼收费站",
                "新丰收费站",
                "兵马俑收费站",
                "华洲收费站",
                "华洲西收费站",
                "罗敷收费站",
                "渭南东收费站",
                "渭南西收费站",
                "灞桥收费站",
                "华阴收费站",
                "秦东收费站",
                "豁口收费站",
                "港口收费站"
        };
        for (int i = 0; i < 13; i++) {
            MyRow ro = new MyRow();
            ro.put("station", staions[i]);
            ro.put("num", Math.round(new Random().nextInt(20000)));
            ro.put("num1", Math.round(new Random().nextInt(10000)));
            ro.put("num2", Math.round(new Random().nextInt(10000)));
            datas.add(ro);
        }
        MiddleBottomListAdapter listAdapter = new MiddleBottomListAdapter(this, datas);
        list.setAdapter(listAdapter);
    }

    private void initRightChart() {
        PieChart pie1Chart = findViewById(R.id.chart_right_top1);
        PieChart pie2Chart = findViewById(R.id.chart_right_top2);
        initPieChart(pie1Chart);
        initPieChart(pie2Chart);
        BarChart barChart = findViewById(R.id.chart_right_middle);
        initBarChartData(barChart, false);
        LineChart lineChart = findViewById(R.id.chart_right_bottom);
        initLineChartData(lineChart);
    }

    @Override
    public void onFault(Result result, String method) {
    }

    public void initBarChartData(BarChart chart, boolean isMonth) {
        chart.setFitBars(true);
        chart.setVisibleXRange(6, 6);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(60);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        String[] xdatas;
        if (isMonth)
            xdatas = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        else
            xdatas = new String[]{"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextSize(7);
        if (isMonth) {
            xAxis.setAxisMaximum(12);
            xAxis.setLabelCount(12, false);
        } else {
            xAxis.setAxisMaximum(7);
            xAxis.setLabelCount(7, false);
        }
        xAxis.setAxisMinimum(0);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setTextColor(getResources().getColor(R.color.white));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (isMonth)
                    return value >= 0 && value < 12 ? xdatas[(int) value] : "";
                else
                    return value >= 0 && value < 7 ? xdatas[(int) value] : "";
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        if (isMonth)
            leftAxis.setGranularity(1000f);
        else
            leftAxis.setGranularity(3000f);
        leftAxis.setLabelCount(10, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextSize(7);
        leftAxis.setSpaceTop(15f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextColor(getResources().getColor(R.color.white));
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);//不显示右侧y轴

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(getResources().getColor(R.color.white));
        l.setFormSize(7f);
        l.setTextSize(7f);
        l.setCustom(new LegendEntry[]{
                new LegendEntry("收费额", Legend.LegendForm.LINE, Float.NaN, Float.NaN, null, getResources().getColor(R.color.theme_color)),
                new LegendEntry("车流量", Legend.LegendForm.LINE, Float.NaN, Float.NaN, null, getResources().getColor(R.color.theme_color2))});


        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();
        for (int i = 0; i < (isMonth ? 12 : 7); i++) {
            values1.add(new BarEntry(i, new Random().nextInt(20000)));
            values2.add(new BarEntry(i, new Random().nextInt(10000)));
        }

        BarDataSet set1 = new BarDataSet(values1, "");
        set1.setDrawIcons(false);
        set1.setColors(getResources().getColor(R.color.theme_color));
        BarDataSet set2 = new BarDataSet(values2, "");
        set2.setDrawIcons(false);
        set2.setColors(getResources().getColor(R.color.theme_color2));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(7f);
        data.setBarWidth(0.65f / 2);
        data.groupBars(0f, 0.3f, 0.05f);
        data.setValueTextColor(getResources().getColor(R.color.white));

        chart.setData(data);
    }

    public void initLineChartData(LineChart lineChart) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextSize(7f);
        xAxis.setTextColor(getResources().getColor(R.color.white));
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(23f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(24, false);
        final String[] xvalues = new String[24];
        for (int i = 0; i < 24; i++) {
            xvalues[i] = i + ":00";
        }
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value >= 0 && value < 24 ? xvalues[(int) value] : "";
            }
        });
        YAxis leftAxis = lineChart.getAxisLeft();//y轴独有
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setTextColor(getResources().getColor(R.color.white));
        leftAxis.setGranularity(2000f);//设置轴值之间最小间隔1
        leftAxis.setAxisMinimum(0f);//为这个轴设置一个自定义的最小值。
        leftAxis.setTextSize(7f);
        //保证Y轴从0开始，不然会上移一点
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);//y轴的数值显示在外侧

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);//不显示右侧线
        Legend legend = lineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(getResources().getColor(R.color.white));
        legend.setFormSize(7f);
        legend.setTextSize(7f);
        legend.setCustom(new LegendEntry[]{
                new LegendEntry("收费额", Legend.LegendForm.LINE, Float.NaN, Float.NaN, null, getResources().getColor(R.color.theme_color)),
                new LegendEntry("车流量", Legend.LegendForm.LINE, Float.NaN, Float.NaN, null, getResources().getColor(R.color.theme_color2))});

        ArrayList<Entry> values1 = new ArrayList<>();
        ArrayList<Entry> values2 = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            values1.add(new Entry(i, new Random().nextInt(20000)));
            values2.add(new Entry(i, new Random().nextInt(10000)));
        }
        LineDataSet set1 = new LineDataSet(values1, "");
        set1.setColor(getResources().getColor(R.color.title_blue));
        set1.setCircleColor(getResources().getColor(R.color.title_blue));
        set1.setCircleRadius(1.5f);
        set1.setDrawCircleHole(true);
        set1.setValueTextSize(7f);
        set1.setValueTextColor(getResources().getColor(R.color.white));
        set1.setFillColor(getResources().getColor(R.color.theme_color));
        set1.setDrawFilled(true);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);//设置显示为曲线还是折线

        LineDataSet set2 = new LineDataSet(values2, "");
        set2.setColor(getResources().getColor(R.color.title_blue));
        set2.setCircleColor(getResources().getColor(R.color.title_blue));
        set2.setValueTextColor(getResources().getColor(R.color.white));
        set2.setCircleRadius(1.5f);
        set2.setDrawCircleHole(true);
        set2.setValueTextSize(7f);
        set2.setFillColor(getResources().getColor(R.color.theme_color2));
        set2.setDrawFilled(true);
        set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);//设置显示为曲线还是折线

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        LineData data = new LineData(dataSets);
        lineChart.setDrawGridBackground(false);
        lineChart.setVisibleXRange(6, 6);
        lineChart.getDescription().setEnabled(false);

        lineChart.setData(data);
    }

    private void initPieChart(PieChart pieChart) {
        List<PieEntry> yvals = new ArrayList<>();
        yvals.add(new PieEntry(2.0f, ""));
        yvals.add(new PieEntry(3.0f, ""));
        List<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.theme_color));
        colors.add(getResources().getColor(R.color.theme_color2));

        //显示为圆环
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(120f);//设置中间洞的大小
        pieChart.setHoleColor(Color.TRANSPARENT);

        // 半透明圈
        pieChart.setTransparentCircleRadius(100f);
        pieChart.setTransparentCircleColor(Color.WHITE); //设置半透明圆圈的颜色
        pieChart.setTransparentCircleAlpha(125); //设置半透明圆圈的透明度

        //饼状图中间可以添加文字
        pieChart.setDrawCenterText(false);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("60%"); //设置中间文字
        pieChart.setCenterTextColor(Color.WHITE); //中间问题的颜色
        pieChart.setCenterTextSizePixels(20);  //中间文字的大小px
        pieChart.setCenterTextRadiusPercent(100f);
        pieChart.setCenterTextOffset(0, 0); //中间文字的偏移量
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationAngle(90);// 初始旋转角度
        pieChart.setRotationEnabled(true);// 可以手动旋转
        pieChart.setUsePercentValues(true);//显示成百分比

        //图相对于上下左右的偏移
        //pieChart.setExtraOffsets(5, 20, 10, 5);
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

