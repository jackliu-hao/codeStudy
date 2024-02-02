package org.apache.http.conn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public final class PublicSuffixListParser {
   public PublicSuffixList parse(Reader reader) throws IOException {
      List<String> rules = new ArrayList();
      List<String> exceptions = new ArrayList();
      BufferedReader r = new BufferedReader(reader);

      String line;
      while((line = r.readLine()) != null) {
         if (!line.isEmpty() && !line.startsWith("//")) {
            if (line.startsWith(".")) {
               line = line.substring(1);
            }

            boolean isException = line.startsWith("!");
            if (isException) {
               line = line.substring(1);
            }

            if (isException) {
               exceptions.add(line);
            } else {
               rules.add(line);
            }
         }
      }

      return new PublicSuffixList(DomainType.UNKNOWN, rules, exceptions);
   }

   public List<PublicSuffixList> parseByType(Reader reader) throws IOException {
      List<PublicSuffixList> result = new ArrayList(2);
      BufferedReader r = new BufferedReader(reader);
      new StringBuilder(256);
      DomainType domainType = null;
      List<String> rules = null;
      List<String> exceptions = null;

      while(true) {
         while(true) {
            while(true) {
               String line;
               do {
                  if ((line = r.readLine()) == null) {
                     return result;
                  }
               } while(line.isEmpty());

               if (line.startsWith("//")) {
                  if (domainType == null) {
                     if (line.contains("===BEGIN ICANN DOMAINS===")) {
                        domainType = DomainType.ICANN;
                     } else if (line.contains("===BEGIN PRIVATE DOMAINS===")) {
                        domainType = DomainType.PRIVATE;
                     }
                  } else if (line.contains("===END ICANN DOMAINS===") || line.contains("===END PRIVATE DOMAINS===")) {
                     if (rules != null) {
                        result.add(new PublicSuffixList(domainType, rules, exceptions));
                     }

                     domainType = null;
                     rules = null;
                     exceptions = null;
                  }
               } else if (domainType != null) {
                  if (line.startsWith(".")) {
                     line = line.substring(1);
                  }

                  boolean isException = line.startsWith("!");
                  if (isException) {
                     line = line.substring(1);
                  }

                  if (isException) {
                     if (exceptions == null) {
                        exceptions = new ArrayList();
                     }

                     exceptions.add(line);
                  } else {
                     if (rules == null) {
                        rules = new ArrayList();
                     }

                     rules.add(line);
                  }
               }
            }
         }
      }
   }
}
