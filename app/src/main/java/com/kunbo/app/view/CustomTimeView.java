package com.kunbo.app.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.kunbo.app.C;
import com.kunbo.app.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.Date;

public class CustomTimeView extends androidx.appcompat.widget.AppCompatTextView {
    private static CustomTimeView textView;
    private TimeHandler mTimehandler = new TimeHandler();
    private boolean running = true;

    public CustomTimeView(Context context) {
        this(context, null);
    }

    public CustomTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textView = this;
        init();
    }

    private void init() {
        try {
            //初始化textview显示时间
            updateClock();
            if (running) {
                //更新进程开始
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mTimehandler.startScheduleUpdate();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    //更新Handler通过handler的延时发送消息来更新时间
    private static class TimeHandler extends Handler {
        private boolean mStopped;

        //开始更新
        public void startScheduleUpdate() {
            post();
        }

        private void post() {
            //每隔1秒发送一次消息
            sendMessageDelayed(obtainMessage(0), 1000);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateClock();
            removeMessages(msg.what);
            //实现实时更新
            post();
        }
    }

    private static void updateClock() {
        StringBuilder datetime = new StringBuilder();
        datetime.append(C.dfs_yMdHms.format(System.currentTimeMillis()))
                .append("\t\t")
                .append(Utils.getWeekOfDate(new Date()));
        textView.setText(datetime);
    }

}
