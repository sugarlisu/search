package com.su.search.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * 加/解密
 * 
 * @author EX-HUANGZHENHUA001
 * 
 */
public class DESUtil {

	public static final String DEFAULT_SEED = "PA18-WCM-SEARCH-2012";

	private static final String EncodeAlgorithm = "DESede";

	private static DESUtil instance = null;

	private SecretKey key = null;

	public static DESUtil getInstance() {
		if (instance == null) {
			instance = new DESUtil();
			if (!instance.init()) {
				instance = null;
			}
		}
		return instance;
	}

	private boolean init() {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance(EncodeAlgorithm);
			String strKey = "TOA-IBP";
			SecureRandom random = new SecureRandom(strKey.getBytes());
			keygen.init(random);
			key = keygen.generateKey();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return key != null;
	}

	/**
	 * 获取Cipher对象
	 * 
	 * @param mode
	 * @return
	 */
	private Cipher getCipher(int mode) {
		try {
			Cipher cipher = Cipher.getInstance(EncodeAlgorithm);
			cipher.init(mode, key);
			return cipher;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Cipher getEncodeCipher() {
		return this.getCipher(Cipher.ENCRYPT_MODE);
	}

	public Cipher getDecodeCipher() {
		return this.getCipher(Cipher.DECRYPT_MODE);
	}

	/**
	 * 加密，若输入为null或加密过程出现异常，则输出为null
	 * 
	 * @param seed
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static String ecryptString(String seed, String source)
			throws Exception {
		SecretKey mykey = null;

		if (seed == null)
			throw new Exception("加密key,不能为空");

		if (source == null)
			return "";
		try {
			KeyGenerator keygen = KeyGenerator.getInstance(EncodeAlgorithm);
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(seed.getBytes());
			keygen.init(random);
			mykey = keygen.generateKey();
		} catch (Exception ex) {
			throw ex;
		}

		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(EncodeAlgorithm);
			cipher.init(Cipher.ENCRYPT_MODE, mykey);
		} catch (Exception e) {
			throw e;
		}
		StringBuffer sbf = new StringBuffer();

		int blockSize = cipher.getBlockSize();
		int outputSize = cipher.getOutputSize(blockSize);

		byte[] src = source.getBytes();

		byte[] outBytes = new byte[outputSize];

		int i = 0;
		try {
			for (; i < src.length - blockSize; i = i + blockSize) {
				int outLength = cipher.update(src, i, blockSize, outBytes);
				sbf.append(Bytes2HexString(outBytes, outLength));
			}

			if (i == src.length) {
				outBytes = cipher.doFinal();
			} else {
				outBytes = cipher.doFinal(src, i, src.length - i);
			}
			sbf.append(Bytes2HexString(outBytes));
			return sbf.toString();
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 解密，若输入为null或解密过程出现异常，则输出为null
	 * 
	 * @param seed
	 * @param encryptData
	 * @return
	 * @throws Exception
	 */
	public static String decryptString(String seed, String encryptData)
			throws Exception {
		SecretKey mykey = null;

		if (seed == null)
			throw new Exception("解密key不能为空");

		if (encryptData == null)
			return "";
		try {
			KeyGenerator keygen = KeyGenerator.getInstance(EncodeAlgorithm);
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(seed.getBytes());
			keygen.init(random);
			mykey = keygen.generateKey();
		} catch (Exception ex) {
			throw ex;
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(EncodeAlgorithm);
			cipher.init(Cipher.DECRYPT_MODE, mykey);
		} catch (Exception e) {
			throw e;
		}
		StringBuffer sbf = new StringBuffer();
		int blockSize = cipher.getBlockSize();
		int outputSize = cipher.getOutputSize(blockSize);
		byte[] src = stringToBytes(encryptData);
		byte[] outBytes = new byte[outputSize];
		int i = 0;
		try {
			for (; i <= src.length - blockSize; i = i + blockSize) {
				int outLength = cipher.update(src, i, blockSize, outBytes);
				sbf.append(new String(outBytes, 0, outLength));
			}
			if (i == src.length)
				outBytes = cipher.doFinal();
			else {
				outBytes = cipher.doFinal(src, i, src.length - i);
			}
			sbf.append(new String(outBytes));
			return sbf.toString();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 转换字节数组
	 * 
	 * @param str
	 * @return
	 */
	private static byte[] stringToBytes(String str) {
		if (str == null || str.length() < 2 || str.length() % 2 != 0)
			return new byte[0];
		int len = str.length();
		byte[] bs = new byte[len / 2];
		for (int i = 0; i * 2 < len; i++) {
			bs[i] = (byte) (Integer.parseInt(str.substring(i * 2, i * 2 + 2),
					16) & 0xFF);
		}
		return bs;
	}

	public static String Bytes2HexString(byte[] bs) {
		if (bs == null || bs.length == 0)
			return "";
		return Bytes2HexString(bs, bs.length);
	}

	/**
	 * 转换十六进的字符串
	 * 
	 * @param b
	 * @param length
	 * @return
	 */
	public static String Bytes2HexString(byte[] b, int length) {
		String ret = "";
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			// System.out.println(b[i]+":"+hex);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static void main(String[] args) throws Exception {
		String seed = "pa18-wcm-search";
		String source = "pa18-wcm";
		String enc = DESUtil.ecryptString(seed, source);
		System.out.println("enc:" + enc);
		System.out.println("desc:" + decryptString(seed, enc));
	}

}