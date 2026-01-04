// IN2011 Computer Networks
// Coursework 2023/2024
//
// Construct the hashID for a string

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashID {

    public static byte[] computeHashID(String line) throws Exception {
        if (line.endsWith("\n")) {
            // What this does and how it works is covered in a later lecture
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(line.getBytes(StandardCharsets.UTF_8));
            return md.digest();

        } else {
            // 2D#4 computes hashIDs of lines, i.e. strings ending with '\n'
            throw new Exception("No new line at the end of input to HashID");
        }
    }
//    public static String computeHashID(String line) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            md.update(line.getBytes(StandardCharsets.UTF_8));
//            byte[] hashBytes = md.digest();
//            StringBuilder hashBuilder = new StringBuilder();
//            for (byte b : hashBytes) {
//                hashBuilder.append(String.format("%02x", b));
//            }
//            return hashBuilder.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null; // Return null in case of an error
//        }
//    }
}
