package vemv;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
 
public class RSA {
    
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static String[] generate() throws Exception {
        
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        BASE64Encoder b64 = new BASE64Encoder();

        generator.initialize(1024, new SecureRandom());

        KeyPair pair = generator.generateKeyPair();
        Key pubKey = pair.getPublic();
        Key privKey = pair.getPrivate();

        return new String[]{b64.encode(pubKey.getEncoded()), b64.encode(privKey.getEncoded())};
        
    }
     
    public static String encrypt (String key, String input) throws Exception {

        BASE64Decoder b64 = new BASE64Decoder();
        AsymmetricKeyParameter publicKey =
            (AsymmetricKeyParameter) PublicKeyFactory.createKey(b64.decodeBuffer(key));
        AsymmetricBlockCipher e = new RSAEngine();
        e = new org.bouncycastle.crypto.encodings.PKCS1Encoding(e);
        e.init(true, publicKey);

        byte[] messageBytes = input.getBytes();
        byte[] hexEncodedCipher = e.processBlock(messageBytes, 0, messageBytes.length);

        return getHexString(hexEncodedCipher);

    }
    
    public static String decrypt (String key, String encryptedData) throws Exception {
 
        String outputData;
    
        BASE64Decoder b64 = new BASE64Decoder();
        AsymmetricKeyParameter privateKey =
            (AsymmetricKeyParameter) PrivateKeyFactory.createKey(b64.decodeBuffer(key));
        AsymmetricBlockCipher e = new RSAEngine();
        e = new org.bouncycastle.crypto.encodings.PKCS1Encoding(e);
        e.init(false, privateKey);

        byte[] messageBytes = hexStringToByteArray(encryptedData);
        byte[] hexEncodedCipher = e.processBlock(messageBytes, 0, messageBytes.length);

        return new String(hexEncodedCipher);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
  
    public static String getHexString(byte[] b) throws Exception {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
                Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
    
}