/*    */ package com.sun.jna.platform.unix;public interface LibCAPI extends Reboot, Resource { public static final int HOST_NAME_MAX = 255;
/*    */   
/*    */   int getuid();
/*    */   
/*    */   int geteuid();
/*    */   
/*    */   int getgid();
/*    */   
/*    */   int getegid();
/*    */   
/*    */   int setuid(int paramInt);
/*    */   
/*    */   int seteuid(int paramInt);
/*    */   
/*    */   int setgid(int paramInt);
/*    */   
/*    */   int setegid(int paramInt);
/*    */   
/*    */   int gethostname(byte[] paramArrayOfbyte, int paramInt);
/*    */   
/*    */   int sethostname(String paramString, int paramInt);
/*    */   
/*    */   int getdomainname(byte[] paramArrayOfbyte, int paramInt);
/*    */   
/*    */   int setdomainname(String paramString, int paramInt);
/*    */   
/*    */   String getenv(String paramString);
/*    */   
/*    */   int setenv(String paramString1, String paramString2, int paramInt);
/*    */   
/*    */   int unsetenv(String paramString);
/*    */   
/*    */   int getloadavg(double[] paramArrayOfdouble, int paramInt);
/*    */   
/*    */   int close(int paramInt);
/*    */   
/*    */   int msync(Pointer paramPointer, size_t paramsize_t, int paramInt);
/*    */   
/*    */   int munmap(Pointer paramPointer, size_t paramsize_t);
/*    */   
/*    */   public static class size_t extends IntegerType {
/* 42 */     public static final size_t ZERO = new size_t();
/*    */     
/*    */     private static final long serialVersionUID = 1L;
/*    */     
/*    */     public size_t() {
/* 47 */       this(0L);
/*    */     }
/*    */     
/*    */     public size_t(long value) {
/* 51 */       super(Native.SIZE_T_SIZE, value, true);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static class ssize_t
/*    */     extends IntegerType
/*    */   {
/* 60 */     public static final ssize_t ZERO = new ssize_t();
/*    */     
/*    */     private static final long serialVersionUID = 1L;
/*    */     
/*    */     public ssize_t() {
/* 65 */       this(0L);
/*    */     }
/*    */     
/*    */     public ssize_t(long value) {
/* 69 */       super(Native.SIZE_T_SIZE, value, false);
/*    */     }
/*    */   } }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platfor\\unix\LibCAPI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */