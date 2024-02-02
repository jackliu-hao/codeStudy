/*    */ package oshi.jna.platform.mac;
/*    */ 
/*    */ import com.sun.jna.Native;
/*    */ import com.sun.jna.Structure;
/*    */ import com.sun.jna.Structure.FieldOrder;
/*    */ import com.sun.jna.platform.mac.SystemB;
/*    */ import oshi.jna.platform.unix.CLibrary;
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
/*    */ public interface SystemB
/*    */   extends SystemB, CLibrary
/*    */ {
/*    */   public static final int UTX_USERSIZE = 256;
/*    */   public static final int UTX_LINESIZE = 32;
/*    */   public static final int UTX_IDSIZE = 4;
/* 38 */   public static final SystemB INSTANCE = (SystemB)Native.load("System", SystemB.class);
/*    */   public static final int UTX_HOSTSIZE = 256;
/*    */   
/*    */   MacUtmpx getutxent();
/*    */   
/*    */   @FieldOrder({"ut_user", "ut_id", "ut_line", "ut_pid", "ut_type", "ut_tv", "ut_host", "ut_pad"})
/*    */   public static class MacUtmpx
/*    */     extends Structure
/*    */   {
/* 47 */     public byte[] ut_user = new byte[256];
/* 48 */     public byte[] ut_id = new byte[4];
/* 49 */     public byte[] ut_line = new byte[32];
/*    */     public int ut_pid;
/*    */     public short ut_type;
/*    */     public SystemB.Timeval ut_tv;
/* 53 */     public byte[] ut_host = new byte[256];
/* 54 */     public byte[] ut_pad = new byte[16];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\jna\platform\mac\SystemB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */