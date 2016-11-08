package com.uais.uais.profile;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;


import it.neokree.materialnavdrawer.util.Utils;

public class CircularImageView {

    private Resources resources;
    private Drawable photo;

    public CircularImageView(Resources resources, int photo){
        this.resources = resources;
        // resize and caching bitmap
        new ResizePhotoResource().execute(photo);
    }
    public Drawable getCircularPhoto() {
        return photo;
    }

    private class ResizePhotoResource extends  AsyncTask<Integer, Void, BitmapDrawable> {

        @Override
        protected BitmapDrawable doInBackground(Integer... params) {
            Point photoSize = Utils.getUserPhotoSize(resources);

            Bitmap photo = Utils.resizeBitmapFromResource(resources,params[0],photoSize.x,photoSize.y);

            BitmapDrawable circularPhoto = new BitmapDrawable(resources, Utils.getCroppedBitmapDrawable(photo));
            return circularPhoto;
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            photo = drawable;
        }
    }
}
