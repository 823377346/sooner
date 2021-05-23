package com.harry.core.util;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;

/**
 * @author Tony Luo 2019-9-23
 */
public abstract class BinaryUtil {


  /**
   * @param binaryStr
   * @return the byte array
   */
  public static byte[] binaryStrToByteArray(String binaryStr) {
    if (binaryStr == null) {
      throw new IllegalArgumentException("null");
    }
    long num = Long.parseLong(binaryStr, 2);
    int strLen = binaryStr.length();

    ByteBuffer bytes = ByteBuffer.allocate(strLen < 8 ? 8 : strLen).putLong(num);
    return bytes.array();
  }

  /**
   * @param binaryStr
   * @return
   */
  public static long binaryStrToLong(String binaryStr) {

    return Long.parseLong(binaryStr, 2);
  }


  /**
   * @param array
   * @return the {@code long} represented by the byte argument
   * @throws IllegalArgumentException if the byte does not contain a parsable {@code long}.
   */

  public static long byteArrayToLong(byte[] array) {
    if (array == null) {
      throw new IllegalArgumentException("null");
    }
    //byte array to int and binaryString
    // big-endian by default
    ByteBuffer wrapped = ByteBuffer.wrap(array);
    return wrapped.getLong();

  }


  /**
   * @param array
   * @return
   */
  public static String numberByteArrayToString(byte[] array) {
    if (array == null) {
      throw new IllegalArgumentException("null");
    }
    return Long.toBinaryString(byteArrayToLong(array));

  }

  public static String getReadableSize(byte[] array) {

    if (null == array) {
      return "0";
    }
    return getReadableSize(array.length);

  }

  public static String getReadableSize(long size) {
    if (size <= 0) {
      return "0";
    }
    final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
  }
}
