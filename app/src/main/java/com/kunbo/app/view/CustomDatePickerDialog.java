/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kunbo.app.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.kunbo.app.R;
import com.kunbo.app.utils.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 自定义时间日期选择器
 */
public class CustomDatePickerDialog implements View.OnClickListener, CustomDatePickerView.OnSelectListener {
    private static final String DATE_FORMAT_PATTERN_YMD = "yyyy-MM-dd";
    private static final String DATE_FORMAT_PATTERN_YMD_HM = "yyyy-MM-dd HH:mm";
    private static final String DATE_FORMAT_PATTERN_HM = "HH:mm";
    private Context mContext;
    private Callback mCallback;
    private Calendar mBeginTime, mEndTime, mSelectedTime;
    private boolean mCanDialogShow;

    private Dialog mPickerDialog;
    private CustomDatePickerView mDpvYear, mDpvMonth, mDpvDay, mDpvHour, mDpvMinute;
    private TextView mTitle, mTvYearUnit, mTvMonthUnit, mTvDayUnit, mTvHourUnit, mTvMinuteUnit;

    private View clickView;//触发日期控件的view

    private int mBeginYear, mBeginMonth, mBeginDay, mBeginHour, mBeginMinute,
            mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;
    private List<String> mYearUnits = new ArrayList<>(), mMonthUnits = new ArrayList<>(), mDayUnits = new ArrayList<>(),
            mHourUnits = new ArrayList<>(), mMinuteUnits = new ArrayList<>();
    private DecimalFormat mDecimalFormat = new DecimalFormat("00");

    private boolean mCanShowTime, mCanshowDate;
    private int mScrollUnits = SCROLL_UNIT_HOUR + SCROLL_UNIT_MINUTE;

    /**
     * 时间单位：时、分
     */
    private static final int SCROLL_UNIT_HOUR = 0b1;
    private static final int SCROLL_UNIT_MINUTE = 0b10;

    /**
     * 时间单位的最大显示值
     */
    private static final int MAX_MINUTE_UNIT = 59;
    private static final int MAX_HOUR_UNIT = 23;
    private static final int MAX_MONTH_UNIT = 12;

    /**
     * 级联滚动延迟时间
     */
    private static final long LINKAGE_DELAY_DEFAULT = 100L;

    /**
     * 时间选择结果回调接口
     */
    public interface Callback {
        void onTimeSelected(View v, long timestamp);
    }

    /**
     * 通过时间戳初始换时间选择器，毫秒级别
     *
     * @param context      Activity Context
     * @param callback     选择结果回调
     * @param beginDateStr 日期字符串，格式为 yyyy-MM-dd HH:mm
     * @param endDateStr   日期字符串，格式为 yyyy-MM-dd HH:mm
     */
    public CustomDatePickerDialog(Context context, Callback callback, String beginDateStr, String endDateStr) {
        long beginTimestamp = 0, endTimestamp = 0;
        if (Utils.checkNullString(beginDateStr)) {
            beginDateStr = "1900-01-01 00:00";
            beginTimestamp = str2Long(beginDateStr, mCanshowDate, mCanShowTime);
        }
        if (Utils.checkNullString(endDateStr)) {
            endDateStr = "2099-12-31 00:00";
            endTimestamp = str2Long(endDateStr, mCanshowDate, mCanShowTime);
        }
        if (beginTimestamp >= endTimestamp) {
            Toast.makeText(context,"开始日期不能大于结束日期！",Toast.LENGTH_SHORT).show();
            return;
        }
        if (context == null || callback == null || beginTimestamp >= endTimestamp) {
            mCanDialogShow = false;
            return;
        }
        mContext = context;
        mCallback = callback;
        mBeginTime = Calendar.getInstance();
        mBeginTime.setTimeInMillis(beginTimestamp);
        mEndTime = Calendar.getInstance();
        mEndTime.setTimeInMillis(endTimestamp);
        mSelectedTime = Calendar.getInstance();

        initView();
        initData();
        mCanDialogShow = true;
    }

    private void initView() {
        mPickerDialog = new Dialog(mContext, R.style.date_picker_dialog);
        mPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPickerDialog.setContentView(R.layout.dialog_date_picker);

        Window window = mPickerDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }

        mPickerDialog.findViewById(R.id.tv_cancel).setOnClickListener(this);
        mPickerDialog.findViewById(R.id.tv_confirm).setOnClickListener(this);
        mTvYearUnit = mPickerDialog.findViewById(R.id.tv_year_unit);
        mTvMonthUnit = mPickerDialog.findViewById(R.id.tv_month_unit);
        mTvDayUnit = mPickerDialog.findViewById(R.id.tv_day_unit);
        mTvHourUnit = mPickerDialog.findViewById(R.id.tv_hour_unit);
        mTvMinuteUnit = mPickerDialog.findViewById(R.id.tv_minute_unit);

        mTitle = mPickerDialog.findViewById(R.id.tv_title);
        mDpvYear = mPickerDialog.findViewById(R.id.dpv_year);
        mDpvYear.setOnSelectListener(this);
        mDpvMonth = mPickerDialog.findViewById(R.id.dpv_month);
        mDpvMonth.setOnSelectListener(this);
        mDpvDay = mPickerDialog.findViewById(R.id.dpv_day);
        mDpvDay.setOnSelectListener(this);
        mDpvHour = mPickerDialog.findViewById(R.id.dpv_hour);
        mDpvHour.setOnSelectListener(this);
        mDpvMinute = mPickerDialog.findViewById(R.id.dpv_minute);
        mDpvMinute.setOnSelectListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_cancel) {
            String str = ((TextView) clickView).getText().toString();
            if (str.length() < 20)
                ((TextView) clickView).setText("");
            dismissDialog();
        } else if (i == R.id.tv_confirm) {
            if (mCallback != null) {
                mCallback.onTimeSelected(clickView, mSelectedTime.getTimeInMillis());
            }
        }
    }

    public void dismissDialog() {
        if (mPickerDialog != null && mPickerDialog.isShowing()) {
            mPickerDialog.dismiss();
        }
    }

    @Override
    public void onSelect(View view, String selected) {
        if (view == null || TextUtils.isEmpty(selected)) return;

        int timeUnit;
        try {
            timeUnit = Integer.parseInt(selected);
        } catch (Throwable ignored) {
            return;
        }
        int i = view.getId();
        if (i == R.id.dpv_year) {
            mSelectedTime.set(Calendar.YEAR, timeUnit);
            linkageMonthUnit(true, LINKAGE_DELAY_DEFAULT);
        } else if (i == R.id.dpv_month) {// 防止类似 2018/12/31 滚动到11月时因溢出变成 2018/12/01
            int lastSelectedMonth = mSelectedTime.get(Calendar.MONTH) + 1;
            mSelectedTime.add(Calendar.MONTH, timeUnit - lastSelectedMonth);
            linkageDayUnit(true, LINKAGE_DELAY_DEFAULT);
        } else if (i == R.id.dpv_day) {
            mSelectedTime.set(Calendar.DAY_OF_MONTH, timeUnit);
            linkageHourUnit(true, LINKAGE_DELAY_DEFAULT);
        } else if (i == R.id.dpv_hour) {
            mSelectedTime.set(Calendar.HOUR_OF_DAY, timeUnit);
            linkageMinuteUnit(true);
        } else if (i == R.id.dpv_minute) {
            mSelectedTime.set(Calendar.MINUTE, timeUnit);
        }
    }

    private void initData() {
        mSelectedTime.setTimeInMillis(mBeginTime.getTimeInMillis());

        mBeginYear = mBeginTime.get(Calendar.YEAR);
        // Calendar.MONTH 值为 0-11
        mBeginMonth = mBeginTime.get(Calendar.MONTH) + 1;
        mBeginDay = mBeginTime.get(Calendar.DAY_OF_MONTH);
        mBeginHour = mBeginTime.get(Calendar.HOUR_OF_DAY);
        mBeginMinute = mBeginTime.get(Calendar.MINUTE);

        mEndYear = mEndTime.get(Calendar.YEAR);
        mEndMonth = mEndTime.get(Calendar.MONTH) + 1;
        mEndDay = mEndTime.get(Calendar.DAY_OF_MONTH);
        mEndHour = mEndTime.get(Calendar.HOUR_OF_DAY);
        mEndMinute = mEndTime.get(Calendar.MINUTE);

        boolean canSpanYear = mBeginYear != mEndYear;
        boolean canSpanMon = !canSpanYear && mBeginMonth != mEndMonth;
        boolean canSpanDay = !canSpanMon && mBeginDay != mEndDay;
        boolean canSpanHour = !canSpanDay && mBeginHour != mEndHour;
        boolean canSpanMinute = !canSpanHour && mBeginMinute != mEndMinute;
        if (canSpanYear) {
            initDateUnits(MAX_MONTH_UNIT, mBeginTime.getActualMaximum(Calendar.DAY_OF_MONTH), MAX_HOUR_UNIT, MAX_MINUTE_UNIT);
        } else if (canSpanMon) {
            initDateUnits(mEndMonth, mBeginTime.getActualMaximum(Calendar.DAY_OF_MONTH), MAX_HOUR_UNIT, MAX_MINUTE_UNIT);
        } else if (canSpanDay) {
            initDateUnits(mEndMonth, mEndDay, MAX_HOUR_UNIT, MAX_MINUTE_UNIT);
        } else if (canSpanHour) {
            initDateUnits(mEndMonth, mEndDay, mEndHour, MAX_MINUTE_UNIT);
        } else if (canSpanMinute) {
            initDateUnits(mEndMonth, mEndDay, mEndHour, mEndMinute);
        }
    }

    private void initDateUnits(int endMonth, int endDay, int endHour, int endMinute) {
        for (int i = mBeginYear; i <= mEndYear; i++) {
            mYearUnits.add(String.valueOf(i));
        }

        for (int i = mBeginMonth; i <= endMonth; i++) {
            mMonthUnits.add(mDecimalFormat.format(i));
        }

        for (int i = mBeginDay; i <= endDay; i++) {
            mDayUnits.add(mDecimalFormat.format(i));
        }

        if ((mScrollUnits & SCROLL_UNIT_HOUR) != SCROLL_UNIT_HOUR) {
            mHourUnits.add(mDecimalFormat.format(mBeginHour));
        } else {
            for (int i = mBeginHour; i <= endHour; i++) {
                mHourUnits.add(mDecimalFormat.format(i));
            }
        }

        if ((mScrollUnits & SCROLL_UNIT_MINUTE) != SCROLL_UNIT_MINUTE) {
            mMinuteUnits.add(mDecimalFormat.format(mBeginMinute));
        } else {
            for (int i = mBeginMinute; i <= endMinute; i++) {
                mMinuteUnits.add(mDecimalFormat.format(i));
            }
        }

        mDpvYear.setDataList(mYearUnits);
        mDpvYear.setSelected(0);
        mDpvMonth.setDataList(mMonthUnits);
        mDpvMonth.setSelected(0);
        mDpvDay.setDataList(mDayUnits);
        mDpvDay.setSelected(0);
        mDpvHour.setDataList(mHourUnits);
        mDpvHour.setSelected(0);
        mDpvMinute.setDataList(mMinuteUnits);
        mDpvMinute.setSelected(0);

        setCanScroll();
    }

    private void setCanScroll() {
        mDpvYear.setCanScroll(mYearUnits.size() > 1);
        mDpvMonth.setCanScroll(mMonthUnits.size() > 1);
        mDpvDay.setCanScroll(mDayUnits.size() > 1);
        mDpvHour.setCanScroll(mHourUnits.size() > 1 && (mScrollUnits & SCROLL_UNIT_HOUR) == SCROLL_UNIT_HOUR);
        mDpvMinute.setCanScroll(mMinuteUnits.size() > 1 && (mScrollUnits & SCROLL_UNIT_MINUTE) == SCROLL_UNIT_MINUTE);
    }

    /**
     * 联动“月”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private void linkageMonthUnit(final boolean showAnim, final long delay) {
        int minMonth;
        int maxMonth;
        int selectedYear = mSelectedTime.get(Calendar.YEAR);
        if (mBeginYear == mEndYear) {
            minMonth = mBeginMonth;
            maxMonth = mEndMonth;
        } else if (selectedYear == mBeginYear) {
            minMonth = mBeginMonth;
            maxMonth = MAX_MONTH_UNIT;
        } else if (selectedYear == mEndYear) {
            minMonth = 1;
            maxMonth = mEndMonth;
        } else {
            minMonth = 1;
            maxMonth = MAX_MONTH_UNIT;
        }

        // 重新初始化时间单元容器
        mMonthUnits.clear();
        for (int i = minMonth; i <= maxMonth; i++) {
            mMonthUnits.add(mDecimalFormat.format(i));
        }
        mDpvMonth.setDataList(mMonthUnits);

        // 确保联动时不会溢出或改变关联选中值
        int selectedMonth = getValueInRange(mSelectedTime.get(Calendar.MONTH) + 1, minMonth, maxMonth);
        mSelectedTime.set(Calendar.MONTH, selectedMonth - 1);
        mDpvMonth.setSelected(selectedMonth - minMonth);
        if (showAnim) {
            mDpvMonth.startAnim();
        }

        // 联动“日”变化
        mDpvMonth.postDelayed(new Runnable() {
            @Override
            public void run() {
                linkageDayUnit(showAnim, delay);
            }
        }, delay);
    }

    /**
     * 联动“日”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private void linkageDayUnit(final boolean showAnim, final long delay) {
        int minDay;
        int maxDay;
        int selectedYear = mSelectedTime.get(Calendar.YEAR);
        int selectedMonth = mSelectedTime.get(Calendar.MONTH) + 1;
        if (mBeginYear == mEndYear && mBeginMonth == mEndMonth) {
            minDay = mBeginDay;
            maxDay = mEndDay;
        } else if (selectedYear == mBeginYear && selectedMonth == mBeginMonth) {
            minDay = mBeginDay;
            maxDay = mSelectedTime.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else if (selectedYear == mEndYear && selectedMonth == mEndMonth) {
            minDay = 1;
            maxDay = mEndDay;
        } else {
            minDay = 1;
            maxDay = mSelectedTime.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        mDayUnits.clear();
        for (int i = minDay; i <= maxDay; i++) {
            mDayUnits.add(mDecimalFormat.format(i));
        }
        mDpvDay.setDataList(mDayUnits);

        int selectedDay = getValueInRange(mSelectedTime.get(Calendar.DAY_OF_MONTH), minDay, maxDay);
        mSelectedTime.set(Calendar.DAY_OF_MONTH, selectedDay);
        mDpvDay.setSelected(selectedDay - minDay);
        if (showAnim) {
            mDpvDay.startAnim();
        }

        mDpvDay.postDelayed(new Runnable() {
            @Override
            public void run() {
                linkageHourUnit(showAnim, delay);
            }
        }, delay);
    }

    /**
     * 联动“时”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private void linkageHourUnit(final boolean showAnim, final long delay) {
        if ((mScrollUnits & SCROLL_UNIT_HOUR) == SCROLL_UNIT_HOUR) {
            int minHour;
            int maxHour;
            int selectedYear = mSelectedTime.get(Calendar.YEAR);
            int selectedMonth = mSelectedTime.get(Calendar.MONTH) + 1;
            int selectedDay = mSelectedTime.get(Calendar.DAY_OF_MONTH);
            if (mBeginYear == mEndYear && mBeginMonth == mEndMonth && mBeginDay == mEndDay) {
                minHour = mBeginHour;
                maxHour = mEndHour;
            } else if (selectedYear == mBeginYear && selectedMonth == mBeginMonth && selectedDay == mBeginDay) {
                minHour = mBeginHour;
                maxHour = MAX_HOUR_UNIT;
            } else if (selectedYear == mEndYear && selectedMonth == mEndMonth && selectedDay == mEndDay) {
                minHour = 0;
                maxHour = mEndHour;
            } else {
                minHour = 0;
                maxHour = MAX_HOUR_UNIT;
            }

            mHourUnits.clear();
            for (int i = minHour; i <= maxHour; i++) {
                mHourUnits.add(mDecimalFormat.format(i));
            }
            mDpvHour.setDataList(mHourUnits);

            int selectedHour = getValueInRange(mSelectedTime.get(Calendar.HOUR_OF_DAY), minHour, maxHour);
            mSelectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
            mDpvHour.setSelected(selectedHour - minHour);
            if (showAnim) {
                mDpvHour.startAnim();
            }
        }

        mDpvHour.postDelayed(new Runnable() {
            @Override
            public void run() {
                linkageMinuteUnit(showAnim);
            }
        }, delay);
    }

    /**
     * 联动“分”变化
     *
     * @param showAnim 是否展示滚动动画
     */
    private void linkageMinuteUnit(final boolean showAnim) {
        if ((mScrollUnits & SCROLL_UNIT_MINUTE) == SCROLL_UNIT_MINUTE) {
            int minMinute;
            int maxMinute;
            int selectedYear = mSelectedTime.get(Calendar.YEAR);
            int selectedMonth = mSelectedTime.get(Calendar.MONTH) + 1;
            int selectedDay = mSelectedTime.get(Calendar.DAY_OF_MONTH);
            int selectedHour = mSelectedTime.get(Calendar.HOUR_OF_DAY);
            if (mBeginYear == mEndYear && mBeginMonth == mEndMonth && mBeginDay == mEndDay && mBeginHour == mEndHour) {
                minMinute = mBeginMinute;
                maxMinute = mEndMinute;
            } else if (selectedYear == mBeginYear && selectedMonth == mBeginMonth && selectedDay == mBeginDay && selectedHour == mBeginHour) {
                minMinute = mBeginMinute;
                maxMinute = MAX_MINUTE_UNIT;
            } else if (selectedYear == mEndYear && selectedMonth == mEndMonth && selectedDay == mEndDay && selectedHour == mEndHour) {
                minMinute = 0;
                maxMinute = mEndMinute;
            } else {
                minMinute = 0;
                maxMinute = MAX_MINUTE_UNIT;
            }

            mMinuteUnits.clear();
            for (int i = minMinute; i <= maxMinute; i++) {
                mMinuteUnits.add(mDecimalFormat.format(i));
            }
            mDpvMinute.setDataList(mMinuteUnits);

            int selectedMinute = getValueInRange(mSelectedTime.get(Calendar.MINUTE), minMinute, maxMinute);
            mSelectedTime.set(Calendar.MINUTE, selectedMinute);
            mDpvMinute.setSelected(selectedMinute - minMinute);
            if (showAnim) {
                mDpvMinute.startAnim();
            }
        }

        setCanScroll();
    }

    private int getValueInRange(int value, int minValue, int maxValue) {
        if (value < minValue) {
            return minValue;
        } else if (value > maxValue) {
            return maxValue;
        } else {
            return value;
        }
    }

    /**
     * 展示时间选择器
     *
     * @param dateStr 日期字符串，格式为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm
     */
    public void show(View view, String title, String dateStr) {
        if (!canShow() || TextUtils.isEmpty(dateStr)) return;
        if (!TextUtils.isEmpty(title)) {
            if (title.contains("开始")) {
                mTitle.setTextColor(Color.GREEN);
            } else {
                mTitle.setTextColor(Color.RED);
            }
            mTitle.setText(title);
        }
        // 弹窗时，考虑用户体验，不展示滚动动画
        if (setSelectedTime(dateStr, false)) {
            clickView = view;
            mPickerDialog.show();
        }
    }

    private boolean canShow() {
        return mCanDialogShow && mPickerDialog != null;
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param dateStr  日期字符串
     * @param showAnim 是否展示动画
     * @return 是否设置成功
     */
    public boolean setSelectedTime(String dateStr, boolean showAnim) {
        return canShow() && !TextUtils.isEmpty(dateStr)
                && setSelectedTime(str2Long(dateStr, mCanshowDate, mCanShowTime), showAnim);
    }

    /**
     * 展示时间选择器
     *
     * @param timestamp 时间戳，毫秒级别
     */
    public void show(long timestamp) {
        if (!canShow()) return;

        if (setSelectedTime(timestamp, false)) {
            mPickerDialog.show();
        }
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param timestamp 毫秒级时间戳
     * @param showAnim  是否展示动画
     * @return 是否设置成功
     */
    public boolean setSelectedTime(long timestamp, boolean showAnim) {
        if (!canShow()) return false;

        if (timestamp < mBeginTime.getTimeInMillis()) {
            timestamp = mBeginTime.getTimeInMillis();
        } else if (timestamp > mEndTime.getTimeInMillis()) {
            timestamp = mEndTime.getTimeInMillis();
        }
        mSelectedTime.setTimeInMillis(timestamp);

        mYearUnits.clear();
        for (int i = mBeginYear; i <= mEndYear; i++) {
            mYearUnits.add(String.valueOf(i));
        }
        mDpvYear.setDataList(mYearUnits);
        mDpvYear.setSelected(mSelectedTime.get(Calendar.YEAR) - mBeginYear);
        linkageMonthUnit(showAnim, showAnim ? LINKAGE_DELAY_DEFAULT : 0);
        return true;
    }

    /**
     * 设置是否允许点击屏幕或物理返回键关闭
     */
    public void setCancelable(boolean cancelable) {
        if (!canShow()) return;

        mPickerDialog.setCancelable(cancelable);
    }

    /**
     * 设置是否显示月、日 （默认不显示时分）
     */
    public void setCanShowMonthDay(boolean showMonth, boolean showDay) {
        if (!canShow()) return;
        if (showMonth && !showDay) {//Y-M
            initScrollUnit();
            mDpvYear.setVisibility(View.VISIBLE);
            mTvYearUnit.setVisibility(View.VISIBLE);
            mDpvMonth.setVisibility(View.VISIBLE);
            mTvMonthUnit.setVisibility(View.VISIBLE);
            mDpvDay.setVisibility(View.GONE);
            mTvDayUnit.setVisibility(View.GONE);
        } else if (!showMonth && !showDay) {// Y
            initScrollUnit();
            mDpvYear.setVisibility(View.VISIBLE);
            mTvYearUnit.setVisibility(View.VISIBLE);
            mDpvMonth.setVisibility(View.GONE);
            mTvMonthUnit.setVisibility(View.GONE);
            mDpvDay.setVisibility(View.GONE);
            mTvDayUnit.setVisibility(View.GONE);
        } else {
            initScrollUnit();
            mDpvYear.setVisibility(View.VISIBLE);
            mTvYearUnit.setVisibility(View.VISIBLE);
            mDpvMonth.setVisibility(View.VISIBLE);
            mTvMonthUnit.setVisibility(View.VISIBLE);
            mDpvDay.setVisibility(View.VISIBLE);
            mTvDayUnit.setVisibility(View.VISIBLE);
        }
        mDpvHour.setVisibility(View.GONE);
        mTvHourUnit.setVisibility(View.GONE);
        mDpvMinute.setVisibility(View.GONE);
        mTvMinuteUnit.setVisibility(View.GONE);
        mCanShowTime = false;
        mCanshowDate = true;
    }

    /**
     * 设置日期控件是否显示时和分（默认显示年月日时分）
     */
    public void setCanShowDateTime(boolean canshowDate, boolean canShowHour, boolean canshowMinute) {
        if (!canShow()) return;
        if (!canshowDate && !canShowHour && !canshowMinute) {
            mCanDialogShow = false;
        }

        if (canshowDate) {
            if (!canShowHour && !canshowMinute) {//Y-M-D
                initScrollUnit(SCROLL_UNIT_HOUR, SCROLL_UNIT_MINUTE);
                mDpvHour.setVisibility(View.GONE);
                mTvHourUnit.setVisibility(View.GONE);
                mDpvMinute.setVisibility(View.GONE);
                mTvMinuteUnit.setVisibility(View.GONE);
            } else if (!canshowMinute) {//Y-M-D h
                initScrollUnit();
                mDpvHour.setVisibility(View.VISIBLE);
                mTvHourUnit.setVisibility(View.VISIBLE);
                mDpvMinute.setVisibility(View.GONE);
                mTvMinuteUnit.setVisibility(View.GONE);
            } else {//Y-M-D h:m
                initScrollUnit();
                mDpvHour.setVisibility(View.VISIBLE);
                mTvHourUnit.setVisibility(View.VISIBLE);
                mDpvMinute.setVisibility(View.VISIBLE);
                mTvMinuteUnit.setVisibility(View.VISIBLE);
            }
        } else {// h:m
            initScrollUnit();
            mDpvYear.setVisibility(View.GONE);
            mTvYearUnit.setVisibility(View.GONE);
            mDpvMonth.setVisibility(View.GONE);
            mTvMonthUnit.setVisibility(View.GONE);
            mDpvDay.setVisibility(View.GONE);
            mTvDayUnit.setVisibility(View.GONE);
            mDpvHour.setVisibility(View.VISIBLE);
            mTvHourUnit.setVisibility(View.VISIBLE);
            mDpvMinute.setVisibility(View.VISIBLE);
            mTvMinuteUnit.setVisibility(View.VISIBLE);
        }
        mCanShowTime = canShowHour;
        mCanshowDate = canshowDate;
    }

    private void initScrollUnit(Integer... units) {
        if (units == null || units.length == 0) {
            mScrollUnits = SCROLL_UNIT_HOUR + SCROLL_UNIT_MINUTE;
        } else {
            for (int unit : units) {
                mScrollUnits ^= unit;
            }
        }
    }

    /**
     * 设置日期控件是否可以循环滚动
     */
    public void setScrollLoop(boolean canLoop) {
        if (!canShow()) return;

        mDpvYear.setCanScrollLoop(canLoop);
        mDpvMonth.setCanScrollLoop(canLoop);
        mDpvDay.setCanScrollLoop(canLoop);
        mDpvHour.setCanScrollLoop(canLoop);
        mDpvMinute.setCanScrollLoop(canLoop);
    }

    /**
     * 设置日期控件是否展示滚动动画
     */
    public void setCanShowAnim(boolean canShowAnim) {
        if (!canShow()) return;

        mDpvYear.setCanShowAnim(canShowAnim);
        mDpvMonth.setCanShowAnim(canShowAnim);
        mDpvDay.setCanShowAnim(canShowAnim);
        mDpvHour.setCanShowAnim(canShowAnim);
        mDpvMinute.setCanShowAnim(canShowAnim);
    }

    /**
     * 销毁弹窗
     */
    public void onDestroy() {
        if (mPickerDialog != null) {
            mPickerDialog.dismiss();
            mPickerDialog = null;
            mDpvYear.onDestroy();
            mDpvMonth.onDestroy();
            mDpvDay.onDestroy();
            mDpvHour.onDestroy();
            mDpvMinute.onDestroy();
        }
    }

    /**
     * 时间戳转字符串
     *
     * @param timestamp     时间戳
     * @param isPreciseTime 是否包含时分
     * @return 格式化的日期字符串
     */
    public static String long2Str(long timestamp, boolean isShowDate, boolean isPreciseTime) {
        return long2Str(timestamp, getFormatPattern(isShowDate, isPreciseTime));
    }

    private static String long2Str(long timestamp, String pattern) {
        return new SimpleDateFormat(pattern, Locale.CHINA).format(new Date(timestamp));
    }

    /**
     * 字符串转时间戳
     *
     * @param dateStr       日期字符串
     * @param isPreciseTime 是否包含时分
     * @return 时间戳
     */
    public static long str2Long(String dateStr, boolean isShowDate, boolean isPreciseTime) {
        return str2Long(dateStr, getFormatPattern(isShowDate, isPreciseTime));
    }

    private static long str2Long(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat(pattern, Locale.CHINA).parse(dateStr).getTime();
        } catch (Throwable ignored) {
        }
        return 0;
    }

    private static String getFormatPattern(boolean showDate, boolean showSpecificTime) {
        if (showDate && showSpecificTime) {
            return DATE_FORMAT_PATTERN_YMD_HM;
        } else if (!showDate && showSpecificTime) {
            return DATE_FORMAT_PATTERN_HM;
        } else {
            return DATE_FORMAT_PATTERN_YMD;
        }
    }

}