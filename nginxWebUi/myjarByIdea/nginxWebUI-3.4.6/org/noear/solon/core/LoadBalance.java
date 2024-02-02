package org.noear.solon.core;

@FunctionalInterface
public interface LoadBalance {
   static LoadBalance get(String service) {
      return get("", service);
   }

   static LoadBalance get(String group, String service) {
      return Bridge.upstreamFactory().create(group, service);
   }

   String getServer();

   public interface Factory {
      LoadBalance create(String group, String service);
   }
}
