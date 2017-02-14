package com.udacity.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.history)
    LineChart historyChart;

    String symbol;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent receivedIntent = getIntent();
        symbol = receivedIntent.getStringExtra("symbol");

        setTitle(symbol);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Description description = new Description();
        description.setText(getString(R.string.description, symbol));

        historyChart.setDescription(description);
        historyChart.setDrawBorders(false);


        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Uri.withAppendedPath(Contract.Quote.URI, symbol), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        String historyString = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
        List<String> items = Arrays.asList(historyString.split("\\s*\\n\\s*"));
        List<String> xValues = new ArrayList<String>();
        List<Entry> yValues = new ArrayList<>();

        for (int i = items.size() - 1; i >= 0; i--) {
            String[] item = items.get(i).split(",");
            xValues.add(item[0]);
            yValues.add(new Entry(items.size() - i - 1, Float.valueOf(item[1])));
        }

        LineDataSet yValuesDataSet = new LineDataSet(yValues, "Stock Value(in $)");
        LineData yValueLine = new LineData(yValuesDataSet);

        String[] xAxisValues = new String[xValues.size()];
        xValues.toArray(xAxisValues);
        XAxis xAxis = historyChart.getXAxis();
        xAxis.setValueFormatter(new MyXValueFormatter(xAxisValues));
        historyChart.setData(yValueLine);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public class MyXValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        MyXValueFormatter(String[] values) {
            mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }
}
