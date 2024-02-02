/*    */ package org.wildfly.common.archive;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.zip.DataFormatException;
/*    */ import java.util.zip.Inflater;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class JDKSpecific
/*    */ {
/*    */   static ByteBuffer inflate(Inflater inflater, ByteBuffer[] bufs, long offset, int compSize, int uncompSize) throws DataFormatException, IOException {
/* 16 */     int cnt = 0;
/* 17 */     byte[] b = new byte[Math.min(16384, compSize)];
/* 18 */     byte[] out = new byte[uncompSize];
/* 19 */     int op = 0;
/* 20 */     label12: while (cnt < compSize) {
/* 21 */       int rem = compSize - cnt;
/* 22 */       int acnt = Math.min(rem, b.length);
/* 23 */       Archive.readBytes(bufs, offset, b, 0, acnt);
/* 24 */       cnt += acnt;
/* 25 */       inflater.setInput(b, 0, acnt);
/*    */       while (true)
/* 27 */       { op += inflater.inflate(out, op, uncompSize - op);
/* 28 */         if (inflater.needsInput())
/*    */           continue label12;  } 
/* 30 */     }  if (!inflater.finished()) {
/* 31 */       throw new IOException("Corrupted compression stream");
/*    */     }
/* 33 */     return ByteBuffer.wrap(out);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\archive\JDKSpecific.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */