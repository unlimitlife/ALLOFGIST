package com.allofgist.dell.allofgistlite;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class FavoriteWidget extends AppWidgetProvider {
    public ArrayList<Integer> keylist;
    public Context mcontext;
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

    }

    /*static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,int appWidgetId) {
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.favoritelist_widget);
    }*/


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.favorite_widget);
            Intent intent = new Intent(context, FavoriteWidgetService.class);

            rv.setRemoteAdapter(R.id.favoritelist_widget, intent);

            Intent itemIntent = new Intent(Intent.ACTION_VIEW);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    0,itemIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            Intent SettingIntent = new Intent(context,FavoriteSettingActivity.class);
            SettingIntent.putExtra("ID",MainActivity.id);
            PendingIntent pendingIntent1 = PendingIntent.getActivity(context,
                    0,SettingIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            rv.setPendingIntentTemplate(R.id.favoritelist_widget, pendingIntent);
            rv.setOnClickPendingIntent(R.id.favorite_widget_layout,pendingIntent1);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
    }

    @Override
    public void onEnabled(Context context) {

        // Enter relevant functionality for when the first widget is created
        /*mcontext = context;
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.favorite_widget);
        rv.setViewVisibility(R.id.favorite_notice_widget,View.VISIBLE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, FavoriteWidget.class));
        appWidgetManager.updateAppWidget(appWidgetIds,rv);*/
        //super.onEnabled(context);

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
    }

}

