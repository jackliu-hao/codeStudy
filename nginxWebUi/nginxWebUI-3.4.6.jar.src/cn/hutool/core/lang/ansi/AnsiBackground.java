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
/*     */ public enum AnsiBackground
/*     */   implements AnsiElement
/*     */ {
/*  16 */   DEFAULT("49"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  21 */   BLACK("40"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  26 */   RED("41"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   GREEN("42"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  36 */   YELLOW("43"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  41 */   BLUE("44"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   MAGENTA("45"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   CYAN("46"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   WHITE("47"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   BRIGHT_BLACK("100"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   BRIGHT_RED("101"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   BRIGHT_GREEN("102"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   BRIGHT_YELLOW("103"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  81 */   BRIGHT_BLUE("104"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  86 */   BRIGHT_MAGENTA("105"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  91 */   BRIGHT_CYAN("106"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   BRIGHT_WHITE("107");
/*     */   
/*     */   private final String code;
/*     */   
/*     */   AnsiBackground(String code) {
/* 101 */     this.code = code;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 106 */     return this.code;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\ansi\AnsiBackground.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */