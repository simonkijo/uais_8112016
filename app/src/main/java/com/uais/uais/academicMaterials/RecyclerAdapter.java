package com.uais.uais.academicMaterials;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.uais.uais.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<? extends ChosenFile> files;

    public RecyclerAdapter(List<? extends ChosenFile> files_) {
        files = files_;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_academic, parent, false);
        return RecyclerItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

        ChosenFile file =  files.get(position);
        holder.setItemText(file.getDisplayName());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return files==null ? 0: files.size();
    }

}
