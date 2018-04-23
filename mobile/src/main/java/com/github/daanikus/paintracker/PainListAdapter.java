package com.github.daanikus.paintracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PainListAdapter extends RecyclerView.Adapter<PainListAdapter.PainViewHolder> {

    class PainViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;

        private PainViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Pain> mPains; // Cached copy of pains

    PainListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public PainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new PainViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PainViewHolder holder, int position) {
        if (mPains != null) {
            Pain current = mPains.get(position);
            holder.wordItemView.setText(current.getComment());
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No Data");
        }
    }

    void setPains(List<Pain> pains){
        mPains = pains;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mPains has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mPains != null)
            return mPains.size();
        else return 0;
    }
}