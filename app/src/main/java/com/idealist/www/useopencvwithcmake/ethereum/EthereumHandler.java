package com.idealist.www.useopencvwithcmake.ethereum;

import android.app.ProgressDialog;
import android.util.Pair;

import com.idealist.www.useopencvwithcmake.interfaces.ApiCallback;
import com.idealist.www.useopencvwithcmake.utils.Constants;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.KeyStore;
import java.util.UUID;

/**
 * Created by alex on 24/08/2018.
 */

public class EthereumHandler {

    public Pair<String, String> generateWallet(){
        Pair<String,String> key = null;
        try {
            Web3j web3j = Web3jFactory.build(new HttpService(Constants.urlEth));

            String seed = UUID.randomUUID().toString();
            ECKeyPair exKey = Keys.createEcKeyPair();

            WalletFile wallet = Wallet.create(seed,exKey,2,2);


            Credentials c = Credentials.create(Wallet.decrypt(seed, wallet));
            String publicKeyGenerated = c.getAddress();
            String privateKeyGenerated = c.getEcKeyPair().getPrivateKey().toString(16);
            privateKeyGenerated = "0x"+privateKeyGenerated;

            key = new Pair<> (publicKeyGenerated, privateKeyGenerated);

            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                    .getEntry(key.first, null);

        }
        catch (Exception e){
            String ex = e.getMessage();
        }
        return key;
    }

    public void getBalance (String addressFrom, ApiCallback callback){

        Web3j web3j = Web3jFactory.build(new HttpService(Constants.urlEth));
        try {
            EthGetBalance ethGetBalance1 = web3j
                    .ethGetBalance(addressFrom, DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();

            BigInteger wei = ethGetBalance1.getBalance();
            double ethInDouble = wei.doubleValue() / Constants.weiToEth.doubleValue();

            callback.OnSuccess(String.valueOf(ethInDouble));

        }
        catch (Exception e){
            String ex = e.getMessage();
            callback.OnFailure(ex);
        }
    }

    public void sendTransaction(String addressTo, String addressFrom, String value, ApiCallback callback){

        String etherReceipt = "";
        try{

            Web3j web3j = Web3jFactory.build(new HttpService(Constants.urlEth));

            Credentials credentials = Credentials.create(addressFrom);

            TransactionReceipt transactionReceipt = Transfer.sendFunds(
                    web3j, credentials, addressTo,
                    new BigDecimal(value), Convert.Unit.ETHER).sendAsync().get();
            etherReceipt = transactionReceipt.getTransactionHash();

            callback.OnSuccess(etherReceipt);

        } catch (Exception e) {
            String a = e.getMessage();
            callback.OnFailure("Exception:"+ a);
        }

    }

    public void buyEthers(String myAddress, ApiCallback callback){
        String etherReceipt = "";
        try{

            Web3j web3j = Web3jFactory.build(new HttpService(Constants.urlEth));


            Credentials credentials = Credentials.create(Constants.bankPrivateKey);


            TransactionReceipt transactionReceipt = Transfer.sendFunds(
                    web3j, credentials, myAddress,
                    new BigDecimal(3.0), Convert.Unit.ETHER).sendAsync().get();
            etherReceipt = transactionReceipt.getTransactionHash();

            callback.OnSuccess("Ok");

        } catch (Exception e) {
            String a = e.getMessage();
            callback.OnFailure("Exception:"+ a);
        }
    }
}
