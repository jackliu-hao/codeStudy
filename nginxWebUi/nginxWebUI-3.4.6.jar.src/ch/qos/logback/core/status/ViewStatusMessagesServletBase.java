/*     */ package ch.qos.logback.core.status;
/*     */ 
/*     */ import ch.qos.logback.core.CoreConstants;
/*     */ import ch.qos.logback.core.helpers.Transform;
/*     */ import ch.qos.logback.core.util.CachingDateFormatter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.List;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ViewStatusMessagesServletBase
/*     */   extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = -3551928133801157219L;
/*  35 */   private static CachingDateFormatter SDF = new CachingDateFormatter("yyyy-MM-dd HH:mm:ss");
/*     */   
/*  37 */   static String SUBMIT = "submit";
/*  38 */   static String CLEAR = "Clear";
/*     */   
/*     */   int count;
/*     */ 
/*     */   
/*     */   protected abstract StatusManager getStatusManager(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse);
/*     */   
/*     */   protected abstract String getPageTitle(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse);
/*     */   
/*     */   protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/*  48 */     this.count = 0;
/*  49 */     StatusManager sm = getStatusManager(req, resp);
/*     */     
/*  51 */     resp.setContentType("text/html");
/*  52 */     PrintWriter output = resp.getWriter();
/*     */     
/*  54 */     output.append("<html>\r\n");
/*  55 */     output.append("<head>\r\n");
/*  56 */     printCSS(req.getContextPath(), output);
/*  57 */     output.append("</head>\r\n");
/*  58 */     output.append("<body>\r\n");
/*  59 */     output.append(getPageTitle(req, resp));
/*     */     
/*  61 */     output.append("<form method=\"POST\">\r\n");
/*  62 */     output.append("<input type=\"submit\" name=\"" + SUBMIT + "\" value=\"" + CLEAR + "\">");
/*  63 */     output.append("</form>\r\n");
/*     */     
/*  65 */     if (CLEAR.equalsIgnoreCase(req.getParameter(SUBMIT))) {
/*  66 */       sm.clear();
/*  67 */       sm.add(new InfoStatus("Cleared all status messages", this));
/*     */     } 
/*     */     
/*  70 */     output.append("<table>");
/*  71 */     StringBuilder buf = new StringBuilder();
/*  72 */     if (sm != null) {
/*  73 */       printList(buf, sm);
/*     */     } else {
/*  75 */       output.append("Could not find status manager");
/*     */     } 
/*  77 */     output.append(buf);
/*  78 */     output.append("</table>");
/*  79 */     output.append("</body>\r\n");
/*  80 */     output.append("</html>\r\n");
/*  81 */     output.flush();
/*  82 */     output.close();
/*     */   }
/*     */   
/*     */   public void printCSS(String localRef, PrintWriter output) {
/*  86 */     output.append("  <STYLE TYPE=\"text/css\">\r\n");
/*  87 */     output.append("    .warn  { font-weight: bold; color: #FF6600;} \r\n");
/*  88 */     output.append("    .error { font-weight: bold; color: #CC0000;} \r\n");
/*  89 */     output.append("    table { margin-left: 2em; margin-right: 2em; border-left: 2px solid #AAA; }\r\n");
/*  90 */     output.append("    tr.even { background: #FFFFFF; }\r\n");
/*  91 */     output.append("    tr.odd  { background: #EAEAEA; }\r\n");
/*  92 */     output.append("    td { padding-right: 1ex; padding-left: 1ex; border-right: 2px solid #AAA; }\r\n");
/*  93 */     output.append("    td.date { text-align: right; font-family: courier, monospace; font-size: smaller; }");
/*  94 */     output.append(CoreConstants.LINE_SEPARATOR);
/*     */     
/*  96 */     output.append("  td.level { text-align: right; }");
/*  97 */     output.append(CoreConstants.LINE_SEPARATOR);
/*  98 */     output.append("    tr.header { background: #596ED5; color: #FFF; font-weight: bold; font-size: larger; }");
/*  99 */     output.append(CoreConstants.LINE_SEPARATOR);
/*     */     
/* 101 */     output.append("  td.exception { background: #A2AEE8; white-space: pre; font-family: courier, monospace;}");
/* 102 */     output.append(CoreConstants.LINE_SEPARATOR);
/*     */     
/* 104 */     output.append("  </STYLE>\r\n");
/*     */   }
/*     */ 
/*     */   
/*     */   public void printList(StringBuilder buf, StatusManager sm) {
/* 109 */     buf.append("<table>\r\n");
/* 110 */     printHeader(buf);
/* 111 */     List<Status> statusList = sm.getCopyOfStatusList();
/* 112 */     for (Status s : statusList) {
/* 113 */       this.count++;
/* 114 */       printStatus(buf, s);
/*     */     } 
/* 116 */     buf.append("</table>\r\n");
/*     */   }
/*     */   
/*     */   public void printHeader(StringBuilder buf) {
/* 120 */     buf.append("  <tr class=\"header\">\r\n");
/* 121 */     buf.append("    <th>Date </th>\r\n");
/* 122 */     buf.append("    <th>Level</th>\r\n");
/* 123 */     buf.append("    <th>Origin</th>\r\n");
/* 124 */     buf.append("    <th>Message</th>\r\n");
/* 125 */     buf.append("  </tr>\r\n");
/*     */   }
/*     */ 
/*     */   
/*     */   String statusLevelAsString(Status s) {
/* 130 */     switch (s.getEffectiveLevel()) {
/*     */       case 0:
/* 132 */         return "INFO";
/*     */       case 1:
/* 134 */         return "<span class=\"warn\">WARN</span>";
/*     */       case 2:
/* 136 */         return "<span class=\"error\">ERROR</span>";
/*     */     } 
/* 138 */     return null;
/*     */   }
/*     */   
/*     */   String abbreviatedOrigin(Status s) {
/* 142 */     Object o = s.getOrigin();
/* 143 */     if (o == null) {
/* 144 */       return null;
/*     */     }
/* 146 */     String fqClassName = o.getClass().getName();
/* 147 */     int lastIndex = fqClassName.lastIndexOf('.');
/* 148 */     if (lastIndex != -1) {
/* 149 */       return fqClassName.substring(lastIndex + 1, fqClassName.length());
/*     */     }
/* 151 */     return fqClassName;
/*     */   }
/*     */ 
/*     */   
/*     */   private void printStatus(StringBuilder buf, Status s) {
/*     */     String trClass;
/* 157 */     if (this.count % 2 == 0) {
/* 158 */       trClass = "even";
/*     */     } else {
/* 160 */       trClass = "odd";
/*     */     } 
/* 162 */     buf.append("  <tr class=\"").append(trClass).append("\">\r\n");
/* 163 */     String dateStr = SDF.format(s.getDate().longValue());
/* 164 */     buf.append("    <td class=\"date\">").append(dateStr).append("</td>\r\n");
/* 165 */     buf.append("    <td class=\"level\">").append(statusLevelAsString(s)).append("</td>\r\n");
/* 166 */     buf.append("    <td>").append(abbreviatedOrigin(s)).append("</td>\r\n");
/* 167 */     buf.append("    <td>").append(s.getMessage()).append("</td>\r\n");
/* 168 */     buf.append("  </tr>\r\n");
/* 169 */     if (s.getThrowable() != null) {
/* 170 */       printThrowable(buf, s.getThrowable());
/*     */     }
/*     */   }
/*     */   
/*     */   private void printThrowable(StringBuilder buf, Throwable t) {
/* 175 */     buf.append("  <tr>\r\n");
/* 176 */     buf.append("    <td colspan=\"4\" class=\"exception\"><pre>");
/* 177 */     StringWriter sw = new StringWriter();
/* 178 */     PrintWriter pw = new PrintWriter(sw);
/* 179 */     t.printStackTrace(pw);
/* 180 */     buf.append(Transform.escapeTags(sw.getBuffer()));
/* 181 */     buf.append("    </pre></td>\r\n");
/* 182 */     buf.append("  </tr>\r\n");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\status\ViewStatusMessagesServletBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */