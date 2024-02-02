/*     */ package org.h2.mvstore;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Cursor<K, V>
/*     */   implements Iterator<K>
/*     */ {
/*     */   private final boolean reverse;
/*     */   private final K to;
/*     */   private CursorPos<K, V> cursorPos;
/*     */   private CursorPos<K, V> keeper;
/*     */   private K current;
/*     */   private K last;
/*     */   private V lastValue;
/*     */   private Page<K, V> lastPage;
/*     */   
/*     */   public Cursor(RootReference<K, V> paramRootReference, K paramK1, K paramK2) {
/*  29 */     this(paramRootReference, paramK1, paramK2, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Cursor(RootReference<K, V> paramRootReference, K paramK1, K paramK2, boolean paramBoolean) {
/*  39 */     this.lastPage = paramRootReference.root;
/*  40 */     this.cursorPos = traverseDown(this.lastPage, paramK1, paramBoolean);
/*  41 */     this.to = paramK2;
/*  42 */     this.reverse = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/*  47 */     if (this.cursorPos != null) {
/*  48 */       byte b = this.reverse ? -1 : 1;
/*  49 */       while (this.current == null) {
/*  50 */         Page<K, V> page = this.cursorPos.page;
/*  51 */         int i = this.cursorPos.index;
/*  52 */         if (this.reverse ? (i < 0) : (i >= upperBound(page))) {
/*     */           
/*  54 */           CursorPos<K, V> cursorPos = this.cursorPos;
/*  55 */           this.cursorPos = this.cursorPos.parent;
/*  56 */           if (this.cursorPos == null) {
/*  57 */             return false;
/*     */           }
/*  59 */           cursorPos.parent = this.keeper;
/*  60 */           this.keeper = cursorPos;
/*     */         } else {
/*     */           
/*  63 */           while (!page.isLeaf()) {
/*  64 */             page = page.getChildPage(i);
/*  65 */             i = this.reverse ? (upperBound(page) - 1) : 0;
/*  66 */             if (this.keeper == null) {
/*  67 */               this.cursorPos = new CursorPos<>(page, i, this.cursorPos); continue;
/*     */             } 
/*  69 */             CursorPos<K, V> cursorPos = this.keeper;
/*  70 */             this.keeper = this.keeper.parent;
/*  71 */             cursorPos.parent = this.cursorPos;
/*  72 */             cursorPos.page = page;
/*  73 */             cursorPos.index = i;
/*  74 */             this.cursorPos = cursorPos;
/*     */           } 
/*     */           
/*  77 */           if (this.reverse ? (i >= 0) : (i < page.getKeyCount())) {
/*  78 */             K k = page.getKey(i);
/*  79 */             if (this.to != null && Integer.signum(page.map.getKeyType().compare(k, this.to)) == b) {
/*  80 */               return false;
/*     */             }
/*  82 */             this.current = this.last = k;
/*  83 */             this.lastValue = page.getValue(i);
/*  84 */             this.lastPage = page;
/*     */           } 
/*     */         } 
/*  87 */         this.cursorPos.index += b;
/*     */       } 
/*     */     } 
/*  90 */     return (this.current != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public K next() {
/*  95 */     if (!hasNext()) {
/*  96 */       throw new NoSuchElementException();
/*     */     }
/*  98 */     this.current = null;
/*  99 */     return this.last;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public K getKey() {
/* 108 */     return this.last;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V getValue() {
/* 117 */     return this.lastValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Page<K, V> getPage() {
/* 127 */     return this.lastPage;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void skip(long paramLong) {
/* 137 */     if (paramLong < 10L) {
/* 138 */       while (paramLong-- > 0L && hasNext()) {
/* 139 */         next();
/*     */       }
/* 141 */     } else if (hasNext()) {
/* 142 */       assert this.cursorPos != null;
/* 143 */       CursorPos<K, V> cursorPos1 = this.cursorPos;
/*     */       CursorPos<K, V> cursorPos2;
/* 145 */       for (; (cursorPos2 = cursorPos1.parent) != null; cursorPos1 = cursorPos2);
/* 146 */       Page<K, V> page = cursorPos1.page;
/* 147 */       MVMap<K, V> mVMap = page.map;
/* 148 */       long l = mVMap.getKeyIndex(next());
/* 149 */       this.last = mVMap.getKey(l + (this.reverse ? -paramLong : paramLong));
/* 150 */       this.cursorPos = traverseDown(page, this.last, this.reverse);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <K, V> CursorPos<K, V> traverseDown(Page<K, V> paramPage, K paramK, boolean paramBoolean) {
/* 170 */     CursorPos<K, V> cursorPos = (paramK != null) ? CursorPos.<K, V>traverseDown(paramPage, paramK) : (paramBoolean ? paramPage.getAppendCursorPos(null) : paramPage.getPrependCursorPos(null));
/* 171 */     int i = cursorPos.index;
/* 172 */     if (i < 0) {
/* 173 */       i ^= 0xFFFFFFFF;
/* 174 */       if (paramBoolean) {
/* 175 */         i--;
/*     */       }
/* 177 */       cursorPos.index = i;
/*     */     } 
/* 179 */     return cursorPos;
/*     */   }
/*     */   
/*     */   private static <K, V> int upperBound(Page<K, V> paramPage) {
/* 183 */     return paramPage.isLeaf() ? paramPage.getKeyCount() : paramPage.map.getChildPageCount(paramPage);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\Cursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */