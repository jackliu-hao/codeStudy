/*     */ package cn.hutool.core.lang.ansi;
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
/*     */ public enum AnsiColor
/*     */   implements AnsiElement
/*     */ {
/*  16 */   DEFAULT("39"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  21 */   BLACK("30"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  26 */   RED("31"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   GREEN("32"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  36 */   YELLOW("33"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  41 */   BLUE("34"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   MAGENTA("35"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   CYAN("36"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   WHITE("37"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   BRIGHT_BLACK("90"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   BRIGHT_RED("91"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   BRIGHT_GREEN("92"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   BRIGHT_YELLOW("93"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  81 */   BRIGHT_BLUE("94"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  86 */   BRIGHT_MAGENTA("95"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  91 */   BRIGHT_CYAN("96"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   BRIGHT_WHITE("97");
/*     */   
/*     */   private final String code;
/*     */   
/*     */   AnsiColor(String code) {
/* 101 */     this.code = code;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 106 */     return this.code;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\ansi\AnsiColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */