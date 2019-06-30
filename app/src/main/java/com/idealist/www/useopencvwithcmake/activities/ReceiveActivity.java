package com.idealist.www.useopencvwithcmake.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.idealist.www.useopencvwithcmake.R;
import com.idealist.www.useopencvwithcmake.ethereum.KeysStorage;
import com.idealist.www.useopencvwithcmake.interfaces.IGenerateQR;
import com.idealist.www.useopencvwithcmake.utils.GenerateQRTask;

public class ReceiveActivity extends AppCompatActivity implements IGenerateQR {

    private ImageView imageView;
    private Button shareBtn;
    private TextView codeTV;

    GenerateQRTask task;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        setupComponents();
        retrieveKeys();


    }

    private void setupComponents(){
        imageView = (ImageView) findViewById(R.id.image_qr_generate);
        shareBtn = (Button) findViewById(R.id.share_address);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareQR();
            }
        });
    }

    private void retrieveKeys(){
        codeTV = (TextView) findViewById(R.id.code_receive);

        KeysStorage cryptography = new KeysStorage(this);
        Pair<String,String> keys =cryptography.retrieveKeys();

        if(keys.first.equals("") | keys.second.equals("")){
            codeTV.setText("No Public Key. Create a Wallet first");
        }
        else {
            String qrCode = keys.first;

            codeTV.setText(qrCode);
            task = new GenerateQRTask(qrCode, this);
            task.execute();
        }
    }

    private void shareQR(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("EthereumWallet requires permission to share the QR code. ")
                        .setCancelable(false)
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } else {
                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        123);

            }
        }
        else {
            String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
            Uri bmpUri = Uri.parse(pathofBmp);


            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra("sms_body", "some text");
            sendIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            sendIntent.setType("image/png");
            startActivity(Intent.createChooser(sendIntent, "Share with"));

        }
    }

    @Override
    public void onPostExecute(Bitmap img) {
        bitmap = img;
        imageView.setImageBitmap(img);
    }


}
