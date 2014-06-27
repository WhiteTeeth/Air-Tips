package org.baiya.practice.pm25;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

import org.baiya.practice.pm25.bean.PmBean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WidgetBig extends BaseWidget {

    public static final String TAG = WidgetBig.class.getSimpleName();
    private static SimpleDateFormat mSimpleDateFormat;

    static {
        mSimpleDateFormat = new SimpleDateFormat("HH:mm");
    }

    private static Bitmap getLeftBg(Context context, int color) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_21_left_bg);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = DisplayMetricsTools.dpToPx(65.0F, metrics);
        int height = DisplayMetricsTools.dpToPx(84.0F, metrics);
        return createBitmap(bitmap, color, width, height);
    }

    public static Bitmap getPmValueBmp(Context context, String pmValue, int color) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "GothamRnd-Medium.otf");
        int textSize = context.getResources().getDimensionPixelSize(R.dimen.big_widget_text_size);
        int digitalSize = context.getResources().getDimensionPixelSize(R.dimen.big_widget_digital_size);
        if (TextUtils.isDigitsOnly(pmValue)) {
            textSize = digitalSize;
        }
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int digitalPx = DisplayMetricsTools.dpToPx(textSize, metrics);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(typeface);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(digitalPx);
        Rect rect = new Rect();
        paint.getTextBounds(pmValue, 0, pmValue.length(), rect);
        int height = Math.abs(rect.bottom - rect.top);
        int width = Math.abs(rect.right - rect.left);
        int bitmapHeight = DisplayMetricsTools.dpToPx(3.0F, metrics) + height;
        Bitmap newBitmap = Bitmap.createBitmap(width, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawText(pmValue, width / 2, height, paint);
        return newBitmap;
    }

    public static RemoteViews getRemoteViews(Context context, final PmBean pmBean) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_21);
        Date date = new Date(System.currentTimeMillis());
        String time = mSimpleDateFormat.format(date);
        int level = getLevel(pmBean.pm25);
        remoteViews.setTextViewText(R.id.pm25_place, pmBean.city.getLocaleStr(context));
        remoteViews.setTextViewText(R.id.pm25_update_time, time);
        remoteViews.setImageViewResource(R.id.pm25_icon, ICON_BIG[level]);
        remoteViews.setImageViewBitmap(R.id.left_bg,
                getLeftBg(context, BACKGROUND[level]));
        remoteViews.setImageViewBitmap(R.id.pm25_value,
                getPmValueBmp(context, pmBean.pm25, BACKGROUND[level]));
        PendingIntent pendingIntent = UpdateService.getPendingIntent(context, pmBean.widgetId);
        remoteViews.setOnClickPendingIntent(R.id.right_container, pendingIntent);
        return remoteViews;
    }

}
