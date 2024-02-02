/*     */ package cn.hutool.core.date.chinese;
/*     */ 
/*     */ import java.time.LocalDate;
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
/*     */ public class LunarInfo
/*     */ {
/*     */   public static final int BASE_YEAR = 1900;
/*  20 */   public static final long BASE_DAY = LocalDate.of(1900, 1, 31).toEpochDay();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  29 */   private static final long[] LUNAR_CODE = new long[] { 19416L, 19168L, 42352L, 21717L, 53856L, 55632L, 91476L, 22176L, 39632L, 21970L, 19168L, 42422L, 42192L, 53840L, 119381L, 46400L, 54944L, 44450L, 38320L, 84343L, 18800L, 42160L, 46261L, 27216L, 27968L, 109396L, 11104L, 38256L, 21234L, 18800L, 25958L, 54432L, 59984L, 92821L, 23248L, 11104L, 100067L, 37600L, 116951L, 51536L, 54432L, 120998L, 46416L, 22176L, 107956L, 9680L, 37584L, 53938L, 43344L, 46423L, 27808L, 46416L, 86869L, 19872L, 42416L, 83315L, 21168L, 43432L, 59728L, 27296L, 44710L, 43856L, 19296L, 43748L, 42352L, 21088L, 62051L, 55632L, 23383L, 22176L, 38608L, 19925L, 19152L, 42192L, 54484L, 53840L, 54616L, 46400L, 46752L, 103846L, 38320L, 18864L, 43380L, 42160L, 45690L, 27216L, 27968L, 44870L, 43872L, 38256L, 19189L, 18800L, 25776L, 29859L, 59984L, 27480L, 23232L, 43872L, 38613L, 37600L, 51552L, 55636L, 54432L, 55888L, 30034L, 22176L, 43959L, 9680L, 37584L, 51893L, 43344L, 46240L, 47780L, 44368L, 21977L, 19360L, 42416L, 86390L, 21168L, 43312L, 31060L, 27296L, 44368L, 23378L, 19296L, 42726L, 42208L, 53856L, 60005L, 54576L, 23200L, 30371L, 38608L, 19195L, 19152L, 42192L, 118966L, 53840L, 54560L, 56645L, 46496L, 22224L, 21938L, 18864L, 42359L, 42160L, 43600L, 111189L, 27936L, 44448L, 84835L, 37744L, 18936L, 18800L, 25776L, 92326L, 59984L, 27424L, 108228L, 43744L, 37600L, 53987L, 51552L, 54615L, 54432L, 55888L, 23893L, 22176L, 42704L, 21972L, 21200L, 43448L, 43344L, 46240L, 46758L, 44368L, 21920L, 43940L, 42416L, 21168L, 45683L, 26928L, 29495L, 27296L, 44368L, 84821L, 19296L, 42352L, 21732L, 53600L, 59752L, 54560L, 55968L, 92838L, 22224L, 19168L, 43476L, 41680L, 53584L, 62034L };
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
/*  53 */   public static final int MAX_YEAR = 1900 + LUNAR_CODE.length - 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int yearDays(int y) {
/*  62 */     int sum = 348;
/*  63 */     for (int i = 32768; i > 8; i >>= 1) {
/*  64 */       if ((getCode(y) & i) != 0L)
/*  65 */         sum++; 
/*     */     } 
/*  67 */     return sum + leapDays(y);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int leapDays(int y) {
/*  77 */     if (leapMonth(y) != 0) {
/*  78 */       return ((getCode(y) & 0x10000L) != 0L) ? 30 : 29;
/*     */     }
/*     */     
/*  81 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int monthDays(int y, int m) {
/*  92 */     return ((getCode(y) & (65536 >> m)) == 0L) ? 29 : 30;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int leapMonth(int y) {
/* 103 */     return (int)(getCode(y) & 0xFL);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static long getCode(int year) {
/* 113 */     return LUNAR_CODE[year - 1900];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\chinese\LunarInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */