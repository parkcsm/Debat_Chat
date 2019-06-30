package com.idealist.www.useopencvwithcmake.utils;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.idealist.www.useopencvwithcmake.interfaces.IGenerateQR;


/**
 * Created by alex on 24/07/2017.
 */

public class GenerateQRTask extends AsyncTask<Void,Void,Bitmap> {

    private String mQRcode;
    private IGenerateQR igenerateQR = null;
    private ImageView iv;
    private Bitmap bitmap;
    public static int white = 0xFFFFFFFF;
    public static int black = 0xFF000000;
    public final static int QRcodeWidth = 500 ;

    public GenerateQRTask(String qrCode,IGenerateQR igenerateQR){
        this.igenerateQR = igenerateQR;
        mQRcode = qrCode;
        //this.iv = iv;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {


        try {
            //bitmap = TextToImageEncode(qrCode);
            bitmap = encodeAsBitmap(mQRcode);
            //igenerateQR.onPostExecute(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;

    }
    @Override
    public void onPostExecute(Bitmap returned_bitmap) {
        try {
            if(this.igenerateQR != null)
                this.igenerateQR.onPostExecute(this.bitmap);
        }
        catch(Exception e) {
            String a = e.getMessage();
        }
        //task.cancel(true);
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, QRcodeWidth, QRcodeWidth, null);
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
                pixels[offset + x] = result.get(x, y) ? black:white;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, QRcodeWidth, 0, 0, w, h);
        return bitmap;
    }
}
