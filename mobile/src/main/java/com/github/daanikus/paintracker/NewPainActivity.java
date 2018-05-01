package com.github.daanikus.paintracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class NewPainActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.painlistsql.REPLY";

    private EditText mEditPainView;
    private SeekBar seekBar;

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

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditPainView.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String painComment = mEditPainView.getText().toString();
                    int progress = seekBar.getProgress();
                    long time = System.currentTimeMillis();
                    Bundle b = new Bundle();

                    b.putString("COMMENT", painComment);
                    b.putInt("PAIN_LEVEL", progress);
                    b.putLong("TIMESTAMP", time);
                    replyIntent.putExtras(b);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}