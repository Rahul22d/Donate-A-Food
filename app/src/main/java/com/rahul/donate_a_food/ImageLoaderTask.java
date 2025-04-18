package com.rahul.donate_a_food;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.URL;

public class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;

    public ImageLoaderTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imageUrl = params[0];
        Bitmap bitmap = null;
        try {
            // Create a URL object from the image URL
            InputStream inputStream = new URL(imageUrl).openStream();
            // Decode the InputStream into a Bitmap
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        // Set the bitmap to the ImageView after background task completion
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}

