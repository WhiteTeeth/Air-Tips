package org.baiya.practice.pm25;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

import org.baiya.practice.pm25.bean.PmBean;

public class WidgetSmall extends BaseWidget {
    public static final String TAG = WidgetBig.class.getSimpleName();

    private static final int TEXTCOLOR = 0xc4ffffff;

    private static Bitmap getBgContainer(Context context, int color) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_11_circule_mask);
        return createBitmap(bitmap, color, 0, 0);
    }

    public static Bitmap getDigitalValueBmp(Context context, String pmValue) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "GothamRnd-Medium.otf");
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int textSize = DisplayMetricsTools.dpToPx(20.0F, displayMetrics);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(typeface);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(TEXTCOLOR);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(textSize);
        int width = (int) paint.measureText(pmValue);
        int height = textSize;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(pmValue, 0.0F, height, paint);
        return bitmap;
    }

    public static Bitmap getValueBmp(Context context, String value) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "GothamRnd-Medium.otf");
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int textSize = DisplayMetricsTools.dpToPx(10.0F, displayMetrics);
        int padding = DisplayMetricsTools.dpToPx(6.0F, displayMetrics);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(typeface);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(TEXTCOLOR);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(textSize);
        int width = (int) paint.measureText(value);
        int height = textSize + padding;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(value, 0.0F, textSize + padding / 2, paint);
        return bitmap;
    }

    public static RemoteViews getRemoteViews(Context context, PmBean pmBean) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_11);
        remoteViews.setTextViewText(R.id.place_value, pmBean.city.getLocaleStr(context));
        int level = BaseWidget.getLevel(pmBean.pm25);
        remoteViews.setImageViewBitmap(R.id.circle_bg, getBgContainer(context, BACKGROUND[level]));
        switch (pmBean.display) {
            case GRAPH:
                remoteViews.setImageViewResource(R.id.pm25_icon, ICON_SMALL[level]);
                break;
            case VALUE:
                Bitmap pmValueBmp = getDigitalValueBmp(context, pmBean.pm25);
                remoteViews.setImageViewBitmap(R.id.pm25_icon, pmValueBmp);
                break;
            default:
                break;
        }
        switch (pmBean.style) {
            case BLACK:
                remoteViews.setImageViewResource(R.id.bg, R.drawable.widget_11_black_bg);
                break;
            case WHITE:
                remoteViews.setImageViewResource(R.id.bg, R.drawable.widget_11_white_bg);
                break;
            default:
                break;
        }
        PendingIntent pendingIntent = UpdateService.getPendingIntent(context, pmBean.widgetId);
        remoteViews.setOnClickPendingIntent(R.id.circle_bg, pendingIntent);
        return remoteViews;
    }

}
