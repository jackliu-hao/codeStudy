package ch.qos.logback.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HardenedObjectInputStream extends ObjectInputStream {
   final List<String> whitelistedClassNames = new ArrayList();
   static final String[] JAVA_PACKAGES = new String[]{"java.lang", "java.util"};

   public HardenedObjectInputStream(InputStream in, String[] whilelist) throws IOException {
      super(in);
      if (whilelist != null) {
         for(int i = 0; i < whilelist.length; ++i) {
            this.whitelistedClassNames.add(whilelist[i]);
         }
      }

   }

   public HardenedObjectInputStream(InputStream in, List<String> whitelist) throws IOException {
      super(in);
      this.whitelistedClassNames.addAll(whitelist);
   }

   protected Class<?> resolveClass(ObjectStreamClass anObjectStreamClass) throws IOException, ClassNotFoundException {
      String incomingClassName = anObjectStreamClass.getName();
      if (!this.isWhitelisted(incomingClassName)) {
         throw new InvalidClassException("Unauthorized deserialization attempt", anObjectStreamClass.getName());
      } else {
         return super.resolveClass(anObjectStreamClass);
      }
   }

   private boolean isWhitelisted(String incomingClassName) {
      for(int i = 0; i < JAVA_PACKAGES.length; ++i) {
         if (incomingClassName.startsWith(JAVA_PACKAGES[i])) {
            return true;
         }
      }

      Iterator var4 = this.whitelistedClassNames.iterator();

      String whiteListed;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         whiteListed = (String)var4.next();
      } while(!incomingClassName.equals(whiteListed));

      return true;
   }

   protected void addToWhitelist(List<String> additionalAuthorizedClasses) {
      this.whitelistedClassNames.addAll(additionalAuthorizedClasses);
   }
}
