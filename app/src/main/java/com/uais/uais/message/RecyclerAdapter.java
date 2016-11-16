package com.uais.uais.message;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.uais.uais.R;
import com.uais.uais.message.master_child.ChildFragmentMessage;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DataConstructor> mItemList;
    Context context;

    public RecyclerAdapter(List<DataConstructor> itemList) {
        mItemList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_card, parent, false);
        view.setOnClickListener(ChildFragmentMessage.onClickListener);
        return RecyclerItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

        CharSequence mMessages = mItemList.get(position).getmSms_();
        CharSequence subject = mItemList.get(position).getmSubject();
        CharSequence time = mItemList.get(position).getmTime();

        holder.setMessages(Html.fromHtml(String.valueOf(mMessages)));
        holder.setItemSubject(Html.fromHtml(String.valueOf(subject)));
        holder.setItemTime(time);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

}