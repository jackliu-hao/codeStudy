/*    */ package cn.hutool.bloomfilter.filter;
/*    */ 
/*    */ import cn.hutool.core.util.HashUtil;
/*    */ 
/*    */ public class JSFilter extends FuncFilter {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public JSFilter(long maxValue) {
/*  9 */     this(maxValue, DEFAULT_MACHINE_NUM);
/*    */   }
/*    */   
/*    */   public JSFilter(long maxValue, int machineNum) {
/* 13 */     super(maxValue, machineNum, HashUtil::jsHash);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\filter\JSFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */