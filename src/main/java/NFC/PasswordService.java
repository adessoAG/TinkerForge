package NFC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Password class to implement a strategy for resolving the correct password.
 * You can use the @enumeratePassWord function to make your own.
 * The passwordExplorer will ask each turn for a new password to try with that function call.
 * For each unique Tag one PasswordService get called. So you can store your progress and/or the resolved password when
 * successful.
 */
public class PasswordService {
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private int[] password;
  private int enumeration;
  private final int[] tagID;
  static final int[][] commonPasswords = {
      {0XFF, 0XFF, 0XFF, 0XFF, 0XFF, 0XFF},
      {0XD3, 0XF7, 0XD3, 0XF7, 0XD3, 0XF7},
      {0XA0, 0XA1, 0XA2, 0XA3, 0XA4, 0XA5},
      {0XB0, 0XB1, 0XB2, 0XB3, 0XB4, 0XB5},
      {0X4D, 0X3A, 0X99, 0XC3, 0X51, 0XDD},
      {0X1A, 0X98, 0X2C, 0X7E, 0X45, 0X9A},
      {0XAA, 0XBB, 0XCC, 0XDD, 0XEE, 0XFF},
      {0X00, 0X00, 0X00, 0X00, 0X00, 0X00},
      {0XAB, 0XCD, 0XEF, 0X12, 0X34, 0X56},
  };

  public PasswordService(int[] tagID) {
    this.tagID = tagID;
    enumeration = 0;
  }

  public int[] getPassword() {
    if (password == null) {
      return new int[6];
    }
    return password;
  }

  public int[] getTagID() {
    return tagID;
  }

  public int getEnumeration() {
    return enumeration;
  }

  public void setEnumeration(int enumeration) {
    this.enumeration = enumeration;
  }

  /**
   * After checking all common passwords try to bruteforce the password.
   */
  public int[] enumeratePassWord() {
    if (enumeration < commonPasswords.length) {
      this.password = commonPasswords[enumeration];
      return commonPasswords[enumeration++];
    } else if (password == null) {
      int[] password = {0x00, 0x00, 0x00, 0x00, 79, 223};
      this.password = password;
      return password;
    } else {
      if (password[5] < 0xFF) {
        password[5] += 1;
      } else if (password[4] < 0xFF) {
        password[4] += 1;
        password[5] = 0x00;
      } else if (password[3] < 0xFF) {
        password[3] += 1;
        password[5] = 0x00;
        password[4] = 0x00;
      } else if (password[2] < 0xFF) {
        password[2] += 1;
        password[5] = 0x00;
        password[4] = 0x00;
        password[3] = 0x00;
      } else if (password[1] < 0xFF) {
        password[1] += 1;
        password[5] = 0x00;
        password[4] = 0x00;
        password[3] = 0x00;
        password[2] = 0x00;
      } else if (password[0] < 0xFF) {
        password[0] += 1;
        password[5] = 0x00;
        password[4] = 0x00;
        password[3] = 0x00;
        password[2] = 0x00;
        password[1] = 0x00;
      } else {
        logger.info("Password not found!");
      }
    }
    return password;
  }
}
