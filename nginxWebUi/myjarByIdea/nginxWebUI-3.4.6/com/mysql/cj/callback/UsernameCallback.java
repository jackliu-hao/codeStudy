package com.mysql.cj.callback;

public class UsernameCallback implements MysqlCallback {
   private String username;

   public UsernameCallback(String username) {
      this.username = username;
   }

   public String getUsername() {
      return this.username;
   }
}
