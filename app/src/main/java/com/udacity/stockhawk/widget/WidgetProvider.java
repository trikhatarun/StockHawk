package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;

/**
 * Created by trikh on 23-02-2017.
 */

public class WidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Intent adapter = new Intent(context, WidgetService.class);
            remoteViews.setEmptyView(R.id.list_view, R.id.empty_view);
            remoteViews.setRemoteAdapter(R.id.list_view, adapter);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
