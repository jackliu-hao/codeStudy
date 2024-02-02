/*     */ package javax.mail;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Locale;
/*     */ import java.util.Vector;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Flags
/*     */   implements Cloneable, Serializable
/*     */ {
/* 102 */   private int system_flags = 0;
/* 103 */   private Hashtable user_flags = null;
/*     */   
/*     */   private static final int ANSWERED_BIT = 1;
/*     */   
/*     */   private static final int DELETED_BIT = 2;
/*     */   
/*     */   private static final int DRAFT_BIT = 4;
/*     */   
/*     */   private static final int FLAGGED_BIT = 8;
/*     */   
/*     */   private static final int RECENT_BIT = 16;
/*     */   
/*     */   private static final int SEEN_BIT = 32;
/*     */   
/*     */   private static final int USER_BIT = -2147483648;
/*     */   private static final long serialVersionUID = 6243590407214169028L;
/*     */   
/*     */   public Flags() {}
/*     */   
/*     */   public static final class Flag
/*     */   {
/* 124 */     public static final Flag ANSWERED = new Flag(1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 131 */     public static final Flag DELETED = new Flag(2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 137 */     public static final Flag DRAFT = new Flag(4);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 143 */     public static final Flag FLAGGED = new Flag(8);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     public static final Flag RECENT = new Flag(16);
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
/* 163 */     public static final Flag SEEN = new Flag(32);
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
/* 174 */     public static final Flag USER = new Flag(-2147483648);
/*     */     
/*     */     private int bit;
/*     */     
/*     */     private Flag(int bit) {
/* 179 */       this.bit = bit;
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
/*     */   
/*     */   public Flags(Flags flags) {
/* 195 */     this.system_flags = flags.system_flags;
/* 196 */     if (flags.user_flags != null) {
/* 197 */       this.user_flags = (Hashtable)flags.user_flags.clone();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flags(Flag flag) {
/* 206 */     this.system_flags |= flag.bit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flags(String flag) {
/* 215 */     this.user_flags = new Hashtable(1);
/* 216 */     this.user_flags.put(flag.toLowerCase(Locale.ENGLISH), flag);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(Flag flag) {
/* 225 */     this.system_flags |= flag.bit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(String flag) {
/* 234 */     if (this.user_flags == null)
/* 235 */       this.user_flags = new Hashtable(1); 
/* 236 */     this.user_flags.put(flag.toLowerCase(Locale.ENGLISH), flag);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(Flags f) {
/* 246 */     this.system_flags |= f.system_flags;
/*     */     
/* 248 */     if (f.user_flags != null) {
/* 249 */       if (this.user_flags == null) {
/* 250 */         this.user_flags = new Hashtable(1);
/*     */       }
/* 252 */       Enumeration e = f.user_flags.keys();
/*     */       
/* 254 */       while (e.hasMoreElements()) {
/* 255 */         String s = e.nextElement();
/* 256 */         this.user_flags.put(s, f.user_flags.get(s));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(Flag flag) {
/* 267 */     this.system_flags &= flag.bit ^ 0xFFFFFFFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(String flag) {
/* 276 */     if (this.user_flags != null) {
/* 277 */       this.user_flags.remove(flag.toLowerCase(Locale.ENGLISH));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(Flags f) {
/* 287 */     this.system_flags &= f.system_flags ^ 0xFFFFFFFF;
/*     */     
/* 289 */     if (f.user_flags != null) {
/* 290 */       if (this.user_flags == null) {
/*     */         return;
/*     */       }
/* 293 */       Enumeration e = f.user_flags.keys();
/* 294 */       while (e.hasMoreElements()) {
/* 295 */         this.user_flags.remove(e.nextElement());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(Flag flag) {
/* 305 */     return ((this.system_flags & flag.bit) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(String flag) {
/* 314 */     if (this.user_flags == null) {
/* 315 */       return false;
/*     */     }
/* 317 */     return this.user_flags.containsKey(flag.toLowerCase(Locale.ENGLISH));
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
/*     */   public boolean contains(Flags f) {
/* 329 */     if ((f.system_flags & this.system_flags) != f.system_flags) {
/* 330 */       return false;
/*     */     }
/*     */     
/* 333 */     if (f.user_flags != null) {
/* 334 */       if (this.user_flags == null)
/* 335 */         return false; 
/* 336 */       Enumeration e = f.user_flags.keys();
/*     */       
/* 338 */       while (e.hasMoreElements()) {
/* 339 */         if (!this.user_flags.containsKey(e.nextElement())) {
/* 340 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 345 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 354 */     if (!(obj instanceof Flags)) {
/* 355 */       return false;
/*     */     }
/* 357 */     Flags f = (Flags)obj;
/*     */ 
/*     */     
/* 360 */     if (f.system_flags != this.system_flags) {
/* 361 */       return false;
/*     */     }
/*     */     
/* 364 */     if (f.user_flags == null && this.user_flags == null)
/* 365 */       return true; 
/* 366 */     if (f.user_flags != null && this.user_flags != null && f.user_flags.size() == this.user_flags.size()) {
/*     */       
/* 368 */       Enumeration e = f.user_flags.keys();
/*     */       
/* 370 */       while (e.hasMoreElements()) {
/* 371 */         if (!this.user_flags.containsKey(e.nextElement()))
/* 372 */           return false; 
/*     */       } 
/* 374 */       return true;
/*     */     } 
/*     */     
/* 377 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 386 */     int hash = this.system_flags;
/* 387 */     if (this.user_flags != null) {
/* 388 */       Enumeration e = this.user_flags.keys();
/* 389 */       while (e.hasMoreElements())
/* 390 */         hash += ((String)e.nextElement()).hashCode(); 
/*     */     } 
/* 392 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flag[] getSystemFlags() {
/* 402 */     Vector v = new Vector();
/* 403 */     if ((this.system_flags & 0x1) != 0)
/* 404 */       v.addElement(Flag.ANSWERED); 
/* 405 */     if ((this.system_flags & 0x2) != 0)
/* 406 */       v.addElement(Flag.DELETED); 
/* 407 */     if ((this.system_flags & 0x4) != 0)
/* 408 */       v.addElement(Flag.DRAFT); 
/* 409 */     if ((this.system_flags & 0x8) != 0)
/* 410 */       v.addElement(Flag.FLAGGED); 
/* 411 */     if ((this.system_flags & 0x10) != 0)
/* 412 */       v.addElement(Flag.RECENT); 
/* 413 */     if ((this.system_flags & 0x20) != 0)
/* 414 */       v.addElement(Flag.SEEN); 
/* 415 */     if ((this.system_flags & Integer.MIN_VALUE) != 0) {
/* 416 */       v.addElement(Flag.USER);
/*     */     }
/* 418 */     Flag[] f = new Flag[v.size()];
/* 419 */     v.copyInto((Object[])f);
/* 420 */     return f;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getUserFlags() {
/* 430 */     Vector v = new Vector();
/* 431 */     if (this.user_flags != null) {
/* 432 */       Enumeration e = this.user_flags.elements();
/*     */       
/* 434 */       while (e.hasMoreElements()) {
/* 435 */         v.addElement(e.nextElement());
/*     */       }
/*     */     } 
/* 438 */     String[] f = new String[v.size()];
/* 439 */     v.copyInto((Object[])f);
/* 440 */     return f;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 447 */     Flags f = null;
/*     */     try {
/* 449 */       f = (Flags)super.clone();
/* 450 */     } catch (CloneNotSupportedException cex) {}
/*     */ 
/*     */     
/* 453 */     if (this.user_flags != null)
/* 454 */       f.user_flags = (Hashtable)this.user_flags.clone(); 
/* 455 */     return f;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Flags.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */