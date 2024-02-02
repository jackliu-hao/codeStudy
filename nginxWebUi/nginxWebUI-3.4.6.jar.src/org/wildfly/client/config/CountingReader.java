/*    */ package org.wildfly.client.config;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class CountingReader
/*    */   extends Reader
/*    */ {
/* 28 */   private int lineNumber = 1;
/* 29 */   private int columnNumber = 1;
/* 30 */   private int characterOffset = 0;
/*    */   
/*    */   private final Reader reader;
/*    */   
/*    */   CountingReader(Reader reader) {
/* 35 */     this.reader = reader;
/*    */   }
/*    */   
/*    */   public int read() throws IOException {
/* 39 */     int ch = this.reader.read();
/* 40 */     if (ch == -1) return -1; 
/* 41 */     processChar(ch);
/* 42 */     return ch;
/*    */   }
/*    */   
/*    */   private void processChar(int ch) {
/* 46 */     switch (ch) {
/*    */       case 10:
/* 48 */         this.characterOffset++;
/* 49 */         this.lineNumber++;
/* 50 */         this.columnNumber = 1;
/*    */         return;
/*    */     } 
/*    */     
/* 54 */     if (!Character.isLowSurrogate((char)ch)) {
/* 55 */       this.characterOffset++;
/* 56 */       this.columnNumber++;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int read(char[] cbuf) throws IOException {
/* 64 */     int cnt = this.reader.read(cbuf);
/* 65 */     if (cnt > 0) {
/* 66 */       for (int i = 0; i < cnt; i++) {
/* 67 */         processChar(cbuf[i]);
/*    */       }
/*    */     }
/* 70 */     return cnt;
/*    */   }
/*    */   
/*    */   public int read(char[] cbuf, int off, int len) throws IOException {
/* 74 */     int cnt = this.reader.read(cbuf, off, len);
/* 75 */     if (cnt > 0) {
/* 76 */       for (int i = 0; i < cnt; i++) {
/* 77 */         processChar(cbuf[i + off]);
/*    */       }
/*    */     }
/* 80 */     return cnt;
/*    */   }
/*    */   
/*    */   public void close() throws IOException {
/* 84 */     this.reader.close();
/*    */   }
/*    */   
/*    */   public int getLineNumber() {
/* 88 */     return this.lineNumber;
/*    */   }
/*    */   
/*    */   public int getColumnNumber() {
/* 92 */     return this.columnNumber;
/*    */   }
/*    */   
/*    */   public int getCharacterOffset() {
/* 96 */     return this.characterOffset;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\CountingReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */