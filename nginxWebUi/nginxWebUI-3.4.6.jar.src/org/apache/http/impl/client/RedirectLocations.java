/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.util.AbstractList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class RedirectLocations
/*     */   extends AbstractList<Object>
/*     */ {
/*  51 */   private final Set<URI> unique = new HashSet<URI>();
/*  52 */   private final List<URI> all = new ArrayList<URI>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(URI uri) {
/*  59 */     return this.unique.contains(uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(URI uri) {
/*  66 */     this.unique.add(uri);
/*  67 */     this.all.add(uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean remove(URI uri) {
/*  74 */     boolean removed = this.unique.remove(uri);
/*  75 */     if (removed) {
/*  76 */       Iterator<URI> it = this.all.iterator();
/*  77 */       while (it.hasNext()) {
/*  78 */         URI current = it.next();
/*  79 */         if (current.equals(uri)) {
/*  80 */           it.remove();
/*     */         }
/*     */       } 
/*     */     } 
/*  84 */     return removed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<URI> getAll() {
/*  95 */     return new ArrayList<URI>(this.all);
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
/*     */   
/*     */   public URI get(int index) {
/* 111 */     return this.all.get(index);
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
/*     */   public int size() {
/* 124 */     return this.all.size();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object set(int index, Object element) {
/* 150 */     URI removed = this.all.set(index, (URI)element);
/* 151 */     this.unique.remove(removed);
/* 152 */     this.unique.add((URI)element);
/* 153 */     if (this.all.size() != this.unique.size()) {
/* 154 */       this.unique.addAll(this.all);
/*     */     }
/* 156 */     return removed;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(int index, Object element) {
/* 182 */     this.all.add(index, (URI)element);
/* 183 */     this.unique.add((URI)element);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public URI remove(int index) {
/* 201 */     URI removed = this.all.remove(index);
/* 202 */     this.unique.remove(removed);
/* 203 */     if (this.all.size() != this.unique.size()) {
/* 204 */       this.unique.addAll(this.all);
/*     */     }
/* 206 */     return removed;
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
/*     */   public boolean contains(Object o) {
/* 221 */     return this.unique.contains(o);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\RedirectLocations.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */