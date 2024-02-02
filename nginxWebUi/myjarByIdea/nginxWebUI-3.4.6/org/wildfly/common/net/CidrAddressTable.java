package org.wildfly.common.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import org.wildfly.common.Assert;

public final class CidrAddressTable<T> implements Iterable<Mapping<T>> {
   private static final Mapping[] NO_MAPPINGS = new Mapping[0];
   private final AtomicReference<Mapping<T>[]> mappingsRef;

   public CidrAddressTable() {
      this.mappingsRef = new AtomicReference(empty());
   }

   private CidrAddressTable(Mapping<T>[] mappings) {
      this.mappingsRef = new AtomicReference(mappings);
   }

   public T getOrDefault(InetAddress address, T defVal) {
      Assert.checkNotNullParam("address", address);
      Mapping<T> mapping = this.doGet((Mapping[])this.mappingsRef.get(), address.getAddress(), address instanceof Inet4Address ? 32 : 128, Inet.getScopeId(address));
      return mapping == null ? defVal : mapping.value;
   }

   public T get(InetAddress address) {
      return this.getOrDefault(address, (Object)null);
   }

   public T put(CidrAddress block, T value) {
      Assert.checkNotNullParam("block", block);
      Assert.checkNotNullParam("value", value);
      return this.doPut(block, (Object)null, value, true, true);
   }

   public T putIfAbsent(CidrAddress block, T value) {
      Assert.checkNotNullParam("block", block);
      Assert.checkNotNullParam("value", value);
      return this.doPut(block, (Object)null, value, true, false);
   }

   public T replaceExact(CidrAddress block, T value) {
      Assert.checkNotNullParam("block", block);
      Assert.checkNotNullParam("value", value);
      return this.doPut(block, (Object)null, value, false, true);
   }

   public boolean replaceExact(CidrAddress block, T expect, T update) {
      Assert.checkNotNullParam("block", block);
      Assert.checkNotNullParam("expect", expect);
      Assert.checkNotNullParam("update", update);
      return this.doPut(block, expect, update, false, true) == expect;
   }

   public T removeExact(CidrAddress block) {
      Assert.checkNotNullParam("block", block);
      return this.doPut(block, (Object)null, (Object)null, false, true);
   }

   public boolean removeExact(CidrAddress block, T expect) {
      Assert.checkNotNullParam("block", block);
      return this.doPut(block, expect, (Object)null, false, true) == expect;
   }

   private T doPut(CidrAddress block, T expect, T update, boolean putIfAbsent, boolean putIfPresent) {
      assert putIfAbsent || putIfPresent;

      AtomicReference<Mapping<T>[]> mappingsRef = this.mappingsRef;
      byte[] bytes = block.getNetworkAddress().getAddress();

      Mapping[] oldVal;
      Mapping[] newVal;
      Object existing;
      boolean matchesExpected;
      do {
         oldVal = (Mapping[])mappingsRef.get();
         int idx = this.doFind(oldVal, bytes, block.getNetmaskBits(), block.getScopeId());
         if (idx < 0) {
            if (!putIfAbsent) {
               return null;
            }

            existing = null;
         } else {
            existing = oldVal[idx].value;
         }

         if (expect != null) {
            matchesExpected = Objects.equals(expect, existing);
            if (!matchesExpected) {
               return existing;
            }
         } else {
            matchesExpected = false;
         }

         if (idx >= 0 && !putIfPresent) {
            return existing;
         }

         int oldLen = oldVal.length;
         Mapping removing;
         if (update == null) {
            assert idx >= 0;

            if (oldLen == 1) {
               newVal = empty();
            } else {
               removing = oldVal[idx];
               newVal = (Mapping[])Arrays.copyOf(oldVal, oldLen - 1);
               System.arraycopy(oldVal, idx + 1, newVal, idx, oldLen - idx - 1);

               for(int i = 0; i < oldLen - 1; ++i) {
                  if (newVal[i].parent == removing) {
                     newVal[i] = newVal[i].withNewParent(removing.parent);
                  }
               }
            }
         } else {
            Mapping newMapping;
            int i;
            if (idx >= 0) {
               newVal = (Mapping[])oldVal.clone();
               removing = oldVal[idx];
               newMapping = new Mapping(block, update, oldVal[idx].parent);
               newVal[idx] = newMapping;

               for(i = 0; i < oldLen; ++i) {
                  if (i != idx && newVal[i].parent == removing) {
                     newVal[i] = newVal[i].withNewParent(newMapping);
                  }
               }
            } else {
               newVal = (Mapping[])Arrays.copyOf(oldVal, oldLen + 1);
               removing = this.doGet(oldVal, bytes, block.getNetmaskBits(), block.getScopeId());
               newMapping = new Mapping(block, update, removing);
               newVal[-idx - 1] = newMapping;
               System.arraycopy(oldVal, -idx - 1, newVal, -idx, oldLen + idx + 1);

               for(i = 0; i <= oldLen; ++i) {
                  if (newVal[i] != newMapping && newVal[i].parent == removing && block.matches(newVal[i].range)) {
                     newVal[i] = newVal[i].withNewParent(newMapping);
                  }
               }
            }
         }
      } while(!mappingsRef.compareAndSet(oldVal, newVal));

      return matchesExpected ? expect : existing;
   }

