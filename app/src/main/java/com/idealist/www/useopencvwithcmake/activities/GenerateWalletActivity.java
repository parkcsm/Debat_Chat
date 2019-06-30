package com.idealist.www.useopencvwithcmake.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.idealist.www.useopencvwithcmake.R;
import com.idealist.www.useopencvwithcmake.ethereum.EthereumHandler;
import com.idealist.www.useopencvwithcmake.ethereum.KeysStorage;
import com.idealist.www.useopencvwithcmake.utils.BarcodeCaptureActivity;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class GenerateWalletActivity extends AppCompatActivity {

    private Button storeBtn;
    private Button cancelBtn;
    private Button scanPublicBtn;
    private Button pastePublicBtn;
    private Button scanPrivateBtn;
    private Button pastePrivateBtn;
    private EditText inputPublic;
    private EditText inputPrivate;
    KeysStorage crypto;

    AlertDialog.Builder confirmationMsg;
    private static final int BARCODE_READER_REQUEST_PUBLIC = 1;
    private static final int BARCODE_READER_REQUEST_PRIVATE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_wallet);

        crypto = new KeysStorage(this);
        confirmationMsg = new AlertDialog.Builder(this);

        setupComponents();
        checkIntents();
    }

    private void setupComponents(){
        storeBtn = (Button) findViewById(R.id.store_keys);
        cancelBtn = (Button) findViewById(R.id.cancel_store);
        inputPublic = (EditText) findViewById(R.id.public_key_input);
        inputPrivate = (EditText) findViewById(R.id.private_key_input);

        storeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String publicKey = inputPublic.getText().toString();
                String privateKey = inputPrivate.getText().toString();
                Pair<String,String> pair = new Pair<>(publicKey,privateKey);
                crypto.storeKeys(pair);

                displayConfirmation("Keys stored!", "Keys are stored in your device");
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanInputs();
                finish();
            }
        });

    }

    private void checkIntents(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (getIntent().getStringExtra("Operation") != null) {
                String operation = getIntent().getStringExtra("Operation");

                if(operation.equals("generateKeys")){
                    EthereumHandler ethHandler = new EthereumHandler();
                    Pair<String,String> keys = ethHandler.generateWallet();
                    inputPublic.setText(keys.first);
                    inputPrivate.setText(keys.second);
                }
                else if (operation.equals("storeKeys")){
                    setupAuxButtons();
                }
                else if (operation.equals("seeKeys")){
                    storeBtn.setVisibility(View.GONE);
                    cancelBtn.setVisibility(View.GONE);
                    Pair<String,String> keys = crypto.retrieveKeys();
                    inputPublic.setText(keys.first);
                    inputPrivate.setText(keys.second);

                }
            }
        }

    }

    private void setupAuxButtons(){
        scanPublicBtn = (Button) findViewById(R.id.scan_qr_public);
        scanPrivateBtn = (Button) findViewById(R.id.scan_qr_private);
        pastePublicBtn = (Button) findViewById(R.id.paste_qr_public);
        pastePrivateBtn = (Button) findViewById(R.id.paste_qr_private);

        scanPublicBtn.setVisibility(View.VISIBLE);
        scanPrivateBtn.setVisibility(View.VISIBLE);
        pastePublicBtn.setVisibility(View.VISIBLE);
        pastePrivateBtn.setVisibility(View.VISIBLE);

        scanPublicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScan(1);
            }
        });
        scanPrivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScan(2);
            }
        });
        pastePublicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasteCode(1);
            }
        });
        pastePrivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasteCode(2);
            }

        });
    }

    private void pasteCode(int operation){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if (!(clipboard.hasPrimaryClip())) {
            Toast.makeText(getBaseContext(),"You don't have anything in the clipboard.", Toast.LENGTH_LONG).show();
        }
        else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
            Toast.makeText(getBaseContext(),"You don't have text in the clipboard.", Toast.LENGTH_LONG).show();

        } else {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            if(operation == 1){
                inputPublic.setText(item.getText().toString());
            }
            else {
                inputPrivate.setText(item.getText().toString());
            }

        }
    }

    private void openScan(int operation){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("The Ethereum Wallet requires permission to open the camera to scan the QR code. ")
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
                        new String[]{Manifest.permission.CAMERA},
                        123);

            }
        }
        else {
            Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);

            if(operation == 1){
                //Scan the public code
                startActivityForResult(intent, BARCODE_READER_REQUEST_PRIVATE);
            }
            else {
                //Scan the private code
                startActivityForResult(intent, BARCODE_READER_REQUEST_PRIVATE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_PUBLIC || requestCode == BARCODE_READER_REQUEST_PRIVATE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    //barcodeQR = ignorePrefixAndSuffix(barcode.displayValue, amountET);
                    if(requestCode == BARCODE_READER_REQUEST_PUBLIC) {
                        inputPublic.setText(barcode.displayValue);
                    }
                    else {
                        inputPrivate.setText(barcode.displayValue);
                    }
                }
                else {
                    Toast.makeText(this, "barcode no captured",Toast.LENGTH_SHORT).show();
                    if(requestCode == BARCODE_READER_REQUEST_PUBLIC) {
                        inputPublic.setText("");
                    }
                    else {
                        inputPrivate.setText("");
                    }
                }
            } else {
                Toast.makeText(this, "Error in barcode format",Toast.LENGTH_SHORT).show();
                if(requestCode == BARCODE_READER_REQUEST_PUBLIC) {
                    inputPublic.setText("");
                }
                else {
                    inputPrivate.setText("");
                }
            }
        }
        else {
            Toast.makeText(this, "Barcode no captured",Toast.LENGTH_SHORT).show();
            if(requestCode == BARCODE_READER_REQUEST_PUBLIC) {
                inputPublic.setText("");
            }
            else {
                inputPrivate.setText("");
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void displayConfirmation(String title, String errorMessage){
        confirmationMsg.setTitle(title);
        confirmationMsg.setMessage(errorMessage);
        confirmationMsg.setIcon(R.mipmap.ic_valid_black_24dp);
        confirmationMsg.setNeutralButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int whichButton) {
                d.dismiss();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        confirmationMsg.show();
    }

    private void cleanInputs(){
        inputPublic.setText("");
        inputPrivate.setText("");
    }
}
