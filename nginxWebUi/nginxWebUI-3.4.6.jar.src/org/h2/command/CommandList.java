/*     */ package org.h2.command;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.expression.ParameterInterface;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.ResultWithGeneratedKeys;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class CommandList
/*     */   extends Command
/*     */ {
/*     */   private CommandContainer command;
/*     */   private final ArrayList<Prepared> commands;
/*     */   private final ArrayList<Parameter> parameters;
/*     */   private String remaining;
/*     */   private Command remainingCommand;
/*     */   
/*     */   CommandList(SessionLocal paramSessionLocal, String paramString1, CommandContainer paramCommandContainer, ArrayList<Prepared> paramArrayList, ArrayList<Parameter> paramArrayList1, String paramString2) {
/*  31 */     super(paramSessionLocal, paramString1);
/*  32 */     this.command = paramCommandContainer;
/*  33 */     this.commands = paramArrayList;
/*  34 */     this.parameters = paramArrayList1;
/*  35 */     this.remaining = paramString2;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<? extends ParameterInterface> getParameters() {
/*  40 */     return (ArrayList)this.parameters;
/*     */   }
/*     */   
/*     */   private void executeRemaining() {
/*  44 */     for (Prepared prepared : this.commands) {
/*  45 */       prepared.prepare();
/*  46 */       if (prepared.isQuery()) {
/*  47 */         prepared.query(0L); continue;
/*     */       } 
/*  49 */       prepared.update();
/*     */     } 
/*     */     
/*  52 */     if (this.remaining != null) {
/*  53 */       this.remainingCommand = this.session.prepareLocal(this.remaining);
/*  54 */       this.remaining = null;
/*  55 */       if (this.remainingCommand.isQuery()) {
/*  56 */         this.remainingCommand.query(0L);
/*     */       } else {
/*  58 */         this.remainingCommand.update(null);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultWithGeneratedKeys update(Object paramObject) {
/*  65 */     ResultWithGeneratedKeys resultWithGeneratedKeys = this.command.executeUpdate(null);
/*  66 */     executeRemaining();
/*  67 */     return resultWithGeneratedKeys;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface query(long paramLong) {
/*  72 */     ResultInterface resultInterface = this.command.query(paramLong);
/*  73 */     executeRemaining();
/*  74 */     return resultInterface;
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/*  79 */     this.command.stop();
/*  80 */     for (Prepared prepared : this.commands) {
/*  81 */       CommandContainer.clearCTE(this.session, prepared);
/*     */     }
/*  83 */     if (this.remainingCommand != null) {
/*  84 */       this.remainingCommand.stop();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQuery() {
/*  90 */     return this.command.isQuery();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTransactional() {
/*  95 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 100 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface queryMeta() {
/* 105 */     return this.command.queryMeta();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCommandType() {
/* 110 */     return this.command.getCommandType();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<DbObject> getDependencies() {
/* 115 */     HashSet<DbObject> hashSet = new HashSet();
/* 116 */     for (Prepared prepared : this.commands) {
/* 117 */       prepared.collectDependencies(hashSet);
/*     */     }
/* 119 */     return hashSet;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isCurrentCommandADefineCommand() {
/* 124 */     return this.command.isCurrentCommandADefineCommand();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\CommandList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */