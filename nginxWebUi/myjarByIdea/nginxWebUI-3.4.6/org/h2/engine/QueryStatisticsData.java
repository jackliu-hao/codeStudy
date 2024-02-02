package org.h2.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class QueryStatisticsData {
   private static final Comparator<QueryEntry> QUERY_ENTRY_COMPARATOR = Comparator.comparingLong((var0) -> {
      return var0.lastUpdateTime;
   });
   private final HashMap<String, QueryEntry> map = new HashMap();
   private int maxQueryEntries;

   public QueryStatisticsData(int var1) {
      this.maxQueryEntries = var1;
   }

   public synchronized void setMaxQueryEntries(int var1) {
      this.maxQueryEntries = var1;
   }

   public synchronized List<QueryEntry> getQueries() {
      ArrayList var1 = new ArrayList(this.map.values());
      var1.sort(QUERY_ENTRY_COMPARATOR);
      return var1.subList(0, Math.min(var1.size(), this.maxQueryEntries));
   }

   public synchronized void update(String var1, long var2, long var4) {
      QueryEntry var6 = (QueryEntry)this.map.get(var1);
      if (var6 == null) {
         var6 = new QueryEntry(var1);
         this.map.put(var1, var6);
      }

      var6.update(var2, var4);
      if ((float)this.map.size() > (float)this.maxQueryEntries * 1.5F) {
         ArrayList var7 = new ArrayList(this.map.values());
         var7.sort(QUERY_ENTRY_COMPARATOR);
         HashSet var8 = new HashSet(var7.subList(0, var7.size() / 3));
         Iterator var9 = this.map.entrySet().iterator();

         while(var9.hasNext()) {
            Map.Entry var10 = (Map.Entry)var9.next();
            if (var8.contains(var10.getValue())) {
               var9.remove();
            }
         }
      }

   }

   public static final class QueryEntry {
      public final String sqlStatement;
      public int count;
      public long lastUpdateTime;
      public long executionTimeMinNanos;
      public long executionTimeMaxNanos;
      public long executionTimeCumulativeNanos;
      public long rowCountMin;
      public long rowCountMax;
      public long rowCountCumulative;
      public double executionTimeMeanNanos;
      public double rowCountMean;
      private double executionTimeM2Nanos;
      private double rowCountM2;

      public QueryEntry(String var1) {
         this.sqlStatement = var1;
      }

      void update(long var1, long var3) {
         ++this.count;
         this.executionTimeMinNanos = Math.min(var1, this.executionTimeMinNanos);
         this.executionTimeMaxNanos = Math.max(var1, this.executionTimeMaxNanos);
         this.rowCountMin = Math.min(var3, this.rowCountMin);
         this.rowCountMax = Math.max(var3, this.rowCountMax);
         double var5 = (double)var3 - this.rowCountMean;
         this.rowCountMean += var5 / (double)this.count;
         this.rowCountM2 += var5 * ((double)var3 - this.rowCountMean);
         double var7 = (double)var1 - this.executionTimeMeanNanos;
         this.executionTimeMeanNanos += var7 / (double)this.count;
         this.executionTimeM2Nanos += var7 * ((double)var1 - this.executionTimeMeanNanos);
         this.executionTimeCumulativeNanos += var1;
         this.rowCountCumulative += var3;
         this.lastUpdateTime = System.currentTimeMillis();
      }

      public double getExecutionTimeStandardDeviation() {
         return Math.sqrt(this.executionTimeM2Nanos / (double)this.count);
      }

      public double getRowCountStandardDeviation() {
         return Math.sqrt(this.rowCountM2 / (double)this.count);
      }
   }
}
