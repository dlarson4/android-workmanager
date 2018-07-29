package com.example.background.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.background.Constants;
import com.example.background.R;

import java.io.FileNotFoundException;

import androidx.work.Data;
import androidx.work.Worker;

import static com.example.background.Constants.KEY_IMAGE_URI;

public class BlurWorker extends Worker {
    private static final String TAG = "BlurWorker";
    @NonNull
    @Override
    public Result doWork() {

        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);
        if (TextUtils.isEmpty(resourceUri)) {
            Log.e(TAG, "Invalid input uri");
            throw new IllegalArgumentException("Invalid input uri");
        }

        final Context applicationContext = getApplicationContext();

        ContentResolver resolver = applicationContext.getContentResolver();

        try {
            Bitmap original =
                    BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)));

            Bitmap blurred =
                    WorkerUtils.blurBitmap(original, applicationContext);
            Uri outputUri = WorkerUtils.writeBitmapToFile(applicationContext, blurred);

            setOutputData(new Data.Builder().putString(
                    Constants.KEY_IMAGE_URI, outputUri.toString()).build());

            WorkerUtils.makeStatusNotification(outputUri.toString(), applicationContext);
            return Result.SUCCESS;
        } catch (Throwable e) {
            Log.e(TAG, "Error applying blur", e);
            return Result.FAILURE;
        }
    }

    private Bitmap getImage(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.test);
    }
}
