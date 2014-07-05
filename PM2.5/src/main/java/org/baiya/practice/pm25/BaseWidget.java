package org.baiya.practice.pm25;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import org.baiya.practice.pm25.cache.WidgetManager;

public abstract class BaseWidget extends AppWidgetProvider {

    public static final int[] BACKGROUND = {0xff59c991, 0xff81d067, 0xff4f9ed7, 0xfff4cc2f, 0xffdf850d, 0xffc93f4c};
    public static final int[] DATUM = {50, 100, 150, 200, 300, 999};
    public static final int[] ICON_SMALL = {
            R.drawable.widget_11_small_icon_1,
            R.drawable.widget_11_small_icon_2,
            R.drawable.widget_11_small_icon_3,
            R.drawable.widget_11_small_icon_4,
            R.drawable.widget_11_small_icon_5,
            R.drawable.widget_11_small_icon_6};
    public static final int[] ICON_BIG = {
            R.drawable.widget_21_big_icon_1,
            R.drawable.widget_21_big_icon_2,
            R.drawable.widget_21_big_icon_3,
            R.drawable.widget_21_big_icon_4,
            R.drawable.widget_21_big_icon_5,
            R.drawable.widget_21_big_icon_6};

    /**
     * 获取pm等级：0-5。
     *
     * @param pmValue pm值
     * @return pm等级
     */
    public static int getLevel(int pmValue) {
        for (int i = 0; i < DATUM.length; i++) {
            if (pmValue < DATUM[i]) {
                return i;
            }
        }
        return DATUM.length - 1;
    }

    public static int getLevel(String pmValue) {
        int level;
        try {
            level = getLevel(Integer.valueOf(pmValue).intValue());
        } catch (NumberFormatException e) {
            level = 0;
        }
        return level;
    }

    /**
     * 根据图片形状和颜色绘制背景图片
     *
     * @param bitmap
     * @param color
     * @param width
     * @param height
     * @return
     */
    protected static Bitmap createBitmap(Bitmap bitmap, int color, int width, int height) {
        if (width == 0) width = bitmap.getWidth();
        if (height == 0) height = bitmap.getHeight();
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap newBitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        canvas.drawPaint(paint);
        canvas.drawColor(color);
        PorterDuff.Mode mode = PorterDuff.Mode.DST_ATOP;
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(mode);
        paint.setXfermode(porterDuffXfermode);
        canvas.drawBitmap(bitmap, 0.0F, 0.0F, paint);
        bitmap = newBitmap;
        return bitmap;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            UpdateService.updateWidget(context, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            WidgetManager.removePmBean(context, appWidgetId);
        }
    }

}
