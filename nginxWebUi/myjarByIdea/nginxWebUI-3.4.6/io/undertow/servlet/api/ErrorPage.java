package io.undertow.servlet.api;

public class ErrorPage {
   private final String location;
   private final Integer errorCode;
   private final Class<? extends Throwable> exceptionType;

   public ErrorPage(String location, Class<? extends Throwable> exceptionType) {
      this.location = location;
      this.errorCode = null;
      this.exceptionType = exceptionType;
   }

   public ErrorPage(String location, int errorCode) {
      this.location = location;
      this.errorCode = errorCode;
      this.exceptionType = null;
   }

   public ErrorPage(String location) {
      this.location = location;
      this.errorCode = null;
      this.exceptionType = null;
   }

   public String getLocation() {
      return this.location;
   }

   public Integer getErrorCode() {
      return this.errorCode;
   }

   public Class<? extends Throwable> getExceptionType() {
      return this.exceptionType;
   }
}
