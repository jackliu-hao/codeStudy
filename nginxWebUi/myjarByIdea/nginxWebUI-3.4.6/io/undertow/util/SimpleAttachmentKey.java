package io.undertow.util;

class SimpleAttachmentKey<T> extends AttachmentKey<T> {
   private final Class<T> valueClass;

   SimpleAttachmentKey(Class<T> valueClass) {
      this.valueClass = valueClass;
   }

   public T cast(Object value) {
      return this.valueClass.cast(value);
   }

   public String toString() {
      if (this.valueClass != null) {
         StringBuilder sb = new StringBuilder(this.getClass().getName());
         sb.append("<");
         sb.append(this.valueClass.getName());
         sb.append(">");
         return sb.toString();
      } else {
         return super.toString();
      }
   }
}
