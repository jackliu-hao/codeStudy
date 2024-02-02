package com.cym.utils;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuthUtils {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   GoogleAuthenticator gAuth;

   @Init
   public void init() {
      this.gAuth = new GoogleAuthenticator();
   }

   public Boolean testKey(String key, String code) {
      try {
         Integer value = Integer.parseInt(code);
         GoogleAuthenticator gAuth = new GoogleAuthenticator();
         return gAuth.authorize(key, value);
      } catch (Exception var5) {
         this.logger.error((String)var5.getMessage(), (Throwable)var5);
         return false;
      }
   }

   public String makeKey() {
      GoogleAuthenticatorKey key = this.gAuth.createCredentials();
      String key1 = key.getKey();
      System.out.println(key1);
      return key1;
   }

   public int getCode(String key) {
      GoogleAuthenticator gAuth = new GoogleAuthenticator();
      int code = gAuth.getTotpPassword(key);
      System.out.println(code);
      return code;
   }
}
