package org.baiya.practice.pm25.cache;

import android.content.Context;

import org.baiya.practice.pm25.bean.PmBean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class WidgetManager {

    public static PmBean getPmBean(Context context, int widgetId) {
        List<PmBean> pmBeanList = getWidgetList(context);
        for (PmBean pmBean : pmBeanList) {
            if (pmBean.widgetId == widgetId) {
                return pmBean;
            }
        }
        return null;
    }

    public static void removePmBean(Context context, int widgetId) {
        List<PmBean> pmBeanList = getWidgetList(context);
        for (PmBean pmBean : pmBeanList) {
            if (pmBean.widgetId == widgetId) {
                pmBeanList.remove(pmBean);
                break;
            }
        }
        saveWidgetList(context, pmBeanList);
    }

    public static void savePmBean(Context context, PmBean pmBean) {
        List<PmBean> pmBeanList = getWidgetList(context);
        pmBeanList.add(pmBean);
        saveWidgetList(context, pmBeanList);
    }

    public static List<PmBean> getWidgetList(Context context) {
        ArrayList<PmBean> arrayList = new ArrayList<PmBean>();
        if (!isCacheExists(context)) {
            return arrayList;
        }
        ensureFile(context);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(PmBean.TAG);
            if (fileInputStream.available() > 0) {
                ObjectInputStream objectInputStream = new ObjectInputStream(
                        new BufferedInputStream(fileInputStream));
                // 读取widget数量信息
                int size = objectInputStream.readInt();
                for (int i = 0; i < size; i++) {
                    arrayList.add((PmBean) objectInputStream.readObject());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /**
     * 保存widget信息到本地
     *
     * @param context
     * @param pmBeanList
     */
    synchronized public static void saveWidgetList(Context context, List<PmBean> pmBeanList) {
        if (pmBeanList == null) {
            return;
        }
        ensureFile(context);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(PmBean.TAG, MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new BufferedOutputStream(fileOutputStream));
            // 先保存widget数量
            objectOutputStream.writeInt(pmBeanList.size());
            // 逐个保存widget信息
            Iterator<PmBean> iterator = pmBeanList.iterator();
            while (iterator.hasNext()) {
                PmBean object = iterator.next();
                objectOutputStream.reset();
                objectOutputStream.writeObject(object);
            }
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ensureFile(Context context) {
        File file = context.getFilesDir();
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean isCacheExists(Context context) {
        File file = context.getFileStreamPath(PmBean.TAG);
        return file.exists();
    }

}
