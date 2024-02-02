/*    */ package cn.hutool.bloomfilter.filter;
/*    */ 
/*    */ import cn.hutool.core.util.HashUtil;
/*    */ 
/*    */ public class HfIpFilter extends FuncFilter {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public HfIpFilter(long maxValue) {
/*  9 */     this(maxValue, DEFAULT_MACHINE_NUM);
/*    */   }
/*    */   
/*    */   public HfIpFilter(long maxValue, int machineNum) {
/* 13 */     super(maxValue, machineNum, HashUtil::hfIpHash);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\filter\HfIpFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */