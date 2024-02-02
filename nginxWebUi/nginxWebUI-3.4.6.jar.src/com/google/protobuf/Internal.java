/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.AbstractList;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.RandomAccess;
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
/*     */ public final class Internal
/*     */ {
/*  58 */   static final Charset UTF_8 = Charset.forName("UTF-8");
/*  59 */   static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
/*     */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*     */   
/*     */   static <T> T checkNotNull(T obj) {
/*  63 */     if (obj == null) {
/*  64 */       throw new NullPointerException();
/*     */     }
/*  66 */     return obj;
/*     */   }
/*     */ 
/*     */   
/*     */   static <T> T checkNotNull(T obj, String message) {
/*  71 */     if (obj == null) {
/*  72 */       throw new NullPointerException(message);
/*     */     }
/*  74 */     return obj;
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
/*     */   public static String stringDefaultValue(String bytes) {
/* 100 */     return new String(bytes.getBytes(ISO_8859_1), UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteString bytesDefaultValue(String bytes) {
/* 111 */     return ByteString.copyFrom(bytes.getBytes(ISO_8859_1));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] byteArrayDefaultValue(String bytes) {
/* 119 */     return bytes.getBytes(ISO_8859_1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuffer byteBufferDefaultValue(String bytes) {
/* 128 */     return ByteBuffer.wrap(byteArrayDefaultValue(bytes));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuffer copyByteBuffer(ByteBuffer source) {
/* 139 */     ByteBuffer temp = source.duplicate();
/*     */ 
/*     */     
/* 142 */     temp.clear();
/* 143 */     ByteBuffer result = ByteBuffer.allocate(temp.capacity());
/* 144 */     result.put(temp);
/* 145 */     result.clear();
/* 146 */     return result;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isValidUtf8(ByteString byteString) {
/* 176 */     return byteString.isValidUtf8();
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isValidUtf8(byte[] byteArray) {
/* 181 */     return Utf8.isValidUtf8(byteArray);
/*     */   }
/*     */ 
/*     */   
/*     */   public static byte[] toByteArray(String value) {
/* 186 */     return value.getBytes(UTF_8);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String toStringUtf8(byte[] bytes) {
/* 191 */     return new String(bytes, UTF_8);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int hashLong(long n) {
/* 225 */     return (int)(n ^ n >>> 32L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int hashBoolean(boolean b) {
/* 234 */     return b ? 1231 : 1237;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int hashEnum(EnumLite e) {
/* 245 */     return e.getNumber();
/*     */   }
/*     */ 
/*     */   
/*     */   public static int hashEnumList(List<? extends EnumLite> list) {
/* 250 */     int hash = 1;
/* 251 */     for (EnumLite e : list) {
/* 252 */       hash = 31 * hash + hashEnum(e);
/*     */     }
/* 254 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean equals(List<byte[]> a, List<byte[]> b) {
/* 259 */     if (a.size() != b.size()) return false; 
/* 260 */     for (int i = 0; i < a.size(); i++) {
/* 261 */       if (!Arrays.equals(a.get(i), b.get(i))) {
/* 262 */         return false;
/*     */       }
/*     */     } 
/* 265 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int hashCode(List<byte[]> list) {
/* 270 */     int hash = 1;
/* 271 */     for (byte[] bytes : list) {
/* 272 */       hash = 31 * hash + hashCode(bytes);
/*     */     }
/* 274 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int hashCode(byte[] bytes) {
/* 283 */     return hashCode(bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int hashCode(byte[] bytes, int offset, int length) {
/* 292 */     int h = partialHash(length, bytes, offset, length);
/* 293 */     return (h == 0) ? 1 : h;
/*     */   }
/*     */ 
/*     */   
/*     */   static int partialHash(int h, byte[] bytes, int offset, int length) {
/* 298 */     for (int i = offset; i < offset + length; i++) {
/* 299 */       h = h * 31 + bytes[i];
/*     */     }
/* 301 */     return h;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean equalsByteBuffer(ByteBuffer a, ByteBuffer b) {
/* 306 */     if (a.capacity() != b.capacity()) {
/* 307 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 311 */     return a.duplicate().clear().equals(b.duplicate().clear());
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean equalsByteBuffer(List<ByteBuffer> a, List<ByteBuffer> b) {
/* 316 */     if (a.size() != b.size()) {
/* 317 */       return false;
/*     */     }
/* 319 */     for (int i = 0; i < a.size(); i++) {
/* 320 */       if (!equalsByteBuffer(a.get(i), b.get(i))) {
/* 321 */         return false;
/*     */       }
/*     */     } 
/* 324 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int hashCodeByteBuffer(List<ByteBuffer> list) {
/* 329 */     int hash = 1;
/* 330 */     for (ByteBuffer bytes : list) {
/* 331 */       hash = 31 * hash + hashCodeByteBuffer(bytes);
/*     */     }
/* 333 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int hashCodeByteBuffer(ByteBuffer bytes) {
/* 340 */     if (bytes.hasArray()) {
/*     */       
/* 342 */       int i = partialHash(bytes.capacity(), bytes.array(), bytes.arrayOffset(), bytes.capacity());
/* 343 */       return (i == 0) ? 1 : i;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 348 */     int bufferSize = (bytes.capacity() > 4096) ? 4096 : bytes.capacity();
/* 349 */     byte[] buffer = new byte[bufferSize];
/* 350 */     ByteBuffer duplicated = bytes.duplicate();
/* 351 */     duplicated.clear();
/* 352 */     int h = bytes.capacity();
/* 353 */     while (duplicated.remaining() > 0) {
/*     */       
/* 355 */       int length = (duplicated.remaining() <= bufferSize) ? duplicated.remaining() : bufferSize;
/* 356 */       duplicated.get(buffer, 0, length);
/* 357 */       h = partialHash(h, buffer, 0, length);
/*     */     } 
/* 359 */     return (h == 0) ? 1 : h;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends MessageLite> T getDefaultInstance(Class<T> clazz) {
/*     */     try {
/* 366 */       Method method = clazz.getMethod("getDefaultInstance", new Class[0]);
/* 367 */       return (T)method.invoke(method, new Object[0]);
/* 368 */     } catch (Exception e) {
/* 369 */       throw new RuntimeException("Failed to get default instance for " + clazz, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 375 */   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*     */ 
/*     */   
/* 378 */   public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.wrap(EMPTY_BYTE_ARRAY);
/*     */ 
/*     */ 
/*     */   
/* 382 */   public static final CodedInputStream EMPTY_CODED_INPUT_STREAM = CodedInputStream.newInstance(EMPTY_BYTE_ARRAY);
/*     */ 
/*     */ 
/*     */   
/*     */   static Object mergeMessage(Object destination, Object source) {
/* 387 */     return ((MessageLite)destination).toBuilder().mergeFrom((MessageLite)source).buildPartial();
/*     */   }
/*     */   
/*     */   public static interface EnumLite {
/*     */     int getNumber(); }
/*     */   
/*     */   public static interface EnumLiteMap<T extends EnumLite> {
/*     */     T findValueByNumber(int param1Int); }
/*     */   
/*     */   public static interface EnumVerifier {
/*     */     boolean isInRange(int param1Int);
/*     */   }
/*     */   
/*     */   public static class ListAdapter<F, T> extends AbstractList<T> {
/*     */     private final List<F> fromList;
/*     */     private final Converter<F, T> converter;
/*     */     
/*     */     public ListAdapter(List<F> fromList, Converter<F, T> converter) {
/* 405 */       this.fromList = fromList;
/* 406 */       this.converter = converter;
/*     */     }
/*     */ 
/*     */     
/*     */     public T get(int index) {
/* 411 */       return this.converter.convert(this.fromList.get(index));
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 416 */       return this.fromList.size();
/*     */     }
/*     */     
/*     */     public static interface Converter<F, T>
/*     */     {
/*     */       T convert(F param2F);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class MapAdapter<K, V, RealValue>
/*     */     extends AbstractMap<K, V> {
/*     */     private final Map<K, RealValue> realMap;
/*     */     private final Converter<RealValue, V> valueConverter;
/*     */     
/*     */     public static <T extends Internal.EnumLite> Converter<Integer, T> newEnumConverter(final Internal.EnumLiteMap<T> enumMap, final T unrecognizedValue) {
/* 431 */       return new Converter<Integer, T>()
/*     */         {
/*     */           public T doForward(Integer value) {
/* 434 */             T result = (T)enumMap.findValueByNumber(value.intValue());
/* 435 */             return (result == null) ? (T)unrecognizedValue : result;
/*     */           }
/*     */ 
/*     */           
/*     */           public Integer doBackward(T value) {
/* 440 */             return Integer.valueOf(value.getNumber());
/*     */           }
/*     */         };
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public MapAdapter(Map<K, RealValue> realMap, Converter<RealValue, V> valueConverter) {
/* 449 */       this.realMap = realMap;
/* 450 */       this.valueConverter = valueConverter;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public V get(Object key) {
/* 456 */       RealValue result = this.realMap.get(key);
/* 457 */       if (result == null) {
/* 458 */         return null;
/*     */       }
/* 460 */       return this.valueConverter.doForward(result);
/*     */     }
/*     */ 
/*     */     
/*     */     public V put(K key, V value) {
/* 465 */       RealValue oldValue = this.realMap.put(key, this.valueConverter.doBackward(value));
/* 466 */       if (oldValue == null) {
/* 467 */         return null;
/*     */       }
/* 469 */       return this.valueConverter.doForward(oldValue);
/*     */     } public static interface Converter<A, B> {
/*     */       B doForward(A param2A);
/*     */       A doBackward(B param2B); }
/*     */     public Set<Map.Entry<K, V>> entrySet() {
/* 474 */       return new SetAdapter(this.realMap.entrySet());
/*     */     }
/*     */     
/*     */     private class SetAdapter
/*     */       extends AbstractSet<Map.Entry<K, V>>
/*     */     {
/*     */       public SetAdapter(Set<Map.Entry<K, RealValue>> realSet) {
/* 481 */         this.realSet = realSet;
/*     */       }
/*     */       private final Set<Map.Entry<K, RealValue>> realSet;
/*     */       
/*     */       public Iterator<Map.Entry<K, V>> iterator() {
/* 486 */         return new Internal.MapAdapter.IteratorAdapter(this.realSet.iterator());
/*     */       }
/*     */ 
/*     */       
/*     */       public int size() {
/* 491 */         return this.realSet.size();
/*     */       }
/*     */     }
/*     */     
/*     */     private class IteratorAdapter implements Iterator<Map.Entry<K, V>> {
/*     */       private final Iterator<Map.Entry<K, RealValue>> realIterator;
/*     */       
/*     */       public IteratorAdapter(Iterator<Map.Entry<K, RealValue>> realIterator) {
/* 499 */         this.realIterator = realIterator;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean hasNext() {
/* 504 */         return this.realIterator.hasNext();
/*     */       }
/*     */ 
/*     */       
/*     */       public Map.Entry<K, V> next() {
/* 509 */         return new Internal.MapAdapter.EntryAdapter(this.realIterator.next());
/*     */       }
/*     */ 
/*     */       
/*     */       public void remove() {
/* 514 */         this.realIterator.remove();
/*     */       }
/*     */     }
/*     */     
/*     */     private class EntryAdapter implements Map.Entry<K, V> {
/*     */       private final Map.Entry<K, RealValue> realEntry;
/*     */       
/*     */       public EntryAdapter(Map.Entry<K, RealValue> realEntry) {
/* 522 */         this.realEntry = realEntry;
/*     */       }
/*     */ 
/*     */       
/*     */       public K getKey() {
/* 527 */         return this.realEntry.getKey();
/*     */       }
/*     */ 
/*     */       
/*     */       public V getValue() {
/* 532 */         return (V)Internal.MapAdapter.this.valueConverter.doForward(this.realEntry.getValue());
/*     */       }
/*     */ 
/*     */       
/*     */       public V setValue(V value) {
/* 537 */         RealValue oldValue = this.realEntry.setValue((RealValue)Internal.MapAdapter.this.valueConverter.doBackward(value));
/* 538 */         if (oldValue == null) {
/* 539 */           return null;
/*     */         }
/* 541 */         return (V)Internal.MapAdapter.this.valueConverter.doForward(oldValue);
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean equals(Object o) {
/* 546 */         if (o == this) {
/* 547 */           return true;
/*     */         }
/* 549 */         if (!(o instanceof Map.Entry)) {
/* 550 */           return false;
/*     */         }
/*     */         
/* 553 */         Map.Entry<?, ?> other = (Map.Entry<?, ?>)o;
/* 554 */         return (getKey().equals(other.getKey()) && getValue().equals(getValue()));
/*     */       }
/*     */ 
/*     */       
/*     */       public int hashCode() {
/* 559 */         return this.realEntry.hashCode();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface ProtobufList<E> extends List<E>, RandomAccess {
/*     */     void makeImmutable();
/*     */     
/*     */     boolean isModifiable();
/*     */     
/*     */     ProtobufList<E> mutableCopyWithCapacity(int param1Int);
/*     */   }
/*     */   
/*     */   public static interface IntList extends ProtobufList<Integer> {
/*     */     int getInt(int param1Int);
/*     */     
/*     */     void addInt(int param1Int);
/*     */     
/*     */     int setInt(int param1Int1, int param1Int2);
/*     */     
/*     */     IntList mutableCopyWithCapacity(int param1Int);
/*     */   }
/*     */   
/*     */   public static interface BooleanList extends ProtobufList<Boolean> {
/*     */     boolean getBoolean(int param1Int);
/*     */     
/*     */     void addBoolean(boolean param1Boolean);
/*     */     
/*     */     boolean setBoolean(int param1Int, boolean param1Boolean);
/*     */     
/*     */     BooleanList mutableCopyWithCapacity(int param1Int);
/*     */   }
/*     */   
/*     */   public static interface LongList extends ProtobufList<Long> {
/*     */     long getLong(int param1Int);
/*     */     
/*     */     void addLong(long param1Long);
/*     */     
/*     */     long setLong(int param1Int, long param1Long);
/*     */     
/*     */     LongList mutableCopyWithCapacity(int param1Int);
/*     */   }
/*     */   
/*     */   public static interface DoubleList extends ProtobufList<Double> {
/*     */     double getDouble(int param1Int);
/*     */     
/*     */     void addDouble(double param1Double);
/*     */     
/*     */     double setDouble(int param1Int, double param1Double);
/*     */     
/*     */     DoubleList mutableCopyWithCapacity(int param1Int);
/*     */   }
/*     */   
/*     */   public static interface FloatList extends ProtobufList<Float> {
/*     */     float getFloat(int param1Int);
/*     */     
/*     */     void addFloat(float param1Float);
/*     */     
/*     */     float setFloat(int param1Int, float param1Float);
/*     */     
/*     */     FloatList mutableCopyWithCapacity(int param1Int);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Internal.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */