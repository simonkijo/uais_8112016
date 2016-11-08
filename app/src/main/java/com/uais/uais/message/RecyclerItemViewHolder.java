package com.uais.uais.message;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.uais.uais.R;


public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

    private TextView mPersonName, mPersonSubject, mPersonTime, mCount, mSms;
    private ImageView mPersonPhoto;
    public CheckBox mCheckBox;
    private RelativeLayout countContainer;

    public RecyclerItemViewHolder(final View parent, TextView sms, TextView subject, TextView time) {
        super(parent);
        mSms = sms;
        mPersonSubject = subject;
        mPersonTime = time;
    }
    public RecyclerItemViewHolder(final View parent, TextView personName, TextView count, ImageView personPhoto, RelativeLayout container) {
        super(parent);
        mPersonName = personName;
        mPersonPhoto = personPhoto;
        mCount = count;
        countContainer = container;
    }
    public RecyclerItemViewHolder(final View parent, TextView personName, TextView personSubject, TextView personTime, ImageView personPhoto) {
        super(parent);
        mPersonName = personName;
        mPersonSubject = personSubject;
        mPersonTime = personTime;
        mPersonPhoto = personPhoto;
    }
    public RecyclerItemViewHolder(final View parent, TextView message, TextView personSubject, TextView personTime,CheckBox checkBox) {
        super(parent);
        mSms = message;
        mPersonSubject = personSubject;
        mPersonTime = personTime;
        mCheckBox = checkBox;
    }
    public RecyclerItemViewHolder(final View parent, TextView personName, ImageView personPhoto) {
        super(parent);
        mPersonName = personName;
        mPersonPhoto = personPhoto;
    }
    public static RecyclerItemViewHolder newInstanceMaster(View parent) {
        TextView personName = (TextView) parent.findViewById(R.id.sender_name);
        TextView count = (TextView) parent.findViewById(R.id.count);
        ImageView personImage = (ImageView)parent.findViewById(R.id.img_person);
        RelativeLayout countContainer = (RelativeLayout)parent.findViewById(R.id.count_layout);
        return new RecyclerItemViewHolder(parent, personName, count,personImage,countContainer);
    }
    public static RecyclerItemViewHolder newInstance(View parent) {
        TextView message = (TextView) parent.findViewById(R.id.message);
        TextView personSubject = (TextView) parent.findViewById(R.id.subject);
        TextView personTime = (TextView) parent.findViewById(R.id.time);
        return new RecyclerItemViewHolder(parent, message, personSubject,personTime);
    }
    public static RecyclerItemViewHolder instanceForReadMaster(View parent) {
        TextView personName = (TextView) parent.findViewById(R.id.sender_name);
        ImageView personImage = (ImageView)parent.findViewById(R.id.img_person);
        return new RecyclerItemViewHolder(parent, personName,personImage);
    }
    public static RecyclerItemViewHolder instanceForRead(View parent) {
        TextView message = (TextView) parent.findViewById(R.id.message);
        TextView personSubject = (TextView) parent.findViewById(R.id.subject);
        TextView personTime = (TextView) parent.findViewById(R.id.time);
        CheckBox checkBox = (CheckBox)parent.findViewById(R.id.check_id);
        return new RecyclerItemViewHolder(parent, message, personSubject,personTime,checkBox);
    }

    public void setMessages(CharSequence sms){mSms.setText(sms);}
    public void setCount(String count){
        if(count.equals("0")){
           countContainer.setVisibility(View.GONE);
        }else{
            mCount.setText(count);
        }
    }
    public void setItemName(CharSequence text) {
        mPersonName.setText(text);
    }
    public void setItemSubject(CharSequence text) {
        mPersonSubject.setText(text);
    }
    public void setItemTime(CharSequence text) {
        mPersonTime.setText(text);
    }
    public void setItemPhoto(int image) {
        mPersonPhoto.setImageResource(image);
    }
    public void setShowCheckBox(boolean show){
        mCheckBox.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    public void setCheck(boolean check){mCheckBox.setChecked(check);}
    public void setItemPhotoBM(String url, Context context) {
        if(url.equals("url")){
            Picasso.with(context)
                    .load(url)  //http://i.imgur.com/DvpvklR.png
                    .into(mPersonPhoto);
        }else{
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.empty_photo)
                    .error(R.drawable.empty_photo)
                    .resize(140, 140)
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .into(mPersonPhoto);
        }

    }

    private class CropCircleTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            Bitmap result = getCroppedBitmapDrawable(source);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() { return "square()"; }
        //convert bitmap to circle
        Bitmap getCroppedBitmapDrawable(Bitmap bitmap) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
    }
}
