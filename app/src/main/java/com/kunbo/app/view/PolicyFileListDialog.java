package com.kunbo.app.view;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kunbo.app.C;
import com.kunbo.app.R;
import com.kunbo.app.adapter.RecyclerCommonAdapter;
import com.kunbo.app.net.bean.MyData;
import com.kunbo.app.net.bean.MyRow;
import com.kunbo.app.view.recyclerview.ViewHolder;
//import com.tencent.smtt.sdk.ValueCallback;

import static com.kunbo.app.activitys.BaseActivity.getId;

/**
 * 政策制度文件列表
 */
public class PolicyFileListDialog
        //implements ValueCallback
{
    private Context context;
    private AlertDialog dialog;
    //文件的后缀名
    private String[] fileTypes = new String[]{"doc", "docx", "ppt", "pptx", "xls", "xlsx", "pdf", "txt", "epub"};

    public PolicyFileListDialog(@NonNull Context context) {
        this.context = context;
        if (C.dialogTheme != -1) {
            context.setTheme(C.dialogTheme);
        }
    }

    public void show(int from, MyData datas) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        View v = View.inflate(context, R.layout.dialog_policy_files, null);
        TextView title = v.findViewById(R.id.title);
        switch (from) {
            case 0:
            case 1:
                title.setText("政策法规在线预览");
                break;
            case 2:
                title.setText("集团制度在线预览");
                break;
            case 3:
                title.setText("分公司制度在线预览");
                break;
            case 4:
                title.setText("站管理办法在线预览");
                break;
        }

        RecyclerView list = v.findViewById(R.id.file_list);
        PolicyFileAdapter adapter = new PolicyFileAdapter(context, datas);
        list.setAdapter(adapter);

        ad.setView(v);
        dialog = ad.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

   /* @Override
    public void onReceiveValue(Object o) {

    }*/

    class PolicyFileAdapter extends RecyclerCommonAdapter {
        public int from;
        private boolean isFile = false;

        public PolicyFileAdapter(Context context, MyData datas) {
            super(context, R.layout.item_file_list, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object o, int position) {
            MyRow ro = (MyRow) o;

            String fileName = ro.getString("fileName");
            String[] types = fileName.split("\\.");
            if (!TextUtils.isEmpty(fileName)) {
                String type = types[types.length - 1];//当前文件类型
                for (String ty : fileTypes) {
                    if (type.toLowerCase().equals(ty)) {
                        isFile = true;
                        holder.setImage(R.id.img, getId(context, ty, "mipmap"));
                        break;
                    }
                }
            }
            holder.setText(R.id.title, fileName);
            Button scan = holder.getView(R.id.online_scan);
           /* scan.setOnClickListener(view -> {
                PolicyFileInfoDialog dialog = new PolicyFileInfoDialog(context);
                if (isFile)
                    downLoadFile(fileName, C.DOMAIN_LINK + "/base/download/file?id=" + ro.getString("id"));
                else
                    dialog.show(fileName, C.DOMAIN_LINK + ro.getString("accessPath"));
            });*/
        }
    }
/*
    public void downLoadFile(String fileName, String path) {
        LemonBubble.showRoundProgress(this.context, "文件加载中...");
        Retrofit ref = OkHttpUtil.getInstance().getRetrofit();
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(context, "下载地址无效！", Toast.LENGTH_SHORT).show();
            return;
        }
        ref.create(ApiService.class)
                .downloadFileOrApk(path)
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(responseBody -> AppUtil.saveFile(context, responseBody.byteStream(), fileName)
                )
                .subscribe(new DisposableObserver<File>() {
                    @Override
                    public void onNext(File file) {
                        LemonBubble.hide();
                        if (dialog != null)
                            dialog.dismiss();
                        openFileReader(file.getAbsolutePath());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "下载失败," + e.getMessage(), Toast.LENGTH_SHORT).show();
                        LemonBubble.hide();
                    }

                    @Override
                    public void onComplete() {
                        LemonBubble.hide();
                    }
                });
    }*/


}
