/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.lang.DefaultSegment;
/*     */ import cn.hutool.core.lang.Segment;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PageUtil
/*     */ {
/*  13 */   private static int firstPageNo = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getFirstPageNo() {
/*  21 */     return firstPageNo;
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
/*     */   public static synchronized void setFirstPageNo(int customFirstPageNo) {
/*  35 */     firstPageNo = customFirstPageNo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setOneAsFirstPageNo() {
/*  46 */     setFirstPageNo(1);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getStart(int pageNo, int pageSize) {
/*  73 */     if (pageNo < firstPageNo) {
/*  74 */       pageNo = firstPageNo;
/*     */     }
/*     */     
/*  77 */     if (pageSize < 1) {
/*  78 */       pageSize = 0;
/*     */     }
/*     */     
/*  81 */     return (pageNo - firstPageNo) * pageSize;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getEnd(int pageNo, int pageSize) {
/* 109 */     int start = getStart(pageNo, pageSize);
/* 110 */     return getEndByStart(start, pageSize);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int[] transToStartEnd(int pageNo, int pageSize) {
/* 137 */     int start = getStart(pageNo, pageSize);
/* 138 */     return new int[] { start, getEndByStart(start, pageSize) };
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Segment<Integer> toSegment(int pageNo, int pageSize) {
/* 166 */     int[] startEnd = transToStartEnd(pageNo, pageSize);
/* 167 */     return (Segment<Integer>)new DefaultSegment(Integer.valueOf(startEnd[0]), Integer.valueOf(startEnd[1]));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int totalPage(int totalCount, int pageSize) {
/* 178 */     if (pageSize == 0) {
/* 179 */       return 0;
/*     */     }
/* 181 */     return (totalCount % pageSize == 0) ? (totalCount / pageSize) : (totalCount / pageSize + 1);
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
/*     */   public static int[] rainbow(int pageNo, int totalPage, int displayCount) {
/* 196 */     boolean isEven = ((displayCount & 0x1) == 0);
/* 197 */     int left = displayCount >> 1;
/* 198 */     int right = displayCount >> 1;
/*     */     
/* 200 */     int length = displayCount;
/* 201 */     if (isEven) {
/* 202 */       right++;
/*     */     }
/* 204 */     if (totalPage < displayCount) {
/* 205 */       length = totalPage;
/*     */     }
/* 207 */     int[] result = new int[length];
/* 208 */     if (totalPage >= displayCount) {
/* 209 */       if (pageNo <= left) {
/* 210 */         for (int i = 0; i < result.length; i++) {
/* 211 */           result[i] = i + 1;
/*     */         }
/* 213 */       } else if (pageNo > totalPage - right) {
/* 214 */         for (int i = 0; i < result.length; i++) {
/* 215 */           result[i] = i + totalPage - displayCount + 1;
/*     */         }
/*     */       } else {
/* 218 */         for (int i = 0; i < result.length; i++) {
/* 219 */           result[i] = i + pageNo - left + (isEven ? 1 : 0);
/*     */         }
/*     */       } 
/*     */     } else {
/* 223 */       for (int i = 0; i < result.length; i++) {
/* 224 */         result[i] = i + 1;
/*     */       }
/*     */     } 
/* 227 */     return result;
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
/*     */   public static int[] rainbow(int currentPage, int pageCount) {
/* 240 */     return rainbow(currentPage, pageCount, 10);
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
/*     */   private static int getEndByStart(int start, int pageSize) {
/* 253 */     if (pageSize < 1) {
/* 254 */       pageSize = 0;
/*     */     }
/* 256 */     return start + pageSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\PageUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */