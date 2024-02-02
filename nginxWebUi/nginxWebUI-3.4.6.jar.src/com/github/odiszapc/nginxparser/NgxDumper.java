/*     */ package com.github.odiszapc.nginxparser;
/*     */ 
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NgxDumper
/*     */ {
/*     */   private NgxConfig config;
/*     */   private static final int PAD_SIZE = 2;
/*     */   private static final String PAD_SYMBOL = "  ";
/*     */   private static final String LBRACE = "{";
/*     */   private static final String RBRACE = "}";
/*     */   private static final String LF = "\n";
/*     */   private static final String CRLF = "\r\n";
/*     */   
/*     */   public NgxDumper(NgxConfig config) {
/*  37 */     this.config = config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String dump() {
/*  45 */     StringWriter writer = new StringWriter();
/*  46 */     writeToStream(this.config, new PrintWriter(writer), 0);
/*  47 */     return writer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dump(OutputStream out) {
/*  55 */     writeToStream(this.config, new PrintWriter(out), 0);
/*     */   }
/*     */   
/*     */   private void writeToStream(NgxBlock config, PrintWriter writer, int level) {
/*  59 */     for (NgxEntry entry : config) {
/*  60 */       NgxBlock block; NgxIfBlock ifBlock; NgxEntryType type = NgxEntryType.fromClass((Class)entry.getClass());
/*  61 */       switch (type) {
/*     */         case BLOCK:
/*  63 */           block = (NgxBlock)entry;
/*  64 */           writer.append(getOffset(level)).append(block.toString()).append(getLineEnding());
/*     */ 
/*     */           
/*  67 */           writeToStream(block, writer, level + 1);
/*  68 */           writer.append(getOffset(level)).append("}").append(getLineEnding());
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case IF:
/*  74 */           ifBlock = (NgxIfBlock)entry;
/*  75 */           writer.append(getOffset(level)).append(ifBlock.toString()).append(getLineEnding());
/*     */ 
/*     */ 
/*     */           
/*  79 */           writeToStream(ifBlock, writer, level + 1);
/*  80 */           writer.append(getOffset(level)).append("{").append(getLineEnding());
/*     */ 
/*     */ 
/*     */         
/*     */         case COMMENT:
/*     */         case PARAM:
/*  86 */           writer.append(getOffset(level)).append(entry.toString()).append(getLineEnding());
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/*  93 */     writer.flush();
/*     */   }
/*     */   
/*     */   public String getOffset(int level) {
/*  97 */     String offset = "";
/*  98 */     for (int i = 0; i < level; i++) {
/*  99 */       offset = offset + "  ";
/*     */     }
/* 101 */     return offset;
/*     */   }
/*     */   
/*     */   public String getLineEnding() {
/* 105 */     return "\n";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\NgxDumper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */