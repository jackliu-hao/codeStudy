package org.codehaus.plexus.util.reflection;

public class ReflectorException extends Exception {
   public ReflectorException() {
   }

   public ReflectorException(String msg) {
      super(msg);
   }

   public ReflectorException(Throwable root) {
      super(root);
   }

   public ReflectorException(String msg, Throwable root) {
      super(msg, root);
   }
}
