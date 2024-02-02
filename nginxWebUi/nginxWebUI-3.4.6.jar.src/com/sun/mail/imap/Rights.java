/*     */ package com.sun.mail.imap;
/*     */ 
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
/*     */ public class Rights
/*     */   implements Cloneable
/*     */ {
/*  78 */   private boolean[] rights = new boolean[128];
/*     */ 
/*     */   
/*     */   public Rights() {}
/*     */   
/*     */   public static final class Right
/*     */   {
/*  85 */     private static Right[] cache = new Right[128];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  91 */     public static final Right LOOKUP = getInstance('l');
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  97 */     public static final Right READ = getInstance('r');
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 102 */     public static final Right KEEP_SEEN = getInstance('s');
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 107 */     public static final Right WRITE = getInstance('w');
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 112 */     public static final Right INSERT = getInstance('i');
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 118 */     public static final Right POST = getInstance('p');
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 124 */     public static final Right CREATE = getInstance('c');
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 129 */     public static final Right DELETE = getInstance('d');
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 134 */     public static final Right ADMINISTER = getInstance('a');
/*     */ 
/*     */     
/*     */     char right;
/*     */ 
/*     */ 
/*     */     
/*     */     private Right(char right) {
/* 142 */       if (right >= '')
/* 143 */         throw new IllegalArgumentException("Right must be ASCII"); 
/* 144 */       this.right = right;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static synchronized Right getInstance(char right) {
/* 152 */       if (right >= '')
/* 153 */         throw new IllegalArgumentException("Right must be ASCII"); 
/* 154 */       if (cache[right] == null)
/* 155 */         cache[right] = new Right(right); 
/* 156 */       return cache[right];
/*     */     }
/*     */     
/*     */     public String toString() {
/* 160 */       return String.valueOf(this.right);
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
/*     */   public Rights(Rights rights) {
/* 176 */     System.arraycopy(rights.rights, 0, this.rights, 0, this.rights.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Rights(String rights) {
/* 185 */     for (int i = 0; i < rights.length(); i++) {
/* 186 */       add(Right.getInstance(rights.charAt(i)));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Rights(Right right) {
/* 195 */     this.rights[right.right] = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(Right right) {
/* 204 */     this.rights[right.right] = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(Rights rights) {
/* 214 */     for (int i = 0; i < rights.rights.length; i++) {
/* 215 */       if (rights.rights[i]) {
/* 216 */         this.rights[i] = true;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(Right right) {
/* 225 */     this.rights[right.right] = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(Rights rights) {
/* 235 */     for (int i = 0; i < rights.rights.length; i++) {
/* 236 */       if (rights.rights[i]) {
/* 237 */         this.rights[i] = false;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(Right right) {
/* 246 */     return this.rights[right.right];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(Rights rights) {
/* 257 */     for (int i = 0; i < rights.rights.length; i++) {
/* 258 */       if (rights.rights[i] && !this.rights[i]) {
/* 259 */         return false;
/*     */       }
/*     */     } 
/* 262 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 271 */     if (!(obj instanceof Rights)) {
/* 272 */       return false;
/*     */     }
/* 274 */     Rights rights = (Rights)obj;
/*     */     
/* 276 */     for (int i = 0; i < rights.rights.length; i++) {
/* 277 */       if (rights.rights[i] != this.rights[i])
/* 278 */         return false; 
/*     */     } 
/* 280 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 289 */     int hash = 0;
/* 290 */     for (int i = 0; i < this.rights.length; i++) {
/* 291 */       if (this.rights[i])
/* 292 */         hash++; 
/* 293 */     }  return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Right[] getRights() {
/* 303 */     Vector v = new Vector();
/* 304 */     for (int i = 0; i < this.rights.length; i++) {
/* 305 */       if (this.rights[i])
/* 306 */         v.addElement(Right.getInstance((char)i)); 
/* 307 */     }  Right[] rights = new Right[v.size()];
/* 308 */     v.copyInto((Object[])rights);
/* 309 */     return rights;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 316 */     Rights r = null;
/*     */     try {
/* 318 */       r = (Rights)super.clone();
/* 319 */       r.rights = new boolean[128];
/* 320 */       System.arraycopy(this.rights, 0, r.rights, 0, this.rights.length);
/* 321 */     } catch (CloneNotSupportedException cex) {}
/*     */ 
/*     */     
/* 324 */     return r;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 328 */     StringBuffer sb = new StringBuffer();
/* 329 */     for (int i = 0; i < this.rights.length; i++) {
/* 330 */       if (this.rights[i])
/* 331 */         sb.append((char)i); 
/* 332 */     }  return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\Rights.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */