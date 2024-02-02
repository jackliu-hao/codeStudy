package cn.hutool.crypto;

public enum Padding {
   NoPadding,
   ZeroPadding,
   ISO10126Padding,
   OAEPPadding,
   PKCS1Padding,
   PKCS5Padding,
   SSL3Padding;
}
