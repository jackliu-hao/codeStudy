/*     */ package com.sun.jna.platform.linux;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class XAttrUtil
/*     */ {
/*     */   public static void setXAttr(String path, String name, String value) throws IOException {
/*  56 */     setXAttr(path, name, value, Native.getDefaultStringEncoding());
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
/*     */   
/*     */   public static void setXAttr(String path, String name, String value, String encoding) throws IOException {
/*  70 */     setXAttr(path, name, value.getBytes(encoding));
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
/*     */   public static void setXAttr(String path, String name, byte[] value) throws IOException {
/*  82 */     int retval = XAttr.INSTANCE.setxattr(path, name, value, new XAttr.size_t(value.length), 0);
/*  83 */     if (retval != 0) {
/*  84 */       int eno = Native.getLastError();
/*  85 */       throw new IOException("errno: " + eno);
/*     */     } 
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
/*     */   
/*     */   public static void lSetXAttr(String path, String name, String value) throws IOException {
/* 100 */     lSetXAttr(path, name, value, Native.getDefaultStringEncoding());
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
/*     */ 
/*     */   
/*     */   public static void lSetXAttr(String path, String name, String value, String encoding) throws IOException {
/* 115 */     lSetXAttr(path, name, value.getBytes(encoding));
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
/*     */   public static void lSetXAttr(String path, String name, byte[] value) throws IOException {
/* 128 */     int retval = XAttr.INSTANCE.lsetxattr(path, name, value, new XAttr.size_t(value.length), 0);
/* 129 */     if (retval != 0) {
/* 130 */       int eno = Native.getLastError();
/* 131 */       throw new IOException("errno: " + eno);
/*     */     } 
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
/*     */   public static void fSetXAttr(int fd, String name, String value) throws IOException {
/* 145 */     fSetXAttr(fd, name, value, Native.getDefaultStringEncoding());
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
/*     */   
/*     */   public static void fSetXAttr(int fd, String name, String value, String encoding) throws IOException {
/* 159 */     fSetXAttr(fd, name, value.getBytes(encoding));
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
/*     */   public static void fSetXAttr(int fd, String name, byte[] value) throws IOException {
/* 171 */     int retval = XAttr.INSTANCE.fsetxattr(fd, name, value, new XAttr.size_t(value.length), 0);
/* 172 */     if (retval != 0) {
/* 173 */       int eno = Native.getLastError();
/* 174 */       throw new IOException("errno: " + eno);
/*     */     } 
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
/*     */   public static String getXAttr(String path, String name) throws IOException {
/* 188 */     return getXAttr(path, name, Native.getDefaultStringEncoding());
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
/*     */   public static String getXAttr(String path, String name, String encoding) throws IOException {
/* 201 */     byte[] valueMem = getXAttrBytes(path, name);
/* 202 */     return new String(valueMem, Charset.forName(encoding));
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
/*     */   public static byte[] getXAttrBytes(String path, String name) throws IOException {
/*     */     XAttr.ssize_t retval;
/*     */     byte[] valueMem;
/* 216 */     int eno = 0;
/*     */     
/*     */     do {
/* 219 */       retval = XAttr.INSTANCE.getxattr(path, name, (byte[])null, XAttr.size_t.ZERO);
/* 220 */       if (retval.longValue() < 0L) {
/* 221 */         eno = Native.getLastError();
/* 222 */         throw new IOException("errno: " + eno);
/*     */       } 
/*     */       
/* 225 */       valueMem = new byte[retval.intValue()];
/* 226 */       retval = XAttr.INSTANCE.getxattr(path, name, valueMem, new XAttr.size_t(valueMem.length));
/* 227 */       if (retval.longValue() >= 0L)
/* 228 */         continue;  eno = Native.getLastError();
/* 229 */       if (eno != 34) {
/* 230 */         throw new IOException("errno: " + eno);
/*     */       }
/*     */     }
/* 233 */     while (retval.longValue() < 0L && eno == 34);
/*     */     
/* 235 */     return valueMem;
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
/*     */   public static Memory getXAttrAsMemory(String path, String name) throws IOException {
/*     */     XAttr.ssize_t retval;
/*     */     Memory valueMem;
/* 249 */     int eno = 0;
/*     */     
/*     */     do {
/* 252 */       retval = XAttr.INSTANCE.getxattr(path, name, (Pointer)null, XAttr.size_t.ZERO);
/* 253 */       if (retval.longValue() < 0L) {
/* 254 */         eno = Native.getLastError();
/* 255 */         throw new IOException("errno: " + eno);
/*     */       } 
/*     */       
/* 258 */       if (retval.longValue() == 0L) {
/* 259 */         return null;
/*     */       }
/*     */       
/* 262 */       valueMem = new Memory(retval.longValue());
/* 263 */       retval = XAttr.INSTANCE.getxattr(path, name, (Pointer)valueMem, new XAttr.size_t(valueMem.size()));
/* 264 */       if (retval.longValue() >= 0L)
/* 265 */         continue;  eno = Native.getLastError();
/* 266 */       if (eno != 34) {
/* 267 */         throw new IOException("errno: " + eno);
/*     */       }
/*     */     }
/* 270 */     while (retval.longValue() < 0L && eno == 34);
/*     */     
/* 272 */     return valueMem;
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
/*     */   
/*     */   public static String lGetXAttr(String path, String name) throws IOException {
/* 286 */     return lGetXAttr(path, name, Native.getDefaultStringEncoding());
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
/*     */   
/*     */   public static String lGetXAttr(String path, String name, String encoding) throws IOException {
/* 300 */     byte[] valueMem = lGetXAttrBytes(path, name);
/* 301 */     return new String(valueMem, Charset.forName(encoding));
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
/*     */   public static byte[] lGetXAttrBytes(String path, String name) throws IOException {
/*     */     XAttr.ssize_t retval;
/*     */     byte[] valueMem;
/* 316 */     int eno = 0;
/*     */     
/*     */     do {
/* 319 */       retval = XAttr.INSTANCE.lgetxattr(path, name, (byte[])null, XAttr.size_t.ZERO);
/* 320 */       if (retval.longValue() < 0L) {
/* 321 */         eno = Native.getLastError();
/* 322 */         throw new IOException("errno: " + eno);
/*     */       } 
/*     */       
/* 325 */       valueMem = new byte[retval.intValue()];
/* 326 */       retval = XAttr.INSTANCE.lgetxattr(path, name, valueMem, new XAttr.size_t(valueMem.length));
/* 327 */       if (retval.longValue() >= 0L)
/* 328 */         continue;  eno = Native.getLastError();
/* 329 */       if (eno != 34) {
/* 330 */         throw new IOException("errno: " + eno);
/*     */       }
/*     */     }
/* 333 */     while (retval.longValue() < 0L && eno == 34);
/*     */     
/* 335 */     return valueMem;
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
/*     */   public static Memory lGetXAttrAsMemory(String path, String name) throws IOException {
/*     */     XAttr.ssize_t retval;
/*     */     Memory valueMem;
/* 350 */     int eno = 0;
/*     */     
/*     */     do {
/* 353 */       retval = XAttr.INSTANCE.lgetxattr(path, name, (Pointer)null, XAttr.size_t.ZERO);
/* 354 */       if (retval.longValue() < 0L) {
/* 355 */         eno = Native.getLastError();
/* 356 */         throw new IOException("errno: " + eno);
/*     */       } 
/*     */       
/* 359 */       if (retval.longValue() == 0L) {
/* 360 */         return null;
/*     */       }
/*     */       
/* 363 */       valueMem = new Memory(retval.longValue());
/* 364 */       retval = XAttr.INSTANCE.lgetxattr(path, name, (Pointer)valueMem, new XAttr.size_t(valueMem.size()));
/* 365 */       if (retval.longValue() >= 0L)
/* 366 */         continue;  eno = Native.getLastError();
/* 367 */       if (eno != 34) {
/* 368 */         throw new IOException("errno: " + eno);
/*     */       }
/*     */     }
/* 371 */     while (retval.longValue() < 0L && eno == 34);
/*     */     
/* 373 */     return valueMem;
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
/*     */   public static String fGetXAttr(int fd, String name) throws IOException {
/* 386 */     return fGetXAttr(fd, name, Native.getDefaultStringEncoding());
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
/*     */   public static String fGetXAttr(int fd, String name, String encoding) throws IOException {
/* 399 */     byte[] valueMem = fGetXAttrBytes(fd, name);
/* 400 */     return new String(valueMem, Charset.forName(encoding));
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
/*     */   public static byte[] fGetXAttrBytes(int fd, String name) throws IOException {
/*     */     XAttr.ssize_t retval;
/*     */     byte[] valueMem;
/* 414 */     int eno = 0;
/*     */     
/*     */     do {
/* 417 */       retval = XAttr.INSTANCE.fgetxattr(fd, name, (byte[])null, XAttr.size_t.ZERO);
/* 418 */       if (retval.longValue() < 0L) {
/* 419 */         eno = Native.getLastError();
/* 420 */         throw new IOException("errno: " + eno);
/*     */       } 
/*     */       
/* 423 */       valueMem = new byte[retval.intValue()];
/* 424 */       retval = XAttr.INSTANCE.fgetxattr(fd, name, valueMem, new XAttr.size_t(valueMem.length));
/* 425 */       if (retval.longValue() >= 0L)
/* 426 */         continue;  eno = Native.getLastError();
/* 427 */       if (eno != 34) {
/* 428 */         throw new IOException("errno: " + eno);
/*     */       }
/*     */     }
/* 431 */     while (retval.longValue() < 0L && eno == 34);
/*     */     
/* 433 */     return valueMem;
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
/*     */   public static Memory fGetXAttrAsMemory(int fd, String name) throws IOException {
/*     */     XAttr.ssize_t retval;
/*     */     Memory valueMem;
/* 447 */     int eno = 0;
/*     */     
/*     */     do {
/* 450 */       retval = XAttr.INSTANCE.fgetxattr(fd, name, (Pointer)null, XAttr.size_t.ZERO);
/* 451 */       if (retval.longValue() < 0L) {
/* 452 */         eno = Native.getLastError();
/* 453 */         throw new IOException("errno: " + eno);
/*     */       } 
/*     */       
/* 456 */       if (retval.longValue() == 0L) {
/* 457 */         return null;
/*     */       }
/*     */       
/* 460 */       valueMem = new Memory(retval.longValue());
/* 461 */       retval = XAttr.INSTANCE.fgetxattr(fd, name, (Pointer)valueMem, new XAttr.size_t(valueMem.size()));
/* 462 */       if (retval.longValue() >= 0L)
/* 463 */         continue;  eno = Native.getLastError();
/* 464 */       if (eno != 34) {
/* 465 */         throw new IOException("errno: " + eno);
/*     */       }
/*     */     }
/* 468 */     while (retval.longValue() < 0L && eno == 34);
/*     */     
/* 470 */     return valueMem;
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
/*     */   public static Collection<String> listXAttr(String path) throws IOException {
/* 482 */     return listXAttr(path, Native.getDefaultStringEncoding());
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
/*     */   public static Collection<String> listXAttr(String path, String encoding) throws IOException {
/*     */     XAttr.ssize_t retval;
/*     */     byte[] listMem;
/* 496 */     int eno = 0;
/*     */     
/*     */     do {
/* 499 */       retval = XAttr.INSTANCE.listxattr(path, (byte[])null, XAttr.size_t.ZERO);
/* 500 */       if (retval.longValue() < 0L) {
/* 501 */         eno = Native.getLastError();
/* 502 */         throw new IOException("errno: " + eno);
/*     */       } 
/*     */       
/* 505 */       listMem = new byte[retval.intValue()];
/* 506 */       retval = XAttr.INSTANCE.listxattr(path, listMem, new XAttr.size_t(listMem.length));
/* 507 */       if (retval.longValue() >= 0L)
/* 508 */         continue;  eno = Native.getLastError();
/* 509 */       if (eno != 34) {
/* 510 */         throw new IOException("errno: " + eno);
/*     */       }
/*     */     }
/* 513 */     while (retval.longValue() < 0L && eno == 34);
/*     */     
/* 515 */     return splitBufferToStrings(listMem, encoding);
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
/*     */   public static Collection<String> lListXAttr(String path) throws IOException {
/* 528 */     return lListXAttr(path, Native.getDefaultStringEncoding());
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
/*     */   public static Collection<String> lListXAttr(String path, String encoding) throws IOException {
/*     */     XAttr.ssize_t retval;
/*     */     byte[] listMem;
/* 543 */     int eno = 0;
/*     */     
/*     */     do {
/* 546 */       retval = XAttr.INSTANCE.llistxattr(path, (byte[])null, XAttr.size_t.ZERO);
/* 547 */       if (retval.longValue() < 0L) {
/* 548 */         eno = Native.getLastError();
/* 549 */         throw new IOException("errno: " + eno);
/*     */       } 
/*     */       
/* 552 */       listMem = new byte[retval.intValue()];
/* 553 */       retval = XAttr.INSTANCE.llistxattr(path, listMem, new XAttr.size_t(listMem.length));
/* 554 */       if (retval.longValue() >= 0L)
/* 555 */         continue;  eno = Native.getLastError();
/* 556 */       if (eno != 34) {
/* 557 */         throw new IOException("errno: " + eno);
/*     */       }
/*     */     }
/* 560 */     while (retval.longValue() < 0L && eno == 34);
/*     */     
/* 562 */     return splitBufferToStrings(listMem, encoding);
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
/*     */   public static Collection<String> fListXAttr(int fd) throws IOException {
/* 574 */     return fListXAttr(fd, Native.getDefaultStringEncoding());
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
/*     */   public static Collection<String> fListXAttr(int fd, String encoding) throws IOException {
/*     */     XAttr.ssize_t retval;
/*     */     byte[] listMem;
/* 588 */     int eno = 0;
/*     */     
/*     */     do {
/* 591 */       retval = XAttr.INSTANCE.flistxattr(fd, (byte[])null, XAttr.size_t.ZERO);
/* 592 */       if (retval.longValue() < 0L) {
/* 593 */         eno = Native.getLastError();
/* 594 */         throw new IOException("errno: " + eno);
/*     */       } 
/*     */       
/* 597 */       listMem = new byte[retval.intValue()];
/* 598 */       retval = XAttr.INSTANCE.flistxattr(fd, listMem, new XAttr.size_t(listMem.length));
/* 599 */       if (retval.longValue() >= 0L)
/* 600 */         continue;  eno = Native.getLastError();
/* 601 */       if (eno != 34) {
/* 602 */         throw new IOException("errno: " + eno);
/*     */       }
/*     */     }
/* 605 */     while (retval.longValue() < 0L && eno == 34);
/*     */     
/* 607 */     return splitBufferToStrings(listMem, encoding);
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
/*     */   public static void removeXAttr(String path, String name) throws IOException {
/* 619 */     int retval = XAttr.INSTANCE.removexattr(path, name);
/* 620 */     if (retval != 0) {
/* 621 */       int eno = Native.getLastError();
/* 622 */       throw new IOException("errno: " + eno);
/*     */     } 
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
/*     */   public static void lRemoveXAttr(String path, String name) throws IOException {
/* 635 */     int retval = XAttr.INSTANCE.lremovexattr(path, name);
/* 636 */     if (retval != 0) {
/* 637 */       int eno = Native.getLastError();
/* 638 */       throw new IOException("errno: " + eno);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void fRemoveXAttr(int fd, String name) throws IOException {
/* 650 */     int retval = XAttr.INSTANCE.fremovexattr(fd, name);
/* 651 */     if (retval != 0) {
/* 652 */       int eno = Native.getLastError();
/* 653 */       throw new IOException("errno: " + eno);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static Collection<String> splitBufferToStrings(byte[] valueMem, String encoding) throws IOException {
/* 659 */     Charset charset = Charset.forName(encoding);
/* 660 */     Set<String> attributesList = new LinkedHashSet<String>(1);
/* 661 */     int offset = 0;
/* 662 */     for (int i = 0; i < valueMem.length; i++) {
/*     */       
/* 664 */       if (valueMem[i] == 0) {
/*     */         
/* 666 */         String name = new String(valueMem, offset, i - offset, charset);
/* 667 */         attributesList.add(name);
/* 668 */         offset = i + 1;
/*     */       } 
/*     */     } 
/* 671 */     return attributesList;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\linux\XAttrUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */