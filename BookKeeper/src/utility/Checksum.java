package utility;

public class Checksum {
	public static String calcChecksum(String base, String salt) {
		return CryptoStuff.hashPWSha256(base + salt);
	}
}
