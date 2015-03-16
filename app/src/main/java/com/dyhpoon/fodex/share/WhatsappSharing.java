package com.dyhpoon.fodex.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.dyhpoon.fodex.util.MediaImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by darrenpoon on 16/3/15.
 */
public class WhatsappSharing extends Sharing {

    private static final String WHATSAPP_PACKAGE = "com.whatsapp";

    @Override
    public void shareImage(Context context, Uri uri) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        Bitmap bitmap = MediaImage.getDecodedBitmap(context, uri, width, height);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");

        try {
            f.createNewFile();
            new FileOutputStream(f).write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        shareIntent.putExtra(Intent.EXTRA_STREAM,
                Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg"));
        if (isPackageInstalled(WHATSAPP_PACKAGE, context)) {
            shareIntent.setPackage(WHATSAPP_PACKAGE);
            context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            Toast.makeText(context, "Please Install Whatsapp", Toast.LENGTH_LONG).show();
        }
    }
}
