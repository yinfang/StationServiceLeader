package com.kunbo.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kunbo.app.R;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.view.AutoScrollRecyclerview;


public class MiddleBottomListAdapter extends AutoScrollRecyclerview.Adapter<MiddleBottomListAdapter.MyViewHolder> {
    private Context context;
    private MyData datas;

    public MiddleBottomListAdapter(Context context, MyData datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_middle_bottom, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyRow ro = datas.get(position);

        holder.station.setText(ro.getString("station"));
        if (ro.getInt("num") <= 1000)
            holder.num.setTextColor(context.getResources().getColor(R.color.red));
        else
            holder.num.setTextColor(context.getResources().getColor(R.color.white));
        holder.num.setText(ro.getString("num"));
        holder.num1.setText(ro.getString("num1"));
        holder.num2.setText(ro.getString("num2"));
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView station, num, num1, num2, remark;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            station = itemView.findViewById(R.id.list_station);
            num = itemView.findViewById(R.id.list_num);
            num1 = itemView.findViewById(R.id.list_num1);
            num2 = itemView.findViewById(R.id.list_num2);
        }
    }
}


