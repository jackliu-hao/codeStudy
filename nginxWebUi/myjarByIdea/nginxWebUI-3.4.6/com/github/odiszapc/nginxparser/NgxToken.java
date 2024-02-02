package com.github.odiszapc.nginxparser;

public class NgxToken {
   private String token;

   public NgxToken(String token) {
      this.token = token;
   }

   public String getToken() {
      return this.token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public String toString() {
      return this.token;
   }
}
