package com.github.daanikus.paintracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class NewPainActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.painlistsql.REPLY";
    private EditText mEditPainView;
    private SeekBar seekBar;
    private int[] painLocation = new int[2];

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pain);
        mEditPainView = findViewById(R.id.edit_pain);
        seekBar = findViewById(R.id.pain_seekbar);
        final TextView seekTextView = findViewById(R.id.new_pain_seek_text_view);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekTextView.setText("Pain Level: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Unused, but must override
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Unused, but must override
            }
        });

        ImageView image = findViewById(R.id.human_image_view);
        image.setOnTouchListener(new View.OnTouchListener() { // TODO work out the performClick override
            long lastClicked = System.currentTimeMillis();
            final long DEBOUNCE_TIME = 1000;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.BLACK);
                if (System.currentTimeMillis() - lastClicked > DEBOUNCE_TIME) {
                    float x = event.getX();
                    float y = event.getY();
                    int[] imageLocation = new int[2];
                    v.getLocationOnScreen(imageLocation);
                    painLocation[0] = (int) x - imageLocation[0];
                    painLocation[1] = (int) y - imageLocation[1];
                    Toast.makeText(getApplicationContext(), "Location Recorded", Toast.LENGTH_SHORT).show();
                }
                lastClicked = System.currentTimeMillis();
                return true;
            }
        });

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (seekBar.getProgress() == 0) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String painComment = mEditPainView.getText().toString();
                    int progress = seekBar.getProgress();
                    long time = System.currentTimeMillis();

                    Bundle b = new Bundle();

                    b.putString("COMMENT", painComment);
                    b.putInt("PAIN_LEVEL", progress);
                    b.putLong("TIMESTAMP", time);
                    b.putInt("LOCATION_X", painLocation[0]);
                    b.putInt("LOCATION_Y", painLocation[1]);
                    replyIntent.putExtras(b);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}