package com.idealist.www.useopencvwithcmake.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.idealist.www.useopencvwithcmake.BroadCast_RoomList;
import com.idealist.www.useopencvwithcmake.R;
import com.idealist.www.useopencvwithcmake.ethereum.EthereumHandler;
import com.idealist.www.useopencvwithcmake.ethereum.KeysStorage;
import com.idealist.www.useopencvwithcmake.interfaces.ApiCallback;
import com.idealist.www.useopencvwithcmake.utils.BarcodeCaptureActivity;

public class SendActivity extends AppCompatActivity {
    private EditText addressET;
    private EditText amountET;
    private TextView balanceTV;
    private ImageView scanQR;
    private Button sendBtn;

    String barcodeQR = "";
    String balance = "";
    Pair<String, String> keys;
    ProgressDialog progressDialog;
    AlertDialog dialog;
    AlertDialog.Builder errorMsg;
    AlertDialog.Builder confirmationMsg;

    private static final int BARCODE_READER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        errorMsg = new AlertDialog.Builder(this);
        confirmationMsg = new AlertDialog.Builder(this);

        setupBalance();
        setupComponents();
    }

    private void setupComponents() {
        addressET = (EditText) findViewById(R.id.account_input_send);
        amountET = (EditText) findViewById(R.id.amount_input_send);
        scanQR = (ImageView) findViewById(R.id.imageView_qr_btc);
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScan();
            }
        });
        sendBtn = (Button) findViewById(R.id.send_ethers);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startThread();
            }

        });
    }

    private void setupBalance() {
        balanceTV = (TextView) findViewById(R.id.text_balance_send);

        KeysStorage crypto = new KeysStorage(this);
        keys = crypto.retrieveKeys();
        if (keys.first.equals("") | keys.second.equals("")) {
            balanceTV.setText("Balance: Create a Wallet first");
        } else {
            EthereumHandler ethereum = new EthereumHandler();
            ethereum.getBalance(keys.first, new ApiCallback() {
                @Override
                public void OnSuccess(String result) {
                    balance = result;
                    balanceTV.setText("Balance: " + result);
                }

                @Override
                public void OnFailure(String message) {
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                    balanceTV.setText("Error getting the balance");
                }
            });

        }
    }

    private void openScan() {
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
        } else {
            Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    barcodeQR = ignorePrefixAndSuffix(barcode.displayValue, amountET);
                    addressET.setText(barcodeQR);
                } else {
                    Toast.makeText(this, "barcode no captured", Toast.LENGTH_SHORT).show();
                    addressET.setText("");
                }
            } else {
                Toast.makeText(this, "Error in barcode format", Toast.LENGTH_SHORT).show();
                addressET.setText("");
            }
        } else {
            Toast.makeText(this, "Barcode no captured", Toast.LENGTH_SHORT).show();
            addressET.setText("");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String ignorePrefixAndSuffix(String barcode, EditText editTextAmount) {
        String address = barcode;
        if (address.contains(":")) {
            String[] parts = address.split(":");
            address = parts[1];
        }
        if (address.contains("?")) {
            String[] parts = address.split("\\?");
            address = parts[0];
            String amount = parts[1].split("=")[1];
            editTextAmount.setText(amount);
        }
        return address;
    }

    private void sendEther() {

        final String publicCode = addressET.getText().toString();
        final String amountSend = amountET.getText().toString();

        if (amountSend.equals("0") || amountSend.equals("0.00") || amountSend.equals("")) {
            displayError("Operation failed!", "Please insert a positive amount.");
        } else if (publicCode == "" || publicCode == null) {
            displayError("Operation failed!", "Receiver incorrect.");
        } else if (amountSend == null || balance == null || balance == "") {
            displayError("Operation failed!", "Impossible to access to your account.");
        } else if (Double.parseDouble(amountSend) > Double.parseDouble(balance)) {
            displayError("Operation failed!", "Insufficient funds.");
        } else {
            sendTransfer(publicCode, amountSend, keys.second);
        }

    }

    public void sendTransfer(String publicCode, String amount, String user) {
        EthereumHandler ethHandler = new EthereumHandler();
        ethHandler.sendTransaction(publicCode, user, amount, new ApiCallback() {
            @Override
            public void OnSuccess(String result) {
                displayConfirmation("Transaction Completed succesfully",
                        "You can check your transaction, with this hash: " + result);
            }

            @Override
            public void OnFailure(String message) {
                displayError("Error",
                        "Something happens with your transaction: " + message);
            }
        });
    }

    private void displayError(String title, String errorMessage) {
        errorMsg.setTitle(title);
        errorMsg.setMessage(errorMessage);
        errorMsg.setIcon(R.mipmap.ic_error_black_24dp);
        errorMsg.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int whichButton) {
                d.dismiss();
            }
        });
        errorMsg.show();
    }

    private void displayConfirmation(String title, String errorMessage) {
        confirmationMsg.setTitle(title);
        confirmationMsg.setMessage(errorMessage);
        confirmationMsg.setIcon(R.mipmap.ic_valid_black_24dp);
        confirmationMsg.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int whichButton) {
                d.dismiss();
//                Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });
        confirmationMsg.show();
    }

    private void startThread() {
//        progressDialog = ProgressDialog.show(SendActivity.this,
//                "","이더를 전송중입니다. 잠시만 기다려주세요. 시간이 최대 30초까지 소요됩니다.",true);

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(SendActivity.this);
        LayoutInflater inflater = LayoutInflater.from(SendActivity.this);
        final View mView = inflater.inflate(R.layout.waiting_for_ether_transfer, null);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();

        new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    handler.sendEmptyMessage(0);

                } catch (Exception e) {
                    Log.e("threadmessage", e.getMessage());
                }
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sendEther();
//            progressDialog.dismiss();
            dialog.dismiss();
        }
    };

}
