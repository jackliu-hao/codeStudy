/*     */ package cn.hutool.core.math;
/*     */ 
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
/*     */ public class MathUtil
/*     */ {
/*     */   public static long arrangementCount(int n, int m) {
/*  23 */     return Arrangement.count(n, m);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long arrangementCount(int n) {
/*  33 */     return Arrangement.count(n);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String[]> arrangementSelect(String[] datas, int m) {
/*  44 */     return (new Arrangement(datas)).select(m);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String[]> arrangementSelect(String[] datas) {
/*  54 */     return (new Arrangement(datas)).select();
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
/*     */   public static long combinationCount(int n, int m) {
/*  66 */     return Combination.count(n, m);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String[]> combinationSelect(String[] datas, int m) {
/*  77 */     return (new Combination(datas)).select(m);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long yuanToCent(double yuan) {
/*  88 */     return (new Money(yuan)).getCent();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double centToYuan(long cent) {
/*  99 */     long yuan = cent / 100L;
/* 100 */     int centPart = (int)(cent % 100L);
/* 101 */     return (new Money(yuan, centPart)).getAmount().doubleValue();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\math\MathUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */