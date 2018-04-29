import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


public class AES {

    private static String key;
    private static String initVector;


    public static void initKey() {

        String date = (String) DataHandler.getAppPreferences().get("AES.date");
        key = (date + "zox the smart fox").substring(0, 32);

        String nanoTime = String.valueOf((long) DataHandler.getAppPreferences().get("AES.nanoTime"));
        initVector = (nanoTime + "zeek the foxy geek").substring(0, 16);

    }

    public static String encrypt(String value) {

        try {

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception ex) {
            ex.printStackTrace();
            new MessageDialog("Error encrypting the entered password \n" +
                    "Try again later or report this issue", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(ex.getStackTrace()).showAndWait();
        }

        return null;
    }

    public static String decrypt(String encrypted) {

        try {

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

            return new String(original);

        } catch (Exception ex) {
            new MessageDialog("Error decrypting the password \n" +
                    "Try again later or report this issue", MessageDialog.Type.ERROR,
                    MessageDialog.Buttons.CLOSE).createErrorDialog(ex.getStackTrace()).showAndWait();
        }

        return null;
    }

}
