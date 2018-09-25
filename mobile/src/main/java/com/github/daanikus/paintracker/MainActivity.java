package com.github.daanikus.paintracker;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Here the user can view and interact with their pain history by viewing the graph, and tapping on
 * a particular data point to expand its information. The user can also add a new pain entry by
 * tapping the floating action button.
 */

public class MainActivity extends AppCompatActivity {

    private static PainViewModel mPainViewModel;
    public static final int NEW_PAIN_ACTIVITY_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private GraphView graph;
    private TextView cardTextView;
    private TextView graphDayTextView;
    private ArrayList<Pain> staticData;
    private DrawerLayout mDrawerLayout;

    /**
     * Initializes the users home screen with a graph and button to add a new pain entry. Updates
     * the graph with the users history of previous pain entries.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        this.graph = initializeGraph();

        cardTextView = findViewById(R.id.card_text_view);

        graphDayTextView = findViewById(R.id.graph_day_text_view);

        FloatingActionButton fab = findViewById(R.id.fab);

        final List<Pain> staticData = null; //TODO what's this?

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewPainActivity.class);
                startActivityForResult(intent, NEW_PAIN_ACTIVITY_REQUEST_CODE);
            }
        });


        mPainViewModel = ViewModelProviders.of(this).get(PainViewModel.class);
        mPainViewModel.getAllPains().observe(this, new Observer<List<Pain>>() {
            @Override
            public void onChanged(@Nullable final List<Pain> pains) {
                updateStaticData(pains);
                updateGraph(pains);
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        switch (menuItem.getTitle().toString()) {
                            case "Home":
                                Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                                break;
                            case "History":
                                // Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                                Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
                                startActivity(historyIntent);
                                break;
                            case "Export to PDF":
                                // Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                                createPdf();
                                break;
                            case "Help":
                                AlertDialog helpDialog = new AlertDialog.Builder(MainActivity.this).create();
                                helpDialog.setTitle("Help");
                                helpDialog.setMessage(getString(R.string.help_dialog));
                                helpDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                helpDialog.show();
                                break;
                            case "About":
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog.setTitle("About");
                                alertDialog.setMessage(getString(R.string.about_app));
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                                break;
                        }

                        return true;
                    }
                });

        startAlertRecevicerRepeating(AlarmManager.INTERVAL_HALF_HOUR);

    }

    public void updateStaticData(List<Pain> pains) {
        this.staticData = (ArrayList) pains;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Stores a pain entry in the database.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_PAIN_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            Pain pain;
            try {
                pain = new Pain(b.getString("COMMENT"),
                        b.getInt("PAIN_LEVEL"),
                        b.getLong("TIMESTAMP"),
                        b.getInt("LOCATION_X"),
                        b.getInt("LOCATION_Y"));
                mPainViewModel.insert(pain);
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e(TAG, "Pain object is null");
            }

        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Creates a graph with various attributes that define how data is displayed and how the user
     * can interact with it.
     *
     * @return graph
     */
    public GraphView initializeGraph() {
        int PAIN_SCALE_LOWER = 0;
        int PAIN_SCALE_UPPER = 10;
        int X_MIN = 600000;
        int X_MAX = 6000000;
        int X_AXIS_LABEL_ANGLE = 55;

        GraphView graph = findViewById(R.id.graph);
        graph.getViewport().setBackgroundColor(getResources().getColor(android.R.color.white));

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(PAIN_SCALE_LOWER);
        graph.getViewport().setMaxY(PAIN_SCALE_UPPER);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(X_MIN);
        graph.getViewport().setMaxX(X_MAX);

        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);


        graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(android.R.color.black));
        graph.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(android.R.color.black));
        graph.getGridLabelRenderer().setGridColor(getResources().getColor(android.R.color.black));

        graph.getGridLabelRenderer().setHorizontalLabelsAngle(X_AXIS_LABEL_ANGLE);
        graph.getGridLabelRenderer().setNumHorizontalLabels(PAIN_SCALE_UPPER);

        return graph;
    }

    /**
     * Updates the graph with all history (pain points) and displays the information (time, comment,
     * level, x and y coordinates) in a card below the graph on the click of a data point.
     *
     * @param pains
     */
    public void updateGraph(final List<Pain> pains) {
        this.staticData = new ArrayList<>(pains);
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>();

        this.graph.removeAllSeries();
        for (Pain p : pains) {
            Stats.updateStats(p);
            Date date = new Date(p.getTimestamp());
            if (p.getTimestamp() > Stats.getMostRecent()) {
                Stats.setMostRecent(p.getTimestamp());
            }
            series.appendData(new DataPoint(date, p.getPainLevel()),
                    true, 10);
        }
        Stats.printStats();


        series.setColor(getResources().getColor(R.color.colorAccent));
        this.graph.addSeries(series);

        DateFormat sdf = new SimpleDateFormat("h:mm a");
        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(
                new DateAsXAxisLabelFormatter(MainActivity.this, sdf));
        graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 5 because of the space

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getViewport().scrollToEnd();

        // Get the most recent entry to set the graph date
        if (!pains.isEmpty()) {
            Pain lastPainForDate = pains.get(pains.size() - 1);
            if (lastPainForDate != null) {
                String date = lastPainForDate.getDayAsFormattedString();
                graphDayTextView.setText("Graph showing: " + date);
                graphDayTextView.setTextColor(getResources().getColor(R.color.black));
                Log.d(TAG,"Day set to " + date);
            }
        }

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                // Set card view
                for (Pain p : pains) {
                    if (p.getTimestamp() == dataPoint.getX()) {
                        cardTextView.setText(p.getTimeAsFormattedString()
                                        + "\nComment: "
                                        + p.getComment()
                                        + "\nPain Level: "
                                        + p.getPainLevel()
                                        + "\nX: "
                                        + p.getLocationX()
                                        + "  Y: "
                                        + p.getLocationY());

                        Log.i("Static Data:", "Timestamp - "+p.getTimestamp());
                        break;
                    }
                }

            }
        });
    }

    public void createPdf() {
        // create a new document
        PdfDocument document = new PdfDocument();

        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        // draw something on the page
        //View content = this.getContentView();
        //content.draw(page.getCanvas());
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        //ByteArrayOutputStream os = new ByteArrayOutputStream();
        Bitmap graphImage = this.graph.takeSnapshot();
        canvas.drawBitmap(graphImage, new Rect(0, 0, 100, 100),  new Rect(0, 0, 100, 100), null);
        for (Pain p : staticData) {
            String output = (p.getTimeAsFormattedString()
                    + "\nComment: "
                    + p.getComment()
                    + "\nPain Level: "
                    + p.getPainLevel()
                    + "\nX: "
                    + p.getLocationX()
                    + "  Y: "
                    + p.getLocationY());
            canvas.drawText(output, 100, 100, paint);
        }
        document.finishPage(page);



        // write the document content
        String targetPdf = Environment.getExternalStorageDirectory().getPath();
        File filePath = new File(targetPdf + "/test.pdf");
        Boolean success = false;
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
        if (success) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(filePath.toString()));
            intent.setType("application/pdf");
            PackageManager pm = getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
            if (activities.size() > 0) {
                startActivity(intent);
            } else {
                Toast.makeText(this,
                        "No PDF viewer found. File saved to storage."
                        , Toast.LENGTH_LONG).show();
            }
        }
    }

    //Reminder

    /**
     * This function starts a repeating alarm, that will, every half hour, call the AlertReceiver.
     * The AlertReceiver then evaluates whether a notification should be pushed or not.
     *
     * @param interval
     */
    public void startAlertRecevicerRepeating(long interval){
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

}
