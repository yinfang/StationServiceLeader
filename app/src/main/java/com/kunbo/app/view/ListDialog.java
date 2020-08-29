package com.kunbo.app.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunbo.app.C;
import com.kunbo.app.R;
import com.kunbo.app.adapter.RecyclerCommonAdapter;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.view.recyclerview.ViewHolder;

public class ListDialog {
    private Context context;
    private OnSelectedListener onSelectedListener;

    public ListDialog(Context context) {
        this.context = context;
        if (C.dialogTheme != -1) {
            context.setTheme(C.dialogTheme);
        }
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    @SuppressLint("InflateParams")
    public void show(final View view, int resIdTitle, String[] captions) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        if (resIdTitle > 0) {

            ad.setTitle(resIdTitle);
        }
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_list, null);
        MyData data = new MyData();
        for (int i = 0; i < captions.length; i++) {
            MyRow row = new MyRow();
            row.put("name", captions[i]);
            data.add(row);
        }
        DialogListAdapter adapter = new DialogListAdapter(context, data);
        RecyclerView lv = v.findViewById(R.id.list);
        lv.setLayoutManager(new LinearLayoutManager(context));
        lv.setAdapter(adapter);
        ad.setView(v);
        Dialog d = ad.create();
        adapter.setOnItemClickListener(new MyListSelectedListener(d, view));
        d.show();
    }

    public interface OnSelectedListener {
        void onSelected(View view, int index);
    }

    class DialogListAdapter extends RecyclerCommonAdapter {

        public DialogListAdapter(Context a, MyData data) {
            super(a, R.layout.dialog_list_item, data);
        }

        @Override
        protected void convert(ViewHolder holder, Object o, int position) {
            ImageView iv = holder.getView(R.id.imageView1);
            MyRow row = (MyRow) o;
            holder.setText(R.id.name, row.getString("name"));
        }
    }

    class MyListSelectedListener implements RecyclerCommonAdapter.OnItemClickListener {
        Dialog dialog;
        View viewp;

        MyListSelectedListener(Dialog dialog, View viewp) {
            this.dialog = dialog;
            this.viewp = viewp;
        }

        @Override
        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
            if (onSelectedListener != null) {
                onSelectedListener.onSelected(viewp, position);
            }
            dialog.dismiss();
        }

        @Override
        public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
            return false;
        }
    }

}
