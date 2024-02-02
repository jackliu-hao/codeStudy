/*    */ package META-INF.versions.11.org.wildfly.common.archive;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.zip.DataFormatException;
/*    */ import java.util.zip.Inflater;
/*    */ import org.wildfly.common.archive.Archive;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class JDKSpecific
/*    */ {
/*    */   static ByteBuffer inflate(Inflater inflater, ByteBuffer[] bufs, long offset, int compSize, int uncompSize) throws DataFormatException, IOException {
/* 16 */     int cnt = 0;
/* 17 */     byte[] out = new byte[uncompSize];
/* 18 */     int op = 0;
/* 19 */     label12: while (cnt < compSize) {
/* 20 */       int rem = compSize - cnt;
/* 21 */       ByteBuffer buf = bufs[Archive.bufIdx(offset + cnt)].duplicate();
/* 22 */       buf.position(Archive.bufOffs(offset + cnt));
/* 23 */       buf.limit(Math.min(buf.capacity(), buf.position() + rem));
/* 24 */       cnt += buf.remaining();
/* 25 */       inflater.setInput(buf);
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\META-INF\versions\11\org\wildfly\common\archive\JDKSpecific.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */