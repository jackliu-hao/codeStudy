/*     */ package cn.hutool.core.math;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.NumberUtil;
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
/*     */ public class Arrangement
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final String[] datas;
/*     */   
/*     */   public Arrangement(String[] datas) {
/*  29 */     this.datas = datas;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long count(int n) {
/*  39 */     return count(n, n);
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
/*  50 */     if (n == m) {
/*  51 */       return NumberUtil.factorial(n);
/*     */     }
/*  53 */     return (n > m) ? NumberUtil.factorial(n, (n - m)) : 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long countAll(int n) {
/*  63 */     long total = 0L;
/*  64 */     for (int i = 1; i <= n; i++) {
/*  65 */       total += count(n, i);
/*     */     }
/*  67 */     return total;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String[]> select() {
/*  76 */     return select(this.datas.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String[]> select(int m) {
/*  86 */     List<String[]> result = (List)new ArrayList<>((int)count(this.datas.length, m));
/*  87 */     select(this.datas, new String[m], 0, result);
/*  88 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String[]> selectAll() {
/*  97 */     List<String[]> result = (List)new ArrayList<>((int)countAll(this.datas.length));
/*  98 */     for (int i = 1; i <= this.datas.length; i++) {
/*  99 */       result.addAll(select(i));
/*     */     }
/* 101 */     return result;
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
/*     */   private void select(String[] datas, String[] resultList, int resultIndex, List<String[]> result) {
/* 114 */     if (resultIndex >= resultList.length) {
/* 115 */       if (false == result.contains(resultList)) {
/* 116 */         result.add(Arrays.copyOf(resultList, resultList.length));
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 122 */     for (int i = 0; i < datas.length; i++) {
/* 123 */       resultList[resultIndex] = datas[i];
/* 124 */       select((String[])ArrayUtil.remove((Object[])datas, i), resultList, resultIndex + 1, result);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\math\Arrangement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */