package com.uais.uais.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.uais.uais.R;
import com.uais.uais.message.master_child.ChildFragmentOutBoxMaster;

import java.util.List;

/**
 * Created by HP on 10/27/2016.
 */

public class RecyclerAdapterOutBox extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<DataConstructor> mItemList;
    Context context;
    public RecyclerItemViewHolder holder;
    public View view;

    public RecyclerAdapterOutBox(List<DataConstructor> itemList) {
        mItemList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        view = LayoutInflater.from(context).inflate(R.layout.recycler_card_read, parent, false);
        view.setOnClickListener(ChildFragmentOutBoxMaster.onClickListener);
        view.setOnLongClickListener(ChildFragmentOutBoxMaster.onLongClickListener);
        return RecyclerItemViewHolder.instanceForRead(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final int pos = position;
        holder = (RecyclerItemViewHolder) viewHolder;

        CharSequence message = mItemList.get(position).getmSms_();
        CharSequence subject = mItemList.get(position).getmSubject();
        CharSequence time = mItemList.get(position).getmTime();
        boolean checked = mItemList.get(position).isSelected();
        boolean visible = mItemList.get(position).isVisibled();

        holder.setMessages(Html.fromHtml(String.valueOf(message)));
        holder.setItemSubject(subject);
        holder.setItemTime(time);

        holder.setCheck(checked);
        holder.mCheckBox.setTag(mItemList.get(position));
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                DataConstructor dc = (DataConstructor)cb.getTag();
                dc.setSelected(cb.isChecked());
                mItemList.get(pos).setSelected(cb.isChecked());
            }
        });
        holder.setShowCheckBox(visible);
    }
    public List<DataConstructor> getCheckedItem(){
        return mItemList;
    }
    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

}
