package net.qiujuer.italker.factory.data.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.qiujuer.italker.factory.R;
import net.qiujuer.italker.factory.model.api.account.AppVersionModel;
import net.qiujuer.italker.factory.utils.DialogManager;
import net.qiujuer.italker.factory.utils.HttpManager;
import net.qiujuer.italker.factory.view.DialogView;

import java.io.File;


/**
 * FileName: UpdateHelper
 * Founder: LiuGuiLin
 * Profile: App更新帮助类
 * versionCode +1
 */
public class UpdateHelper {

    private Context mContext;

    private DialogView mUpdateView;
    private TextView tv_desc;
    private TextView tv_confirm;
    private TextView tv_cancel;

    private ProgressDialog mProgressDialog;

    public UpdateHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void updateApp(AppVersionModel appVersionModel) {
        createUpdateDialog(appVersionModel);
    }

    /**
     * 更新提示框
     */
    private void createUpdateDialog(final AppVersionModel appVersionModel) {
        mUpdateView = DialogManager.getInstance().initView(mContext, R.layout.dialog_update_app);
        tv_desc = mUpdateView.findViewById(R.id.tv_update_desc);
        tv_confirm = mUpdateView.findViewById(R.id.tv_confirm);
        tv_cancel = mUpdateView.findViewById(R.id.tv_cancel);

        tv_desc.setText(appVersionModel.getVersion_description());
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogManager.getInstance().hide(mUpdateView);
//                downloadApk(appVersionModel.getVersion_download());
                Uri uri = Uri.parse(appVersionModel.getVersion_download_page());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (appVersionModel.getIs_must() == 1) {
                Uri uri = Uri.parse(appVersionModel.getVersion_download_page());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
            } else {
                DialogManager.getInstance().hide(mUpdateView);
            }
            }
        });
        if (appVersionModel.getIs_must() == 1) {
            tv_cancel.setText("更新");
        }
        DialogManager.getInstance().show(mUpdateView);

        initProgress();
    }

    private void initProgress() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
    }

    /**
     *
     * @param path 文件夹路径
     */
    public static void isExist(String path) {
        File file = new File(path);
        //判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 下载
     */
    public void downloadApk(String version_download) {
//        String getPath = Environment.getDataDirectory().getPath();
//        String getPath = mContext.getFilesDir().getPath();
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/free_im";
        isExist(sdcardPath);
        final String filePath = sdcardPath +"/"+ System.currentTimeMillis() + ".apk";

        if (mProgressDialog != null) {
            mProgressDialog.show();
        }

        //开始下载：
        Log.e("TEST", "onDownload version_download:"+version_download);
        HttpManager.getInstance().download(version_download, filePath, new HttpManager.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(String path) {
                mProgressDialog.dismiss();
                Log.e("TEST", "onDownloadSuccess:" + path);
                if (!TextUtils.isEmpty(path)) {
                    installApk(path);
                }
            }

            @Override
            public void onDownloading(int progress) {
                mProgressDialog.setProgress(progress);
                Log.e("TEST", "onDownloading:" + progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                mProgressDialog.dismiss();
                Log.e("TEST", "onDownloadFailed:" + e.toString());
            }
        });
    }

    /**
     * 安装Apk
     *
     * @param filePath
     * @return
     */
    public void installApk(String filePath) {
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        }catch (Exception e){
            Log.e("TEST", "installApk:" + e.toString());
            e.toString();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        File apkFile = new File(filePath);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", apkFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }
}
