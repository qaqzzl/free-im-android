package net.qiujuer.italker.factory.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;


/**
 * 获取设备的信息
 *
 * @author melo
 */
public final class DeviceID {

    /**
     *   ANDROID_ID(恢复出厂+刷机会变) + 序列号(android 10会unknown/android 9需要设备权限)+品牌    +机型
     * @return
     */
    public static String getAndroidId(Context context){
        String androidId =  Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String uniqueCode ;
        uniqueCode = androidId + Build.SERIAL+Build.BRAND+ Build.MODEL;
        return toMD5(uniqueCode);
    }

    /**
     * 获取设备标识
     *
     * @param context
     * @param listener
     */
    public static void getDeviceId(final Context context, final OnDeviceIdListener listener) {
        MdidSdkHelper.InitSdk(context, true, new IIdentifierListener() {
            @Override
            public void OnSupport(boolean isSupport, IdSupplier idSupplier) {
                String deviceId;
                if (isSupport) {
                    // 支持获取补充设备标识
                    deviceId = idSupplier.getOAID();
                } else {
                    // 不支持获取补充设备标识
                    // 可以自己决定设备标识获取方案，这里直接使用了ANDROID_ID
                    deviceId = getAndroidId(context);
                }
                // 将设备标识MD5加密后返回，以获取统一格式
                listener.onSuccess(toMD5(deviceId));
                // 释放连接
                // idSupplier.shutDown();
            }
        });
    }

    /**
     * MD5加密 格式一致
     */
    private static String toMD5(String text){
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] digest = messageDigest.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            int digestInt = digest[i] & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString().substring(8,24);
    }

    /**
     * 获取补充设备标识
     *
     * @param context
     * @param listener
     */
    public static void getSupplierDeviceId(Context context, final OnSupplierDeviceIdListener listener) {
        int result = MdidSdkHelper.InitSdk(context, true, new IIdentifierListener() {
            @Override
            public void OnSupport(boolean isSupport, IdSupplier idSupplier) {
                if (isSupport) {
                    listener.onSuccess(idSupplier);
                }
                // 释放连接
                // idSupplier.shutDown();
            }
        });
        switch (result) {
            case ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT:
                // 不支持的设备厂商
                listener.onFailed("不支持的设备厂商");
                break;
            case ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT:
                listener.onFailed("不支持的设备");
                break;
            case ErrorCode.INIT_ERROR_LOAD_CONFIGFILE:
                listener.onFailed("加载配置文件出错");
                break;
            case ErrorCode.INIT_ERROR_RESULT_DELAY:
                // 获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
                break;
            case ErrorCode.INIT_HELPER_CALL_ERROR:
                listener.onFailed("反射调用出错");
                break;
            default:
                break;
        }
    }

    /**
     * 获取补充设备标识回调
     */
    public interface OnSupplierDeviceIdListener {
        /**
         * 获取补充设备标识成功
         *
         * @param idSupplier
         */
        void onSuccess(IdSupplier idSupplier);

        /**
         * 获取补充设备标识失败
         *
         * @param message 失败原因
         */
        void onFailed(String message);
    }

    /**
     * 获取设备标识回调
     */
    public interface OnDeviceIdListener {
        /**
         * 获取设备标识成功
         */
        void onSuccess(String deviceId);
    }
}