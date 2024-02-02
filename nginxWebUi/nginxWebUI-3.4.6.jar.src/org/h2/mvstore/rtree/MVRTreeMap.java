/*     */ package org.h2.mvstore.rtree;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.h2.mvstore.CursorPos;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.mvstore.Page;
/*     */ import org.h2.mvstore.RootReference;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MVRTreeMap<V>
/*     */   extends MVMap<Spatial, V>
/*     */ {
/*     */   private final SpatialDataType keyType;
/*     */   private boolean quadraticSplit;
/*     */   
/*     */   public MVRTreeMap(Map<String, Object> paramMap, SpatialDataType paramSpatialDataType, DataType<V> paramDataType) {
/*  35 */     super(paramMap, (DataType)paramSpatialDataType, paramDataType);
/*  36 */     this.keyType = paramSpatialDataType;
/*  37 */     this.quadraticSplit = Boolean.parseBoolean(String.valueOf(paramMap.get("quadraticSplit")));
/*     */   }
/*     */   
/*     */   private MVRTreeMap(MVRTreeMap<V> paramMVRTreeMap) {
/*  41 */     super(paramMVRTreeMap);
/*  42 */     this.keyType = paramMVRTreeMap.keyType;
/*  43 */     this.quadraticSplit = paramMVRTreeMap.quadraticSplit;
/*     */   }
/*     */ 
/*     */   
/*     */   public MVRTreeMap<V> cloneIt() {
/*  48 */     return new MVRTreeMap(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RTreeCursor<V> findIntersectingKeys(Spatial paramSpatial) {
/*  58 */     return new IntersectsRTreeCursor<>(getRootPage(), paramSpatial, this.keyType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RTreeCursor<V> findContainedKeys(Spatial paramSpatial) {
/*  69 */     return new ContainsRTreeCursor<>(getRootPage(), paramSpatial, this.keyType);
/*     */   }
/*     */   
/*     */   private boolean contains(Page<Spatial, V> paramPage, int paramInt, Object paramObject) {
/*  73 */     return this.keyType.contains(paramPage.getKey(paramInt), paramObject);
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
/*     */   public V get(Page<Spatial, V> paramPage, Spatial paramSpatial) {
/*  85 */     int i = paramPage.getKeyCount();
/*  86 */     if (!paramPage.isLeaf()) {
/*  87 */       for (byte b = 0; b < i; b++) {
/*  88 */         if (contains(paramPage, b, paramSpatial)) {
/*  89 */           V v = get(paramPage.getChildPage(b), paramSpatial);
/*  90 */           if (v != null) {
/*  91 */             return v;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } else {
/*  96 */       for (byte b = 0; b < i; b++) {
/*  97 */         if (this.keyType.equals(paramPage.getKey(b), paramSpatial)) {
/*  98 */           return (V)paramPage.getValue(b);
/*     */         }
/*     */       } 
/*     */     } 
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V remove(Object paramObject) {
/* 113 */     return operate((Spatial)paramObject, (V)null, MVMap.DecisionMaker.REMOVE);
/*     */   }
/*     */ 
/*     */   
/*     */   public V operate(Spatial paramSpatial, V paramV, MVMap.DecisionMaker<? super V> paramDecisionMaker) {
/* 118 */     byte b = 0;
/* 119 */     ArrayList<Page> arrayList = isPersistent() ? new ArrayList() : null;
/*     */     while (true) {
/* 121 */       RootReference rootReference = flushAndGetRoot();
/* 122 */       if (b++ == 0 && !rootReference.isLockedByCurrentThread()) {
/* 123 */         beforeWrite();
/*     */       }
/* 125 */       Page<Spatial, V> page = rootReference.root;
/* 126 */       if (arrayList != null && page.getTotalCount() > 0L) {
/* 127 */         arrayList.add(page);
/*     */       }
/* 129 */       page = page.copy();
/* 130 */       V v = operate(page, paramSpatial, paramV, paramDecisionMaker, (Collection)arrayList);
/* 131 */       if (!page.isLeaf() && page.getTotalCount() == 0L) {
/* 132 */         if (arrayList != null) {
/* 133 */           arrayList.add(page);
/*     */         }
/* 135 */         page = createEmptyLeaf();
/* 136 */       } else if (page.getKeyCount() > this.store.getKeysPerPage() || (page.getMemory() > this.store.getMaxPageSize() && page
/* 137 */         .getKeyCount() > 3)) {
/*     */ 
/*     */         
/* 140 */         long l = page.getTotalCount();
/* 141 */         Page<Spatial, V> page1 = split(page);
/* 142 */         Spatial spatial1 = getBounds(page);
/* 143 */         Spatial spatial2 = getBounds(page1);
/* 144 */         Spatial[] arrayOfSpatial = (Spatial[])page.createKeyStorage(2);
/* 145 */         arrayOfSpatial[0] = spatial1;
/* 146 */         arrayOfSpatial[1] = spatial2;
/* 147 */         Page.PageReference[] arrayOfPageReference = Page.createRefStorage(3);
/* 148 */         arrayOfPageReference[0] = new Page.PageReference(page);
/* 149 */         arrayOfPageReference[1] = new Page.PageReference(page1);
/* 150 */         arrayOfPageReference[2] = Page.PageReference.empty();
/* 151 */         page = Page.createNode(this, (Object[])arrayOfSpatial, arrayOfPageReference, l, 0);
/* 152 */         if (isPersistent()) {
/* 153 */           this.store.registerUnsavedMemory(page.getMemory());
/*     */         }
/*     */       } 
/*     */       
/* 157 */       if (arrayList == null) {
/* 158 */         if (updateRoot(rootReference, page, b)) {
/* 159 */           return v;
/*     */         }
/*     */       } else {
/* 162 */         RootReference rootReference1 = tryLock(rootReference, b);
/* 163 */         if (rootReference1 != null) {
/*     */           try {
/* 165 */             long l = rootReference1.version;
/* 166 */             int i = 0;
/* 167 */             for (Page page1 : arrayList) {
/* 168 */               if (!page1.isRemoved()) {
/* 169 */                 i += page1.removePage(l);
/*     */               }
/*     */             } 
/* 172 */             this.store.registerUnsavedMemory(i);
/*     */           } finally {
/* 174 */             unlockRoot(page);
/*     */           } 
/* 176 */           return v;
/*     */         } 
/* 178 */         arrayList.clear();
/*     */       } 
/* 180 */       paramDecisionMaker.reset();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private V operate(Page<Spatial, V> paramPage, Spatial paramSpatial, V paramV, MVMap.DecisionMaker<? super V> paramDecisionMaker, Collection<Page<Spatial, V>> paramCollection) {
/*     */     V v;
/* 187 */     if (paramPage.isLeaf()) {
/* 188 */       byte b2 = -1;
/* 189 */       int i = paramPage.getKeyCount();
/* 190 */       for (byte b3 = 0; b3 < i; b3++) {
/* 191 */         if (this.keyType.equals(paramPage.getKey(b3), paramSpatial)) {
/* 192 */           b2 = b3;
/*     */         }
/*     */       } 
/* 195 */       v = (V)((b2 < 0) ? null : paramPage.getValue(b2));
/* 196 */       MVMap.Decision decision = paramDecisionMaker.decide(v, paramV);
/* 197 */       switch (decision) {
/*     */ 
/*     */ 
/*     */         
/*     */         case REMOVE:
/* 202 */           if (b2 >= 0) {
/* 203 */             paramPage.remove(b2);
/*     */           }
/*     */           break;
/*     */         case PUT:
/* 207 */           paramV = (V)paramDecisionMaker.selectValue(v, paramV);
/* 208 */           if (b2 < 0) {
/* 209 */             paramPage.insertLeaf(paramPage.getKeyCount(), paramSpatial, paramV); break;
/*     */           } 
/* 211 */           paramPage.setKey(b2, paramSpatial);
/* 212 */           paramPage.setValue(b2, paramV);
/*     */           break;
/*     */       } 
/*     */       
/* 216 */       return v;
/*     */     } 
/*     */ 
/*     */     
/* 220 */     byte b = -1;
/* 221 */     for (byte b1 = 0; b1 < paramPage.getKeyCount(); b1++) {
/* 222 */       if (contains(paramPage, b1, paramSpatial)) {
/* 223 */         Page<Spatial, V> page1 = paramPage.getChildPage(b1);
/* 224 */         if (get(page1, paramSpatial) != null) {
/* 225 */           b = b1;
/*     */           break;
/*     */         } 
/* 228 */         if (b < 0) {
/* 229 */           b = b1;
/*     */         }
/*     */       } 
/*     */     } 
/* 233 */     if (b < 0) {
/*     */       
/* 235 */       float f = Float.MAX_VALUE;
/* 236 */       for (byte b2 = 0; b2 < paramPage.getKeyCount(); b2++) {
/* 237 */         Object object = paramPage.getKey(b2);
/* 238 */         float f1 = this.keyType.getAreaIncrease(object, paramSpatial);
/* 239 */         if (f1 < f) {
/* 240 */           b = b2;
/* 241 */           f = f1;
/*     */         } 
/*     */       } 
/*     */     } 
/* 245 */     Page<Spatial, V> page = paramPage.getChildPage(b);
/* 246 */     if (paramCollection != null) {
/* 247 */       paramCollection.add(page);
/*     */     }
/* 249 */     page = page.copy();
/* 250 */     if (page.getKeyCount() > this.store.getKeysPerPage() || (page.getMemory() > this.store.getMaxPageSize() && page
/* 251 */       .getKeyCount() > 4)) {
/*     */       
/* 253 */       Page<Spatial, V> page1 = split(page);
/* 254 */       paramPage.setKey(b, getBounds(page));
/* 255 */       paramPage.setChild(b, page);
/* 256 */       paramPage.insertNode(b, getBounds(page1), page1);
/*     */       
/* 258 */       V v1 = operate(paramPage, paramSpatial, paramV, paramDecisionMaker, paramCollection);
/*     */     } else {
/* 260 */       v = operate(page, paramSpatial, paramV, paramDecisionMaker, paramCollection);
/* 261 */       Spatial spatial = (Spatial)paramPage.getKey(b);
/* 262 */       if (!this.keyType.contains(spatial, paramSpatial)) {
/* 263 */         spatial = this.keyType.createBoundingBox(spatial);
/* 264 */         this.keyType.increaseBounds(spatial, paramSpatial);
/* 265 */         paramPage.setKey(b, spatial);
/*     */       } 
/* 267 */       if (page.getTotalCount() > 0L) {
/* 268 */         paramPage.setChild(b, page);
/*     */       } else {
/* 270 */         paramPage.remove(b);
/*     */       } 
/*     */     } 
/* 273 */     return v;
/*     */   }
/*     */   
/*     */   private Spatial getBounds(Page<Spatial, V> paramPage) {
/* 277 */     Spatial spatial = this.keyType.createBoundingBox(paramPage.getKey(0));
/* 278 */     int i = paramPage.getKeyCount();
/* 279 */     for (byte b = 1; b < i; b++) {
/* 280 */       this.keyType.increaseBounds(spatial, paramPage.getKey(b));
/*     */     }
/* 282 */     return spatial;
/*     */   }
/*     */ 
/*     */   
/*     */   public V put(Spatial paramSpatial, V paramV) {
/* 287 */     return operate(paramSpatial, paramV, MVMap.DecisionMaker.PUT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(Spatial paramSpatial, V paramV) {
/* 298 */     operate(paramSpatial, paramV, MVMap.DecisionMaker.PUT);
/*     */   }
/*     */   
/*     */   private Page<Spatial, V> split(Page<Spatial, V> paramPage) {
/* 302 */     return this.quadraticSplit ? 
/* 303 */       splitQuadratic(paramPage) : 
/* 304 */       splitLinear(paramPage);
/*     */   }
/*     */   
/*     */   private Page<Spatial, V> splitLinear(Page<Spatial, V> paramPage) {
/* 308 */     int i = paramPage.getKeyCount();
/* 309 */     ArrayList<Object> arrayList = new ArrayList(i);
/* 310 */     for (byte b = 0; b < i; b++) {
/* 311 */       arrayList.add(paramPage.getKey(b));
/*     */     }
/* 313 */     int[] arrayOfInt = this.keyType.getExtremes(arrayList);
/* 314 */     if (arrayOfInt == null) {
/* 315 */       return splitQuadratic(paramPage);
/*     */     }
/* 317 */     Page<Spatial, V> page1 = newPage(paramPage.isLeaf());
/* 318 */     Page<Spatial, V> page2 = newPage(paramPage.isLeaf());
/* 319 */     move(paramPage, page1, arrayOfInt[0]);
/* 320 */     if (arrayOfInt[1] > arrayOfInt[0]) {
/* 321 */       arrayOfInt[1] = arrayOfInt[1] - 1;
/*     */     }
/* 323 */     move(paramPage, page2, arrayOfInt[1]);
/* 324 */     Spatial spatial1 = this.keyType.createBoundingBox(page1.getKey(0));
/* 325 */     Spatial spatial2 = this.keyType.createBoundingBox(page2.getKey(0));
/* 326 */     while (paramPage.getKeyCount() > 0) {
/* 327 */       Object object = paramPage.getKey(0);
/* 328 */       float f1 = this.keyType.getAreaIncrease(spatial1, object);
/* 329 */       float f2 = this.keyType.getAreaIncrease(spatial2, object);
/* 330 */       if (f1 < f2) {
/* 331 */         this.keyType.increaseBounds(spatial1, object);
/* 332 */         move(paramPage, page1, 0); continue;
/*     */       } 
/* 334 */       this.keyType.increaseBounds(spatial2, object);
/* 335 */       move(paramPage, page2, 0);
/*     */     } 
/*     */     
/* 338 */     while (page2.getKeyCount() > 0) {
/* 339 */       move(page2, paramPage, 0);
/*     */     }
/* 341 */     return page1;
/*     */   }
/*     */   
/*     */   private Page<Spatial, V> splitQuadratic(Page<Spatial, V> paramPage) {
/* 345 */     Page<Spatial, V> page1 = newPage(paramPage.isLeaf());
/* 346 */     Page<Spatial, V> page2 = newPage(paramPage.isLeaf());
/* 347 */     float f = Float.MIN_VALUE;
/* 348 */     byte b1 = 0, b2 = 0;
/* 349 */     int i = paramPage.getKeyCount();
/* 350 */     for (byte b3 = 0; b3 < i; b3++) {
/* 351 */       Object object = paramPage.getKey(b3);
/* 352 */       for (byte b = 0; b < i; b++) {
/* 353 */         if (b3 != b) {
/*     */ 
/*     */           
/* 356 */           Object object1 = paramPage.getKey(b);
/* 357 */           float f1 = this.keyType.getCombinedArea(object, object1);
/* 358 */           if (f1 > f) {
/* 359 */             f = f1;
/* 360 */             b1 = b3;
/* 361 */             b2 = b;
/*     */           } 
/*     */         } 
/*     */       } 
/* 365 */     }  move(paramPage, page1, b1);
/* 366 */     if (b1 < b2) {
/* 367 */       b2--;
/*     */     }
/* 369 */     move(paramPage, page2, b2);
/* 370 */     Spatial spatial1 = this.keyType.createBoundingBox(page1.getKey(0));
/* 371 */     Spatial spatial2 = this.keyType.createBoundingBox(page2.getKey(0));
/* 372 */     while (paramPage.getKeyCount() > 0) {
/* 373 */       float f1 = 0.0F, f2 = 0.0F, f3 = 0.0F;
/* 374 */       byte b4 = 0;
/* 375 */       i = paramPage.getKeyCount();
/* 376 */       for (byte b5 = 0; b5 < i; b5++) {
/* 377 */         Object object = paramPage.getKey(b5);
/* 378 */         float f4 = this.keyType.getAreaIncrease(spatial1, object);
/* 379 */         float f5 = this.keyType.getAreaIncrease(spatial2, object);
/* 380 */         float f6 = Math.abs(f4 - f5);
/* 381 */         if (f6 > f1) {
/* 382 */           f1 = f6;
/* 383 */           f2 = f4;
/* 384 */           f3 = f5;
/* 385 */           b4 = b5;
/*     */         } 
/*     */       } 
/* 388 */       if (f2 < f3) {
/* 389 */         this.keyType.increaseBounds(spatial1, paramPage.getKey(b4));
/* 390 */         move(paramPage, page1, b4); continue;
/*     */       } 
/* 392 */       this.keyType.increaseBounds(spatial2, paramPage.getKey(b4));
/* 393 */       move(paramPage, page2, b4);
/*     */     } 
/*     */     
/* 396 */     while (page2.getKeyCount() > 0) {
/* 397 */       move(page2, paramPage, 0);
/*     */     }
/* 399 */     return page1;
/*     */   }
/*     */   
/*     */   private Page<Spatial, V> newPage(boolean paramBoolean) {
/* 403 */     Page<Spatial, V> page = paramBoolean ? createEmptyLeaf() : createEmptyNode();
/* 404 */     if (isPersistent()) {
/* 405 */       this.store.registerUnsavedMemory(page.getMemory());
/*     */     }
/* 407 */     return page;
/*     */   }
/*     */   
/*     */   private static <V> void move(Page<Spatial, V> paramPage1, Page<Spatial, V> paramPage2, int paramInt) {
/* 411 */     Spatial spatial = (Spatial)paramPage1.getKey(paramInt);
/* 412 */     if (paramPage1.isLeaf()) {
/* 413 */       Object object = paramPage1.getValue(paramInt);
/* 414 */       paramPage2.insertLeaf(0, spatial, object);
/*     */     } else {
/* 416 */       Page page = paramPage1.getChildPage(paramInt);
/* 417 */       paramPage2.insertNode(0, spatial, page);
/*     */     } 
/* 419 */     paramPage1.remove(paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addNodeKeys(ArrayList<Spatial> paramArrayList, Page<Spatial, V> paramPage) {
/* 430 */     if (paramPage != null && !paramPage.isLeaf()) {
/* 431 */       int i = paramPage.getKeyCount();
/* 432 */       for (byte b = 0; b < i; b++) {
/* 433 */         paramArrayList.add(paramPage.getKey(b));
/* 434 */         addNodeKeys(paramArrayList, paramPage.getChildPage(b));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQuadraticSplit() {
/* 441 */     return this.quadraticSplit;
/*     */   }
/*     */   
/*     */   public void setQuadraticSplit(boolean paramBoolean) {
/* 445 */     this.quadraticSplit = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int getChildPageCount(Page<Spatial, V> paramPage) {
/* 450 */     return paramPage.getRawChildPageCount() - 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public static abstract class RTreeCursor<V>
/*     */     implements Iterator<Spatial>
/*     */   {
/*     */     private final Spatial filter;
/*     */     
/*     */     private CursorPos<Spatial, V> pos;
/*     */     private Spatial current;
/*     */     private final Page<Spatial, V> root;
/*     */     private boolean initialized;
/*     */     
/*     */     protected RTreeCursor(Page<Spatial, V> param1Page, Spatial param1Spatial) {
/* 465 */       this.root = param1Page;
/* 466 */       this.filter = param1Spatial;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 471 */       if (!this.initialized) {
/*     */         
/* 473 */         this.pos = new CursorPos(this.root, 0, null);
/* 474 */         fetchNext();
/* 475 */         this.initialized = true;
/*     */       } 
/* 477 */       return (this.current != null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void skip(long param1Long) {
/* 487 */       while (hasNext() && param1Long-- > 0L) {
/* 488 */         fetchNext();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public Spatial next() {
/* 494 */       if (!hasNext()) {
/* 495 */         return null;
/*     */       }
/* 497 */       Spatial spatial = this.current;
/* 498 */       fetchNext();
/* 499 */       return spatial;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void fetchNext() {
/* 506 */       while (this.pos != null) {
/* 507 */         Page page = this.pos.page;
/* 508 */         if (page.isLeaf()) {
/* 509 */           while (this.pos.index < page.getKeyCount()) {
/* 510 */             Spatial spatial = (Spatial)page.getKey(this.pos.index++);
/* 511 */             if (this.filter == null || check(true, spatial, this.filter)) {
/* 512 */               this.current = spatial;
/*     */               return;
/*     */             } 
/*     */           } 
/*     */         } else {
/* 517 */           boolean bool = false;
/* 518 */           while (this.pos.index < page.getKeyCount()) {
/* 519 */             int i = this.pos.index++;
/* 520 */             Spatial spatial = (Spatial)page.getKey(i);
/* 521 */             if (this.filter == null || check(false, spatial, this.filter)) {
/* 522 */               Page page1 = this.pos.page.getChildPage(i);
/* 523 */               this.pos = new CursorPos(page1, 0, this.pos);
/* 524 */               bool = true;
/*     */               break;
/*     */             } 
/*     */           } 
/* 528 */           if (bool) {
/*     */             continue;
/*     */           }
/*     */         } 
/*     */         
/* 533 */         this.pos = this.pos.parent;
/*     */       } 
/* 535 */       this.current = null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected abstract boolean check(boolean param1Boolean, Spatial param1Spatial1, Spatial param1Spatial2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class IntersectsRTreeCursor<V>
/*     */     extends RTreeCursor<V>
/*     */   {
/*     */     private final SpatialDataType keyType;
/*     */ 
/*     */ 
/*     */     
/*     */     public IntersectsRTreeCursor(Page<Spatial, V> param1Page, Spatial param1Spatial, SpatialDataType param1SpatialDataType) {
/* 553 */       super(param1Page, param1Spatial);
/* 554 */       this.keyType = param1SpatialDataType;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected boolean check(boolean param1Boolean, Spatial param1Spatial1, Spatial param1Spatial2) {
/* 560 */       return this.keyType.isOverlap(param1Spatial1, param1Spatial2);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class ContainsRTreeCursor<V> extends RTreeCursor<V> {
/*     */     private final SpatialDataType keyType;
/*     */     
/*     */     public ContainsRTreeCursor(Page<Spatial, V> param1Page, Spatial param1Spatial, SpatialDataType param1SpatialDataType) {
/* 568 */       super(param1Page, param1Spatial);
/* 569 */       this.keyType = param1SpatialDataType;
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean check(boolean param1Boolean, Spatial param1Spatial1, Spatial param1Spatial2) {
/* 574 */       return param1Boolean ? this.keyType
/* 575 */         .isInside(param1Spatial1, param1Spatial2) : this.keyType
/* 576 */         .isOverlap(param1Spatial1, param1Spatial2);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getType() {
/* 582 */     return "rtree";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Builder<V>
/*     */     extends MVMap.BasicBuilder<MVRTreeMap<V>, Spatial, V>
/*     */   {
/* 592 */     private int dimensions = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder() {
/* 598 */       setKeyType((DataType)new SpatialDataType(this.dimensions));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<V> dimensions(int param1Int) {
/* 608 */       this.dimensions = param1Int;
/* 609 */       setKeyType((DataType)new SpatialDataType(param1Int));
/* 610 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<V> valueType(DataType<? super V> param1DataType) {
/* 621 */       setValueType(param1DataType);
/* 622 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public MVRTreeMap<V> create(Map<String, Object> param1Map) {
/* 627 */       return new MVRTreeMap<>(param1Map, (SpatialDataType)getKeyType(), getValueType());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\rtree\MVRTreeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */