package com.simple.tracker.app;

import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;

@Service
public class CredentialsService {
    public Credentials getCredentialsByPrivKey(String privateKey) {
        return Credentials.create(privateKey);
    }

    public Credentials getCredentialsByKeyFile(String password, String keyPath) {
        try {
            return WalletUtils.loadCredentials(password, keyPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
        return null;
    }

}
