package com.only.standard.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.only.standard.R;

/**
 * Created by only on 16/10/19.
 * Email: onlybeyond99@gmail.com
 */

public class TestAdapter extends RecyclerView.Adapter<RecycleViewHolder> {

    private Context mContext;

    public TestAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_test, parent, false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 25;
    }
}
