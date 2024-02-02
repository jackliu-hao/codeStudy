/*    */ package org.apache.http.impl.cookie;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.conn.util.PublicSuffixList;
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
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class PublicSuffixListParser
/*    */ {
/*    */   private final PublicSuffixFilter filter;
/*    */   private final org.apache.http.conn.util.PublicSuffixListParser parser;
/*    */   
/*    */   PublicSuffixListParser(PublicSuffixFilter filter) {
/* 52 */     this.filter = filter;
/* 53 */     this.parser = new org.apache.http.conn.util.PublicSuffixListParser();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void parse(Reader reader) throws IOException {
/* 65 */     PublicSuffixList suffixList = this.parser.parse(reader);
/* 66 */     this.filter.setPublicSuffixes(suffixList.getRules());
/* 67 */     this.filter.setExceptions(suffixList.getExceptions());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\PublicSuffixListParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */