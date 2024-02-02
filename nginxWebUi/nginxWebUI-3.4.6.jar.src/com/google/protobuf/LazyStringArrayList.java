/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.AbstractList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.RandomAccess;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LazyStringArrayList
/*     */   extends AbstractProtobufList<String>
/*     */   implements LazyStringList, RandomAccess
/*     */ {
/*  64 */   private static final LazyStringArrayList EMPTY_LIST = new LazyStringArrayList();
/*     */   
/*     */   static {
/*  67 */     EMPTY_LIST.makeImmutable();
/*     */   }
/*     */   
/*     */   static LazyStringArrayList emptyList() {
/*  71 */     return EMPTY_LIST;
/*     */   }
/*     */ 
/*     */   
/*  75 */   public static final LazyStringList EMPTY = EMPTY_LIST;
/*     */   
/*     */   private final List<Object> list;
/*     */   
/*     */   public LazyStringArrayList() {
/*  80 */     this(10);
/*     */   }
/*     */   
/*     */   public LazyStringArrayList(int initialCapacity) {
/*  84 */     this(new ArrayList(initialCapacity));
/*     */   }
/*     */   
/*     */   public LazyStringArrayList(LazyStringList from) {
/*  88 */     this.list = new ArrayList(from.size());
/*  89 */     addAll(from);
/*     */   }
/*     */   
/*     */   public LazyStringArrayList(List<String> from) {
/*  93 */     this(new ArrayList(from));
/*     */   }
/*     */   
/*     */   private LazyStringArrayList(ArrayList<Object> list) {
/*  97 */     this.list = list;
/*     */   }
/*     */ 
/*     */   
/*     */   public LazyStringArrayList mutableCopyWithCapacity(int capacity) {
/* 102 */     if (capacity < size()) {
/* 103 */       throw new IllegalArgumentException();
/*     */     }
/* 105 */     ArrayList<Object> newList = new ArrayList(capacity);
/* 106 */     newList.addAll(this.list);
/* 107 */     return new LazyStringArrayList(newList);
/*     */   }
/*     */ 
/*     */   
/*     */   public String get(int index) {
/* 112 */     Object o = this.list.get(index);
/* 113 */     if (o instanceof String)
/* 114 */       return (String)o; 
/* 115 */     if (o instanceof ByteString) {
/* 116 */       ByteString bs = (ByteString)o;
/* 117 */       String str = bs.toStringUtf8();
/* 118 */       if (bs.isValidUtf8()) {
/* 119 */         this.list.set(index, str);
/*     */       }
/* 121 */       return str;
/*     */     } 
/* 123 */     byte[] ba = (byte[])o;
/* 124 */     String s = Internal.toStringUtf8(ba);
/* 125 */     if (Internal.isValidUtf8(ba)) {
/* 126 */       this.list.set(index, s);
/*     */     }
/* 128 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 134 */     return this.list.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public String set(int index, String s) {
/* 139 */     ensureIsMutable();
/* 140 */     Object o = this.list.set(index, s);
/* 141 */     return asString(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, String element) {
/* 146 */     ensureIsMutable();
/* 147 */     this.list.add(index, element);
/* 148 */     this.modCount++;
/*     */   }
/*     */   
/*     */   private void add(int index, ByteString element) {
/* 152 */     ensureIsMutable();
/* 153 */     this.list.add(index, element);
/* 154 */     this.modCount++;
/*     */   }
/*     */   
/*     */   private void add(int index, byte[] element) {
/* 158 */     ensureIsMutable();
/* 159 */     this.list.add(index, element);
/* 160 */     this.modCount++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addAll(Collection<? extends String> c) {
/* 169 */     return addAll(size(), c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(int index, Collection<? extends String> c) {
/* 174 */     ensureIsMutable();
/*     */ 
/*     */ 
/*     */     
/* 178 */     Collection<?> collection = (c instanceof LazyStringList) ? ((LazyStringList)c).getUnderlyingElements() : c;
/* 179 */     boolean ret = this.list.addAll(index, collection);
/* 180 */     this.modCount++;
/* 181 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAllByteString(Collection<? extends ByteString> values) {
/* 186 */     ensureIsMutable();
/* 187 */     boolean ret = this.list.addAll(values);
/* 188 */     this.modCount++;
/* 189 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAllByteArray(Collection<byte[]> c) {
/* 194 */     ensureIsMutable();
/* 195 */     boolean ret = this.list.addAll(c);
/* 196 */     this.modCount++;
/* 197 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public String remove(int index) {
/* 202 */     ensureIsMutable();
/* 203 */     Object o = this.list.remove(index);
/* 204 */     this.modCount++;
/* 205 */     return asString(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 210 */     ensureIsMutable();
/* 211 */     this.list.clear();
/* 212 */     this.modCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(ByteString element) {
/* 217 */     ensureIsMutable();
/* 218 */     this.list.add(element);
/* 219 */     this.modCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(byte[] element) {
/* 224 */     ensureIsMutable();
/* 225 */     this.list.add(element);
/* 226 */     this.modCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getRaw(int index) {
/* 231 */     return this.list.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteString getByteString(int index) {
/* 236 */     Object o = this.list.get(index);
/* 237 */     ByteString b = asByteString(o);
/* 238 */     if (b != o) {
/* 239 */       this.list.set(index, b);
/*     */     }
/* 241 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getByteArray(int index) {
/* 246 */     Object o = this.list.get(index);
/* 247 */     byte[] b = asByteArray(o);
/* 248 */     if (b != o) {
/* 249 */       this.list.set(index, b);
/*     */     }
/* 251 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(int index, ByteString s) {
/* 256 */     setAndReturn(index, s);
/*     */   }
/*     */   
/*     */   private Object setAndReturn(int index, ByteString s) {
/* 260 */     ensureIsMutable();
/* 261 */     return this.list.set(index, s);
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(int index, byte[] s) {
/* 266 */     setAndReturn(index, s);
/*     */   }
/*     */   
/*     */   private Object setAndReturn(int index, byte[] s) {
/* 270 */     ensureIsMutable();
/* 271 */     return this.list.set(index, s);
/*     */   }
/*     */   
/*     */   private static String asString(Object o) {
/* 275 */     if (o instanceof String)
/* 276 */       return (String)o; 
/* 277 */     if (o instanceof ByteString) {
/* 278 */       return ((ByteString)o).toStringUtf8();
/*     */     }
/* 280 */     return Internal.toStringUtf8((byte[])o);
/*     */   }
/*     */ 
/*     */   
/*     */   private static ByteString asByteString(Object o) {
/* 285 */     if (o instanceof ByteString)
/* 286 */       return (ByteString)o; 
/* 287 */     if (o instanceof String) {
/* 288 */       return ByteString.copyFromUtf8((String)o);
/*     */     }
/* 290 */     return ByteString.copyFrom((byte[])o);
/*     */   }
/*     */ 
/*     */   
/*     */   private static byte[] asByteArray(Object o) {
/* 295 */     if (o instanceof byte[])
/* 296 */       return (byte[])o; 
/* 297 */     if (o instanceof String) {
/* 298 */       return Internal.toByteArray((String)o);
/*     */     }
/* 300 */     return ((ByteString)o).toByteArray();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<?> getUnderlyingElements() {
/* 306 */     return Collections.unmodifiableList(this.list);
/*     */   }
/*     */ 
/*     */   
/*     */   public void mergeFrom(LazyStringList other) {
/* 311 */     ensureIsMutable();
/* 312 */     for (Object o : other.getUnderlyingElements()) {
/* 313 */       if (o instanceof byte[]) {
/* 314 */         byte[] b = (byte[])o;
/*     */ 
/*     */         
/* 317 */         this.list.add(Arrays.copyOf(b, b.length)); continue;
/*     */       } 
/* 319 */       this.list.add(o);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class ByteArrayListView
/*     */     extends AbstractList<byte[]> implements RandomAccess {
/*     */     private final LazyStringArrayList list;
/*     */     
/*     */     ByteArrayListView(LazyStringArrayList list) {
/* 328 */       this.list = list;
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] get(int index) {
/* 333 */       return this.list.getByteArray(index);
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 338 */       return this.list.size();
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] set(int index, byte[] s) {
/* 343 */       Object o = this.list.setAndReturn(index, s);
/* 344 */       this.modCount++;
/* 345 */       return LazyStringArrayList.asByteArray(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public void add(int index, byte[] s) {
/* 350 */       this.list.add(index, s);
/* 351 */       this.modCount++;
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] remove(int index) {
/* 356 */       Object o = this.list.remove(index);
/* 357 */       this.modCount++;
/* 358 */       return LazyStringArrayList.asByteArray(o);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public List<byte[]> asByteArrayList() {
/* 364 */     return new ByteArrayListView(this);
/*     */   }
/*     */   
/*     */   private static class ByteStringListView extends AbstractList<ByteString> implements RandomAccess {
/*     */     private final LazyStringArrayList list;
/*     */     
/*     */     ByteStringListView(LazyStringArrayList list) {
/* 371 */       this.list = list;
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteString get(int index) {
/* 376 */       return this.list.getByteString(index);
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 381 */       return this.list.size();
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteString set(int index, ByteString s) {
/* 386 */       Object o = this.list.setAndReturn(index, s);
/* 387 */       this.modCount++;
/* 388 */       return LazyStringArrayList.asByteString(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public void add(int index, ByteString s) {
/* 393 */       this.list.add(index, s);
/* 394 */       this.modCount++;
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteString remove(int index) {
/* 399 */       Object o = this.list.remove(index);
/* 400 */       this.modCount++;
/* 401 */       return LazyStringArrayList.asByteString(o);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public List<ByteString> asByteStringList() {
/* 407 */     return new ByteStringListView(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public LazyStringList getUnmodifiableView() {
/* 412 */     if (isModifiable()) {
/* 413 */       return new UnmodifiableLazyStringList(this);
/*     */     }
/* 415 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\LazyStringArrayList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */