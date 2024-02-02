package javax.mail.search;

import javax.mail.Message;

public final class SizeTerm extends IntegerComparisonTerm {
   private static final long serialVersionUID = -2556219451005103709L;

   public SizeTerm(int comparison, int size) {
      super(comparison, size);
   }

   public boolean match(Message msg) {
      int size;
      try {
         size = msg.getSize();
      } catch (Exception var4) {
         return false;
      }

      return size == -1 ? false : super.match(size);
   }

   public boolean equals(Object obj) {
      return !(obj instanceof SizeTerm) ? false : super.equals(obj);
   }
}
