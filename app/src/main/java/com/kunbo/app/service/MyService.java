package com.kunbo.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;


import com.kunbo.app.C;
import com.kunbo.app.activitys.MainActivity;
import com.kunbo.app.R;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeoutException;

public class MyService extends Service {
    public static final int NOTICE_ID = 100;
    private ConnectionFactory factory;
    private boolean running = true;
    private static String message;

    private final MyHandler mHandler = new MyHandler(this);

    //用于从线程中获取数据，更新ui
    private static class MyHandler extends Handler {
        private final WeakReference<MyService> mService;//使用软引用持有外部类的实例，避免发生内存泄露

        public MyHandler(MyService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MyService service = mService.get();
            message = msg.getData().getString("msg");
            openNotifacation(service);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //如果API大于18，需要弹出一个可见通知
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_logo);
        builder.setContentTitle("KeepAppAlive");
        builder.setContentText("MyService is running...");
        startForeground(NOTICE_ID, builder.build());
        // 如果觉得常驻通知栏体验不好
        // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
        Intent intent = new Intent(this, CancelNoticeService.class);
        startService(intent);

        setupConnectionFactory();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("通知服务启动", "通知服务启动");
        setupConnectionFactory();
        if (running)
            //开启消费者线程
            new Thread(() -> basicConsume(mHandler)).start();
        return START_STICKY;
    }

    /**
     * 连接设置
     */
    private void setupConnectionFactory() {
        if (factory == null) {
            factory = new ConnectionFactory();
            factory.setHost(C.PUSH_IP);
            factory.setPort(C.PUSH_PORT);
            factory.setUsername(C.PUSH_USERNAME);
            factory.setPassword(C.PUSH_PASSWORD);
        }
    }

    /**
     * 收消息（从发布者那边订阅消息）
     *
     * @param handler
     */
    private void basicConsume(Handler handler) {
        try {
            //连接
            Connection connection = factory.newConnection();
            //通道
            final Channel channel = connection.createChannel();
            //实现Consumer的最简单方法是将便捷类DefaultConsumer子类化。可以在basicConsume 调用上传递此子类的对象以设置订阅：
            channel.basicConsume(C.PUSH_QUENE_NAME, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    super.handleDelivery(consumerTag, envelope, properties, body);
                    Log.d("推送消息", body.toString());
                    String msg = new String(body);
                    long deliveryTag = envelope.getDeliveryTag();
                    channel.basicAck(deliveryTag, false);
                    //从message池中获取msg对象更高效
                    Message uimsg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", msg);
                    uimsg.setData(bundle);
                    handler.sendMessage(uimsg);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static void openNotifacation(MyService myService) {
        Notification.Builder builder;
        NotificationManager mNotificationManager = (NotificationManager) myService.getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(myService.getApplicationContext(), MainActivity.class);//点击通知后进入的活动
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(myService.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//大于8.0
            NotificationChannel channel = new NotificationChannel("111", "station_service", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), Notification.AUDIO_ATTRIBUTES_DEFAULT);
            channel.enableVibration(true);//震动
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
            builder = new Notification.Builder(myService, "111")
                    .setContentTitle(myService.getString(R.string.app_name))
                    .setContentText("今日数据统计已完成，请点击查看详情")
                    .setSmallIcon(R.mipmap.ic_logo)
                    .setContentIntent(resultPendingIntent)
                    .setChannelId("111")
                    .setAutoCancel(true);//点击通知后自动消失
        } else {
            builder = new Notification.Builder(myService)
                    .setContentTitle(myService.getString(R.string.app_name))
                    .setContentText("今日数据统计已完成，请点击查看详情")
                    .setSmallIcon(R.mipmap.ic_logo)
                    .setContentIntent(resultPendingIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(true)//点击通知后自动消失
                    .setDefaults(Notification.DEFAULT_ALL);//通知的声音震动等都随系统
        }

        mNotificationManager.notify(0x888, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        mHandler.removeCallbacksAndMessages(null);
        // 如果Service被杀死，干掉通知
        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mManager != null)
            mManager.cancel(NOTICE_ID);
        // 重启服务
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        startService(intent);
    }
}
