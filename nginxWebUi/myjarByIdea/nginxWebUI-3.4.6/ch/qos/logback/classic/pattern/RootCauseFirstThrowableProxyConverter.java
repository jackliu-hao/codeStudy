package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;

public class RootCauseFirstThrowableProxyConverter extends ExtendedThrowableProxyConverter {
   protected String throwableProxyToString(IThrowableProxy tp) {
      StringBuilder buf = new StringBuilder(2048);
      this.recursiveAppendRootCauseFirst(buf, (String)null, 1, tp);
      return buf.toString();
   }

   protected void recursiveAppendRootCauseFirst(StringBuilder sb, String prefix, int indent, IThrowableProxy tp) {
      if (tp.getCause() != null) {
         this.recursiveAppendRootCauseFirst(sb, prefix, indent, tp.getCause());
         prefix = null;
      }

      ThrowableProxyUtil.indent(sb, indent - 1);
      if (prefix != null) {
         sb.append(prefix);
      }

      ThrowableProxyUtil.subjoinFirstLineRootCauseFirst(sb, tp);
      sb.append(CoreConstants.LINE_SEPARATOR);
      this.subjoinSTEPArray(sb, indent, tp);
      IThrowableProxy[] suppressed = tp.getSuppressed();
      if (suppressed != null) {
         IThrowableProxy[] var6 = suppressed;
         int var7 = suppressed.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            IThrowableProxy current = var6[var8];
            this.recursiveAppendRootCauseFirst(sb, "Suppressed: ", indent + 1, current);
         }
      }

   }
}
