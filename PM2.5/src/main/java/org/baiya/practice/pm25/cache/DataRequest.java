package org.baiya.practice.pm25.cache;

import android.content.Context;
import android.text.TextUtils;

import org.baiya.practice.pm25.bean.City;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

public class DataRequest extends ZipRequest {

    public interface PmListener {
        public void onSuccess(String pm);

        public void onError(String msg);
    }

    private Context mContext;
    private PmListener mPmListener;
    public static final String PM_FILE = "pm25_in.json";

    public DataRequest(Context context) {
        mContext = context;
    }

    public void setPmListener(PmListener listener) {
        mPmListener = listener;
    }

    private String readFromCache() {
        File file = new File(mContext.getFilesDir(), PM_FILE);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String str = bufferedReader.readLine();
                if (str == null) {
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                stringBuilder.append(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取city的aqi
     *
     * @param pm25
     * @param city
     * @return
     */
    private String getAqi(String pm25, String city) {
        if (TextUtils.isEmpty(pm25)) {
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray(pm25);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String str = jsonObject.getString("area");
                if (city.equals(str)) {
                    return jsonObject.getString("aqi");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            deleteCache();
        }
        return null;
    }

    private void deleteCache() {
        File file = new File(mContext.getFilesDir(), PM_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    private boolean isCacheExpired() {
        File file = new File(mContext.getFilesDir(), PM_FILE);
        if (file.exists()) {
            long time = System.currentTimeMillis();
            long lastModified = file.lastModified();
            if (time - lastModified <= 86400000L) {
                return false;
            }
        }
        return true;
    }

    private String saveCache(String pm25FromNet) {
        File file = new File(mContext.getFilesDir(), PM_FILE);
        try {
            ensureFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray(pm25FromNet);
            JSONArray newJsonArray = new JSONArray();
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject newJsonObject = new JSONObject();
                newJsonObject.put("area", jsonObject.getString("area"));
                newJsonObject.put("aqi", jsonObject.getString("aqi"));
                newJsonArray.put(newJsonObject);
            }

            String pm = newJsonArray.toString();

            StringReader stringReader = new StringReader(pm);

            BufferedReader bufferedReader = new BufferedReader(stringReader);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

            char[] chars = new char[8192];
            while (true) {
                int i = bufferedReader.read(chars);
                if (i <= 0) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    bufferedReader.close();
                    break;
                }
                bufferedWriter.write(chars, 0, i);
            }

            return pm;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void ensureFile(File file) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

    public String getAqi(City c) {
        String city = c.chRCN;
        if (TextUtils.isEmpty(city)) {
            return null;
        }
        String pm = null;
        if (isCacheExpired()) {
            String pmFromNet = request("http://114.34.187.37/pm25_in.zip");
            if (!TextUtils.isEmpty(pmFromNet)) {
                pm = saveCache(pmFromNet);
            }
        } else {
            pm = readFromCache();
        }
        String pmValue = getAqi(pm, city);
        if (!TextUtils.isEmpty(pmValue) && mPmListener != null) {
            mPmListener.onSuccess(pmValue);
        } else if (mPmListener != null) {
            mPmListener.onError(null);
        }
        return pmValue;
    }
}
