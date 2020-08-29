package com.kunbo.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.widget.TextView;

import com.kunbo.app.R;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.view.recyclerview.ViewHolder;

/**
 * 机电统计 列表适配
 */
public class ElectStatisticAdapter extends RecyclerCommonAdapter {
    private Context ctx;

    public ElectStatisticAdapter(Context ctx, MyData datas) {
        super(ctx, R.layout.item_elect_statistic, datas);
        this.ctx = ctx;
    }

    @Override
    protected void convert(ViewHolder holder, Object o, int position) {
        MyRow ro = (MyRow) o;
        holder.setText(R.id.item_station, ro.getString("stationName"));
        holder.setText(R.id.item_total_num, String.format(ctx.getString(R.string.onduty_item_title), ro.getString("personName")));

        setHtmlText(holder, R.id.item_run_num, String.format(ctx.getResources().getString(R.string.car_num),
                getHtmlText(ro.getFloat("runNum"), "#CC3DF358")));
        setHtmlText(holder, R.id.item_bad_num, String.format(ctx.getResources().getString(R.string.car_num),
                getHtmlText(ro.getFloat("badNum"), "#ff0000")));
        setHtmlText(holder, R.id.item_repair_num, String.format(ctx.getResources().getString(R.string.car_num),
                getHtmlText(ro.getFloat("repairNum"), "#ffaa3f")));
    }

    public void setHtmlText(ViewHolder holder, int resId, String value) {
        if (value != null) {
            TextView tv = holder.getView(resId);
            if (tv != null) {
                tv.setText(Html.fromHtml(value));
            }
        }
    }

    public String getHtmlText(Object content, String color) {
        return "<font color=\"" + color + "\"size=\"28\"><b>" + content + "</b></font>"; //粗体效果<b>
    }

}