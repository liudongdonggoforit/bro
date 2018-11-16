package com.sh.browser.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dong on 2017/9/25.
 */

public class UCUtils {
    /**
     * 时间戳转为时间(年月日，时分秒)
     *
     * @param cc_time 时间戳
     * @return
     */
    public static String getStrTime(long cc_time) {
        String re_StrTime = null;
        //同理也可以转为其它样式的时间格式.例如："yyyy/MM/dd HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        // 例如：cc_time=1291778220
        re_StrTime = sdf.format(new Date(cc_time * 1000L));

        return re_StrTime;
    }

    /**
     * 时间转换为时间戳
     *
     * @param format  时间对应格式  例如: yyyy-MM-dd
     * @return
     */
    public static long getTimeStamp(String timeStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(timeStr);
            long timeStamp = date.getTime();
            return timeStamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取IMEI
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            try {
                return tm.getDeviceId();
            } catch (Exception e) {
                return getAndroidId(context);
            }

        }
        return tm.getDeviceId();
    }
    /**
     * 获取Android
     * @return
     */
    public static String getAndroidId(Context context) {
       return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 将Json对象上传服务器
     * @param url
     * @param jsonObject
     * @throws JSONException
     */
    public static JSONObject postJsonToServer(URL url, JSONObject jsonObject) throws JSONException {

        //把JSON数据转换成String类型使用输出流向服务器写
        //            String str = URLEncoder.encode(URLEncoder.encode(String.valueOf(jsonObject), "UTF-8"), "UTF-8");
        String str = jsonObject.toString();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(2000);
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setDoOutput(true);//设置允许输出
            httpURLConnection.setRequestMethod("POST");//设置请求的方式
            httpURLConnection.setRequestProperty("ser-Agent", "Fiddler");

            //把上面访问方式改为异步操作,就不会出现 android.os.NetworkOnMainThreadException异常
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream());
            out.print(str);//写入输出流
            out.flush();//立即刷新

            out.close();


            int code = httpURLConnection.getResponseCode();
            if (code == 200 && ((url.toString()).equals(httpURLConnection.getURL().toString()))) {
                //获取服务器响应后返回的数据
                InputStream is = httpURLConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = -1;

                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }

                is.close();
                String result = baos.toString();
                return new JSONObject(result);
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
