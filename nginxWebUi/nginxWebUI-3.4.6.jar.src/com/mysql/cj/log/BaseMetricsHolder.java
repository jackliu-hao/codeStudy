/*     */ package com.mysql.cj.log;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BaseMetricsHolder
/*     */ {
/*     */   private static final int HISTOGRAM_BUCKETS = 20;
/*  40 */   private long longestQueryTimeMs = 0L;
/*     */   
/*  42 */   private long maximumNumberTablesAccessed = 0L;
/*     */   
/*  44 */   private long minimumNumberTablesAccessed = Long.MAX_VALUE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   private long numberOfPreparedExecutes = 0L;
/*     */   
/*  51 */   private long numberOfPrepares = 0L;
/*     */   
/*  53 */   private long numberOfQueriesIssued = 0L;
/*     */   
/*  55 */   private long numberOfResultSetsCreated = 0L;
/*     */   
/*     */   private long[] numTablesMetricsHistBreakpoints;
/*     */   
/*     */   private int[] numTablesMetricsHistCounts;
/*     */   
/*  61 */   private long[] oldHistBreakpoints = null;
/*     */   
/*  63 */   private int[] oldHistCounts = null;
/*     */   
/*  65 */   private long shortestQueryTimeMs = Long.MAX_VALUE;
/*     */   
/*  67 */   private double totalQueryTimeMs = 0.0D;
/*     */   
/*     */   private long[] perfMetricsHistBreakpoints;
/*     */   
/*     */   private int[] perfMetricsHistCounts;
/*     */   
/*     */   private long queryTimeCount;
/*     */   
/*     */   private double queryTimeSum;
/*     */   private double queryTimeSumSquares;
/*     */   private double queryTimeMean;
/*     */   
/*     */   private void createInitialHistogram(long[] breakpoints, long lowerBound, long upperBound) {
/*  80 */     double bucketSize = (upperBound - lowerBound) / 20.0D * 1.25D;
/*     */     
/*  82 */     if (bucketSize < 1.0D) {
/*  83 */       bucketSize = 1.0D;
/*     */     }
/*     */     
/*  86 */     for (int i = 0; i < 20; i++) {
/*  87 */       breakpoints[i] = lowerBound;
/*  88 */       lowerBound = (long)(lowerBound + bucketSize);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void addToHistogram(int[] histogramCounts, long[] histogramBreakpoints, long value, int numberOfTimes, long currentLowerBound, long currentUpperBound) {
/*  94 */     if (histogramCounts == null) {
/*  95 */       createInitialHistogram(histogramBreakpoints, currentLowerBound, currentUpperBound);
/*     */     } else {
/*  97 */       for (int i = 0; i < 20; i++) {
/*  98 */         if (histogramBreakpoints[i] >= value) {
/*  99 */           histogramCounts[i] = histogramCounts[i] + numberOfTimes;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void addToPerformanceHistogram(long value, int numberOfTimes) {
/* 108 */     checkAndCreatePerformanceHistogram();
/*     */     
/* 110 */     addToHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, value, numberOfTimes, (this.shortestQueryTimeMs == Long.MAX_VALUE) ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*     */   }
/*     */ 
/*     */   
/*     */   private void addToTablesAccessedHistogram(long value, int numberOfTimes) {
/* 115 */     checkAndCreateTablesAccessedHistogram();
/*     */     
/* 117 */     addToHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, value, numberOfTimes, (this.minimumNumberTablesAccessed == Long.MAX_VALUE) ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkAndCreatePerformanceHistogram() {
/* 122 */     if (this.perfMetricsHistCounts == null) {
/* 123 */       this.perfMetricsHistCounts = new int[20];
/*     */     }
/*     */     
/* 126 */     if (this.perfMetricsHistBreakpoints == null) {
/* 127 */       this.perfMetricsHistBreakpoints = new long[20];
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkAndCreateTablesAccessedHistogram() {
/* 132 */     if (this.numTablesMetricsHistCounts == null) {
/* 133 */       this.numTablesMetricsHistCounts = new int[20];
/*     */     }
/*     */     
/* 136 */     if (this.numTablesMetricsHistBreakpoints == null) {
/* 137 */       this.numTablesMetricsHistBreakpoints = new long[20];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerQueryExecutionTime(long queryTimeMs) {
/* 146 */     if (queryTimeMs > this.longestQueryTimeMs) {
/* 147 */       this.longestQueryTimeMs = queryTimeMs;
/*     */       
/* 149 */       repartitionPerformanceHistogram();
/*     */     } 
/*     */     
/* 152 */     addToPerformanceHistogram(queryTimeMs, 1);
/*     */     
/* 154 */     if (queryTimeMs < this.shortestQueryTimeMs) {
/* 155 */       this.shortestQueryTimeMs = (queryTimeMs == 0L) ? 1L : queryTimeMs;
/*     */     }
/*     */     
/* 158 */     this.numberOfQueriesIssued++;
/*     */     
/* 160 */     this.totalQueryTimeMs += queryTimeMs;
/*     */   }
/*     */ 
/*     */   
/*     */   private void repartitionHistogram(int[] histCounts, long[] histBreakpoints, long currentLowerBound, long currentUpperBound) {
/* 165 */     if (this.oldHistCounts == null) {
/* 166 */       this.oldHistCounts = new int[histCounts.length];
/* 167 */       this.oldHistBreakpoints = new long[histBreakpoints.length];
/*     */     } 
/*     */     
/* 170 */     System.arraycopy(histCounts, 0, this.oldHistCounts, 0, histCounts.length);
/*     */     
/* 172 */     System.arraycopy(histBreakpoints, 0, this.oldHistBreakpoints, 0, histBreakpoints.length);
/*     */     
/* 174 */     createInitialHistogram(histBreakpoints, currentLowerBound, currentUpperBound);
/*     */     
/* 176 */     for (int i = 0; i < 20; i++) {
/* 177 */       addToHistogram(histCounts, histBreakpoints, this.oldHistBreakpoints[i], this.oldHistCounts[i], currentLowerBound, currentUpperBound);
/*     */     }
/*     */   }
/*     */   
/*     */   private void repartitionPerformanceHistogram() {
/* 182 */     checkAndCreatePerformanceHistogram();
/*     */     
/* 184 */     repartitionHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, (this.shortestQueryTimeMs == Long.MAX_VALUE) ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*     */   }
/*     */ 
/*     */   
/*     */   private void repartitionTablesAccessedHistogram() {
/* 189 */     checkAndCreateTablesAccessedHistogram();
/*     */     
/* 191 */     repartitionHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, (this.minimumNumberTablesAccessed == Long.MAX_VALUE) ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
/*     */   }
/*     */ 
/*     */   
/*     */   public void reportMetrics(Log log) {
/* 196 */     StringBuilder logMessage = new StringBuilder(256);
/*     */     
/* 198 */     logMessage.append("** Performance Metrics Report **\n");
/* 199 */     logMessage.append("\nLongest reported query: " + this.longestQueryTimeMs + " ms");
/* 200 */     logMessage.append("\nShortest reported query: " + this.shortestQueryTimeMs + " ms");
/* 201 */     logMessage.append("\nAverage query execution time: " + (this.totalQueryTimeMs / this.numberOfQueriesIssued) + " ms");
/* 202 */     logMessage.append("\nNumber of statements executed: " + this.numberOfQueriesIssued);
/* 203 */     logMessage.append("\nNumber of result sets created: " + this.numberOfResultSetsCreated);
/* 204 */     logMessage.append("\nNumber of statements prepared: " + this.numberOfPrepares);
/* 205 */     logMessage.append("\nNumber of prepared statement executions: " + this.numberOfPreparedExecutes);
/*     */     
/* 207 */     if (this.perfMetricsHistBreakpoints != null) {
/* 208 */       logMessage.append("\n\n\tTiming Histogram:\n");
/* 209 */       int maxNumPoints = 20;
/* 210 */       int highestCount = Integer.MIN_VALUE;
/*     */       int i;
/* 212 */       for (i = 0; i < 20; i++) {
/* 213 */         if (this.perfMetricsHistCounts[i] > highestCount) {
/* 214 */           highestCount = this.perfMetricsHistCounts[i];
/*     */         }
/*     */       } 
/*     */       
/* 218 */       if (highestCount == 0) {
/* 219 */         highestCount = 1;
/*     */       }
/*     */       
/* 222 */       for (i = 0; i < 19; i++) {
/*     */         
/* 224 */         if (i == 0) {
/* 225 */           logMessage.append("\n\tless than " + this.perfMetricsHistBreakpoints[i + 1] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*     */         } else {
/* 227 */           logMessage.append("\n\tbetween " + this.perfMetricsHistBreakpoints[i] + " and " + this.perfMetricsHistBreakpoints[i + 1] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*     */         } 
/*     */ 
/*     */         
/* 231 */         logMessage.append("\t");
/*     */         
/* 233 */         int numPointsToGraph = (int)(maxNumPoints * this.perfMetricsHistCounts[i] / highestCount);
/*     */         
/* 235 */         for (int j = 0; j < numPointsToGraph; j++) {
/* 236 */           logMessage.append("*");
/*     */         }
/*     */         
/* 239 */         if (this.longestQueryTimeMs < this.perfMetricsHistCounts[i + 1]) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */       
/* 244 */       if (this.perfMetricsHistBreakpoints[18] < this.longestQueryTimeMs) {
/* 245 */         logMessage.append("\n\tbetween ");
/* 246 */         logMessage.append(this.perfMetricsHistBreakpoints[18]);
/* 247 */         logMessage.append(" and ");
/* 248 */         logMessage.append(this.perfMetricsHistBreakpoints[19]);
/* 249 */         logMessage.append(" ms: \t");
/* 250 */         logMessage.append(this.perfMetricsHistCounts[19]);
/*     */       } 
/*     */     } 
/*     */     
/* 254 */     if (this.numTablesMetricsHistBreakpoints != null) {
/* 255 */       logMessage.append("\n\n\tTable Join Histogram:\n");
/* 256 */       int maxNumPoints = 20;
/* 257 */       int highestCount = Integer.MIN_VALUE;
/*     */       int i;
/* 259 */       for (i = 0; i < 20; i++) {
/* 260 */         if (this.numTablesMetricsHistCounts[i] > highestCount) {
/* 261 */           highestCount = this.numTablesMetricsHistCounts[i];
/*     */         }
/*     */       } 
/*     */       
/* 265 */       if (highestCount == 0) {
/* 266 */         highestCount = 1;
/*     */       }
/*     */       
/* 269 */       for (i = 0; i < 19; i++) {
/*     */         
/* 271 */         if (i == 0) {
/* 272 */           logMessage.append("\n\t" + this.numTablesMetricsHistBreakpoints[i + 1] + " tables or less: \t\t" + this.numTablesMetricsHistCounts[i]);
/*     */         } else {
/* 274 */           logMessage.append("\n\tbetween " + this.numTablesMetricsHistBreakpoints[i] + " and " + this.numTablesMetricsHistBreakpoints[i + 1] + " tables: \t" + this.numTablesMetricsHistCounts[i]);
/*     */         } 
/*     */ 
/*     */         
/* 278 */         logMessage.append("\t");
/*     */         
/* 280 */         int numPointsToGraph = (int)(maxNumPoints * this.numTablesMetricsHistCounts[i] / highestCount);
/*     */         
/* 282 */         for (int j = 0; j < numPointsToGraph; j++) {
/* 283 */           logMessage.append("*");
/*     */         }
/*     */         
/* 286 */         if (this.maximumNumberTablesAccessed < this.numTablesMetricsHistBreakpoints[i + 1]) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */       
/* 291 */       if (this.numTablesMetricsHistBreakpoints[18] < this.maximumNumberTablesAccessed) {
/* 292 */         logMessage.append("\n\tbetween ");
/* 293 */         logMessage.append(this.numTablesMetricsHistBreakpoints[18]);
/* 294 */         logMessage.append(" and ");
/* 295 */         logMessage.append(this.numTablesMetricsHistBreakpoints[19]);
/* 296 */         logMessage.append(" tables: ");
/* 297 */         logMessage.append(this.numTablesMetricsHistCounts[19]);
/*     */       } 
/*     */     } 
/*     */     
/* 301 */     log.logInfo(logMessage);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reportNumberOfTablesAccessed(int numTablesAccessed) {
/* 320 */     if (numTablesAccessed < this.minimumNumberTablesAccessed) {
/* 321 */       this.minimumNumberTablesAccessed = numTablesAccessed;
/*     */     }
/*     */     
/* 324 */     if (numTablesAccessed > this.maximumNumberTablesAccessed) {
/* 325 */       this.maximumNumberTablesAccessed = numTablesAccessed;
/*     */       
/* 327 */       repartitionTablesAccessedHistogram();
/*     */     } 
/*     */     
/* 330 */     addToTablesAccessedHistogram(numTablesAccessed, 1);
/*     */   }
/*     */   
/*     */   public void incrementNumberOfPreparedExecutes() {
/* 334 */     this.numberOfPreparedExecutes++;
/*     */ 
/*     */     
/* 337 */     this.numberOfQueriesIssued++;
/*     */   }
/*     */   
/*     */   public void incrementNumberOfPrepares() {
/* 341 */     this.numberOfPrepares++;
/*     */   }
/*     */   
/*     */   public void incrementNumberOfResultSetsCreated() {
/* 345 */     this.numberOfResultSetsCreated++;
/*     */   }
/*     */   
/*     */   public void reportQueryTime(long millisOrNanos) {
/* 349 */     this.queryTimeCount++;
/* 350 */     this.queryTimeSum += millisOrNanos;
/* 351 */     this.queryTimeSumSquares += (millisOrNanos * millisOrNanos);
/* 352 */     this.queryTimeMean = (this.queryTimeMean * (this.queryTimeCount - 1L) + millisOrNanos) / this.queryTimeCount;
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
/*     */ 
/*     */   
/*     */   public boolean checkAbonormallyLongQuery(long millisOrNanos) {
/* 366 */     boolean res = false;
/* 367 */     if (this.queryTimeCount > 14L) {
/* 368 */       double stddev = Math.sqrt((this.queryTimeSumSquares - this.queryTimeSum * this.queryTimeSum / this.queryTimeCount) / (this.queryTimeCount - 1L));
/* 369 */       res = (millisOrNanos > this.queryTimeMean + 5.0D * stddev);
/*     */     } 
/* 371 */     reportQueryTime(millisOrNanos);
/* 372 */     return res;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\log\BaseMetricsHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */