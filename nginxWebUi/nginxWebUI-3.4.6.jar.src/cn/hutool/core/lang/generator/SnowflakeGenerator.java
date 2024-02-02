/*    */ package cn.hutool.core.lang.generator;
/*    */ 
/*    */ import cn.hutool.core.lang.Snowflake;
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
/*    */ public class SnowflakeGenerator
/*    */   implements Generator<Long>
/*    */ {
/*    */   private final Snowflake snowflake;
/*    */   
/*    */   public SnowflakeGenerator() {
/* 21 */     this(0L, 0L);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SnowflakeGenerator(long workerId, long dataCenterId) {
/* 31 */     this.snowflake = new Snowflake(workerId, dataCenterId);
/*    */   }
/*    */ 
/*    */   
/*    */   public Long next() {
/* 36 */     return Long.valueOf(this.snowflake.nextId());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\generator\SnowflakeGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */