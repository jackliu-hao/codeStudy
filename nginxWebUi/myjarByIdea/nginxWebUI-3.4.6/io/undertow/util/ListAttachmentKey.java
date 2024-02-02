package io.undertow.util;

class ListAttachmentKey<T> extends AttachmentKey<AttachmentList<T>> {
   private final Class<T> valueClass;

   ListAttachmentKey(Class<T> valueClass) {
      this.valueClass = valueClass;
   }

   public AttachmentList<T> cast(Object value) {
      if (value == null) {
         return null;
      } else {
         AttachmentList<?> list = (AttachmentList)value;
         Class<?> listValueClass = list.getValueClass();
         if (listValueClass != this.valueClass) {
            throw new ClassCastException();
         } else {
            return list;
         }
      }
   }

   Class<T> getValueClass() {
      return this.valueClass;
   }
}
