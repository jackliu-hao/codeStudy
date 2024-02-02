package org.apache.http.conn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public final class PublicSuffixMatcherLoader {
   private static volatile PublicSuffixMatcher DEFAULT_INSTANCE;

   private static PublicSuffixMatcher load(InputStream in) throws IOException {
      List<PublicSuffixList> lists = (new PublicSuffixListParser()).parseByType(new InputStreamReader(in, Consts.UTF_8));
      return new PublicSuffixMatcher(lists);
   }

   public static PublicSuffixMatcher load(URL url) throws IOException {
      Args.notNull(url, "URL");
      InputStream in = url.openStream();

      PublicSuffixMatcher var2;
      try {
         var2 = load(in);
      } finally {
         in.close();
      }

      return var2;
   }

   public static PublicSuffixMatcher load(File file) throws IOException {
      Args.notNull(file, "File");
      InputStream in = new FileInputStream(file);

      PublicSuffixMatcher var2;
      try {
         var2 = load((InputStream)in);
      } finally {
         in.close();
      }

      return var2;
   }

   public static PublicSuffixMatcher getDefault() {
      if (DEFAULT_INSTANCE == null) {
         Class var0 = PublicSuffixMatcherLoader.class;
         synchronized(PublicSuffixMatcherLoader.class) {
            if (DEFAULT_INSTANCE == null) {
               URL url = PublicSuffixMatcherLoader.class.getResource("/mozilla/public-suffix-list.txt");
               if (url != null) {
                  try {
                     DEFAULT_INSTANCE = load(url);
                  } catch (IOException var5) {
                     Log log = LogFactory.getLog(PublicSuffixMatcherLoader.class);
                     if (log.isWarnEnabled()) {
                        log.warn("Failure loading public suffix list from default resource", var5);
                     }
                  }
               } else {
                  DEFAULT_INSTANCE = new PublicSuffixMatcher(DomainType.ICANN, Arrays.asList("com"), (Collection)null);
               }
            }
         }
      }

      return DEFAULT_INSTANCE;
   }
}
