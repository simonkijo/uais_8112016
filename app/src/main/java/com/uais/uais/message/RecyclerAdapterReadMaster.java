package com.uais.uais.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uais.uais.R;
import com.uais.uais.message.master_child.MasterFragmentRead;

import java.util.List;

/**
 * Created by HP on 11/8/2016.
 */

public class RecyclerAdapterReadMaster extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<DataConstructor> mItemList;
    Context context;

    public RecyclerAdapterReadMaster(List<DataConstructor> itemList) {
        mItemList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_card_read_master, parent, false);
        view.setOnClickListener(MasterFragmentRead.onClickListener);
        return RecyclerItemViewHolder.instanceForReadMaster(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

        String name = mItemList.get(position).getPersonName();
        String url = mItemList.get(position).getUrl();

        holder.setItemName(name);
        holder.setItemPhotoBM(url, context);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

}
