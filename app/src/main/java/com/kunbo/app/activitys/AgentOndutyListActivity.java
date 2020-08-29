package com.kunbo.app.activitys;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.kunbo.app.C;
import com.kunbo.app.R;
import com.kunbo.app.adapter.AgentOndutyAdapter;
import com.kunbo.app.modle.User;
import com.kunbo.app.net.OkHttpUtil;
import com.kunbo.app.net.api.ApiService;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.net.bean.Result;
import com.kunbo.app.net.response.OnSuccessAndFaultListener;
import com.kunbo.app.net.response.OnSuccessAndFaultSub;
import com.kunbo.app.utils.AppUtil;
import com.kunbo.app.view.CustomDatePickerDialog;
import com.kunbo.app.view.lemonbubble.LemonBubble;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static com.kunbo.app.net.api.ApiSubscribe.getAgentOndutyList;
import static com.kunbo.app.net.api.ApiSubscribe.getMonitorOndutyList;
import static com.kunbo.app.net.api.ApiSubscribe.getTicketOndutyList;

/**
 * 站长 票证 班长 值班记录列表公用界面
 */
public class AgentOndutyListActivity extends BaseActivity implements OnSuccessAndFaultListener, ValueCallback<String> {
    @BindView(R.id.agent_list)
    RecyclerView agentList;
    @BindView(R.id.empty_ll)
    LinearLayout emptyLL;
    @BindView(R.id.header_title)
    TextView title;
    private AgentOndutyAdapter adapter;
    private MyData datas = new MyData();
    private CustomDatePickerDialog mTimerPicker;
    private int from;
    private String dateTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_onduty_list);
        ButterKnife.bind(this);
        title.setText("政策详情");

        from = getIntent().getIntExtra("from", 0);
        User user = User.getInstance();
        int[] unReads = user.getUnReads();
        if (from == 0) {
            title.setText("站长值班记录");
            unReads[0] = 0;
        } else if (from == 1) {
            title.setText("票证值班记录");
            unReads[1] = 0;
        } else {
            title.setText("班长值班记录");
            unReads[2] = 0;
        }
        user.setUnReads(unReads);

        setEText(R.id.tv_date_selecte, C.df_yMd.format(new Date()));
        adapter = new AgentOndutyAdapter(this, datas);
        agentList.setAdapter(adapter);

        dateTime = C.df_yMd.format(new Date());
        initDate();
    }

    public void initDate() {
        switch (from) {
            case 0://站长
                getAgentOndutyList(dateTime, new OnSuccessAndFaultSub(this, "getList", this));
                break;
            case 1://票证
                getTicketOndutyList(dateTime, new OnSuccessAndFaultSub(this, "getList", this));
                break;
            case 2://班长
                getMonitorOndutyList(dateTime, new OnSuccessAndFaultSub(this, "getList", this));
                break;
        }
    }

    /**
     * 初始化时间日期选择器
     */
    private void initTimerPicker() {
        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePickerDialog(this, (v, timestamp) -> {
            String date = C.df_yMd.format(new Date(timestamp));
            dateTime = date;
            setEText(v.getId(), date);
            if (datas != null) {
                datas.clear();
            }
            mTimerPicker.dismissDialog();
            initDate();
        }, "", "");
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示年月日时分
        mTimerPicker.setCanShowDateTime(true, false, false);
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
    }

    @OnClick(R.id.tv_date_selecte)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date_selecte:
                initTimerPicker();
                mTimerPicker.show(view, "", C.df_yMd.format(new Date()));
                break;
        }
    }

    @Override
    public void onSuccess(Result result, String method) {
        super.onSuccess(result, method);
        switch (method) {
            case "getList":
                MyData data = (MyData) ((MyRow) result.obj).get("records");
                if (data != null && data.size() > 0) {
                    datas.addAll(data);
                    emptyLL.setVisibility(View.GONE);
                } else {
                    emptyLL.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    public void OnItemClick(int position) {
        MyRow ro = datas.get(position);
        String path = ro.getString("path").replace("//", "/");
        if (TextUtils.isEmpty(path)) {
            return;
        }
        String[] ss = path.split("/");
        LemonBubble.showRoundProgress(this, "文件加载中...");
        Retrofit ref = OkHttpUtil.getInstance().getRetrofit();
        ref.create(ApiService.class)
                .downloadFileOrApk(C.BASE_URL_IMAGE + path)
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(responseBody -> AppUtil.saveFile(this, responseBody.byteStream(), ss[ss.length - 1])
                )
                .subscribe(new DisposableObserver<File>() {
                    @Override
                    public void onNext(File file) {
                        LemonBubble.hide();
                        openFileReader(file.getAbsolutePath());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(AgentOndutyListActivity.this, "下载失败," + e.getMessage(), Toast.LENGTH_SHORT).show();
                        LemonBubble.hide();
                    }

                    @Override
                    public void onComplete() {
                        LemonBubble.hide();
                    }
                });
    }

    /**
     * QbSdk打开文档
     *
     * @param pathName
     */
    public void openFileReader(String pathName) {
        HashMap<String, String> params = new HashMap<>();
        params.put("local", "true");//“true”表示是进入文件查看器，如果不设置或设置为“false”，则进入 miniqb 浏览器模式,非必填项
        JSONObject Object = new JSONObject();
        try {
            Object.put("pkgName", getApplicationContext().getPackageName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("style", "1");//“1”表示文件查看器使用微信的 UI 样式。不设置此 key或设置错误值，则为默认 UI 样式
        params.put("menuData", Object.toString());
        QbSdk.getMiniQBVersion(this);
        int ret = QbSdk.openFileReader(this, pathName, params, this);
        if (ret == -1 || ret == -6) {
            Toast.makeText(this, "文档加载失败，路径错误！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReceiveValue(String s) {

    }
}
