package com.kunbo.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kunbo.app.R;

import java.io.File;
import java.security.MessageDigest;

public class GlideUtil<T> {

    public final int BLUR_VALUE = 20; //模糊
    public final int CORNER_RADIUS = 20; //圆角
    public final float THUMB_SIZE = 0.5f; //0-1之间  10%原图的大小


    private static GlideUtil mInstance;
    private int loadingImage = R.drawable.placeholderimg;

    private GlideUtil() {
    }

    public static GlideUtil getInstance() {
        if (mInstance == null) {
            synchronized (GlideUtil.class) {
                if (mInstance == null) {
                    mInstance = new GlideUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 常规图
     */
    public void setImage(Context context, ImageView iv, T model) {
        setImage(context, iv, model, 0, false, 0);
    }

    public void setImage(Activity a, int ImageViewId, T resValue) {
        View view = a.findViewById(ImageViewId);
        if (view instanceof ImageView) {
            setImage(a, (ImageView) view, resValue);
        }

    }


    /**
     * 加载圆图
     */
    public void setImage(Context context, ImageView iv, T model, boolean isCircle) {
        setImage(context, iv, model, 0, isCircle, 0);
    }

    /**
     * 加载圆图,带默认图片
     */
    public void setImage(Context context, ImageView iv, T model, int loadingimg, boolean isCircle) {
        setImage(context, iv, model, loadingimg, isCircle, 0);
    }

    public void setImage(Activity a, int ImageViewId, T model, boolean isCircle) {
        View view = a.findViewById(ImageViewId);
        if (view instanceof ImageView) {
            setImage(a, (ImageView) view, model, isCircle);
        }
    }

    public void setImage(Activity a, int ImageViewId, T model, int loadingimg, boolean isCircle) {
        View view = a.findViewById(ImageViewId);
        if (view instanceof ImageView) {
            setImage(a, (ImageView) view, model, loadingimg, isCircle);
        }
    }

    /**
     * 加载圆角图片
     */
    public void setImage(Context context, ImageView iv, T model, boolean isCircle, int rounded) {
        setImage(context, iv, model, 0, false, rounded);
    }


    public void setImage(Activity a, int ImageViewId, T model, boolean isCircle, int rounded) {
        View view = a.findViewById(ImageViewId);
        if (view instanceof ImageView) {
            setImage(a, (ImageView) view, model, rounded);
        }

    }

    /**
     * 自定义预加载图片
     */
    public void setImage(Context context, ImageView iv, T model, int loadingImage) {
        setImage(context, iv, model, loadingImage, false, 0);
    }


    public void setImage(Activity a, int ImageViewId, T model, int loadingImage) {
        View view = a.findViewById(ImageViewId);
        if (view instanceof ImageView) {
            setImage(a, (ImageView) view, model, loadingImage);
        }

    }

    /**
     * 加载图片并设置为指定大小
     *
     * @param context
     * @param imageView
     * @param withSize
     * @param heightSize
     */
    public void loadOverrideImage(Context context, ImageView imageView,
                                  T model, int withSize, int heightSize, boolean isCircle, int rounded) {
        RequestOptions options = new RequestOptions()
                .override(withSize, heightSize);
        RequestBuilder<Bitmap> builder = getBitmapBuilder(context, model);
        if (isCircle) {
            builder.apply(options.bitmapTransform(new CircleCrop()));
        }
        if (rounded > 0) {
            builder.apply(options.bitmapTransform(new RoundedCorners(rounded)));//圆角半径
        }
        builder.apply(options).into(imageView);
    }

    /**
     * 加载图片并对其进行模糊处理
     *
     * @param context
     * @param imageView
     * @param imgUrl
     */
    public void loadBlurImage(Context context, ImageView imageView, String imgUrl) {
//         .crossFade()
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(true)//不使用内存缓存
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE)//使用磁盘缓存
                .error(loadingImage)
                .bitmapTransform(new BlurTransformation(context));

        Glide.with(context)
                .load(imgUrl)
                .apply(options)
                .into(imageView);
    }

    /**
     * 加载gif
     *
     * @param context
     * @param imageView
     * @param imgUrl
     */
    public void loadGifImage(Context context, ImageView imageView, String imgUrl) {
//        .crossFade()
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(true)//不使用内存缓存
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用磁盘缓存
                .error(loadingImage);
        Glide.with(context)
                .asGif()
                .apply(options)
                .load(imgUrl)
                .into(imageView);
    }

    /**
     * 加载gif的缩略图
     *
     * @param context
     * @param imageView
     * @param imgUrl
     */
    public void loadGifThumbnailImage(Context context, ImageView imageView, String imgUrl) {

//        .crossFade()
        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(loadingImage);
        Glide.with(context)
                .asGif()
                .apply(options)
                .load(imgUrl)
                .thumbnail(THUMB_SIZE)
                .into(imageView);
    }

    /**
     * 加载图片
     *
     * @param model .load(String string)	string可以为一个文件路径、uri或者url
     *              .load(Uri uri)	uri类型
     *              .load(File file)	文件
     *              .load(Integer resourceId)	资源Id,R.drawable.xxx或者R.mipmap.xxx
     *              .load(byte[] model)	byte[]类型
     *              .load(T model)
     */
    protected void setImage(Context context, ImageView iv, T model, int resLoadingImage, boolean isCircle, int
            rounded) {
        if (resLoadingImage == 0) {//设置默认图片
            resLoadingImage = loadingImage;
        }
        RequestBuilder<Bitmap> builder = getBitmapBuilder(context, model);

        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(resLoadingImage)
                .placeholder(resLoadingImage);

        builder.apply(options);
        if (isCircle) {
            builder.apply(options.bitmapTransform(new CircleCrop()));
        }
        if (rounded > 0) {
            builder.apply(options.bitmapTransform(new RoundedCorners(rounded)));//圆角半径
        }
        builder.thumbnail(0.1f).into(iv);//预加载缩略图
    }

    /**
     * dontAnimate() 没有任何淡入淡出效果
     * crossFade() 一个淡入淡出动画方法还有另外重载方法 .crossFade(int duration)
     * 。如果你想要去减慢（或加快）动画，随时可以传一个毫秒的时间给这个方法。动画默认的持续时间是 300毫秒。
     */
    public RequestBuilder<Bitmap> getBitmapBuilder(Context context, T model) {

        RequestBuilder<Bitmap> request = null;
        if (model instanceof String) {
            String url = (String) model;
            if (!Utils.checkNullString(url)) {
                request = Glide.with(context).asBitmap().load(url);
            }
        } else if (model instanceof byte[]) {
            byte[] imageByte = (byte[]) model;
            request = Glide.with(context).asBitmap()
                    .load(imageByte);
        } else if (model instanceof Integer) {
            Integer imageResId = (Integer) model;
            request = Glide.with(context).asBitmap()
                    .load(imageResId);
        } else if (model instanceof Uri) {
            Uri uri = (Uri) model;
            request = Glide.with(context).asBitmap()
                    .load(uri);
        } else if (model instanceof File) {
            File file = (File) model;
            if (!Utils.checkNullString(file.getAbsolutePath())) {
                request = Glide.with(context)
                        .asBitmap()
                        .load(file);
            }
        } else {
            request = Glide.with(context)
                    .asBitmap()
                    .load(model);
        }
        return request;
    }

    /**
     * 同步加载图片
     *
     * @param context
     * @param imgUrl
     * @param target
     */
    public void loadBitmapSync(Context context, String imgUrl, SimpleTarget<Bitmap> target) {
        RequestOptions options = new RequestOptions()
                .priority(Priority.NORMAL)//下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL)//下载的优先级
                .error(loadingImage);
        Glide.with(context)
                .asBitmap()
                .load(imgUrl)
                .apply(options)
                .into(target);
    }

    /**
     * 恢复请求，一般在停止滚动的时候
     *
     * @param context
     */
    public void resumeRequests(Context context) {
        Glide.with(context).resumeRequests();
    }

    /**
     * 暂停请求 正在滚动的时候
     *
     * @param context
     */
    public void pauseRequests(Context context) {
        Glide.with(context).pauseRequests();
    }

    /**
     * 清除磁盘缓存
     *
     * @param context
     */
    public void clearDiskCache(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();//清理磁盘缓存 需要在子线程中执行
            }
        }).start();
    }

    /**
     * 清除内存缓存
     *
     * @param context
     */
    public void clearMemory(Context context) {
        Glide.get(context).clearMemory();//清理内存缓存  可以在UI主线程中进行
    }

    /**
     * 模糊图像
     */
    public class BlurTransformation extends BitmapTransformation {

        private RenderScript rs;

        public BlurTransformation(Context context) {
            rs = RenderScript.create(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Bitmap blurredBitmap = toTransform.copy(Bitmap.Config.RGB_565, true);

            // Allocate memory for Renderscript to work with
            Allocation input = Allocation.createFromBitmap(
                    rs,
                    blurredBitmap,
                    Allocation.MipmapControl.MIPMAP_FULL,
                    Allocation.USAGE_SHARED
            );
            Allocation output = Allocation.createTyped(rs, input.getType());

            // Load up an instance of the specific script that we want to use.
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(input);

            // Set the blur radius
            script.setRadius(10);

            // Start the ScriptIntrinisicBlur
            script.forEach(output);

            // Copy the output to the blurred bitmap
            output.copyTo(blurredBitmap);

            toTransform.recycle();

            return blurredBitmap;
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }
}