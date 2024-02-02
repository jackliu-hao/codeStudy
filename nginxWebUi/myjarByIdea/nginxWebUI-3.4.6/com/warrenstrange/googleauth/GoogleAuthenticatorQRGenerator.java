package com.warrenstrange.googleauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.http.client.utils.URIBuilder;

public final class GoogleAuthenticatorQRGenerator {
   private static final String TOTP_URI_FORMAT = "https://api.qrserver.com/v1/create-qr-code/?data=%s&size=200x200&ecc=M&margin=0";

   private static String internalURLEncode(String s) {
      try {
         return URLEncoder.encode(s, "UTF-8");
      } catch (UnsupportedEncodingException var2) {
         throw new IllegalArgumentException("UTF-8 encoding is not supported by URLEncoder.", var2);
      }
   }

   private static String formatLabel(String issuer, String accountName) {
      if (accountName != null && accountName.trim().length() != 0) {
         StringBuilder sb = new StringBuilder();
         if (issuer != null) {
            if (issuer.contains(":")) {
               throw new IllegalArgumentException("Issuer cannot contain the ':' character.");
            }

            sb.append(issuer);
            sb.append(":");
         }

         sb.append(accountName);
         return sb.toString();
      } else {
         throw new IllegalArgumentException("Account name must not be empty.");
      }
   }

   public static String getOtpAuthURL(String issuer, String accountName, GoogleAuthenticatorKey credentials) {
      return String.format("https://api.qrserver.com/v1/create-qr-code/?data=%s&size=200x200&ecc=M&margin=0", internalURLEncode(getOtpAuthTotpURL(issuer, accountName, credentials)));
   }

   public static String getOtpAuthTotpURL(String issuer, String accountName, GoogleAuthenticatorKey credentials) {
      URIBuilder uri = (new URIBuilder()).setScheme("otpauth").setHost("totp").setPath("/" + formatLabel(issuer, accountName)).setParameter("secret", credentials.getKey());
      if (issuer != null) {
         if (issuer.contains(":")) {
            throw new IllegalArgumentException("Issuer cannot contain the ':' character.");
         }

         uri.setParameter("issuer", issuer);
      }

      GoogleAuthenticatorConfig config = credentials.getConfig();
      uri.setParameter("algorithm", getAlgorithmName(config.getHmacHashFunction()));
      uri.setParameter("digits", String.valueOf(config.getCodeDigits()));
      uri.setParameter("period", String.valueOf((int)(config.getTimeStepSizeInMillis() / 1000L)));
      return uri.toString();
   }

   private static String getAlgorithmName(HmacHashFunction hashFunction) {
      switch (hashFunction) {
         case HmacSHA1:
            return "SHA1";
         case HmacSHA256:
            return "SHA256";
         case HmacSHA512:
            return "SHA512";
         default:
            throw new IllegalArgumentException(String.format("Unknown algorithm %s", hashFunction));
      }
   }
}
