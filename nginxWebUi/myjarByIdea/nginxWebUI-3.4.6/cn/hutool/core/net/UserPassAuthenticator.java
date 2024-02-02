package cn.hutool.core.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class UserPassAuthenticator extends Authenticator {
   private final String user;
   private final char[] pass;

   public UserPassAuthenticator(String user, char[] pass) {
      this.user = user;
      this.pass = pass;
   }

   protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(this.user, this.pass);
   }
}
