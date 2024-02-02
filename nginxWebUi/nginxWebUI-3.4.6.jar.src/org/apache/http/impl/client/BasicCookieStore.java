/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.TreeSet;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.CookieStore;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieIdentityComparator;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class BasicCookieStore
/*     */   implements CookieStore, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -7581093305228232025L;
/*  62 */   private final TreeSet<Cookie> cookies = new TreeSet<Cookie>((Comparator<? super Cookie>)new CookieIdentityComparator());
/*  63 */   private transient ReadWriteLock lock = new ReentrantReadWriteLock();
/*     */ 
/*     */   
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/*  67 */     stream.defaultReadObject();
/*     */ 
/*     */     
/*  70 */     this.lock = new ReentrantReadWriteLock();
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
/*     */   public void addCookie(Cookie cookie) {
/*  85 */     if (cookie != null) {
/*  86 */       this.lock.writeLock().lock();
/*     */       
/*     */       try {
/*  89 */         this.cookies.remove(cookie);
/*  90 */         if (!cookie.isExpired(new Date())) {
/*  91 */           this.cookies.add(cookie);
/*     */         }
/*     */       } finally {
/*  94 */         this.lock.writeLock().unlock();
/*     */       } 
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
/*     */   public void addCookies(Cookie[] cookies) {
/* 110 */     if (cookies != null) {
/* 111 */       for (Cookie cookie : cookies) {
/* 112 */         addCookie(cookie);
/*     */       }
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
/*     */   public List<Cookie> getCookies() {
/* 125 */     this.lock.readLock().lock();
/*     */     
/*     */     try {
/* 128 */       return new ArrayList<Cookie>(this.cookies);
/*     */     } finally {
/* 130 */       this.lock.readLock().unlock();
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
/*     */   public boolean clearExpired(Date date) {
/* 144 */     if (date == null) {
/* 145 */       return false;
/*     */     }
/* 147 */     this.lock.writeLock().lock();
/*     */     try {
/* 149 */       boolean removed = false;
/* 150 */       for (Iterator<Cookie> it = this.cookies.iterator(); it.hasNext();) {
/* 151 */         if (((Cookie)it.next()).isExpired(date)) {
/* 152 */           it.remove();
/* 153 */           removed = true;
/*     */         } 
/*     */       } 
/* 156 */       return removed;
/*     */     } finally {
/* 158 */       this.lock.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 167 */     this.lock.writeLock().lock();
/*     */     try {
/* 169 */       this.cookies.clear();
/*     */     } finally {
/* 171 */       this.lock.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 177 */     this.lock.readLock().lock();
/*     */     try {
/* 179 */       return this.cookies.toString();
/*     */     } finally {
/* 181 */       this.lock.readLock().unlock();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\BasicCookieStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */