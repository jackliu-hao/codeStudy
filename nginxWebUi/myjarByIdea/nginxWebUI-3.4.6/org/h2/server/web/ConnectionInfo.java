package org.h2.server.web;

import org.h2.util.StringUtils;

public class ConnectionInfo implements Comparable<ConnectionInfo> {
   public String driver;
   public String url;
   public String user;
   String name;
   int lastAccess;

   ConnectionInfo() {
   }

   public ConnectionInfo(String var1) {
      String[] var2 = StringUtils.arraySplit(var1, '|', false);
      this.name = get(var2, 0);
      this.driver = get(var2, 1);
      this.url = get(var2, 2);
      this.user = get(var2, 3);
   }

   private static String get(String[] var0, int var1) {
      return var0 != null && var0.length > var1 ? var0[var1] : "";
   }

   String getString() {
      return StringUtils.arrayCombine(new String[]{this.name, this.driver, this.url, this.user}, '|');
   }

   public int compareTo(ConnectionInfo var1) {
      return Integer.compare(var1.lastAccess, this.lastAccess);
   }
}
