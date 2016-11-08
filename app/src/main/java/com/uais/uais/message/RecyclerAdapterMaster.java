package com.uais.uais.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uais.uais.R;
import com.uais.uais.message.master_child.MasterFragmentInbox;

import java.util.List;

/**
 * Created by HP on 11/7/2016.
 */

public class RecyclerAdapterMaster extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<DataConstructor> mItemList;
    Context context;

    public RecyclerAdapterMaster(List<DataConstructor> itemList) {
        mItemList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_card_master, parent, false);
        view.setOnClickListener(MasterFragmentInbox.onClickListener);
        return RecyclerItemViewHolder.newInstanceMaster(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

        String name = mItemList.get(position).getPersonName();
        int count = mItemList.get(position).getCountNew();
        String url = mItemList.get(position).getUrl();

        holder.setItemName(name);
        holder.setCount(String.valueOf(count));
        holder.setItemPhotoBM(url, context);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

}
