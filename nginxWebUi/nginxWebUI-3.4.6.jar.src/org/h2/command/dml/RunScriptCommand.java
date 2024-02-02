/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.h2.command.CommandContainer;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.util.ScriptReader;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RunScriptCommand
/*     */   extends ScriptBase
/*     */ {
/*     */   private static final char UTF8_BOM = '﻿';
/*  33 */   private Charset charset = StandardCharsets.UTF_8;
/*     */   
/*     */   private boolean quirksMode;
/*     */   
/*     */   private boolean variableBinary;
/*     */   
/*     */   private boolean from1X;
/*     */   
/*     */   public RunScriptCommand(SessionLocal paramSessionLocal) {
/*  42 */     super(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/*  47 */     this.session.getUser().checkAdmin();
/*  48 */     byte b = 0;
/*  49 */     boolean bool1 = this.session.isQuirksMode();
/*  50 */     boolean bool2 = this.session.isVariableBinary();
/*     */     try {
/*  52 */       openInput(this.charset);
/*     */       
/*  54 */       this.reader.mark(1);
/*  55 */       if (this.reader.read() != 65279) {
/*  56 */         this.reader.reset();
/*     */       }
/*  58 */       if (this.quirksMode) {
/*  59 */         this.session.setQuirksMode(true);
/*     */       }
/*  61 */       if (this.variableBinary) {
/*  62 */         this.session.setVariableBinary(true);
/*     */       }
/*  64 */       ScriptReader scriptReader = new ScriptReader(this.reader);
/*     */       while (true) {
/*  66 */         String str = scriptReader.readStatement();
/*  67 */         if (str == null) {
/*     */           break;
/*     */         }
/*  70 */         execute(str);
/*  71 */         b++;
/*  72 */         if ((b & 0x7F) == 0) {
/*  73 */           checkCanceled();
/*     */         }
/*     */       } 
/*  76 */       scriptReader.close();
/*  77 */     } catch (IOException iOException) {
/*  78 */       throw DbException.convertIOException(iOException, null);
/*     */     } finally {
/*  80 */       if (this.quirksMode) {
/*  81 */         this.session.setQuirksMode(bool1);
/*     */       }
/*  83 */       if (this.variableBinary) {
/*  84 */         this.session.setVariableBinary(bool2);
/*     */       }
/*  86 */       closeIO();
/*     */     } 
/*  88 */     return b;
/*     */   }
/*     */   
/*     */   private void execute(String paramString) {
/*  92 */     if (this.from1X) {
/*  93 */       paramString = paramString.trim();
/*  94 */       if (paramString.startsWith("INSERT INTO SYSTEM_LOB_STREAM VALUES(")) {
/*  95 */         int i = paramString.indexOf(", NULL, '");
/*  96 */         if (i >= 0)
/*     */         {
/*  98 */           paramString = (new StringBuilder(paramString.length() + 1)).append(paramString, 0, i + 8).append("X'").append(paramString, i + 9, paramString.length()).toString();
/*     */         }
/*     */       } 
/*     */     } 
/*     */     try {
/* 103 */       Prepared prepared = this.session.prepare(paramString);
/* 104 */       CommandContainer commandContainer = new CommandContainer(this.session, paramString, prepared);
/* 105 */       if (commandContainer.isQuery()) {
/* 106 */         commandContainer.executeQuery(0L, false);
/*     */       } else {
/* 108 */         commandContainer.executeUpdate(null);
/*     */       } 
/* 110 */     } catch (DbException dbException) {
/* 111 */       throw dbException.addSQL(paramString);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setCharset(Charset paramCharset) {
/* 116 */     this.charset = paramCharset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setQuirksMode(boolean paramBoolean) {
/* 126 */     this.quirksMode = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVariableBinary(boolean paramBoolean) {
/* 137 */     this.variableBinary = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFrom1X() {
/* 144 */     this.variableBinary = this.quirksMode = this.from1X = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface queryMeta() {
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 154 */     return 64;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\RunScriptCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */