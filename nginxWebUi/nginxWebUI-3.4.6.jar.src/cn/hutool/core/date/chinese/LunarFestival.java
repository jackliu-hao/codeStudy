/*     */ package cn.hutool.core.date.chinese;
/*     */ 
/*     */ import cn.hutool.core.lang.Pair;
/*     */ import cn.hutool.core.map.TableMap;
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
/*     */ public class LunarFestival
/*     */ {
/*  18 */   private static final TableMap<Pair<Integer, Integer>, String> L_FTV = new TableMap(16);
/*     */ 
/*     */   
/*     */   static {
/*  22 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(1)), "春节");
/*  23 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(2)), "犬日");
/*  24 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(3)), "猪日");
/*  25 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(4)), "羊日");
/*  26 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(5)), "牛日 破五日");
/*  27 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(6)), "马日 送穷日");
/*  28 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(7)), "人日 人胜节");
/*  29 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(8)), "谷日 八仙日");
/*  30 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(9)), "天日 九皇会");
/*  31 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(10)), "地日 石头生日");
/*  32 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(12)), "火日 老鼠娶媳妇日");
/*  33 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(13)), "上（试）灯日 关公升天日");
/*  34 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(15)), "元宵节 上元节");
/*  35 */     L_FTV.put(new Pair(Integer.valueOf(1), Integer.valueOf(18)), "落灯日");
/*     */ 
/*     */     
/*  38 */     L_FTV.put(new Pair(Integer.valueOf(2), Integer.valueOf(1)), "中和节 太阳生日");
/*  39 */     L_FTV.put(new Pair(Integer.valueOf(2), Integer.valueOf(2)), "龙抬头");
/*  40 */     L_FTV.put(new Pair(Integer.valueOf(2), Integer.valueOf(12)), "花朝节");
/*  41 */     L_FTV.put(new Pair(Integer.valueOf(2), Integer.valueOf(19)), "观世音圣诞");
/*     */ 
/*     */     
/*  44 */     L_FTV.put(new Pair(Integer.valueOf(3), Integer.valueOf(3)), "上巳节");
/*     */ 
/*     */     
/*  47 */     L_FTV.put(new Pair(Integer.valueOf(4), Integer.valueOf(1)), "祭雹神");
/*  48 */     L_FTV.put(new Pair(Integer.valueOf(4), Integer.valueOf(4)), "文殊菩萨诞辰");
/*  49 */     L_FTV.put(new Pair(Integer.valueOf(4), Integer.valueOf(8)), "佛诞节");
/*     */ 
/*     */     
/*  52 */     L_FTV.put(new Pair(Integer.valueOf(5), Integer.valueOf(5)), "端午节 端阳节");
/*     */ 
/*     */     
/*  55 */     L_FTV.put(new Pair(Integer.valueOf(6), Integer.valueOf(6)), "晒衣节 姑姑节");
/*  56 */     L_FTV.put(new Pair(Integer.valueOf(6), Integer.valueOf(6)), "天贶节");
/*  57 */     L_FTV.put(new Pair(Integer.valueOf(6), Integer.valueOf(24)), "彝族火把节");
/*     */ 
/*     */     
/*  60 */     L_FTV.put(new Pair(Integer.valueOf(7), Integer.valueOf(7)), "七夕");
/*  61 */     L_FTV.put(new Pair(Integer.valueOf(7), Integer.valueOf(14)), "鬼节(南方)");
/*  62 */     L_FTV.put(new Pair(Integer.valueOf(7), Integer.valueOf(15)), "中元节");
/*  63 */     L_FTV.put(new Pair(Integer.valueOf(7), Integer.valueOf(15)), "盂兰盆节 中元节");
/*  64 */     L_FTV.put(new Pair(Integer.valueOf(7), Integer.valueOf(30)), "地藏节");
/*     */ 
/*     */     
/*  67 */     L_FTV.put(new Pair(Integer.valueOf(8), Integer.valueOf(15)), "中秋节");
/*     */ 
/*     */     
/*  70 */     L_FTV.put(new Pair(Integer.valueOf(9), Integer.valueOf(9)), "重阳节");
/*     */ 
/*     */     
/*  73 */     L_FTV.put(new Pair(Integer.valueOf(10), Integer.valueOf(1)), "祭祖节");
/*  74 */     L_FTV.put(new Pair(Integer.valueOf(10), Integer.valueOf(15)), "下元节");
/*     */ 
/*     */     
/*  77 */     L_FTV.put(new Pair(Integer.valueOf(11), Integer.valueOf(17)), "阿弥陀佛圣诞");
/*     */ 
/*     */     
/*  80 */     L_FTV.put(new Pair(Integer.valueOf(12), Integer.valueOf(8)), "腊八节");
/*  81 */     L_FTV.put(new Pair(Integer.valueOf(12), Integer.valueOf(16)), "尾牙");
/*  82 */     L_FTV.put(new Pair(Integer.valueOf(12), Integer.valueOf(23)), "小年");
/*  83 */     L_FTV.put(new Pair(Integer.valueOf(12), Integer.valueOf(30)), "除夕");
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
/*     */   public static List<String> getFestivals(int year, int month, int day) {
/*  97 */     if (12 == month && 29 == day && 
/*  98 */       29 == LunarInfo.monthDays(year, month)) {
/*  99 */       day++;
/*     */     }
/*     */     
/* 102 */     return getFestivals(month, day);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> getFestivals(int month, int day) {
/* 113 */     return L_FTV.getValues(new Pair(Integer.valueOf(month), Integer.valueOf(day)));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\chinese\LunarFestival.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */