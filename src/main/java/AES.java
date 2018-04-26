import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.prefs.Preferences;


public class AES {

    private static final String AES_NODE = "AES";
    private static final String AES_KEY = "key";
    private static final String AES_VECTOR = "vector";
    private static Preferences programData = Preferences.userRoot().node(AES_NODE);

    private static String key;
    private static String initVector;


    public static void initKey() {

        String s1 = programData.get(AES_KEY, null);
        if(s1 == null) {
            s1 = new Date().toString();
            programData.put(AES_KEY, s1);
        }
        key = (s1 + "zox the smart fox").substring(0, 32);

        String s2 = programData.get(AES_VECTOR, null);
        if(s2 == null) {
            s2 = String.valueOf(System.nanoTime());
            programData.put(AES_VECTOR, s2);
        }
        initVector = (s2 + "zeek the foxy geek").substring(0, 16);

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
