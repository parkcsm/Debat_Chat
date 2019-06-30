package com.idealist.www.useopencvwithcmake.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.idealist.www.useopencvwithcmake.R;
import com.idealist.www.useopencvwithcmake.ethereum.EthereumHandler;
import com.idealist.www.useopencvwithcmake.ethereum.KeysStorage;
import com.idealist.www.useopencvwithcmake.interfaces.ApiCallback;

public class MainActivity extends AppCompatActivity {


    private Button walletBtn;
    private Button sendBtn;
    private Button receiveBtn;
    private Button buyBtn;
    private Button seeBtn;
    private TextView balance;
    Pair<String,String> keys;

    AlertDialog.Builder errorMsg;
    AlertDialog.Builder confirmationMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        balance = (TextView) findViewById(R.id.text_balance);

        errorMsg = new AlertDialog.Builder(this);
        confirmationMsg = new AlertDialog.Builder(this);

    }

    private void setupButtons(){
        walletBtn = (Button) findViewById(R.id.create_wallet);
        walletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWallet();
            }
        });

        sendBtn = (Button) findViewById(R.id.send_ether);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEther();
            }
        });

        receiveBtn = (Button) findViewById(R.id.receive_ether);
        receiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveEther();
            }
        });

//        buyBtn = (Button) findViewById(R.id.buy_ether);
//        buyBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                buyEther();
//            }
//        });

        seeBtn = (Button) findViewById(R.id.keys_ether);
        seeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seeKeys();
            }
        });

    }

    private void setupBalance(){
        KeysStorage crypto = new KeysStorage(this);
        keys = crypto.retrieveKeys();
        if(keys.first.equals("") | keys.second.equals("")){
            balance.setText("No Balance. Create a Wallet first");
        }
        else{
            EthereumHandler ethereum = new EthereumHandler();
            ethereum.getBalance(keys.first, new ApiCallback() {
                @Override
                public void OnSuccess(String result) {
                    balance.setText(result);
                }

                @Override
                public void OnFailure(String message) {
                    Toast.makeText(getBaseContext(),message,Toast.LENGTH_LONG).show();
                    balance.setText("Error getting the balance");
                }
            });
        }
    }


    private void createWallet(){
        Intent intent = new Intent(getBaseContext(), WalletActivity.class);
        startActivity(intent);
    }

    private void sendEther(){
        Intent intent = new Intent(getBaseContext(), SendActivity.class);
        startActivity(intent);
    }
    private void receiveEther(){
        Intent intent = new Intent(getBaseContext(), ReceiveActivity.class);
        startActivity(intent);
    }
    private void buyEther(){
        EthereumHandler ethHand = new EthereumHandler();
        ethHand.buyEthers(keys.first, new ApiCallback() {
            @Override
            public void OnSuccess(String result) {
                displayConfirmation("Purchase Completed", "You have 3 ethers more");
            }

            @Override
            public void OnFailure(String message) {
                displayError("Error",message);
            }
        });

    }

    private void seeKeys(){
        Intent intent = new Intent(getBaseContext(), GenerateWalletActivity.class);
        intent.putExtra("Operation", "seeKeys");
        startActivity(intent);
    }

    private void displayError(String title, String errorMessage){
        errorMsg.setTitle(title);
        errorMsg.setMessage(errorMessage);
        errorMsg.setIcon(R.mipmap.ic_error_black_24dp);
        errorMsg.setNeutralButton("Refresh", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int whichButton) {
                d.dismiss();
            }
        });
        errorMsg.show();
    }

    private void displayConfirmation(String title, String errorMessage){
        confirmationMsg.setTitle(title);
        confirmationMsg.setMessage(errorMessage);
        confirmationMsg.setIcon(R.mipmap.ic_valid_black_24dp);
        confirmationMsg.setNeutralButton("Refresh", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int whichButton) {
                d.dismiss();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        confirmationMsg.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupButtons();
        setupBalance();
    }
}
