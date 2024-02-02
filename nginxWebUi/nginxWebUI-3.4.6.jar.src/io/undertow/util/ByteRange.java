/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
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
/*     */ public class ByteRange
/*     */ {
/*     */   private final List<Range> ranges;
/*     */   
/*     */   public ByteRange(List<Range> ranges) {
/*  38 */     this.ranges = ranges;
/*     */   }
/*     */   
/*     */   public int getRanges() {
/*  42 */     return this.ranges.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getStart(int range) {
/*  51 */     return ((Range)this.ranges.get(range)).getStart();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getEnd(int range) {
/*  60 */     return ((Range)this.ranges.get(range)).getEnd();
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
/*     */   public static ByteRange parse(String rangeHeader) {
/*  72 */     if (rangeHeader == null || rangeHeader.length() < 7) {
/*  73 */       return null;
/*     */     }
/*  75 */     if (!rangeHeader.startsWith("bytes=")) {
/*  76 */       return null;
/*     */     }
/*  78 */     List<Range> ranges = new ArrayList<>();
/*  79 */     String[] parts = rangeHeader.substring(6).split(",");
/*  80 */     for (String part : parts) {
/*     */       try {
/*  82 */         int index = part.indexOf('-');
/*  83 */         if (index == 0) {
/*     */ 
/*     */ 
/*     */           
/*  87 */           long val = Long.parseLong(part.substring(1));
/*  88 */           if (val < 0L) {
/*  89 */             UndertowLogger.REQUEST_LOGGER.debugf("Invalid range spec %s", rangeHeader);
/*  90 */             return null;
/*     */           } 
/*  92 */           ranges.add(new Range(-1L, val));
/*     */         } else {
/*  94 */           long end; if (index == -1) {
/*  95 */             UndertowLogger.REQUEST_LOGGER.debugf("Invalid range spec %s", rangeHeader);
/*  96 */             return null;
/*     */           } 
/*  98 */           long start = Long.parseLong(part.substring(0, index));
/*  99 */           if (start < 0L) {
/* 100 */             UndertowLogger.REQUEST_LOGGER.debugf("Invalid range spec %s", rangeHeader);
/* 101 */             return null;
/*     */           } 
/*     */           
/* 104 */           if (index + 1 < part.length()) {
/* 105 */             end = Long.parseLong(part.substring(index + 1));
/*     */           } else {
/* 107 */             end = -1L;
/*     */           } 
/* 109 */           ranges.add(new Range(start, end));
/*     */         } 
/* 111 */       } catch (NumberFormatException e) {
/* 112 */         UndertowLogger.REQUEST_LOGGER.debugf("Invalid range spec %s", rangeHeader);
/* 113 */         return null;
/*     */       } 
/*     */     } 
/* 116 */     if (ranges.isEmpty()) {
/* 117 */       return null;
/*     */     }
/* 119 */     return new ByteRange(ranges);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RangeResponseResult getResponseResult(long resourceContentLength, String ifRange, Date lastModified, String eTag) {
/*     */     long rangeLength;
/* 128 */     if (this.ranges.isEmpty()) {
/* 129 */       return null;
/*     */     }
/* 131 */     long start = getStart(0);
/* 132 */     long end = getEnd(0);
/*     */     
/* 134 */     if (ifRange != null && !ifRange.isEmpty()) {
/* 135 */       if (ifRange.charAt(0) == '"') {
/*     */         
/* 137 */         if (eTag != null && !eTag.equals(ifRange)) {
/* 138 */           return null;
/*     */         }
/*     */       } else {
/* 141 */         Date ifDate = DateUtils.parseDate(ifRange);
/* 142 */         if (ifDate != null && lastModified != null && ifDate.getTime() < lastModified.getTime()) {
/* 143 */           return null;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 148 */     if (start == -1L) {
/*     */       
/* 150 */       if (end < 0L)
/*     */       {
/* 152 */         return new RangeResponseResult(0L, 0L, 0L, "bytes */" + resourceContentLength, 416);
/*     */       }
/* 154 */       start = Math.max(resourceContentLength - end, 0L);
/* 155 */       end = resourceContentLength - 1L;
/* 156 */       rangeLength = resourceContentLength - start;
/* 157 */     } else if (end == -1L) {
/*     */       
/* 159 */       long toWrite = resourceContentLength - start;
/* 160 */       if (toWrite > 0L) {
/* 161 */         rangeLength = toWrite;
/*     */       } else {
/*     */         
/* 164 */         return new RangeResponseResult(0L, 0L, 0L, "bytes */" + resourceContentLength, 416);
/*     */       } 
/* 166 */       end = resourceContentLength - 1L;
/*     */     } else {
/* 168 */       end = Math.min(end, resourceContentLength - 1L);
/* 169 */       if (start >= resourceContentLength || start > end) {
/* 170 */         return new RangeResponseResult(0L, 0L, 0L, "bytes */" + resourceContentLength, 416);
/*     */       }
/* 172 */       rangeLength = end - start + 1L;
/*     */     } 
/* 174 */     return new RangeResponseResult(start, end, rangeLength, "bytes " + start + "-" + end + "/" + resourceContentLength, 206);
/*     */   }
/*     */   
/*     */   public static class RangeResponseResult {
/*     */     private final long start;
/*     */     private final long end;
/*     */     private final long contentLength;
/*     */     private final String contentRange;
/*     */     private final int statusCode;
/*     */     
/*     */     public RangeResponseResult(long start, long end, long contentLength, String contentRange, int statusCode) {
/* 185 */       this.start = start;
/* 186 */       this.end = end;
/* 187 */       this.contentLength = contentLength;
/* 188 */       this.contentRange = contentRange;
/* 189 */       this.statusCode = statusCode;
/*     */     }
/*     */     
/*     */     public long getStart() {
/* 193 */       return this.start;
/*     */     }
/*     */     
/*     */     public long getEnd() {
/* 197 */       return this.end;
/*     */     }
/*     */     
/*     */     public long getContentLength() {
/* 201 */       return this.contentLength;
/*     */     }
/*     */     
/*     */     public String getContentRange() {
/* 205 */       return this.contentRange;
/*     */     }
/*     */     
/*     */     public int getStatusCode() {
/* 209 */       return this.statusCode;
/*     */     } }
/*     */   
/*     */   public static class Range {
/*     */     private final long start;
/*     */     private final long end;
/*     */     
/*     */     public Range(long start, long end) {
/* 217 */       this.start = start;
/* 218 */       this.end = end;
/*     */     }
/*     */     
/*     */     public long getStart() {
/* 222 */       return this.start;
/*     */     }
/*     */     
/*     */     public long getEnd() {
/* 226 */       return this.end;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ByteRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */