/*     */ package org.noear.solon.data.tranImp;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.sql.DataSource;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.data.annotation.Tran;
/*     */ import org.noear.solon.data.tran.TranManager;
/*     */ import org.noear.solon.data.tran.TranNode;
/*     */ import org.noear.solon.ext.RunnableEx;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class DbTran
/*     */   extends DbTranNode
/*     */   implements TranNode
/*     */ {
/*     */   private final Tran meta;
/*  24 */   private final Map<DataSource, Connection> conMap = new HashMap<>();
/*     */   
/*     */   public Tran getMeta() {
/*  27 */     return this.meta;
/*     */   }
/*     */   
/*     */   public Connection getConnection(DataSource ds) throws SQLException {
/*  31 */     if (this.conMap.containsKey(ds)) {
/*  32 */       return this.conMap.get(ds);
/*     */     }
/*  34 */     Connection con = ds.getConnection();
/*  35 */     con.setAutoCommit(false);
/*  36 */     con.setReadOnly(this.meta.readOnly());
/*  37 */     if ((this.meta.isolation()).level > 0) {
/*  38 */       con.setTransactionIsolation((this.meta.isolation()).level);
/*     */     }
/*     */     
/*  41 */     this.conMap.putIfAbsent(ds, con);
/*  42 */     return con;
/*     */   }
/*     */ 
/*     */   
/*     */   public DbTran(Tran meta) {
/*  47 */     this.meta = meta;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute(RunnableEx runnable) throws Throwable {
/*     */     try {
/*  54 */       TranManager.currentSet(this);
/*     */ 
/*     */ 
/*     */       
/*  58 */       runnable.run();
/*     */       
/*  60 */       if (this.parent == null) {
/*  61 */         commit();
/*     */       }
/*  63 */     } catch (Throwable ex) {
/*  64 */       if (this.parent == null) {
/*  65 */         rollback();
/*     */       }
/*     */       
/*  68 */       throw Utils.throwableUnwrap(ex);
/*     */     } finally {
/*  70 */       TranManager.currentRemove();
/*     */       
/*  72 */       if (this.parent == null) {
/*  73 */         close();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void commit() throws Throwable {
/*  80 */     super.commit();
/*     */     
/*  82 */     for (Map.Entry<DataSource, Connection> kv : this.conMap.entrySet()) {
/*  83 */       ((Connection)kv.getValue()).commit();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void rollback() throws Throwable {
/*  89 */     super.rollback();
/*  90 */     for (Map.Entry<DataSource, Connection> kv : this.conMap.entrySet()) {
/*  91 */       ((Connection)kv.getValue()).rollback();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws Throwable {
/*  97 */     super.close();
/*  98 */     for (Map.Entry<DataSource, Connection> kv : this.conMap.entrySet()) {
/*     */       try {
/* 100 */         if (!((Connection)kv.getValue()).isClosed()) {
/* 101 */           ((Connection)kv.getValue()).close();
/*     */         
/*     */         }
/*     */       
/*     */       }
/* 106 */       catch (Throwable ex) {
/* 107 */         EventBus.push(ex);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tranImp\DbTran.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */