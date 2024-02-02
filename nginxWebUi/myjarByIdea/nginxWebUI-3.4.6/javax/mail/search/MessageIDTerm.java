package javax.mail.search;

import javax.mail.Message;

public final class MessageIDTerm extends StringTerm {
   private static final long serialVersionUID = -2121096296454691963L;

   public MessageIDTerm(String msgid) {
      super(msgid);
   }

   public boolean match(Message msg) {
      String[] s;
      try {
         s = msg.getHeader("Message-ID");
      } catch (Exception var4) {
         return false;
      }

      if (s == null) {
         return false;
      } else {
         for(int i = 0; i < s.length; ++i) {
            if (super.match(s[i])) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean equals(Object obj) {
      return !(obj instanceof MessageIDTerm) ? false : super.equals(obj);
   }
}