   private static <T> Mapping<T>[] empty() {
      return NO_MAPPINGS;
   }

   public void clear() {
      this.mappingsRef.set(empty());
   }

   public int size() {
      return ((Mapping[])this.mappingsRef.get()).length;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public CidrAddressTable<T> clone() {
      return new CidrAddressTable((Mapping[])this.mappingsRef.get());
   }

   public Iterator<Mapping<T>> iterator() {
      final Mapping<T>[] mappings = (Mapping[])this.mappingsRef.get();
      return new Iterator<Mapping<T>>() {
         int idx;

         public boolean hasNext() {
            return this.idx < mappings.length;
         }

         public Mapping<T> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return mappings[this.idx++];
            }
         }
      };
   }

   public Spliterator<Mapping<T>> spliterator() {
      Mapping<T>[] mappings = (Mapping[])this.mappingsRef.get();
      return Spliterators.spliterator(mappings, 1040);
   }

   public String toString() {
      StringBuilder b = new StringBuilder();
      Mapping<T>[] mappings = (Mapping[])this.mappingsRef.get();
      b.append(mappings.length).append(" mappings");
      Mapping[] var3 = mappings;
      int var4 = mappings.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Mapping<T> mapping = var3[var5];
         b.append(System.lineSeparator()).append('\t').append(mapping.range);
         if (mapping.parent != null) {
            b.append(" (parent ").append(mapping.parent.range).append(')');
         }

         b.append(" -> ").append(mapping.value);
      }

      return b.toString();
   }

   private int doFind(Mapping<T>[] table, byte[] bytes, int maskBits, int scopeId) {
      int low = 0;
      int high = table.length - 1;

      while(low <= high) {
         int mid = low + high >>> 1;
         Mapping<T> mapping = table[mid];
         int cmp = mapping.range.compareAddressBytesTo(bytes, maskBits, scopeId);
         if (cmp < 0) {
            low = mid + 1;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   private Mapping<T> doGet(Mapping<T>[] table, byte[] bytes, int netmaskBits, int scopeId) {
      int idx = this.doFind(table, bytes, netmaskBits, scopeId);
      if (idx >= 0) {
         assert table[idx].range.matches(bytes, scopeId);

         return table[idx];
      } else {
         int pre = -idx - 2;
         if (pre >= 0) {
            if (table[pre].range.matches(bytes, scopeId)) {
               return table[pre];
            }

            for(Mapping<T> parent = table[pre].parent; parent != null; parent = parent.parent) {
               if (parent.range.matches(bytes, scopeId)) {
                  return parent;
               }
            }
         }

         return null;
      }
   }

   public static final class Mapping<T> {
      final CidrAddress range;
      final T value;
      final Mapping<T> parent;

      Mapping(CidrAddress range, T value, Mapping<T> parent) {
         this.range = range;
         this.value = value;
         this.parent = parent;
      }

      Mapping<T> withNewParent(Mapping<T> newParent) {
         return new Mapping(this.range, this.value, newParent);
      }

      public CidrAddress getRange() {
         return this.range;
      }

      public T getValue() {
         return this.value;
      }

      public Mapping<T> getParent() {
         return this.parent;
      }
   }
}
