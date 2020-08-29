package com.kunbo.app.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.kunbo.app.R;
import com.kunbo.app.activitys.AgentOndutyListActivity;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.view.recyclerview.ViewHolder;

/**
 * 站长票证值班记录 列表适配
 */
public class AgentOndutyAdapter extends RecyclerCommonAdapter {
    private Context ctx;

    public AgentOndutyAdapter(Context ctx, MyData datas) {
        super(ctx, R.layout.item_agent_onduty, datas);
        this.ctx = ctx;
    }

    @Override
    protected void convert(ViewHolder holder, Object o, int position) {
        MyRow ro = (MyRow) o;

        holder.setText(R.id.item_station, ro.getString("stationName"));
        holder.setText(R.id.item_people, String.format(ctx.getString(R.string.onduty_item_title), ro.getString("personName")));
        holder.setText(R.id.item_date, String.format(ctx.getString(R.string.onduty_item_date), ro.getString("createTime")));

        TextView tvState = holder.getView(R.id.item_state);
        String isCommit = ro.getString("isCommit");
        switch (isCommit) {
            case "没交":
                holder.setText(R.id.item_people, String.format(ctx.getString(R.string.onduty_item_title), "无"));
                holder.setText(R.id.item_date, String.format(ctx.getString(R.string.onduty_item_date), "无"));
                tvState.setTextColor(ctx.getResources().getColor(R.color.red));
                break;
            case "正常":
                tvState.setTextColor(ctx.getResources().getColor(R.color.green));
                break;
            case "迟交":
                tvState.setTextColor(ctx.getResources().getColor(R.color.search_opaque));
                break;
        }
        tvState.setText(isCommit);

        holder.getItemView().setOnClickListener(view -> ((AgentOndutyListActivity) ctx).OnItemClick(position));
    }

}