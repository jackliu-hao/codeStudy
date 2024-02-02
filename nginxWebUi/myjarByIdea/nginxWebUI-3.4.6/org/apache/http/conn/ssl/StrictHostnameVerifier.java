package org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

/** @deprecated */
@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class StrictHostnameVerifier extends AbstractVerifier {
   public static final StrictHostnameVerifier INSTANCE = new StrictHostnameVerifier();

   public final void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
      this.verify(host, cns, subjectAlts, true);
   }

   public final String toString() {
      return "STRICT";
   }
}
