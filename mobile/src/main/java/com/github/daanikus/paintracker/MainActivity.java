package com.github.daanikus.paintracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Notification.VISIBILITY_PUBLIC;

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
    private NotificationManagerCompat notificationManager;
    private static int count = 0;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.graph = initializeGraph();
        cardTextView = findViewById(R.id.card_text_view);
        graphDayTextView = findViewById(R.id.graph_day_text_view);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final List<Pain> staticData = null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewPainActivity.class);
                startActivityForResult(intent, NEW_PAIN_ACTIVITY_REQUEST_CODE);
            }
        });
        Button pdfButton = findViewById(R.id.button_pdf);
        pdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPdf();
            }
        });

        mPainViewModel = ViewModelProviders.of(this).get(PainViewModel.class);
        mPainViewModel.getAllPains().observe(this, new Observer<List<Pain>>() {
            @Override
            public void onChanged(@Nullable final List<Pain> pains) {

                updateGraph(pains);
            }
        });

        notificationManager = NotificationManagerCompat.from(this);
        count++;

        //sendOnChannel1(1);

        if(staticData != null) {
            if(staticData.size() == 0) {
                sendOnChannel1(count);
            }
        }

        //sendOnChannel2();
        startAlarm();
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
        this.staticData = new ArrayList<Pain>(pains);
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>();

        this.graph.removeAllSeries();
        for (Pain p : pains) {
            Date date = new Date(p.getTimestamp());
            series.appendData(new DataPoint(date, p.getPainLevel()),
                    true, 10);
        }


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
                graphDayTextView.setText(date);
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

    //notification
    public void sendOnChannel1(int count){
        Intent activityIntent = new Intent(this, NewPainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        //broadcast toast notification
        /*Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", "Welcome!");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0,
                broadcastIntent, FLAG_UPDATE_CURRENT);*/


        Notification notification = new NotificationCompat.Builder(this, MyNotificationChannel.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Notification")
                .setContentText("Hi!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setVisibility(VISIBILITY_PUBLIC)
                .setColor(Color.BLUE)
                .build();//change colour.

        notificationManager.notify(1, notification);
    }

    //Reminder
    public void startAlarm(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 13);
        c.set(Calendar.MINUTE, 44);
        c.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

        Log.i("Time", ""+c.toString());
    }


}
