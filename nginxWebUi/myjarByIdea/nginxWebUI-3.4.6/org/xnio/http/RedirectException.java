package org.xnio.http;

import java.io.IOException;

public class RedirectException extends IOException {
   private final int statusCode;
   private final String location;

   public RedirectException(int statusCode, String location) {
      this.statusCode = statusCode;
      this.location = location;
   }

   public RedirectException(String msg, int statusCode, String location) {
      super(msg);
      this.statusCode = statusCode;
      this.location = location;
   }

   public RedirectException(Throwable cause, int statusCode, String location) {
      super(cause);
      this.statusCode = statusCode;
      this.location = location;
   }

   public RedirectException(String msg, Throwable cause, int statusCode, String location) {
      super(msg, cause);
      this.statusCode = statusCode;
      this.location = location;
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public String getLocation() {
      return this.location;
   }
}
