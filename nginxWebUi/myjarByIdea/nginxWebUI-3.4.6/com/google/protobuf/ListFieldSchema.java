package com.google.protobuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

abstract class ListFieldSchema {
   private static final ListFieldSchema FULL_INSTANCE = new ListFieldSchemaFull();
   private static final ListFieldSchema LITE_INSTANCE = new ListFieldSchemaLite();

   private ListFieldSchema() {
   }

   abstract <L> List<L> mutableListAt(Object var1, long var2);

   abstract void makeImmutableListAt(Object var1, long var2);

   abstract <L> void mergeListsAt(Object var1, Object var2, long var3);

   static ListFieldSchema full() {
      return FULL_INSTANCE;
   }

   static ListFieldSchema lite() {
      return LITE_INSTANCE;
   }

   // $FF: synthetic method
   ListFieldSchema(Object x0) {
      this();
   }

   private static final class ListFieldSchemaLite extends ListFieldSchema {
      private ListFieldSchemaLite() {
         super(null);
      }

      <L> List<L> mutableListAt(Object message, long offset) {
         Internal.ProtobufList<L> list = getProtobufList(message, offset);
         if (!list.isModifiable()) {
            int size = list.size();
            list = list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
            UnsafeUtil.putObject((Object)message, offset, list);
         }

         return list;
      }

      void makeImmutableListAt(Object message, long offset) {
         Internal.ProtobufList<?> list = getProtobufList(message, offset);
         list.makeImmutable();
      }

      <E> void mergeListsAt(Object msg, Object otherMsg, long offset) {
         Internal.ProtobufList<E> mine = getProtobufList(msg, offset);
         Internal.ProtobufList<E> other = getProtobufList(otherMsg, offset);
         int size = mine.size();
         int otherSize = other.size();
         if (size > 0 && otherSize > 0) {
            if (!mine.isModifiable()) {
               mine = mine.mutableCopyWithCapacity(size + otherSize);
            }

            mine.addAll(other);
         }

         Internal.ProtobufList<E> merged = size > 0 ? mine : other;
         UnsafeUtil.putObject((Object)msg, offset, merged);
      }

      static <E> Internal.ProtobufList<E> getProtobufList(Object message, long offset) {
         return (Internal.ProtobufList)UnsafeUtil.getObject(message, offset);
      }

      // $FF: synthetic method
      ListFieldSchemaLite(Object x0) {
         this();
      }
   }

   private static final class ListFieldSchemaFull extends ListFieldSchema {
      private static final Class<?> UNMODIFIABLE_LIST_CLASS = Collections.unmodifiableList(Collections.emptyList()).getClass();

      private ListFieldSchemaFull() {
         super(null);
      }

      <L> List<L> mutableListAt(Object message, long offset) {
         return mutableListAt(message, offset, 10);
      }

      void makeImmutableListAt(Object message, long offset) {
         List<?> list = (List)UnsafeUtil.getObject(message, offset);
         Object immutable = null;
         if (list instanceof LazyStringList) {
            immutable = ((LazyStringList)list).getUnmodifiableView();
         } else {
            if (UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list.getClass())) {
               return;
            }

            if (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList) {
               if (((Internal.ProtobufList)list).isModifiable()) {
                  ((Internal.ProtobufList)list).makeImmutable();
               }

               return;
            }

            immutable = Collections.unmodifiableList(list);
         }

         UnsafeUtil.putObject(message, offset, immutable);
      }

      private static <L> List<L> mutableListAt(Object message, long offset, int additionalCapacity) {
         List<L> list = getList(message, offset);
         if (((List)list).isEmpty()) {
            if (list instanceof LazyStringList) {
               list = new LazyStringArrayList(additionalCapacity);
            } else if (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList) {
               list = ((Internal.ProtobufList)list).mutableCopyWithCapacity(additionalCapacity);
            } else {
               list = new ArrayList(additionalCapacity);
            }

            UnsafeUtil.putObject(message, offset, list);
         } else if (UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list.getClass())) {
            ArrayList<L> newList = new ArrayList(((List)list).size() + additionalCapacity);
            newList.addAll((Collection)list);
            list = newList;
            UnsafeUtil.putObject((Object)message, offset, newList);
         } else if (list instanceof UnmodifiableLazyStringList) {
            LazyStringArrayList newList = new LazyStringArrayList(((List)list).size() + additionalCapacity);
            newList.addAll((UnmodifiableLazyStringList)list);
            list = newList;
            UnsafeUtil.putObject((Object)message, offset, newList);
         } else if (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList && !((Internal.ProtobufList)list).isModifiable()) {
            list = ((Internal.ProtobufList)list).mutableCopyWithCapacity(((List)list).size() + additionalCapacity);
            UnsafeUtil.putObject(message, offset, list);
         }

         return (List)list;
      }

      <E> void mergeListsAt(Object msg, Object otherMsg, long offset) {
         List<E> other = getList(otherMsg, offset);
         List<E> mine = mutableListAt(msg, offset, other.size());
         int size = mine.size();
         int otherSize = other.size();
         if (size > 0 && otherSize > 0) {
            mine.addAll(other);
         }

         List<E> merged = size > 0 ? mine : other;
         UnsafeUtil.putObject((Object)msg, offset, merged);
      }

      static <E> List<E> getList(Object message, long offset) {
         return (List)UnsafeUtil.getObject(message, offset);
      }

      // $FF: synthetic method
      ListFieldSchemaFull(Object x0) {
         this();
      }
   }
}
