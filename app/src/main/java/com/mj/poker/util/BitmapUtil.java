package com.mj.poker.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class BitmapUtil {
    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapByName(Context context, String name) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        int resID = context.getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(context.getResources(), resID);
    }

    public static Bitmap scale(Bitmap bitmap, int width, int height) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scaleWidth = ((float) width) / srcWidth;
        float scareHeight = ((float) height) / srcHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scareHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, srcWidth, srcHeight, matrix, true);
    }
}
