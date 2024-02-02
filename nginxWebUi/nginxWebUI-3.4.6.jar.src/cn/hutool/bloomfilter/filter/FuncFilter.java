/*    */ package cn.hutool.bloomfilter.filter;
/*    */ 
/*    */ import java.util.function.Function;
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
/*    */ public class FuncFilter
/*    */   extends AbstractFilter
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Function<String, Number> hashFunc;
/*    */   
/*    */   public FuncFilter(long maxValue, Function<String, Number> hashFunc) {
/* 25 */     this(maxValue, DEFAULT_MACHINE_NUM, hashFunc);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FuncFilter(long maxValue, int machineNum, Function<String, Number> hashFunc) {
/* 34 */     super(maxValue, machineNum);
/* 35 */     this.hashFunc = hashFunc;
/*    */   }
/*    */ 
/*    */   
/*    */   public long hash(String str) {
/* 40 */     return ((Number)this.hashFunc.apply(str)).longValue() % this.size;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\filter\FuncFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */