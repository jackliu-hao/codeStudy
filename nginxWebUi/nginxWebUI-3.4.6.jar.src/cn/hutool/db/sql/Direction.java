/*    */ package cn.hutool.db.sql;
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
/*    */ public enum Direction
/*    */ {
/* 14 */   ASC,
/*    */ 
/*    */ 
/*    */   
/* 18 */   DESC;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Direction fromString(String value) throws IllegalArgumentException {
/* 28 */     if (StrUtil.isEmpty(value)) {
/* 29 */       return null;
/*    */     }
/*    */ 
/*    */     
/* 33 */     if (1 == value.length()) {
/* 34 */       if ("A".equalsIgnoreCase(value))
/* 35 */         return ASC; 
/* 36 */       if ("D".equalsIgnoreCase(value)) {
/* 37 */         return DESC;
/*    */       }
/*    */     } 
/*    */     
/*    */     try {
/* 42 */       return valueOf(value.toUpperCase());
/* 43 */     } catch (Exception e) {
/* 44 */       throw new IllegalArgumentException(StrUtil.format("Invalid value [{}] for orders given! Has to be either 'desc' or 'asc' (case insensitive).", new Object[] { value }), e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\Direction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */