package com.github.daanikus.paintracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This activity presents the various statistics, that are provided by the Stats class, in a  user
 * friendly format.
 */
public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = findViewById(R.id.toolbar_stats);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Date date = new Date(Stats.getMostRecent());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy h:mm a");

        TextView mostRecent = (TextView) findViewById(R.id.mostRecent);
        mostRecent.setText("Most Recent: "+ sdf.format(date));

        TextView maxPain = (TextView) findViewById(R.id.maxPain);
        maxPain.setText("Maximum: "+Stats.getMax());

        TextView minPain = (TextView) findViewById(R.id.minPain);
        minPain.setText("Minimum: "+Stats.getMin());

        TextView avePain = (TextView) findViewById(R.id.avePain);
        avePain.setText("Average: "+Stats.getAvePainLevel());

        TextView totalEntries = (TextView) findViewById(R.id.totalEntries);
        totalEntries.setText("Total Entries: "+Stats.getTotalEntries());


    }
}
