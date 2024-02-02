/*     */ package org.wildfly.common.net;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Objects;
/*     */ import java.util.Spliterator;
/*     */ import java.util.Spliterators;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import org.wildfly.common.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CidrAddressTable<T>
/*     */   implements Iterable<CidrAddressTable.Mapping<T>>
/*     */ {
/*  41 */   private static final Mapping[] NO_MAPPINGS = new Mapping[0];
/*     */   
/*     */   private final AtomicReference<Mapping<T>[]> mappingsRef;
/*     */   
/*     */   public CidrAddressTable() {
/*  46 */     this.mappingsRef = (AtomicReference)new AtomicReference<>(empty());
/*     */   }
/*     */   
/*     */   private CidrAddressTable(Mapping<T>[] mappings) {
/*  50 */     this.mappingsRef = (AtomicReference)new AtomicReference<>(mappings);
/*     */   }
/*     */   
/*     */   public T getOrDefault(InetAddress address, T defVal) {
/*  54 */     Assert.checkNotNullParam("address", address);
/*  55 */     Mapping<T> mapping = doGet(this.mappingsRef.get(), address.getAddress(), (address instanceof java.net.Inet4Address) ? 32 : 128, Inet.getScopeId(address));
/*  56 */     return (mapping == null) ? defVal : mapping.value;
/*     */   }
/*     */   
/*     */   public T get(InetAddress address) {
/*  60 */     return getOrDefault(address, null);
/*     */   }
/*     */   
/*     */   public T put(CidrAddress block, T value) {
/*  64 */     Assert.checkNotNullParam("block", block);
/*  65 */     Assert.checkNotNullParam("value", value);
/*  66 */     return doPut(block, null, value, true, true);
/*     */   }
/*     */   
/*     */   public T putIfAbsent(CidrAddress block, T value) {
/*  70 */     Assert.checkNotNullParam("block", block);
/*  71 */     Assert.checkNotNullParam("value", value);
/*  72 */     return doPut(block, null, value, true, false);
/*     */   }
/*     */   
/*     */   public T replaceExact(CidrAddress block, T value) {
/*  76 */     Assert.checkNotNullParam("block", block);
/*  77 */     Assert.checkNotNullParam("value", value);
/*  78 */     return doPut(block, null, value, false, true);
/*     */   }
/*     */   
/*     */   public boolean replaceExact(CidrAddress block, T expect, T update) {
/*  82 */     Assert.checkNotNullParam("block", block);
/*  83 */     Assert.checkNotNullParam("expect", expect);
/*  84 */     Assert.checkNotNullParam("update", update);
/*  85 */     return (doPut(block, expect, update, false, true) == expect);
/*     */   }
/*     */   
/*     */   public T removeExact(CidrAddress block) {
/*  89 */     Assert.checkNotNullParam("block", block);
/*  90 */     return doPut(block, null, null, false, true);
/*     */   }
/*     */   
/*     */   public boolean removeExact(CidrAddress block, T expect) {
/*  94 */     Assert.checkNotNullParam("block", block);
/*  95 */     return (doPut(block, expect, null, false, true) == expect);
/*     */   }
/*     */   
/*     */   private T doPut(CidrAddress block, T expect, T update, boolean putIfAbsent, boolean putIfPresent) {
/*  99 */     assert putIfAbsent || putIfPresent;
/* 100 */     AtomicReference<Mapping<T>[]> mappingsRef = this.mappingsRef;
/* 101 */     byte[] bytes = block.getNetworkAddress().getAddress();
/*     */     
/*     */     while (true) {
/*     */       Mapping[] arrayOfMapping2;
/*     */       T existing;
/*     */       boolean matchesExpected;
/* 107 */       Mapping[] arrayOfMapping1 = (Mapping[])mappingsRef.get();
/* 108 */       int idx = doFind((Mapping<T>[])arrayOfMapping1, bytes, block.getNetmaskBits(), block.getScopeId());
/* 109 */       if (idx < 0) {
/* 110 */         if (!putIfAbsent) {
/* 111 */           return null;
/*     */         }
/* 113 */         existing = null;
/*     */       } else {
/* 115 */         existing = (arrayOfMapping1[idx]).value;
/*     */       } 
/* 117 */       if (expect != null) {
/* 118 */         matchesExpected = Objects.equals(expect, existing);
/* 119 */         if (!matchesExpected) {
/* 120 */           return existing;
/*     */         }
/*     */       } else {
/* 123 */         matchesExpected = false;
/*     */       } 
/* 125 */       if (idx >= 0 && !putIfPresent) {
/* 126 */         return existing;
/*     */       }
/*     */       
/* 129 */       int oldLen = arrayOfMapping1.length;
/* 130 */       if (update == null) {
/* 131 */         assert idx >= 0;
/*     */         
/* 133 */         if (oldLen == 1) {
/* 134 */           arrayOfMapping2 = empty();
/*     */         } else {
/* 136 */           Mapping<T> removing = arrayOfMapping1[idx];
/* 137 */           arrayOfMapping2 = Arrays.<Mapping>copyOf(arrayOfMapping1, oldLen - 1);
/* 138 */           System.arraycopy(arrayOfMapping1, idx + 1, arrayOfMapping2, idx, oldLen - idx - 1);
/*     */           
/* 140 */           for (int i = 0; i < oldLen - 1; i++) {
/* 141 */             if ((arrayOfMapping2[i]).parent == removing) {
/* 142 */               arrayOfMapping2[i] = arrayOfMapping2[i].withNewParent(removing.parent);
/*     */             }
/*     */           } 
/*     */         } 
/* 146 */       } else if (idx >= 0) {
/*     */         
/* 148 */         arrayOfMapping2 = (Mapping[])arrayOfMapping1.clone();
/* 149 */         Mapping<T> oldMapping = arrayOfMapping1[idx];
/* 150 */         Mapping<T> newMapping = new Mapping<>(block, update, (Mapping)(arrayOfMapping1[idx]).parent);
/* 151 */         arrayOfMapping2[idx] = newMapping;
/*     */         
/* 153 */         for (int i = 0; i < oldLen; i++) {
/* 154 */           if (i != idx && (arrayOfMapping2[i]).parent == oldMapping) {
/* 155 */             arrayOfMapping2[i] = arrayOfMapping2[i].withNewParent(newMapping);
/*     */           }
/*     */         } 
/*     */       } else {
/*     */         
/* 160 */         arrayOfMapping2 = Arrays.<Mapping>copyOf(arrayOfMapping1, oldLen + 1);
/* 161 */         Mapping<T> newMappingParent = doGet((Mapping<T>[])arrayOfMapping1, bytes, block.getNetmaskBits(), block.getScopeId());
/* 162 */         Mapping<T> newMapping = new Mapping<>(block, update, newMappingParent);
/* 163 */         arrayOfMapping2[-idx - 1] = newMapping;
/* 164 */         System.arraycopy(arrayOfMapping1, -idx - 1, arrayOfMapping2, -idx, oldLen + idx + 1);
/*     */         
/* 166 */         for (int i = 0; i <= oldLen; i++) {
/* 167 */           if (arrayOfMapping2[i] != newMapping && (arrayOfMapping2[i]).parent == newMappingParent && block.matches((arrayOfMapping2[i]).range)) {
/* 168 */             arrayOfMapping2[i] = arrayOfMapping2[i].withNewParent(newMapping);
/*     */           }
/*     */         } 
/*     */       } 
/* 172 */       if (mappingsRef.compareAndSet(arrayOfMapping1, arrayOfMapping2))
/* 173 */         return matchesExpected ? expect : existing; 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static <T> Mapping<T>[] empty() {
/* 178 */     return (Mapping<T>[])NO_MAPPINGS;
/*     */   }
/*     */   
/*     */   public void clear() {
/* 182 */     this.mappingsRef.set(empty());
/*     */   }
/*     */   
/*     */   public int size() {
/* 186 */     return ((Mapping[])this.mappingsRef.get()).length;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 190 */     return (size() == 0);
/*     */   }
/*     */   
/*     */   public CidrAddressTable<T> clone() {
/* 194 */     return new CidrAddressTable(this.mappingsRef.get());
/*     */   }
/*     */   
/*     */   public Iterator<Mapping<T>> iterator() {
/* 198 */     final Mapping[] mappings = (Mapping[])this.mappingsRef.get();
/* 199 */     return new Iterator<Mapping<T>>() {
/*     */         int idx;
/*     */         
/*     */         public boolean hasNext() {
/* 203 */           return (this.idx < mappings.length);
/*     */         }
/*     */         
/*     */         public CidrAddressTable.Mapping<T> next() {
/* 207 */           if (!hasNext()) throw new NoSuchElementException(); 
/* 208 */           return mappings[this.idx++];
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public Spliterator<Mapping<T>> spliterator() {
/* 214 */     Mapping[] arrayOfMapping = (Mapping[])this.mappingsRef.get();
/* 215 */     return Spliterators.spliterator((Object[])arrayOfMapping, 1040);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 219 */     StringBuilder b = new StringBuilder();
/* 220 */     Mapping[] arrayOfMapping = (Mapping[])this.mappingsRef.get();
/* 221 */     b.append(arrayOfMapping.length).append(" mappings");
/* 222 */     for (Mapping<T> mapping : arrayOfMapping) {
/* 223 */       b.append(System.lineSeparator()).append('\t').append(mapping.range);
/* 224 */       if (mapping.parent != null) {
/* 225 */         b.append(" (parent ").append(mapping.parent.range).append(')');
/*     */       }
/* 227 */       b.append(" -> ").append(mapping.value);
/*     */     } 
/* 229 */     return b.toString();
/*     */   }
/*     */   
/*     */   private int doFind(Mapping<T>[] table, byte[] bytes, int maskBits, int scopeId) {
/* 233 */     int low = 0;
/* 234 */     int high = table.length - 1;
/*     */     
/* 236 */     while (low <= high) {
/*     */       
/* 238 */       int mid = low + high >>> 1;
/*     */ 
/*     */       
/* 241 */       Mapping<T> mapping = table[mid];
/* 242 */       int cmp = mapping.range.compareAddressBytesTo(bytes, maskBits, scopeId);
/*     */       
/* 244 */       if (cmp < 0) {
/*     */         
/* 246 */         low = mid + 1; continue;
/* 247 */       }  if (cmp > 0) {
/*     */         
/* 249 */         high = mid - 1;
/*     */         continue;
/*     */       } 
/* 252 */       return mid;
/*     */     } 
/*     */ 
/*     */     
/* 256 */     return -(low + 1);
/*     */   }
/*     */   
/*     */   private Mapping<T> doGet(Mapping<T>[] table, byte[] bytes, int netmaskBits, int scopeId) {
/* 260 */     int idx = doFind(table, bytes, netmaskBits, scopeId);
/* 261 */     if (idx >= 0) {
/*     */       
/* 263 */       assert (table[idx]).range.matches(bytes, scopeId);
/* 264 */       return table[idx];
/*     */     } 
/*     */     
/* 267 */     int pre = -idx - 2;
/* 268 */     if (pre >= 0) {
/* 269 */       if ((table[pre]).range.matches(bytes, scopeId)) {
/* 270 */         return table[pre];
/*     */       }
/*     */       
/* 273 */       Mapping<T> parent = (table[pre]).parent;
/* 274 */       while (parent != null) {
/* 275 */         if (parent.range.matches(bytes, scopeId)) {
/* 276 */           return parent;
/*     */         }
/* 278 */         parent = parent.parent;
/*     */       } 
/*     */     } 
/* 281 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class Mapping<T>
/*     */   {
/*     */     final CidrAddress range;
/*     */     
/*     */     final T value;
/*     */     
/*     */     final Mapping<T> parent;
/*     */ 
/*     */     
/*     */     Mapping(CidrAddress range, T value, Mapping<T> parent) {
/* 295 */       this.range = range;
/* 296 */       this.value = value;
/* 297 */       this.parent = parent;
/*     */     }
/*     */     
/*     */     Mapping<T> withNewParent(Mapping<T> newParent) {
/* 301 */       return new Mapping(this.range, this.value, newParent);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CidrAddress getRange() {
/* 310 */       return this.range;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public T getValue() {
/* 319 */       return this.value;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Mapping<T> getParent() {
/* 328 */       return this.parent;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\net\CidrAddressTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */