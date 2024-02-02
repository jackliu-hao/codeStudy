/*     */ package org.codehaus.plexus.util.xml;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.LinkedList;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.codehaus.plexus.util.StringUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PrettyPrintXMLWriter
/*     */   implements XMLWriter
/*     */ {
/*  36 */   protected static final String LS = System.getProperty("line.separator");
/*     */   
/*     */   private PrintWriter writer;
/*     */   
/*  40 */   private LinkedList elementStack = new LinkedList();
/*     */ 
/*     */   
/*     */   private boolean tagInProgress;
/*     */ 
/*     */   
/*     */   private int depth;
/*     */ 
/*     */   
/*     */   private String lineIndenter;
/*     */ 
/*     */   
/*     */   private String lineSeparator;
/*     */   
/*     */   private String encoding;
/*     */   
/*     */   private String docType;
/*     */   
/*     */   private boolean readyForNewLine;
/*     */   
/*     */   private boolean tagIsEmpty;
/*     */ 
/*     */   
/*     */   public PrettyPrintXMLWriter(PrintWriter writer, String lineIndenter) {
/*  64 */     this(writer, lineIndenter, (String)null, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrettyPrintXMLWriter(Writer writer, String lineIndenter) {
/*  73 */     this(new PrintWriter(writer), lineIndenter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrettyPrintXMLWriter(PrintWriter writer) {
/*  81 */     this(writer, (String)null, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrettyPrintXMLWriter(Writer writer) {
/*  89 */     this(new PrintWriter(writer));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrettyPrintXMLWriter(PrintWriter writer, String lineIndenter, String encoding, String doctype) {
/* 100 */     this(writer, lineIndenter, LS, encoding, doctype);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrettyPrintXMLWriter(Writer writer, String lineIndenter, String encoding, String doctype) {
/* 111 */     this(new PrintWriter(writer), lineIndenter, encoding, doctype);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrettyPrintXMLWriter(PrintWriter writer, String encoding, String doctype) {
/* 121 */     this(writer, "  ", encoding, doctype);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrettyPrintXMLWriter(Writer writer, String encoding, String doctype) {
/* 131 */     this(new PrintWriter(writer), encoding, doctype);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrettyPrintXMLWriter(PrintWriter writer, String lineIndenter, String lineSeparator, String encoding, String doctype) {
/* 143 */     setWriter(writer);
/*     */     
/* 145 */     setLineIndenter(lineIndenter);
/*     */     
/* 147 */     setLineSeparator(lineSeparator);
/*     */     
/* 149 */     setEncoding(encoding);
/*     */     
/* 151 */     setDocType(doctype);
/*     */     
/* 153 */     if (doctype != null || encoding != null)
/*     */     {
/* 155 */       writeDocumentHeaders();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void startElement(String name) {
/* 162 */     this.tagIsEmpty = false;
/*     */     
/* 164 */     finishTag();
/*     */     
/* 166 */     write("<");
/*     */     
/* 168 */     write(name);
/*     */     
/* 170 */     this.elementStack.addLast(name);
/*     */     
/* 172 */     this.tagInProgress = true;
/*     */     
/* 174 */     setDepth(getDepth() + 1);
/*     */     
/* 176 */     this.readyForNewLine = true;
/*     */     
/* 178 */     this.tagIsEmpty = true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeText(String text) {
/* 184 */     writeText(text, true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeMarkup(String text) {
/* 190 */     writeText(text, false);
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeText(String text, boolean escapeXml) {
/* 195 */     this.readyForNewLine = false;
/*     */     
/* 197 */     this.tagIsEmpty = false;
/*     */     
/* 199 */     finishTag();
/*     */     
/* 201 */     if (escapeXml)
/*     */     {
/* 203 */       text = escapeXml(text);
/*     */     }
/*     */     
/* 206 */     write(StringUtils.unifyLineSeparators(text, this.lineSeparator));
/*     */   }
/*     */ 
/*     */   
/*     */   private static String escapeXml(String text) {
/* 211 */     text = text.replaceAll("&", "&amp;");
/*     */     
/* 213 */     text = text.replaceAll("<", "&lt;");
/*     */     
/* 215 */     text = text.replaceAll(">", "&gt;");
/*     */     
/* 217 */     text = text.replaceAll("\"", "&quot;");
/*     */     
/* 219 */     text = text.replaceAll("'", "&apos;");
/*     */     
/* 221 */     return text;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String escapeXmlAttribute(String text) {
/* 226 */     text = escapeXml(text);
/*     */ 
/*     */     
/* 229 */     text = text.replaceAll("\r\n", "&#10;");
/*     */     
/* 231 */     Pattern pattern = Pattern.compile("([\000-\037])");
/* 232 */     Matcher m = pattern.matcher(text);
/* 233 */     StringBuffer b = new StringBuffer();
/* 234 */     while (m.find())
/*     */     {
/* 236 */       m = m.appendReplacement(b, "&#" + Integer.toString(m.group(1).charAt(0)) + ";");
/*     */     }
/* 238 */     m.appendTail(b);
/*     */     
/* 240 */     return b.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAttribute(String key, String value) {
/* 246 */     write(" ");
/*     */     
/* 248 */     write(key);
/*     */     
/* 250 */     write("=\"");
/*     */     
/* 252 */     write(escapeXmlAttribute(value));
/*     */     
/* 254 */     write("\"");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void endElement() {
/* 260 */     setDepth(getDepth() - 1);
/*     */     
/* 262 */     if (this.tagIsEmpty) {
/*     */       
/* 264 */       write("/");
/*     */       
/* 266 */       this.readyForNewLine = false;
/*     */       
/* 268 */       finishTag();
/*     */       
/* 270 */       this.elementStack.removeLast();
/*     */     }
/*     */     else {
/*     */       
/* 274 */       finishTag();
/*     */       
/* 276 */       write("</" + this.elementStack.removeLast() + ">");
/*     */     } 
/*     */     
/* 279 */     this.readyForNewLine = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void write(String str) {
/* 288 */     getWriter().write(str);
/*     */   }
/*     */ 
/*     */   
/*     */   private void finishTag() {
/* 293 */     if (this.tagInProgress)
/*     */     {
/* 295 */       write(">");
/*     */     }
/*     */     
/* 298 */     this.tagInProgress = false;
/*     */     
/* 300 */     if (this.readyForNewLine)
/*     */     {
/* 302 */       endOfLine();
/*     */     }
/* 304 */     this.readyForNewLine = false;
/*     */     
/* 306 */     this.tagIsEmpty = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getLineIndenter() {
/* 316 */     return this.lineIndenter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setLineIndenter(String lineIndenter) {
/* 326 */     this.lineIndenter = lineIndenter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getLineSeparator() {
/* 337 */     return this.lineSeparator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setLineSeparator(String lineSeparator) {
/* 348 */     this.lineSeparator = lineSeparator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void endOfLine() {
/* 360 */     write(getLineSeparator());
/*     */     
/* 362 */     for (int i = 0; i < getDepth(); i++)
/*     */     {
/* 364 */       write(getLineIndenter());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeDocumentHeaders() {
/* 370 */     write("<?xml version=\"1.0\"");
/*     */     
/* 372 */     if (getEncoding() != null)
/*     */     {
/* 374 */       write(" encoding=\"" + getEncoding() + "\"");
/*     */     }
/*     */     
/* 377 */     write("?>");
/*     */     
/* 379 */     endOfLine();
/*     */     
/* 381 */     if (getDocType() != null) {
/*     */       
/* 383 */       write("<!DOCTYPE ");
/*     */       
/* 385 */       write(getDocType());
/*     */       
/* 387 */       write(">");
/*     */       
/* 389 */       endOfLine();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setWriter(PrintWriter writer) {
/* 400 */     if (writer == null)
/*     */     {
/* 402 */       throw new IllegalArgumentException("writer could not be null");
/*     */     }
/*     */     
/* 405 */     this.writer = writer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected PrintWriter getWriter() {
/* 415 */     return this.writer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setDepth(int depth) {
/* 425 */     this.depth = depth;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getDepth() {
/* 435 */     return this.depth;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setEncoding(String encoding) {
/* 445 */     this.encoding = encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getEncoding() {
/* 455 */     return this.encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setDocType(String docType) {
/* 465 */     this.docType = docType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getDocType() {
/* 475 */     return this.docType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected LinkedList getElementStack() {
/* 483 */     return this.elementStack;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\PrettyPrintXMLWriter.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */