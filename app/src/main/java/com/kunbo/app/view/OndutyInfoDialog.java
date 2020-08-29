package com.kunbo.app.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kunbo.app.C;
import com.kunbo.app.R;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;

import java.util.Date;

/**
 * 值班表每日值班信息弹框
 */
public class OndutyInfoDialog {
    private Context context;
    private StringBuffer sbNames;
    private StringBuffer sbPhones;

    public OndutyInfoDialog(@NonNull Context context) {
        this.context = context;
        if (C.dialogTheme != -1) {
            context.setTheme(C.dialogTheme);
        }
    }

    public void show(MyRow row) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        View v = View.inflate(context, R.layout.dialog_onduty_info, null);
        TextView title = v.findViewById(R.id.title);
        TextView stationNames = v.findViewById(R.id.station_names);
        TextView stationPhones = v.findViewById(R.id.station_phones);
        TextView ticketNames = v.findViewById(R.id.ticket_names);
        TextView ticketPhones = v.findViewById(R.id.ticket_phones);
        TextView monitorNames = v.findViewById(R.id.monitor_names);
        TextView monitorPhones = v.findViewById(R.id.monitor_phones);

        title.setText( C.df_yM.format(new Date()) + "-" + row.getString("dutyDay") + "日值班信息");
        MyData stations = (MyData) row.get("siteAgent");
        getNamesAndPhones(stations);
        stationNames.setText(sbNames.toString());
        stationPhones.setText(sbPhones.toString());

        MyData tickets = (MyData) row.get("ticket");
        getNamesAndPhones(tickets);
        ticketNames.setText(sbNames.toString());
        ticketPhones.setText(sbPhones.toString());

        MyData monitors = (MyData) row.get("monitor");
        getNamesAndPhones(monitors);
        monitorNames.setText(sbNames.toString());
        monitorPhones.setText(sbPhones.toString());

        ad.setView(v);
        AlertDialog d = ad.create();
        d.show();
    }

    public void getNamesAndPhones(MyData data) {
        sbNames = new StringBuffer();
        sbPhones = new StringBuffer();
        if (data != null && data.size() > 0) {
            int size = data.size();
            for (int i = 0; i < size; i++) {
                if (i == size - 1) {
                    sbNames.append(data.get(i).getString("watchkeeperName"));
                    sbPhones.append(data.get(i).getString("watchkeeperPhone"));
                } else {
                    sbNames.append(data.get(i).getString("watchkeeperName") + "\n");
                    sbPhones.append(data.get(i).getString("watchkeeperPhone") + "\n");
                }

            }
        }
    }
}
