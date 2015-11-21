package de.berlin.special.concertmap.navigate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Saeed on 21-Nov-15.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;
    File imageDir;
    String imageName;
    private final String LOG_TAG = DownloadImageTask.class.getSimpleName();

    public DownloadImageTask(ImageView imageView, File imageDir, String imageName) {
        this.imageView = imageView;
        this.imageDir = imageDir;
        this.imageName = imageName;
    }

    protected Bitmap doInBackground(String... urls) {
        String imageURL = urls[0];
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(imageURL).openStream();
            mIcon = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error downloading the image");
            Log.e(LOG_TAG, e.getMessage());
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap imageToSave) {
        imageView.setImageBitmap(imageToSave);

        File file = new File(imageDir, imageName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error writing the image file to sdcard");
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
