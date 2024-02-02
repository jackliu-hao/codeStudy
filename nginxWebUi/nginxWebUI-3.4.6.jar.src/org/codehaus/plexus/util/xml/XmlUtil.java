/*     */ package org.codehaus.plexus.util.xml;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import org.codehaus.plexus.util.IOUtil;
/*     */ import org.codehaus.plexus.util.ReaderFactory;
/*     */ import org.codehaus.plexus.util.StringUtils;
/*     */ import org.codehaus.plexus.util.xml.pull.MXParser;
/*     */ import org.codehaus.plexus.util.xml.pull.XmlPullParser;
/*     */ import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XmlUtil
/*     */ {
/*     */   public static final int DEFAULT_INDENTATION_SIZE = 2;
/*  48 */   public static final String DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isXml(File f) {
/*  58 */     if (f == null)
/*     */     {
/*  60 */       throw new IllegalArgumentException("f could not be null.");
/*     */     }
/*     */     
/*  63 */     if (!f.isFile())
/*     */     {
/*  65 */       throw new IllegalArgumentException("The file '" + f.getAbsolutePath() + "' is not a file.");
/*     */     }
/*     */     
/*  68 */     Reader reader = null;
/*     */     
/*     */     try {
/*  71 */       reader = ReaderFactory.newXmlReader(f);
/*  72 */       MXParser mXParser = new MXParser();
/*  73 */       mXParser.setInput(reader);
/*  74 */       mXParser.nextToken();
/*     */       
/*  76 */       return true;
/*     */     }
/*  78 */     catch (Exception e) {
/*     */       
/*  80 */       return false;
/*     */     }
/*     */     finally {
/*     */       
/*  84 */       IOUtil.close(reader);
/*     */     } 
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
/*     */   public static void prettyFormat(Reader reader, Writer writer) throws IOException {
/* 110 */     prettyFormat(reader, writer, 2, DEFAULT_LINE_SEPARATOR);
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
/*     */   public static void prettyFormat(Reader reader, Writer writer, int indentSize, String lineSeparator) throws IOException {
/* 136 */     if (reader == null)
/*     */     {
/* 138 */       throw new IllegalArgumentException("The reader is null");
/*     */     }
/* 140 */     if (writer == null)
/*     */     {
/* 142 */       throw new IllegalArgumentException("The writer is null");
/*     */     }
/* 144 */     if (indentSize < 0)
/*     */     {
/* 146 */       indentSize = 0;
/*     */     }
/*     */     
/* 149 */     PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter(writer);
/* 150 */     xmlWriter.setLineIndenter(StringUtils.repeat(" ", indentSize));
/* 151 */     xmlWriter.setLineSeparator(lineSeparator);
/*     */     
/* 153 */     MXParser mXParser = new MXParser();
/*     */     
/*     */     try {
/* 156 */       mXParser.setInput(reader);
/*     */       
/* 158 */       prettyFormatInternal((XmlPullParser)mXParser, xmlWriter);
/*     */     }
/* 160 */     catch (XmlPullParserException e) {
/*     */       
/* 162 */       throw new IOException("Unable to parse the XML: " + e.getMessage());
/*     */     } 
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
/*     */   public static void prettyFormat(InputStream is, OutputStream os) throws IOException {
/* 186 */     prettyFormat(is, os, 2, DEFAULT_LINE_SEPARATOR);
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
/*     */   public static void prettyFormat(InputStream is, OutputStream os, int indentSize, String lineSeparator) throws IOException {
/* 210 */     if (is == null)
/*     */     {
/* 212 */       throw new IllegalArgumentException("The is is null");
/*     */     }
/* 214 */     if (os == null)
/*     */     {
/* 216 */       throw new IllegalArgumentException("The os is null");
/*     */     }
/* 218 */     if (indentSize < 0)
/*     */     {
/* 220 */       indentSize = 0;
/*     */     }
/*     */     
/* 223 */     Reader reader = null;
/*     */     
/* 225 */     Writer out = new OutputStreamWriter(os);
/* 226 */     PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter(out);
/* 227 */     xmlWriter.setLineIndenter(StringUtils.repeat(" ", indentSize));
/* 228 */     xmlWriter.setLineSeparator(lineSeparator);
/*     */     
/* 230 */     MXParser mXParser = new MXParser();
/*     */     
/*     */     try {
/* 233 */       reader = ReaderFactory.newXmlReader(is);
/*     */       
/* 235 */       mXParser.setInput(reader);
/*     */       
/* 237 */       prettyFormatInternal((XmlPullParser)mXParser, xmlWriter);
/*     */     }
/* 239 */     catch (XmlPullParserException e) {
/*     */       
/* 241 */       throw new IOException("Unable to parse the XML: " + e.getMessage());
/*     */     }
/*     */     finally {
/*     */       
/* 245 */       IOUtil.close(reader);
/* 246 */       IOUtil.close(out);
/*     */     } 
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
/*     */   private static void prettyFormatInternal(XmlPullParser parser, PrettyPrintXMLWriter writer) throws XmlPullParserException, IOException {
/* 259 */     boolean hasTag = false;
/* 260 */     boolean hasComment = false;
/* 261 */     int eventType = parser.getEventType();
/* 262 */     while (eventType != 1) {
/*     */       
/* 264 */       if (eventType == 2) {
/*     */         
/* 266 */         hasTag = true;
/* 267 */         if (hasComment) {
/*     */           
/* 269 */           writer.writeText(writer.getLineIndenter());
/* 270 */           hasComment = false;
/*     */         } 
/* 272 */         writer.startElement(parser.getName());
/* 273 */         for (int i = 0; i < parser.getAttributeCount(); i++)
/*     */         {
/* 275 */           String key = parser.getAttributeName(i);
/* 276 */           String value = parser.getAttributeValue(i);
/* 277 */           writer.addAttribute(key, value);
/*     */         }
/*     */       
/* 280 */       } else if (eventType == 4) {
/*     */         
/* 282 */         String text = parser.getText();
/* 283 */         if (!text.trim().equals(""))
/*     */         {
/* 285 */           text = StringUtils.removeDuplicateWhitespace(text);
/* 286 */           writer.writeText(text);
/*     */         }
/*     */       
/* 289 */       } else if (eventType == 3) {
/*     */         
/* 291 */         hasTag = false;
/* 292 */         writer.endElement();
/*     */       }
/* 294 */       else if (eventType == 9) {
/*     */         
/* 296 */         hasComment = true;
/* 297 */         if (!hasTag) {
/*     */           
/* 299 */           writer.writeMarkup(writer.getLineSeparator());
/* 300 */           for (int i = 0; i < writer.getDepth(); i++)
/*     */           {
/* 302 */             writer.writeMarkup(writer.getLineIndenter());
/*     */           }
/*     */         } 
/* 305 */         writer.writeMarkup("<!--" + parser.getText().trim() + " -->");
/* 306 */         if (!hasTag)
/*     */         {
/* 308 */           writer.writeMarkup(writer.getLineSeparator());
/* 309 */           for (int i = 0; i < writer.getDepth() - 1; i++)
/*     */           {
/* 311 */             writer.writeMarkup(writer.getLineIndenter());
/*     */           }
/*     */         }
/*     */       
/* 315 */       } else if (eventType == 10) {
/*     */         
/* 317 */         writer.writeMarkup("<!DOCTYPE" + parser.getText() + ">");
/* 318 */         writer.endOfLine();
/*     */       }
/* 320 */       else if (eventType == 8) {
/*     */         
/* 322 */         writer.writeMarkup("<?" + parser.getText() + "?>");
/* 323 */         writer.endOfLine();
/*     */       }
/* 325 */       else if (eventType == 5) {
/*     */         
/* 327 */         writer.writeMarkup("<![CDATA[" + parser.getText() + "]]>");
/*     */       }
/* 329 */       else if (eventType == 6) {
/*     */         
/* 331 */         writer.writeMarkup("&" + parser.getName() + ";");
/*     */       } 
/*     */       
/* 334 */       eventType = parser.nextToken();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\XmlUtil.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */