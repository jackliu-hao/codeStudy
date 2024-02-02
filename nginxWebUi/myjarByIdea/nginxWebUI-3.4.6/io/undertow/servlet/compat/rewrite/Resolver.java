package io.undertow.servlet.compat.rewrite;

public abstract class Resolver {
   public abstract String resolve(String var1);

   public String resolveEnv(String key) {
      return System.getProperty(key);
   }

   public abstract String resolveSsl(String var1);

   public abstract String resolveHttp(String var1);

   public abstract boolean resolveResource(int var1, String var2);
}
