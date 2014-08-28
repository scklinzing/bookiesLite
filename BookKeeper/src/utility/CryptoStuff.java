package utility;
import android.annotation.SuppressLint;
import java.nio.charset.Charset;
import java.security.MessageDigest;
public class CryptoStuff {
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	@SuppressLint("NewApi")
	public static String hashPWSha256(String plainPW) {
		//calc and return sha 256 hash of plainPW
		if(plainPW == null || plainPW.length() < 1)
			return null;
		String h = null;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(plainPW.getBytes(Charset.forName("UTF-8")));
			byte[] hash = md.digest();

			h = bytesToHex(hash);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return h;
	}

}
