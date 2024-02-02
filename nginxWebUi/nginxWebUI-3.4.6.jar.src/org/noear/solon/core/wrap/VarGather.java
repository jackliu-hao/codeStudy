/*    */ package org.noear.solon.core.wrap;
/*    */ 
/*    */ import java.lang.reflect.Parameter;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.function.Consumer;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ import org.noear.solon.core.VarHolder;
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
/*    */ public class VarGather
/*    */   implements Runnable
/*    */ {
/*    */   List<VarHolderOfParam> vars;
/*    */   int varSize;
/*    */   Consumer<Object[]> done;
/*    */   BeanWrap bw;
/*    */   
/*    */   public VarGather(BeanWrap bw, int varSize, Consumer<Object[]> done) {
/* 28 */     this.bw = bw;
/* 29 */     this.done = done;
/* 30 */     this.varSize = varSize;
/* 31 */     this.vars = new ArrayList<>(varSize);
/*    */   }
/*    */   
/*    */   public VarHolder add(Parameter p) {
/* 35 */     VarHolderOfParam p2 = new VarHolderOfParam(this.bw.context(), p, this);
/* 36 */     this.vars.add(p2);
/* 37 */     return p2;
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/* 42 */     for (VarHolderOfParam p1 : this.vars) {
/* 43 */       if (!p1.isDone()) {
/*    */         return;
/*    */       }
/*    */     } 
/*    */     
/* 48 */     if (this.vars.size() != this.varSize) {
/*    */       return;
/*    */     }
/*    */     
/* 52 */     List<Object> args = new ArrayList(this.vars.size());
/* 53 */     for (VarHolderOfParam p1 : this.vars) {
/* 54 */       args.add(p1.getValue());
/*    */     }
/*    */     
/* 57 */     this.done.accept(args.toArray());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\wrap\VarGather.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */