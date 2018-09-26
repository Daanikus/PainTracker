package com.github.daanikus.paintracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Here the user can record a new pain entry: enter pain level, add a comment, and the location of
 * the pain on their body. Time of entry is automatically recorded once the entry is saved.
 */

public class NewPainActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.painlistsql.REPLY";
    private EditText mEditPainView;
    private SeekBar seekBar;
    private int[] painLocation = new int[2]; //as X and Y coordinate

    /**
     * Sets the pain slider, comment field, and pain location tool, as well as defines their
     * functionality, on the activity.
     *
     * @param savedInstanceState
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pain);
        Toolbar toolbar = findViewById(R.id.toolbar_new_pain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        /*
          Tracks the location of a user's pain. Logged as an X and Y coordinate by them tapping the
          image. The most recent tap, before the save button is pushed, is recorded.
         */

        final ImageView image = findViewById(R.id.human_image_view);
        final Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        image.setOnTouchListener(new View.OnTouchListener() { // TODO work out the performClick override
            long lastClicked = System.currentTimeMillis();
            final long DEBOUNCE_TIME = 1000;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (System.currentTimeMillis() - lastClicked > DEBOUNCE_TIME) {
                    Paint paint = new Paint();
                    paint.setColor(Color.GREEN);
                    Canvas canvas = new Canvas(bitmap);
                    int touchX = (int)(event.getX());
                    int touchY = (int)(event.getY());
                    int[] imageLocation = new int[2];
                    v.getLocationOnScreen(imageLocation);
                    painLocation[0] = touchX - imageLocation[0];
                    painLocation[1] = touchY - imageLocation[1];
                    canvas.drawCircle(touchX, touchY, 20, paint);    // for circle dot
                    Toast.makeText(getApplicationContext(), "Location Recorded", Toast.LENGTH_SHORT).show();
                    image.setImageBitmap(bitmap);
                    image.invalidate();
                }
                lastClicked = System.currentTimeMillis();
                return true;
            }
        });

        /*final ImageView ivImage = findViewById(R.id.human_image_view);
        final Bitmap bitmap = ((BitmapDrawable)ivImage.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        ivImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int touchX = (int)(event.getX());
                int touchY = (int)(event.getY());
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                canvas.drawCircle(touchX, touchY, 20, paint);    // for circle dot
                //canvas.drawPoint(touchX, touchY, paint);  // for single point
                ivImage.setImageBitmap(bitmap);
                ivImage.invalidate();
                Toast.makeText(getApplicationContext(), "Location Recorded", Toast.LENGTH_SHORT).show();
                return true;
            }
        });**/

        /*
          Saves a new pain entry to the database.
         */
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(NewPainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}