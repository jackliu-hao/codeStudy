package ch.qos.logback.core.rolling.helper;

public class TokenConverter {
   static final int IDENTITY = 0;
   static final int INTEGER = 1;
   static final int DATE = 1;
   int type;
   TokenConverter next;

   protected TokenConverter(int t) {
      this.type = t;
   }

   public TokenConverter getNext() {
      return this.next;
   }

   public void setNext(TokenConverter next) {
      this.next = next;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int i) {
      this.type = i;
   }
}
