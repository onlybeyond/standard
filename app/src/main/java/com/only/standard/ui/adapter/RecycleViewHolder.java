package com.only.standard.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: only beyond [FR]
 * Date: 2015/9/12
 * Email: onlybeyond99@gmail.com
 */
public class RecycleViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> viewMap = new SparseArray<View>();//save view
    private View itemView;

    public RecycleViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public View getView(int id) {
        View ret = null;
        View view = viewMap.get(id);
        if (view == null) {
            View viewById = itemView.findViewById(id);
            viewMap.put(id, viewById);
            ret = viewById;
        } else {
            ret = view;
        }
        return ret;
    }
}
