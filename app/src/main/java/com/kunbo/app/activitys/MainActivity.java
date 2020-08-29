/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.kunbo.app.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.kunbo.app.C;
import com.kunbo.app.R;
import com.kunbo.app.adapter.MainGridItemAdapter;
import com.kunbo.app.modle.User;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.net.bean.Result;
import com.kunbo.app.net.response.OnSuccessAndFaultListener;
import com.kunbo.app.net.response.OnSuccessAndFaultSub;
import com.kunbo.app.utils.GlideUtil;
import com.kunbo.app.utils.SPUtil;
import com.kunbo.app.utils.Utils;
import com.kunbo.app.view.statusBarUtil.StatusBarUtil;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.indicator.CircleIndicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kunbo.app.net.api.ApiSubscribe.getUncommitNum;
import static com.kunbo.app.net.api.ApiSubscribe.getUserInfo;

public class MainActivity extends BaseActivity implements OnSuccessAndFaultListener {
    @BindView(R.id.header_title)
    TextView title;
    @BindView(R.id.header_back)
    LinearLayout back;
    @BindView(R.id.drawer_ll)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.grid_onduty)
    RecyclerView gridOnduty;
    @BindView(R.id.grid_fee)
    RecyclerView gridFee;
    @BindView(R.id.grid_elec)
    RecyclerView gridElec;
    private long clickTime = 0; // 第一次点击返回键的时间
    private MyData ondutyDatas;
    private MainGridItemAdapter adapter;
    private int[] ondutyIcons = {R.mipmap.icon_agent, R.mipmap.icon_ticket, R.mipmap.icon_monitor};
    private String[] ondutyTitles = {"站长值班记录", "票证值班记录", "班长值班记录"};
    private int[] feeIcons = {R.mipmap.icon_lane, R.mipmap.icon_statistic, 0};
    private String[] feeTitles = {"车道特情", "收费数据统计", ""};
    private int[] elecIcons = {R.mipmap.icon_elect, 0, 0};
    private String[] elecTitles = {"机电设备统计", "", ""};
    private User user = User.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setStatusBarIn(this);

        ButterKnife.bind(this);
        back.setVisibility(View.INVISIBLE);
        title.setText(getString(R.string.app_name));
        initView();
        initData();
    }

    private void initView() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.RIGHT);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.setClickable(true);
                StatusBarUtil.setStatusBarIn(MainActivity.this);
                StatusBarUtil.setStatusBarColor(MainActivity.this, getResources().getColor(R.color.background_gradient_start));//深色背景
                StatusBarUtil.setStatusBarDarkTheme(MainActivity.this, false);//浅色文字主题色
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerView.setClickable(false);
                StatusBarUtil.setStatusBarIn(MainActivity.this);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

    }

    private void initData() {
        //getBanner(new OnSuccessAndFaultSub(this, "getBanner", this));
        getUncommitNum(new OnSuccessAndFaultSub(this, "getUncommitNum", this));
        getUserInfo(new OnSuccessAndFaultSub(this, "getUserInfo", this));

        List<String> urls = new ArrayList<String>();
        //urls.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2439437983,1896017170&fm=26&gp=0.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1597060000116&di=d608cc821a4c475519bfa55e3a20e033&imgtype=0&src=http%3A%2F%2Ffile02.16sucai.com%2Fd%2Ffile%2F2014%2F1020%2Fb78e340cb23e9343fcc6867bf5a89436.jpg");
        banner.setAdapter(new PolicyTopBannerAdapter(this, urls))
                .setIndicator(new CircleIndicator(this))
                .start();

        ondutyDatas = new MyData();
        for (int i = 0; i < ondutyIcons.length; i++) {
            MyRow row = new MyRow();
            row.put("count", 0);
            row.put("img", ondutyIcons[i]);
            row.put("title", ondutyTitles[i]);
            ondutyDatas.add(row);
        }
        adapter = new MainGridItemAdapter(this, ondutyDatas, 0);
        gridOnduty.setNestedScrollingEnabled(false);
        gridOnduty.setAdapter(adapter);

        MyData feeDatas = new MyData();
        for (int i = 0; i < feeIcons.length; i++) {
            MyRow row = new MyRow();
            row.put("count", 0);
            row.put("img", feeIcons[i]);
            row.put("title", feeTitles[i]);
            feeDatas.add(row);
        }
        MainGridItemAdapter adapter1 = new MainGridItemAdapter(this, feeDatas, 1);
        gridFee.setNestedScrollingEnabled(false);
        gridFee.setAdapter(adapter1);

        MyData elecDatas = new MyData();
        for (int i = 0; i < elecIcons.length; i++) {
            MyRow row = new MyRow();
            row.put("count", 0);
            row.put("img", elecIcons[i]);
            row.put("title", elecTitles[i]);
            elecDatas.add(row);
        }
        MainGridItemAdapter adapter2 = new MainGridItemAdapter(this, elecDatas, 2);
        gridElec.setNestedScrollingEnabled(false);
        gridElec.setAdapter(adapter2);

    }

    @OnClick({R.id.header_img_ll, R.id.login_out, R.id.header_tv_ll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_img_ll://个人信息抽屉
                mDrawerLayout.openDrawer(Gravity.LEFT);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                        Gravity.LEFT);
                break;
            case R.id.login_out://注销登录
                Utils.showOKCancelDialog(this, "温馨提示", "确认退出登录？", 1005, null);
                break;
            case R.id.header_tv_ll://数据可视化
                Intent in = new Intent(this, IdataChartActivity.class);
                startActivity(in);
                break;
        }
    }

    @Override
    public void processDialogOK(int type, Object tag) {
        super.processDialogOK(type, tag);
        SPUtil.saveSetting("password", "");
        Intent in = new Intent(this, LoginActivity.class);
        startActivity(in);
        finish();
    }

    public void onOndutyItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, AgentOndutyListActivity.class);
        switch (position) {
            case 0://站长值班记录
                intent.putExtra("from", 0);
                break;
            case 1://票证值班记录
                intent.putExtra("from", 1);
                break;
            case 2://班长值班记录
                intent.putExtra("from", 2);
                break;
        }
        startActivity(intent);
        overridePendingTransition(R.anim.forward_enter, R.anim.forward_exit);
    }

    public void onFeeItemClick(int position) {
        Intent intent;
        switch (position) {
            case 0://车道特情
                intent = new Intent(MainActivity.this, SpecialRecodeListActivity.class);
                startActivity(intent);
                break;
            case 1://收费数据统计
                intent = new Intent(MainActivity.this, StatisticAnalyzeActivity.class);
                startActivity(intent);
                break;
        }
        overridePendingTransition(R.anim.forward_enter, R.anim.forward_exit);
    }

    public void onElectItemClick(int position) {
        Intent intent;
        switch (position) {
            case 0://机电设备统计
                Toast.makeText(this, "功能正在发中，敬请期待！", Toast.LENGTH_LONG).show();
               /* intent = new Intent(MainActivity.this, StatisticAnalyzeActivity.class);
                startActivity(intent);*/
                break;
        }

        overridePendingTransition(R.anim.forward_enter, R.anim.forward_exit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int[] unReads = user.getUnReads();
        boolean change = false;
        for (int i = 0; i < 3; i++) {
            if (unReads[i] == 0) {
                change = true;
                ondutyDatas.get(i).put("count", 0);
            }
        }
        if (change)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccess(Result result, String method) {
        super.onSuccess(result, method);
        switch (method) {
            case "getBanner":

                break;
            case "getUncommitNum"://首页小红点
                MyRow ro = (MyRow) result.obj;
                if (ro != null) {
                    int[] unReads = user.getUnReads();
                    unReads[0] = Integer.parseInt(TextUtils.isEmpty(ro.getString("station")) ? "0" : ro.getString("station"));
                    unReads[1] = Integer.parseInt(TextUtils.isEmpty(ro.getString("ticket")) ? "0" : ro.getString("ticket"));
                    unReads[2] = Integer.parseInt(TextUtils.isEmpty(ro.getString("monitor")) ? "0" : ro.getString("monitor"));
                    user.setUnReads(unReads);
                    for (int i = 0; i < 3; i++) {
                        ondutyDatas.get(i).put("count", unReads[i]);
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case "getUserInfo":
                MyRow row = (MyRow) result.obj;
                if (row != null) {
                    //个人信息
                    MyRow info = (MyRow) row.get("person");
                    User.getInstance().setUserId(row.getString("userId"));
                    User.getInstance().setAccount(row.getString("userName"));
                    if (info != null) {//个人信息
                        User.getInstance().setPersonId(info.getString("id"));
                        User.getInstance().setName(info.getString("name"));
                        User.getInstance().setSex(info.getString("sex"));
                        User.getInstance().setAge(info.getString("age"));
                        User.getInstance().setCounty(info.getString("nativePlace"));
                        User.getInstance().setPhoto(C.BASE_URL_IMAGE + info.getString("imgUrl"));

                        User.getInstance().setTeamName(info.getString("teamName"));
                        User.getInstance().setTeamId(info.getString("teamId"));

                        User.getInstance().setStationId(info.getString("deptId"));
                        User.getInstance().setStationName(info.getString("deptName"));
                    }
                    //角色信息
                    MyData roles = (MyData) row.get("role");
                    if (roles != null && roles.size() > 0) {//角色信息
                        User.getInstance().setRole(roles.get(0).getString("roleCode"));
                        User.getInstance().setJob(roles.get(0).getString("roleDesc"));
                    }

                    if (!TextUtils.isEmpty(User.getInstance().getPhoto())) {
                        GlideUtil.getInstance().setImage(this, R.id.user_img, User.getInstance().getPhoto(), R.mipmap.header, true);
                    }
                    setEText(R.id.tv_name, User.getInstance().getName());
                    String county = !TextUtils.isEmpty(User.getInstance().getCounty() + "\t\t") ? User.getInstance().getCounty() : "";
                    setEText(R.id.tv_sex, !TextUtils.isEmpty(User.getInstance().getSex()) ? User.getInstance().getSex() : "" + county);
                    setEText(R.id.tv_station, User.getInstance().getStationName());
                    setEText(R.id.tv_mobile, !TextUtils.isEmpty(User.getInstance().getTeamName()) ? User.getInstance().getTeamName() : "");
                }
                break;
        }
    }

    /**
     * banner自定义布局
     */
    static class PolicyTopBannerAdapter extends BannerAdapter<String, PolicyTopBannerAdapter.BannerViewHolder> {
        private Context context;

        public PolicyTopBannerAdapter(Context context, List<String> mDatas) {
            super(mDatas);
            this.context = context;
        }

        //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
        @Override
        public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            //注意，必须设置为match_parent，这个是viewpager2强制要求的
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return new BannerViewHolder(imageView);
        }

        @Override
        public void onBindView(BannerViewHolder holder, String data, int position, int size) {
            GlideUtil.getInstance().setImage(context, holder.imageView, data);
        }

        class BannerViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public BannerViewHolder(@NonNull ImageView view) {
                super(view);
                this.imageView = view;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitApp() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Toast.makeText(this, "亲，再按一次就退出应用了哦 ^__^ ", Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
