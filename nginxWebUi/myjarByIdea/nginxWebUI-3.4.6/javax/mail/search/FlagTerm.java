package javax.mail.search;

import javax.mail.Flags;
import javax.mail.Message;

public final class FlagTerm extends SearchTerm {
   protected boolean set;
   protected Flags flags;
   private static final long serialVersionUID = -142991500302030647L;

   public FlagTerm(Flags flags, boolean set) {
      this.flags = flags;
      this.set = set;
   }

   public Flags getFlags() {
      return (Flags)this.flags.clone();
   }

   public boolean getTestSet() {
      return this.set;
   }

   public boolean match(Message msg) {
      try {
         Flags f = msg.getFlags();
         if (this.set) {
            return f.contains(this.flags);
         } else {
            Flags.Flag[] sf = this.flags.getSystemFlags();

            for(int i = 0; i < sf.length; ++i) {
               if (f.contains(sf[i])) {
                  return false;
               }
            }

            String[] s = this.flags.getUserFlags();

            for(int i = 0; i < s.length; ++i) {
               if (f.contains(s[i])) {
                  return false;
               }
            }

            return true;
         }
      } catch (Exception var6) {
         return false;
      }
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof FlagTerm)) {
         return false;
      } else {
         FlagTerm ft = (FlagTerm)obj;
         return ft.set == this.set && ft.flags.equals(this.flags);
      }
   }

   public int hashCode() {
      return this.set ? this.flags.hashCode() : ~this.flags.hashCode();
   }
}
