package com.kunbo.app.adapter;

import com.kunbo.app.R;
import com.kunbo.app.activitys.MainActivity;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.view.recyclerview.ViewHolder;

import java.util.List;

public class MainGridItemAdapter extends RecyclerCommonAdapter {
    private MainActivity context;
    public int flag;

    public MainGridItemAdapter(MainActivity context, List datas, int flag) {
        super(context, R.layout.item_main_grid, datas);
        this.context = context;
        this.flag = flag;
    }

    @Override
    protected void convert(ViewHolder holder, Object o, int position) {
        MyRow ro = (MyRow) o;

        if (Integer.parseInt(ro.get("img").toString()) != 0) {
            holder.setImage(R.id.icon_agent, ro.get("img"));
        }
        holder.setText(R.id.tv_agent, ro.getString("title"));
        int count = ro.getInt("count");
        if (flag == 0 && count > 0) {
            holder.setText(R.id.dot_agent, count + "");
            holder.show(R.id.dot_agent);
        } else {
            holder.hide(R.id.dot_agent);
        }
        switch (flag) {
            case 0:
                holder.getItemView().setOnClickListener(v -> context.onOndutyItemClick(position));
                break;
            case 1:
                holder.getItemView().setOnClickListener(v -> context.onFeeItemClick(position));
                break;
            case 2:
                holder.getItemView().setOnClickListener(v -> context.onElectItemClick(position));
                break;
        }
    }

    /**
     * 根据手机分辨率从DP转成PX
     *
     * @param dpValue
     * @return
     */
    public int dip2px(int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
