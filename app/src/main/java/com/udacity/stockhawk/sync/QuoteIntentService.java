package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.widget.WidgetProvider;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        QuoteSyncJob.getQuotes(getApplicationContext());
        Intent widgetIntent = new Intent(this, WidgetProvider.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int widgetId = R.xml.appwidget;
        int[] ids = {widgetId};
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(widgetIntent);
        Timber.d("Intent handled");
    }
}
