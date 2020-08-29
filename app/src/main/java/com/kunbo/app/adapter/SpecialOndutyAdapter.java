package com.kunbo.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.kunbo.app.R;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.view.recyclerview.ViewHolder;

/**
 * 车道特情记录 列表适配
 */
public class SpecialOndutyAdapter extends RecyclerCommonAdapter {
    private Context ctx;

    public SpecialOndutyAdapter(Context ctx, MyData datas) {
        super(ctx, R.layout.item_agent_onduty, datas);
        this.ctx = ctx;
    }

    @Override
    protected void convert(ViewHolder holder, Object o, int position) {
        MyRow ro = (MyRow) o;

        holder.setText(R.id.item_station, ro.getString("stationName"));
        holder.setText(R.id.item_people, String.format(ctx.getString(R.string.onduty_item_title), TextUtils.isEmpty(ro.getString("secretPersonName")) ? "无" : ro.getString("secretPersonName")));
        holder.setText(R.id.item_date, String.format(ctx.getString(R.string.onduty_item_date), TextUtils.isEmpty(ro.getString("createTime")) ? "无" : ro.getString("createTime")));
    }

}