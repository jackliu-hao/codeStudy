/*    */ package oshi.driver.unix;
/*    */ 
/*    */ import java.util.List;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.ExecutingCommand;
/*    */ import oshi.util.tuples.Pair;
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
/*    */ @ThreadSafe
/*    */ public final class NetStatTcp
/*    */ {
/*    */   public static Pair<Long, Long> queryTcpnetstat() {
/* 47 */     long tcp4 = 0L;
/* 48 */     long tcp6 = 0L;
/* 49 */     List<String> activeConns = ExecutingCommand.runNative("netstat -n -p tcp");
/* 50 */     for (String s : activeConns) {
/* 51 */       if (s.endsWith("ESTABLISHED")) {
/* 52 */         if (s.startsWith("tcp4")) {
/* 53 */           tcp4++; continue;
/* 54 */         }  if (s.startsWith("tcp6")) {
/* 55 */           tcp6++;
/*    */         }
/*    */       } 
/*    */     } 
/* 59 */     return new Pair(Long.valueOf(tcp4), Long.valueOf(tcp6));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\NetStatTcp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */