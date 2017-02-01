package com.example.peter.myapplication;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRDisplayActivity extends AppCompatActivity {
    String QRcode;
    public final static int WIDTH=500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO: change QR string
        QRcode =
                "this string needs to contain all user information, possible a JSON or URL?";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdisplay);
        ImageView imageView = (ImageView) findViewById(R.id.ivQRCode);
        try {
            Bitmap bitmap = encodeAsBitmap(QRcode);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pixels[offset + x] = result.get(x, y) ? getColor(R.color.black):getColor(R.color.white);
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }


}