/*    */ package cn.hutool.bloomfilter.filter;
/*    */ 
/*    */ import cn.hutool.core.util.HashUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultFilter
/*    */   extends FuncFilter
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DefaultFilter(long maxValue) {
/* 14 */     this(maxValue, DEFAULT_MACHINE_NUM);
/*    */   }
/*    */   
/*    */   public DefaultFilter(long maxValue, int machineNumber) {
/* 18 */     super(maxValue, machineNumber, HashUtil::javaDefaultHash);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\filter\DefaultFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */