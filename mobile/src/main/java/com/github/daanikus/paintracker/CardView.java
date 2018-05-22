package com.github.daanikus.paintracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class CardView extends AppCompatActivity {
    private static PainViewModel mPainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        //RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final PainListAdapter adapter = new PainListAdapter(this);
        //recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPainViewModel = ViewModelProviders.of(this).get(PainViewModel.class);
        mPainViewModel.getAllPains().observe(this, new Observer<List<Pain>>() {
            @Override
            public void onChanged(@Nullable final List<Pain> pains) {
                for(Pain p : pains) {
                    Log.i("TEST", p.getId()+": "
                            +p.getPainLevel()+", "
                            +p.getTimestamp()+".\n");
                }
                //adapter.setPains(pains);
            }
        });


    }
}
