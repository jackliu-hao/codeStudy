package io.undertow.servlet.core;

import java.util.Map;
import javax.servlet.ServletException;

public class ErrorPages {
   private final Map<Integer, String> errorCodeLocations;
   private final Map<Class<? extends Throwable>, String> exceptionMappings;
   private final String defaultErrorPage;

   public ErrorPages(Map<Integer, String> errorCodeLocations, Map<Class<? extends Throwable>, String> exceptionMappings, String defaultErrorPage) {
      this.errorCodeLocations = errorCodeLocations;
      this.exceptionMappings = exceptionMappings;
      this.defaultErrorPage = defaultErrorPage;
   }

   public String getErrorLocation(int code) {
      String location = (String)this.errorCodeLocations.get(code);
      return location == null ? this.defaultErrorPage : location;
   }

   public String getErrorLocation(Throwable exception) {
      if (exception == null) {
         return null;
      } else {
         String location = null;

         for(Class c = exception.getClass(); c != null && location == null; c = c.getSuperclass()) {
            location = (String)this.exceptionMappings.get(c);
         }

         if (location == null && exception instanceof ServletException) {
            Class c;
            Throwable rootCause;
            for(rootCause = ((ServletException)exception).getRootCause(); rootCause != null && rootCause instanceof ServletException && location == null; rootCause = ((ServletException)rootCause).getRootCause()) {
               for(c = rootCause.getClass(); c != null && location == null; c = c.getSuperclass()) {
                  location = (String)this.exceptionMappings.get(c);
               }
            }

            if (rootCause != null && location == null) {
               for(c = rootCause.getClass(); c != null && location == null; c = c.getSuperclass()) {
                  location = (String)this.exceptionMappings.get(c);
               }
            }
         }

         if (location == null) {
            location = this.getErrorLocation(500);
         }

         return location;
      }
   }
}
