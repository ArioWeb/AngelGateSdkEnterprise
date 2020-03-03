package com.angelsgate.sdk.SecretChat;

import android.content.Context;

import com.angelsgate.sdk.AngelsGateUtils.Base64Utils;
import com.angelsgate.sdk.AngelsGateUtils.prefs.AngelGatePreferencesHelper;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecretChat {


    public static void generateKey(Context ctx) throws NoSuchAlgorithmException, NoSuchProviderException {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        kpg.initialize(2048, random);
        KeyPair kp = kpg.generateKeyPair();
        Key pub = kp.getPublic();
        Key pvt = kp.getPrivate();
        //////////////////////////////////////SAVING KEY
        Base64Utils.Base64Encode(pvt.getEncoded());
        Base64Utils.Base64Encode(pub.getEncoded());

        AngelGatePreferencesHelper.setMyPrivateKeyGenerated(Base64Utils.Base64Encode(pvt.getEncoded()), ctx);
        AngelGatePreferencesHelper.setMyPublicKeyGenerated(Base64Utils.Base64Encode(pub.getEncoded()), ctx);
    }


    public static PrivateKey LoadPrivateKey(Context ctx) throws NoSuchAlgorithmException, InvalidKeySpecException {

        String base64Private = AngelGatePreferencesHelper.getMyPrivateKeyGenerated(ctx);

        /* Generate private key. */
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(Base64Utils.Base64DecodeToByte(base64Private));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pvt = kf.generatePrivate(ks);

        return pvt;
    }


    public static PublicKey LoadPublicKey(Context ctx) throws NoSuchAlgorithmException, InvalidKeySpecException {

        String base64Public = AngelGatePreferencesHelper.getMyPublicKeyGenerated(ctx);
        /* Generate public key. */
        X509EncodedKeySpec ks = new X509EncodedKeySpec(Base64Utils.Base64DecodeToByte(base64Public));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pub = kf.generatePublic(ks);

        return pub;
    }


}
