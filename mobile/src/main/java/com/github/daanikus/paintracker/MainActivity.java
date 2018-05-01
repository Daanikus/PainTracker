package com.github.daanikus.paintracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PainViewModel mPainViewModel;
    public static final int NEW_PAIN_ACTIVITY_REQUEST_CODE = 1;
    private GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.graph = findViewById(R.id.graph);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewPainActivity.class);
                startActivityForResult(intent, NEW_PAIN_ACTIVITY_REQUEST_CODE);
            }
        });


        // TODO move RecyclerView stuff to new class file
        //RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final PainListAdapter adapter = new PainListAdapter(this);
        //recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPainViewModel = ViewModelProviders.of(this).get(PainViewModel.class);
        mPainViewModel.getAllPains().observe(this, new Observer<List<Pain>>() {
            @Override
            public void onChanged(@Nullable final List<Pain> pains) {
                // Reload graph
                // Update the cached copy of the pains in the adapter.
                updateGraph(pains);
                adapter.setPains(pains);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_PAIN_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            Pain pain;
            try {
                pain = new Pain(b.getString("COMMENT"),
                        b.getInt("PAIN_LEVEL"),
                        b.getLong("TIMESTAMP"));
                mPainViewModel.insert(pain);
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("MainActivity.onActivityResult", "Pain object is null");
            }

        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void initializeGraph() {

    }

    public void updateGraph(final List<Pain> pains) {
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>();
        this.graph.removeAllSeries();
        int i = 0;
        DateFormat sdf = new SimpleDateFormat("h:mm a");
        for (Pain p : pains) {
            Date date = new Date(p.getTimestamp());
            if (date == null) return;
            sdf.format(date);
            series.appendData(new DataPoint(date, p.getPainLevel()),
                    true, 5);
        }

        this.graph.addSeries(series);
        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(
                new DateAsXAxisLabelFormatter(MainActivity.this, sdf));
        graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 5 because of the space

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {

                for (Pain p : pains) {
                    if (p.getTimestamp() == dataPoint.getX()) {
                        Toast.makeText(MainActivity.this,
                                p.getTimeAsFormattedString() + "\n" + p.getComment(),
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

            }
        });
    }

}
