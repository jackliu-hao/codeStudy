/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.util.ClassLoaderUtil;
/*     */ import cn.hutool.core.util.RandomUtil;
/*     */ import cn.hutool.core.util.RuntimeUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.net.NetworkInterface;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Enumeration;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
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
/*     */ public class ObjectId
/*     */ {
/*  49 */   private static final AtomicInteger NEXT_INC = new AtomicInteger(RandomUtil.randomInt());
/*     */   
/*  51 */   private static final int MACHINE = getMachinePiece() | getProcessPiece();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isValid(String s) {
/*  60 */     if (s == null) {
/*  61 */       return false;
/*     */     }
/*  63 */     s = StrUtil.removeAll(s, "-");
/*  64 */     int len = s.length();
/*  65 */     if (len != 24) {
/*  66 */       return false;
/*     */     }
/*     */ 
/*     */     
/*  70 */     for (int i = 0; i < len; i++) {
/*  71 */       char c = s.charAt(i);
/*  72 */       if (c < '0' || c > '9')
/*     */       {
/*     */         
/*  75 */         if (c < 'a' || c > 'f')
/*     */         {
/*     */           
/*  78 */           if (c < 'A' || c > 'F')
/*     */           {
/*     */             
/*  81 */             return false; }  }  } 
/*     */     } 
/*  83 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] nextBytes() {
/*  93 */     ByteBuffer bb = ByteBuffer.wrap(new byte[12]);
/*  94 */     bb.putInt((int)DateUtil.currentSeconds());
/*  95 */     bb.putInt(MACHINE);
/*  96 */     bb.putInt(NEXT_INC.getAndIncrement());
/*     */     
/*  98 */     return bb.array();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String next() {
/* 107 */     return next(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String next(boolean withHyphen) {
/* 117 */     byte[] array = nextBytes();
/* 118 */     StringBuilder buf = new StringBuilder(withHyphen ? 26 : 24);
/*     */     
/* 120 */     for (int i = 0; i < array.length; i++) {
/* 121 */       if (withHyphen && i % 4 == 0 && i != 0) {
/* 122 */         buf.append("-");
/*     */       }
/* 124 */       int t = array[i] & 0xFF;
/* 125 */       if (t < 16) {
/* 126 */         buf.append('0');
/*     */       }
/* 128 */       buf.append(Integer.toHexString(t));
/*     */     } 
/*     */     
/* 131 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getMachinePiece() {
/*     */     int machinePiece;
/*     */     try {
/* 144 */       StringBuilder netSb = new StringBuilder();
/*     */       
/* 146 */       Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
/*     */       
/* 148 */       while (e.hasMoreElements()) {
/* 149 */         NetworkInterface ni = e.nextElement();
/*     */         
/* 151 */         netSb.append(ni.toString());
/*     */       } 
/*     */       
/* 154 */       machinePiece = netSb.toString().hashCode() << 16;
/* 155 */     } catch (Throwable e) {
/*     */       
/* 157 */       machinePiece = RandomUtil.randomInt() << 16;
/*     */     } 
/* 159 */     return machinePiece;
/*     */   }
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
/*     */   private static int getProcessPiece() {
/*     */     int processId;
/*     */     try {
/* 174 */       processId = RuntimeUtil.getPid();
/* 175 */     } catch (Throwable t) {
/* 176 */       processId = RandomUtil.randomInt();
/*     */     } 
/*     */     
/* 179 */     ClassLoader loader = ClassLoaderUtil.getClassLoader();
/*     */     
/* 181 */     int loaderId = (loader != null) ? System.identityHashCode(loader) : 0;
/*     */ 
/*     */ 
/*     */     
/* 185 */     String processSb = Integer.toHexString(processId) + Integer.toHexString(loaderId);
/* 186 */     int processPiece = processSb.hashCode() & 0xFFFF;
/*     */     
/* 188 */     return processPiece;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\ObjectId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */