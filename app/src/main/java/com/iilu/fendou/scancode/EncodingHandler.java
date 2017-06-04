package com.iilu.fendou.scancode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

public class EncodingHandler {

    private static final int BLACK = 0xff434343;
    private static final int WHITE = 0xffffffff;

    /**
     * @param text 写入的文字
     * @param width 宽
     * @param width 高
     * @param bitmap 头像
     * @return
     * @throws WriterException
     */
    public static Bitmap createQRCode(Context context, String text, int width, int height, Bitmap bitmap) {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 字符编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); // 纠错等级
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int matrixWidth = matrix.getWidth();
        int matrixHeight = matrix.getHeight();
        int[] pixels = new int[matrixWidth * matrixHeight];
        for (int y = 0; y < matrixHeight; y++) {
            for (int x = 0; x < matrixWidth; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * matrixWidth + x] = BLACK;
                } else {
                    pixels[y * matrixWidth + x] = WHITE;
                }
            }
        }
        Bitmap resultBitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_4444);
        resultBitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);
        if (bitmap != null) {
            Paint paint = new Paint();
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawARGB(0, 0, 0, 0);// 透明色
            /*Rect dst1 = new Rect();
            dst1.left = matrixWidth * 2 / 5 - 6;
            dst1.top = matrixHeight * 2 / 5 - 6;
            dst1.right = matrixWidth * 3 / 5 + 6;
            dst1.bottom = matrixHeight * 3 / 5 + 6;
            canvas.drawBitmap(bitmap, null, dst1, paint);*/
            Rect dst = new Rect();

            dst.left = matrixWidth * 2 / 5;
            dst.top = matrixHeight * 2 / 5;
            dst.right = matrixWidth * 3 / 5;
            dst.bottom = matrixHeight * 3 / 5;
            canvas.drawBitmap(bitmap, null, dst, paint);
        }
        return resultBitmap;
    }
}
