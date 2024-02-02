/*    */ package cn.hutool.core.io.unit;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum DataUnit
/*    */ {
/* 25 */   BYTES("B", DataSize.ofBytes(1L)),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 30 */   KILOBYTES("KB", DataSize.ofKilobytes(1L)),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 35 */   MEGABYTES("MB", DataSize.ofMegabytes(1L)),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 40 */   GIGABYTES("GB", DataSize.ofGigabytes(1L)),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 45 */   TERABYTES("TB", DataSize.ofTerabytes(1L));
/*    */   static {
/* 47 */     UNIT_NAMES = new String[] { "B", "KB", "MB", "GB", "TB", "PB", "EB" };
/*    */   }
/*    */   
/*    */   public static final String[] UNIT_NAMES;
/*    */   private final String suffix;
/*    */   private final DataSize size;
/*    */   
/*    */   DataUnit(String suffix, DataSize size) {
/* 55 */     this.suffix = suffix;
/* 56 */     this.size = size;
/*    */   }
/*    */   
/*    */   DataSize size() {
/* 60 */     return this.size;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static DataUnit fromSuffix(String suffix) {
/* 71 */     for (DataUnit candidate : values()) {
/*    */       
/* 73 */       if (StrUtil.startWithIgnoreCase(candidate.suffix, suffix)) {
/* 74 */         return candidate;
/*    */       }
/*    */     } 
/* 77 */     throw new IllegalArgumentException("Unknown data unit suffix '" + suffix + "'");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\i\\unit\DataUnit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */