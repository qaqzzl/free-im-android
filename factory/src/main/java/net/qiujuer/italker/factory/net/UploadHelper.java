package net.qiujuer.italker.factory.net;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import net.qiujuer.italker.common.Common;
import net.qiujuer.italker.factory.persistence.Account;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 上传工具类，用于上传任意文件到阿里OSS存储
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class UploadHelper {
    private static final String TAG = UploadHelper.class.getSimpleName();
    // 与你们的存储区域有关系
    public static final String ENDPOINT = "http://free-im-aliyun-oss.qaqzz.com";
    // 上传的仓库名
    private static final String BUCKET_NAME = "free-im";


    private static UploadManager getUploadManager() {
        Configuration config = new Configuration.Builder()
                .connectTimeout(90)              // 链接超时。默认90秒
                .useHttps(true)                  // 是否使用https上传域名
                .useConcurrentResumeUpload(true) // 使用并发上传，使用并发上传时，除最后一块大小不定外，其余每个块大小固定为4M，
                .concurrentTaskCount(3)          // 并发上传线程数量为3
                .responseTimeout(90)             // 服务器响应超时。默认90秒
//                .recorder(recorder)              // recorder分片上传时，已上传片记录器。默认null
//                .recorder(recorder, keyGen)      // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
//                .zone(FixedZone.zone0)           // 设置区域，不指定会自动选择。指定不同区域的上传域名、备用域名、备用IP。
                .build();

        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        UploadManager uploadManager = new UploadManager(config);
        return uploadManager;
    }

    private static String getToken()
    {
        String info = "";
        String token = "";

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        FormBody.Builder builder = new FormBody.Builder();
        Request request = new Request.Builder()
                .url(Common.Constance.API_URL+"common/get.qiniu.upload.token")
                .post(builder.build()).addHeader("Authorization", "Bearer "+Account.getToken()).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            info = response.body().string();
            System.out.println(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(info);
            JSONObject jsonData = new JSONObject(jsonObject.getString("data"));
            token = jsonData.getString("token");
        }catch (Exception e){e.printStackTrace();}
        return token;
    }

    /**
     * 上传的最终方法，成功返回则一个路径
     *
     * @param objKey 上传上去后，在服务器上的独立的KEY
     * @param path   需要上传的文件的路径
     * @return 存储的地址
     */
    private static String upload(String objKey, String path) {

        try {

            // 初始化上传的 uploadManager
            UploadManager uploadManager = getUploadManager();
            // 开始同步上传
            String token = getToken();
            Log.d(TAG, String.format("token:%s", token));
            ResponseInfo responseInfo = uploadManager.syncPut(path, objKey, token, null);
            Log.d(TAG, String.format("responseInfo.response:%s", responseInfo.toString()));
            // 得到一个外网可访问的地址
            String url = "http://free-im-qn.qaqzz.com/"+responseInfo.response.getString("key");
            // 格式打印输出
            Log.d(TAG, String.format("PublicObjectURL:%s", url));
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            // 如果有异常则返回空
            return null;
        }
    }

    /**
     * 上传普通图片
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadImage(String path) {
        String key = getImageObjKey(path);
        return upload(key, path);
    }

    /**
     * 上传头像
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path) {
        String key = getPortraitObjKey(path);
        return upload(key, path);
    }

    /**
     * 上传音频
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadAudio(String path) {
        String key = getAudioObjKey(path);
        return upload(key, path);
    }

    /**
     * 分月存储，避免一个文件夹太多
     *
     * @return yyyyMM
     */
    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    // image/201703/dawewqfas243rfawr234.jpg
    private static String getImageObjKey(String path) {
        String fileMd5 = getFileName(path);
        String dateString = getDateString();
        return String.format("image/%s/%s.jpg", dateString, fileMd5);
    }

    // portrait/201703/dawewqfas243rfawr234.jpg
    private static String getPortraitObjKey(String path) {
        String fileMd5 = getFileName(path);
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }

    // audio/201703/dawewqfas243rfawr234.mp3
    private static String getAudioObjKey(String path) {
        String fileMd5 = getFileName(path);
        String dateString = getDateString();
        return String.format("audio/%s/%s.mp3", dateString, fileMd5);
    }

    private static String getFileName(String path)
    {
//        String fileMd5 = HashUtil.getMD5String(new File(path));
        String fileMd5 = UUID.randomUUID().toString();
        return fileMd5;
    }
}
