/*     */ package org.noear.solon.data.tran;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Stack;
/*     */ import javax.sql.DataSource;
/*     */ import org.noear.solon.data.annotation.Tran;
/*     */ import org.noear.solon.data.tranImp.DbTran;
/*     */ import org.noear.solon.data.tranImp.TranDbImp;
/*     */ import org.noear.solon.data.tranImp.TranDbNewImp;
/*     */ import org.noear.solon.data.tranImp.TranMandatoryImp;
/*     */ import org.noear.solon.data.tranImp.TranNeverImp;
/*     */ import org.noear.solon.data.tranImp.TranNotImp;
/*     */ import org.noear.solon.ext.RunnableEx;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TranExecutorImp
/*     */   implements TranExecutor
/*     */ {
/*  21 */   public static final TranExecutorImp global = new TranExecutorImp();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  27 */   protected ThreadLocal<Stack<TranEntity>> local = new ThreadLocal<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean inTrans() {
/*  34 */     return (TranManager.current() != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean inTransAndReadOnly() {
/*  42 */     DbTran tran = TranManager.current();
/*  43 */     return (tran != null && tran.getMeta().readOnly());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection(DataSource ds) throws SQLException {
/*  53 */     DbTran tran = TranManager.current();
/*     */     
/*  55 */     if (tran == null) {
/*  56 */       return ds.getConnection();
/*     */     }
/*  58 */     return tran.getConnection(ds);
/*     */   }
/*     */ 
/*     */   
/*  62 */   protected TranNode tranNot = (TranNode)new TranNotImp();
/*  63 */   protected TranNode tranNever = (TranNode)new TranNeverImp();
/*  64 */   protected TranNode tranMandatory = (TranNode)new TranMandatoryImp();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute(Tran meta, RunnableEx runnable) throws Throwable {
/*  74 */     if (meta == null) {
/*     */ 
/*     */ 
/*     */       
/*  78 */       runnable.run();
/*     */       
/*     */       return;
/*     */     } 
/*  82 */     switch (meta.policy()) {
/*     */       case supports:
/*  84 */         runnable.run();
/*     */         return;
/*     */       
/*     */       case not_supported:
/*  88 */         this.tranNot.apply(runnable);
/*     */         return;
/*     */       
/*     */       case never:
/*  92 */         this.tranNever.apply(runnable);
/*     */         return;
/*     */       
/*     */       case mandatory:
/*  96 */         this.tranMandatory.apply(runnable);
/*     */         return;
/*     */     } 
/*     */ 
/*     */     
/* 101 */     Stack<TranEntity> stack = this.local.get();
/*     */ 
/*     */     
/* 104 */     if (stack == null) {
/* 105 */       forRoot(stack, meta, runnable);
/*     */     } else {
/* 107 */       forNotRoot(stack, meta, runnable);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void forRoot(Stack<TranEntity> stack, Tran meta, RunnableEx runnable) throws Throwable {
/* 121 */     TranNode tran = create(meta);
/* 122 */     stack = new Stack<>();
/*     */     
/*     */     try {
/* 125 */       this.local.set(stack);
/* 126 */       applyDo(stack, tran, meta, runnable);
/*     */     } finally {
/* 128 */       this.local.remove();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void forNotRoot(Stack<TranEntity> stack, Tran meta, RunnableEx runnable) throws Throwable {
/*     */     TranNode tran;
/* 140 */     switch (meta.policy()) {
/*     */       
/*     */       case required:
/* 143 */         runnable.run();
/*     */         return;
/*     */ 
/*     */ 
/*     */       
/*     */       case requires_new:
/* 149 */         tran = create(meta);
/* 150 */         applyDo(stack, tran, meta, runnable);
/*     */         return;
/*     */ 
/*     */ 
/*     */       
/*     */       case nested:
/* 156 */         tran = create(meta);
/*     */ 
/*     */         
/* 159 */         ((TranEntity)stack.peek()).tran.add(tran);
/*     */         
/* 161 */         applyDo(stack, tran, meta, runnable);
/*     */         return;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyDo(Stack<TranEntity> stack, TranNode tran, Tran meta, RunnableEx runnable) throws Throwable {
/* 177 */     if ((meta.policy()).code <= TranPolicy.nested.code) {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 182 */         stack.push(new TranEntity(tran, meta));
/* 183 */         tran.apply(runnable);
/*     */       } finally {
/*     */         
/* 186 */         stack.pop();
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 191 */       tran.apply(runnable);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TranNode create(Tran meta) {
/* 201 */     if (meta.policy() == TranPolicy.not_supported)
/*     */     {
/* 203 */       return this.tranNot; } 
/* 204 */     if (meta.policy() == TranPolicy.never)
/*     */     {
/* 206 */       return this.tranNever; } 
/* 207 */     if (meta.policy() == TranPolicy.mandatory)
/*     */     {
/* 209 */       return this.tranMandatory;
/*     */     }
/*     */ 
/*     */     
/* 213 */     if (meta.policy() == TranPolicy.requires_new || meta
/* 214 */       .policy() == TranPolicy.nested) {
/* 215 */       return (TranNode)new TranDbNewImp(meta);
/*     */     }
/* 217 */     return (TranNode)new TranDbImp(meta);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tran\TranExecutorImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */