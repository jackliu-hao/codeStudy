package org.h2.security;

public interface BlockCipher {
  public static final int ALIGN = 16;
  
  void setKey(byte[] paramArrayOfbyte);
  
  void encrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  void decrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  int getKeyLength();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\BlockCipher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */