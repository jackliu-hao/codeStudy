package ch.qos.logback.classic.html;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.html.CssBuilder;

public class DefaultCssBuilder implements CssBuilder {
   public void addCss(StringBuilder sbuf) {
      sbuf.append("<style  type=\"text/css\">");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("table { margin-left: 2em; margin-right: 2em; border-left: 2px solid #AAA; }");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("TR.even { background: #FFFFFF; }");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("TR.odd { background: #EAEAEA; }");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("TR.warn TD.Level, TR.error TD.Level, TR.fatal TD.Level {font-weight: bold; color: #FF4040 }");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("TD { padding-right: 1ex; padding-left: 1ex; border-right: 2px solid #AAA; }");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("TD.Time, TD.Date { text-align: right; font-family: courier, monospace; font-size: smaller; }");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("TD.Thread { text-align: left; }");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("TD.Level { text-align: right; }");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("TD.Logger { text-align: left; }");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("TR.header { background: #596ED5; color: #FFF; font-weight: bold; font-size: larger; }");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("TD.Exception { background: #A2AEE8; font-family: courier, monospace;}");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("</style>");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
   }
}
