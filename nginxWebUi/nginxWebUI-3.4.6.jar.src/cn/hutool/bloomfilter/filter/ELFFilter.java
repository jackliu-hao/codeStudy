/*    */ package cn.hutool.bloomfilter.filter;
/*    */ 
/*    */ import cn.hutool.core.util.HashUtil;
/*    */ 
/*    */ public class ELFFilter extends FuncFilter {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public ELFFilter(long maxValue) {
/*  9 */     this(maxValue, DEFAULT_MACHINE_NUM);
/*    */   }
/*    */   
/*    */   public ELFFilter(long maxValue, int machineNumber) {
/* 13 */     super(maxValue, machineNumber, HashUtil::elfHash);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\filter\ELFFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */