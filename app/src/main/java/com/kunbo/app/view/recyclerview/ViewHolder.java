package com.kunbo.app.view.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kunbo.app.utils.GlideUtil;

public class ViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> views;
    public View itemView;
    private Context context;
    private GlideUtil glideUtil = GlideUtil.getInstance();

    public ViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        this.views = new SparseArray<>();
        this.itemView = itemView;
    }

    public static ViewHolder createViewHolder(Context context, View itemView) {
        ViewHolder holder = new ViewHolder(context, itemView);
        return holder;
    }

    public static ViewHolder createViewHolder(Context context,
                                              ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        ViewHolder holder = new ViewHolder(context, itemView);
        return holder;
    }

    public <T extends View> T getView(int id) {
        View view = views.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            views.put(id, view);
        }
        return (T) view;
    }

    /**
     * 获取当前条目
     */
    public View getItemView() {
        return itemView;
    }


    /**************************************以下为辅助方法************************************************/

    public ViewHolder setText(int id, CharSequence text) {
        View view = getView(id);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        }
        return this;
    }

    public ViewHolder setTextColor(int id, int color) {
        View view = getView(id);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(context.getResources().getColor(color));
        }
        return this;
    }

    public ViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public ViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    /**
     * 隐藏控件
     */
    public void hide(int resId) {
        View view = getView(resId);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 显示控件
     */
    public void show(int resId) {
        View view = getView(resId);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置可见
     */
    public ViewHolder setVisibility(int id, int visible) {
        getView(id).setVisibility(visible);
        return this;
    }

    public ViewHolder setImage(int id, Object model) {
        View view = getView(id);
        if (view instanceof ImageView) {
            glideUtil.setImage(context, (ImageView) view, model);
        }
        return this;
    }

    /**
     * 设置网络图片
     *
     * @param id
     * @param model
     * @param isCircle 是否加载圆图
     * @return
     */
    public ViewHolder setImage(int id, Object model, boolean isCircle) {
        View view = getView(id);
        if (view instanceof ImageView) {
            glideUtil.setImage(context, (ImageView) view, model, isCircle);
        }
        return this;
    }

    /**
     * @param id
     * @param model
     * @param isCircle 是否是否加载圆图（false）
     * @param rounde   加载圆角图片，圆角角度
     * @return
     */
    public ViewHolder setImage(int id, Object model, boolean isCircle, int rounde) {
        View view = getView(id);
        if (view instanceof ImageView) {
            glideUtil.setImage(context, (ImageView) view, model, false, rounde);
        }
        return this;
    }

    /**
     * @param model
     * @param isCircle 是否是否加载圆图（false）
     * @param rounde   加载圆角图片，圆角角度
     * @return
     */
    public ViewHolder setImage(ImageView view, Object model, boolean isCircle, int rounde) {
        glideUtil.setImage(context, view, model, false, rounde);
        return this;
    }

    /**
     * 加载图片并设置为指定大小
     *
     * @param id
     * @param model
     * @return
     */
    public ViewHolder setOverrideImage(int id, Object model, int width, int height, boolean isCircle, int rounded) {
        View view = getView(id);
        if (view instanceof ImageView) {
            glideUtil.loadOverrideImage(context, (ImageView) view, model, width, height, isCircle, rounded);
        }
        return this;
    }

    /**
     * @param id
     * @param model
     * @param defaultResId 加载失败的默认图片
     * @return
     */
    public ViewHolder setImage(int id, Object model, int defaultResId) {
        View view = getView(id);
        if (view instanceof ImageView) {
            glideUtil.setImage(context, (ImageView) view, model, defaultResId);
        }
        return this;
    }

    /**
     * 设置hint
     */
    public ViewHolder setHint(int id, CharSequence hint) {
        View view = getView(id);
        if (view instanceof TextView) {
            ((TextView) view).setHint(hint);
        }
        return this;
    }

    /**
     * 设置透明度
     */
    public ViewHolder setAlpha(int viewId, float value) {
        getView(viewId).setAlpha(value);
        return this;
    }

    /**
     * 设置图片资源
     */
    public ViewHolder setImageResource(int id, int drawableRes) {
        View view = getView(id);
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(drawableRes);
        } else {
            view.setBackgroundResource(drawableRes);
        }
        return this;
    }

    public ViewHolder setImageBitmap(int id, Bitmap bm) {
        View view = getView(id);
        if (view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(bm);
        }
        return this;
    }

    public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
        View view = getView(viewId);
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(drawable);
        }
        return this;
    }

    /**
     * 设置RatingBar的星级
     *
     * @param viewId
     * @param rating
     * @return
     */
    public ViewHolder setRating(int viewId, float rating) {
        RatingBar view = getView(viewId);
        view.setRating(rating);
        return this;
    }

    /**
     * 设置标签
     */
    public ViewHolder setTag(int id, Object obj) {
        getView(id).setTag(obj);
        return this;
    }

    public ViewHolder setTag(int viewId, int key, Object tag) {
        View view = getView(viewId);
        view.setTag(key, tag);
        return this;
    }

    /**
     * 设置选中
     */
    public ViewHolder setSelected(int id, boolean isChecked) {
        getView(id).setSelected(isChecked);
        return this;
    }

    public ViewHolder setChecked(int id, boolean isChecked) {
        View view = getView(id);
        if (view instanceof CheckBox) {
            ((CheckBox) view).setChecked(isChecked);
        } else if (view instanceof Switch) {
            ((Switch) view).setChecked(isChecked);
        } else if (view instanceof RadioButton) {
            ((RadioButton) view).setChecked(isChecked);
        }
        return this;
    }

    public ViewHolder setOnTouchListener(int id, View.OnTouchListener touchListener) {
        View view = getView(id);
        if (view instanceof EditText) {
            view.setOnTouchListener(touchListener);
        }
        return this;
    }

    public ViewHolder addTextChangedListener(int id, TextWatcher textWatcher) {
        View view = getView(id);
        if (view instanceof EditText) {
            ((EditText) view).addTextChangedListener(textWatcher);
        }
        return this;
    }

    //设置焦点监听，当获取到焦点的时候才给它设置内容变化监听解决卡的问题
    public ViewHolder setOnFocusChangeListener(int id, View.OnFocusChangeListener onFocusChangeListener) {
        View view = getView(id);
        if (view instanceof EditText) {
            view.setOnFocusChangeListener(onFocusChangeListener);
        }
        return this;
    }

    public ViewHolder setOnCheckedChangeListener(int id, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        View view = getView(id);
        if (view instanceof CheckBox) {
            ((CheckBox) view).setOnCheckedChangeListener(onCheckedChangeListener);
        } else if (view instanceof Switch) {
            ((Switch) view).setOnCheckedChangeListener(onCheckedChangeListener);
        } else if (view instanceof RadioButton) {
            ((RadioButton) view).setOnCheckedChangeListener(onCheckedChangeListener);
        }

        return this;
    }

    /**
     * 点击事件
     */
    public ViewHolder setOnClickListener(int viewId,
                                         View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

}