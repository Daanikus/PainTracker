package com.github.daanikus.paintracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewPainActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.painlistsql.REPLY";

    private EditText mEditPainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pain);
        mEditPainView = findViewById(R.id.edit_pain);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditPainView.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String painComment = mEditPainView.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY, painComment);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}