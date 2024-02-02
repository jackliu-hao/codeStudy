/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QueryStatisticsData
/*     */ {
/*     */   private static final Comparator<QueryEntry> QUERY_ENTRY_COMPARATOR;
/*     */   
/*     */   static {
/*  22 */     QUERY_ENTRY_COMPARATOR = Comparator.comparingLong(paramQueryEntry -> paramQueryEntry.lastUpdateTime);
/*     */   }
/*  24 */   private final HashMap<String, QueryEntry> map = new HashMap<>();
/*     */   
/*     */   private int maxQueryEntries;
/*     */   
/*     */   public QueryStatisticsData(int paramInt) {
/*  29 */     this.maxQueryEntries = paramInt;
/*     */   }
/*     */   
/*     */   public synchronized void setMaxQueryEntries(int paramInt) {
/*  33 */     this.maxQueryEntries = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized List<QueryEntry> getQueries() {
/*  39 */     ArrayList<QueryEntry> arrayList = new ArrayList(this.map.values());
/*     */     
/*  41 */     arrayList.sort(QUERY_ENTRY_COMPARATOR);
/*  42 */     return arrayList.subList(0, Math.min(arrayList.size(), this.maxQueryEntries));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void update(String paramString, long paramLong1, long paramLong2) {
/*  54 */     QueryEntry queryEntry = this.map.get(paramString);
/*  55 */     if (queryEntry == null) {
/*  56 */       queryEntry = new QueryEntry(paramString);
/*  57 */       this.map.put(paramString, queryEntry);
/*     */     } 
/*  59 */     queryEntry.update(paramLong1, paramLong2);
/*     */ 
/*     */ 
/*     */     
/*  63 */     if (this.map.size() > this.maxQueryEntries * 1.5F) {
/*     */       
/*  65 */       ArrayList<QueryEntry> arrayList = new ArrayList(this.map.values());
/*  66 */       arrayList.sort(QUERY_ENTRY_COMPARATOR);
/*     */ 
/*     */       
/*  69 */       HashSet hashSet = new HashSet(arrayList.subList(0, arrayList.size() / 3));
/*     */ 
/*     */ 
/*     */       
/*  73 */       for (Iterator<Map.Entry> iterator = this.map.entrySet().iterator(); iterator.hasNext(); ) {
/*  74 */         Map.Entry entry = iterator.next();
/*  75 */         if (hashSet.contains(entry.getValue())) {
/*  76 */           iterator.remove();
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class QueryEntry
/*     */   {
/*     */     public final String sqlStatement;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int count;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long lastUpdateTime;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long executionTimeMinNanos;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long executionTimeMaxNanos;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long executionTimeCumulativeNanos;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long rowCountMin;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long rowCountMax;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long rowCountCumulative;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public double executionTimeMeanNanos;
/*     */ 
/*     */ 
/*     */     
/*     */     public double rowCountMean;
/*     */ 
/*     */ 
/*     */     
/*     */     private double executionTimeM2Nanos;
/*     */ 
/*     */ 
/*     */     
/*     */     private double rowCountM2;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public QueryEntry(String param1String) {
/* 151 */       this.sqlStatement = param1String;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void update(long param1Long1, long param1Long2) {
/* 161 */       this.count++;
/* 162 */       this.executionTimeMinNanos = Math.min(param1Long1, this.executionTimeMinNanos);
/* 163 */       this.executionTimeMaxNanos = Math.max(param1Long1, this.executionTimeMaxNanos);
/* 164 */       this.rowCountMin = Math.min(param1Long2, this.rowCountMin);
/* 165 */       this.rowCountMax = Math.max(param1Long2, this.rowCountMax);
/*     */       
/* 167 */       double d1 = param1Long2 - this.rowCountMean;
/* 168 */       this.rowCountMean += d1 / this.count;
/* 169 */       this.rowCountM2 += d1 * (param1Long2 - this.rowCountMean);
/*     */       
/* 171 */       double d2 = param1Long1 - this.executionTimeMeanNanos;
/* 172 */       this.executionTimeMeanNanos += d2 / this.count;
/* 173 */       this.executionTimeM2Nanos += d2 * (param1Long1 - this.executionTimeMeanNanos);
/*     */       
/* 175 */       this.executionTimeCumulativeNanos += param1Long1;
/* 176 */       this.rowCountCumulative += param1Long2;
/* 177 */       this.lastUpdateTime = System.currentTimeMillis();
/*     */     }
/*     */ 
/*     */     
/*     */     public double getExecutionTimeStandardDeviation() {
/* 182 */       return Math.sqrt(this.executionTimeM2Nanos / this.count);
/*     */     }
/*     */ 
/*     */     
/*     */     public double getRowCountStandardDeviation() {
/* 187 */       return Math.sqrt(this.rowCountM2 / this.count);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\QueryStatisticsData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */