/*    */ package org.apache.commons.compress.utils;
/*    */ 
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
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
/*    */ public class SkipShieldingInputStream
/*    */   extends FilterInputStream
/*    */ {
/*    */   private static final int SKIP_BUFFER_SIZE = 8192;
/* 42 */   private static final byte[] SKIP_BUFFER = new byte[8192];
/*    */   public SkipShieldingInputStream(InputStream in) {
/* 44 */     super(in);
/*    */   }
/*    */ 
/*    */   
/*    */   public long skip(long n) throws IOException {
/* 49 */     return (n < 0L) ? 0L : read(SKIP_BUFFER, 0, (int)Math.min(n, 8192L));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\SkipShieldingInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */