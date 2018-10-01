package com.github.daanikus.paintracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.Calendar;
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
    public static boolean notificationsOn;
    private static final String TAG = "MainActivity";
    private GraphView graph;
    private TextView cardTextView;
    private TextView graphDayTextView;
    private ArrayList<Pain> staticData;
    private DrawerLayout mDrawerLayout;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 666;

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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        notificationsOn = sharedPref.getBoolean("pref_notif", true);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        this.graph = initializeGraph();

        cardTextView = findViewById(R.id.card_text_view);

        graphDayTextView = findViewById(R.id.graph_day_text_view);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        FloatingActionButton fab = findViewById(R.id.fab);

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

        SharedPreferences sp = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        if (!sp.getBoolean("first", false)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("first", true);
            editor.apply();
            Intent intent = new Intent(this, IntroActivity.class); // Call the AppIntro java class
            startActivity(intent);
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(false);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        switch (menuItem.getTitle().toString()) {
                            case "Statistics":
                                Intent statsIntent = new Intent(MainActivity.this, StatsActivity.class);
                                startActivity(statsIntent);
                                break;
                            case "Settings":
                                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(settingsIntent);
                                break;
                            case "Help":
                                Intent intent = new Intent(MainActivity.this, IntroActivity.class); // Call the AppIntro java class
                                startActivity(intent);
                                break;
                        }

                        return true;
                    }
                });

        int hourlyInterval = 3600000; //time in milliseconds
        startAlertReceiver(hourlyInterval);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
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
            case R.id.about_options:
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("About");

                alertDialog.setMessage(getString(R.string.about_app) + " " + getString(R.string.git_url));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
            case R.id.export_to_pdf_options:
                createPdf();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    createPdf();
                }
                return;
            }

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


        graph.getGridLabelRenderer()
                .setHorizontalLabelsColor(getResources().getColor(android.R.color.black));
        graph.getGridLabelRenderer()
                .setVerticalLabelsColor(getResources().getColor(android.R.color.black));
        graph.getGridLabelRenderer()
                .setGridColor(getResources().getColor(android.R.color.black));

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
        int totalPainEntries = 0;
        int totalPainLevel = 0;
        for (Pain p : pains) {
            totalPainEntries++;
            totalPainLevel += p.getPainLevel();
            Stats.setMax(p.getPainLevel());
            Stats.setMin(p.getPainLevel());
            Date date = new Date(p.getTimestamp());
            if (p.getTimestamp() > Stats.getMostRecent()) {
                Stats.setMostRecent(p.getTimestamp());
            }
            series.appendData(new DataPoint(date, p.getPainLevel()),
                    true, 10);
        }
        Stats.setTotalEntries(totalPainEntries);
        Stats.setTotalPain(totalPainLevel);
        Stats.updateAvePainLevel();
        //count pains to update pains stats...



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
                                        + p.getPainLevel());

                        Log.i("Static Data:", "Timestamp - "+p.getTimestamp());
                        break;
                    }
                }

            }
        });
    }

    public void createPdf() {

        if (staticData == null || staticData.size() == 0) {
            Toast.makeText(this, "No data. Add some entries to generate a PDF", Toast.LENGTH_SHORT).show();
            return;

        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }

        } else {

            // create a new document
            PdfDocument document = new PdfDocument();

            // crate a page description
            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(595, 842, 1).create();


            // start a page
            PdfDocument.Page page = document.startPage(pageInfo);

            // draw something on the page
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            int y = 100;
            int index = 0;
            for (Pain p : staticData) {
                // Need to make a new page for each 11 entries
                if (index % 11 == 0 && index != 0) {
                    document.finishPage(page);
                    page = document.startPage(pageInfo);
                    canvas = page.getCanvas();
                    paint = new Paint();
                    paint.setColor(Color.BLACK);
                    y = 100;
                }
                String output = ("Comment: "
                        + p.getComment()
                        + "\nPain Level: "
                        + p.getPainLevel()
                        + "\n");
                canvas.drawText(p.getTimeAsFormattedString(), 100, y, paint);
                canvas.drawText(output, 100, y + 20, paint);
                canvas.drawText("--------------------", 100, y + 40, paint);
                y += 60;
                index++;
            }
            document.finishPage(page);

            // write the document content
            String targetPdf = Environment.getExternalStorageDirectory().getPath();
            Date currentTime = Calendar.getInstance().getTime();
            File filePath = new File(targetPdf + "/Download/" + currentTime.toString() + ".pdf");
            Boolean success = false;
            try {
                document.writeTo(new FileOutputStream(filePath));
                Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "File permissions off. Please enable storage access for this app and try again.",
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
                            "No PDF viewer found. File saved to Downloads."
                            , Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    /**
     * Starts an alarm, that calls the onReceive method of the AlertReceiver class at a defined
     * interval.
     *
     * @param interval time in milliseconds
     */
    public void startAlertReceiver(long interval){
        // If notifications disabled in settings, don't do anything
        if (!notificationsOn) return;

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }


}
