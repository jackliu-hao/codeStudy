/*     */ package cn.hutool.core.math;
/*     */ 
/*     */ import cn.hutool.core.util.NumberUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
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
/*     */ public class Combination
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final String[] datas;
/*     */   
/*     */   public Combination(String[] datas) {
/*  30 */     this.datas = datas;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long count(int n, int m) {
/*  41 */     if (0 == m || n == m) {
/*  42 */       return 1L;
/*     */     }
/*  44 */     return (n > m) ? (NumberUtil.factorial(n, (n - m)) / NumberUtil.factorial(m)) : 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long countAll(int n) {
/*  54 */     if (n < 0 || n > 63) {
/*  55 */       throw new IllegalArgumentException(StrUtil.format("countAll must have n >= 0 and n <= 63, but got n={}", new Object[] { Integer.valueOf(n) }));
/*     */     }
/*  57 */     return (n == 63) ? Long.MAX_VALUE : ((1L << n) - 1L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String[]> select(int m) {
/*  67 */     List<String[]> result = (List)new ArrayList<>((int)count(this.datas.length, m));
/*  68 */     select(0, new String[m], 0, result);
/*  69 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String[]> selectAll() {
/*  78 */     List<String[]> result = (List)new ArrayList<>((int)countAll(this.datas.length));
/*  79 */     for (int i = 1; i <= this.datas.length; i++) {
/*  80 */       result.addAll(select(i));
/*     */     }
/*  82 */     return result;
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
/*     */   private void select(int dataIndex, String[] resultList, int resultIndex, List<String[]> result) {
/*  94 */     int resultLen = resultList.length;
/*  95 */     int resultCount = resultIndex + 1;
/*  96 */     if (resultCount > resultLen) {
/*  97 */       result.add(Arrays.copyOf(resultList, resultList.length));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 102 */     for (int i = dataIndex; i < this.datas.length + resultCount - resultLen; i++) {
/* 103 */       resultList[resultIndex] = this.datas[i];
/* 104 */       select(i + 1, resultList, resultIndex + 1, result);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\math\Combination.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */