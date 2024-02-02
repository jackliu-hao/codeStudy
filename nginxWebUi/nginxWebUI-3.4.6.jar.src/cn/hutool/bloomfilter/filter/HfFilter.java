/*    */ package cn.hutool.bloomfilter.filter;
/*    */ 
/*    */ import cn.hutool.core.util.HashUtil;
/*    */ 
/*    */ public class HfFilter
/*    */   extends FuncFilter {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public HfFilter(long maxValue) {
/* 10 */     this(maxValue, DEFAULT_MACHINE_NUM);
/*    */   }
/*    */   
/*    */   public HfFilter(long maxValue, int machineNum) {
/* 14 */     super(maxValue, machineNum, HashUtil::hfHash);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\filter\HfFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */