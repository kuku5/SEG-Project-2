package uk.ac.kcl.SEG_Project_2.constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ravi on 24/11/14.
 */
public class Utils {

    public static String createSHA256(String input){


        StringBuilder output = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(input.getBytes());
            byte byteData[] = messageDigest.digest();
            for (byte aByteData : byteData) {
                output.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            return output.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }


    }

}
