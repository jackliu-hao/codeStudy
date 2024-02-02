package org.apache.http.pool;

public interface ConnPoolControl<T> {
   void setMaxTotal(int var1);

   int getMaxTotal();

   void setDefaultMaxPerRoute(int var1);

   int getDefaultMaxPerRoute();

   void setMaxPerRoute(T var1, int var2);

   int getMaxPerRoute(T var1);

   PoolStats getTotalStats();

   PoolStats getStats(T var1);
}
