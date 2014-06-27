package org.baiya.practice.pm25;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import org.baiya.practice.pm25.bean.PmBean;
import org.baiya.practice.pm25.cache.DataRequest;
import org.baiya.practice.pm25.cache.WidgetManager;
import org.baiya.practice.pm25.tools.Logger;

import static org.baiya.practice.pm25.R.string.query_wait;

public class UpdateService extends IntentService {

    public static final String TAG = UpdateService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UpdateService(String name) {
        super(name);
    }

    public UpdateService() {
        this(TAG);
    }

    public static final String EXTRA_UPDATE_REASON = "UPDATE_REASON";
    public static final String EXTRA_WIDGET_ID = "WIDGET_ID";
    public static final int REASON_CREATE = 0;
    public static final int REASON_UPDATE = 1;
    public static final int EXTRA_NULL = 4;

    public static PendingIntent getPendingIntent(Context context, int widgetId) {
        Intent intent = new Intent();
        intent.setClass(context, UpdateService.class);
        intent.putExtra(EXTRA_UPDATE_REASON, REASON_UPDATE);
        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
        return PendingIntent.getService(context, 0, intent, widgetId);
    }

    public static void createWidget(Context context, int widgetId) {
        Intent localIntent = new Intent(context, UpdateService.class);
        localIntent.putExtra(EXTRA_UPDATE_REASON, REASON_CREATE);
        localIntent.putExtra(EXTRA_WIDGET_ID, widgetId);
        context.startService(localIntent);
    }

    public static void updateWidget(Context context, int appWidgetId) {
        Intent localIntent = new Intent(context, UpdateService.class);
        localIntent.putExtra(EXTRA_UPDATE_REASON, REASON_UPDATE);
        localIntent.putExtra(EXTRA_WIDGET_ID, appWidgetId);
        context.startService(localIntent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getIntExtra(EXTRA_UPDATE_REASON, EXTRA_NULL)) {
            case REASON_CREATE: {
                int widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1);
                PmBean pmBean = WidgetManager.getPmBean(this, widgetId);
                if (pmBean != null) {
                    showWaiting(pmBean);
                }
                break;
            }
            case REASON_UPDATE: {
                int widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1);
                PmBean pmBean = WidgetManager.getPmBean(this, widgetId);
                if (pmBean != null) {
                    showWaiting(pmBean);
                }
                break;
            }
            default:
                break;
        }
    }

    private void request(final PmBean pmBean) {
        DataRequest request = new DataRequest(this);
        request.setPmListener(new DataRequest.PmListener() {
            @Override
            public void onSuccess(String pm) {
                pmBean.pm25 = pm;
                RemoteViews remoteViews = getRemoteViews(pmBean);
                updateAppWidget(pmBean.widgetId, remoteViews);
            }

            @Override
            public void onError(String msg) {
                Logger.i("error");
                pmBean.pm25 = "???";
                RemoteViews remoteViews = getRemoteViews(pmBean);
                updateAppWidget(pmBean.widgetId, remoteViews);
            }
        });
        request.getAqi(pmBean.city);
    }

    private void showWaiting(final PmBean pmBean) {
        String pmValue = getString(query_wait);
        RemoteViews remoteViews = getRemoteViews(pmBean);
        int level = BaseWidget.getLevel(pmBean.pm25);
        Bitmap pmValueBmp;
        switch (pmBean.type) {
            case BIG:
                pmValueBmp = WidgetBig.getPmValueBmp(this, pmValue, BaseWidget.BACKGROUND[level]);
                remoteViews.setImageViewBitmap(R.id.pm25_value, pmValueBmp);
                break;
            case SMALL:
                pmValueBmp = WidgetSmall.getValueBmp(this, pmValue);
                remoteViews.setImageViewBitmap(R.id.pm25_icon, pmValueBmp);
                break;
            default:
        }
        updateAppWidget(pmBean.widgetId, remoteViews);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        request(pmBean);
    }

    private RemoteViews getRemoteViews(PmBean pmBean) {
        RemoteViews remoteViews = null;
        switch (pmBean.type) {
            case BIG:
                remoteViews = WidgetBig.getRemoteViews(this, pmBean);
                break;
            case SMALL:
                remoteViews = WidgetSmall.getRemoteViews(this, pmBean);
                break;
            default:
        }
        return remoteViews;
    }

    private void updateAppWidget(int widgetId, RemoteViews remoteViews) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
}
