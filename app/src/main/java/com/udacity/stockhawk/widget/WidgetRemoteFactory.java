package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.DbHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by trikh on 24-02-2017.
 */

public class WidgetRemoteFactory implements RemoteViewsService.RemoteViewsFactory {
    final private DecimalFormat dollarFormat, dollarFormatWithPlus;
    private Context mContext;
    private Cursor mCursor;
    private DbHelper mDbHelper;

    public WidgetRemoteFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
    }

    @Override
    public void onCreate() {
        mDbHelper = new DbHelper(mContext);
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        if (mCursor != null) {
            mCursor.close();
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        mCursor = db.query(Contract.Quote.TABLE_NAME, new String[]{Contract.Quote._ID, Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_PRICE,
                Contract.Quote.COLUMN_ABSOLUTE_CHANGE}, null, null, null, null, null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        mCursor.close();
        mCursor = null;
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews listItemView = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        if (mCursor.moveToPosition(position)) {
            listItemView.setTextViewText(R.id.symbol_widget, mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
            listItemView.setTextViewText(R.id.price_widget, dollarFormat.format(mCursor.getFloat(mCursor.getColumnIndex(Contract.Quote.COLUMN_PRICE))));
            float rawAbsoluteChange = mCursor.getFloat(mCursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
            String changeString = dollarFormatWithPlus.format(rawAbsoluteChange);

            if (rawAbsoluteChange > 0) {
                listItemView.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            } else {
                listItemView.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }

            listItemView.setTextViewText(R.id.change_widget, changeString);
        }
        return listItemView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
