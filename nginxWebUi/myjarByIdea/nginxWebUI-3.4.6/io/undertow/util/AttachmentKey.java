package io.undertow.util;

public abstract class AttachmentKey<T> {
   AttachmentKey() {
   }

   public abstract T cast(Object var1);

   public static <T> AttachmentKey<T> create(Class<? super T> valueClass) {
      return new SimpleAttachmentKey(valueClass);
   }

   public static <T> AttachmentKey<AttachmentList<T>> createList(Class<? super T> valueClass) {
      return new ListAttachmentKey(valueClass);
   }
}
