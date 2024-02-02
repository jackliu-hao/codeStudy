/*     */ package org.codehaus.plexus.util.xml;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.codehaus.plexus.util.IOUtil;
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
/*     */ public class Xpp3DomBuilder
/*     */ {
/*     */   private static final boolean DEFAULT_TRIM = true;
/*     */   
/*     */   public static Xpp3Dom build(Reader reader) throws XmlPullParserException, IOException {
/*  40 */     return build(reader, true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Xpp3Dom build(InputStream is, String encoding) throws XmlPullParserException, IOException {
/*  46 */     return build(is, encoding, true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Xpp3Dom build(InputStream is, String encoding, boolean trim) throws XmlPullParserException, IOException {
/*  52 */     MXParser mXParser = new MXParser();
/*     */     
/*  54 */     mXParser.setInput(is, encoding);
/*     */ 
/*     */     
/*     */     try {
/*  58 */       return build((XmlPullParser)mXParser, trim);
/*     */     }
/*     */     finally {
/*     */       
/*  62 */       IOUtil.close(is);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Xpp3Dom build(Reader reader, boolean trim) throws XmlPullParserException, IOException {
/*  69 */     MXParser mXParser = new MXParser();
/*     */     
/*  71 */     mXParser.setInput(reader);
/*     */ 
/*     */     
/*     */     try {
/*  75 */       return build((XmlPullParser)mXParser, trim);
/*     */     }
/*     */     finally {
/*     */       
/*  79 */       IOUtil.close(reader);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Xpp3Dom build(XmlPullParser parser) throws XmlPullParserException, IOException {
/*  86 */     return build(parser, true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Xpp3Dom build(XmlPullParser parser, boolean trim) throws XmlPullParserException, IOException {
/*  92 */     List elements = new ArrayList();
/*     */     
/*  94 */     List values = new ArrayList();
/*     */     
/*  96 */     int eventType = parser.getEventType();
/*     */     
/*  98 */     while (eventType != 1) {
/*     */       
/* 100 */       if (eventType == 2) {
/*     */         
/* 102 */         String rawName = parser.getName();
/*     */         
/* 104 */         Xpp3Dom childConfiguration = new Xpp3Dom(rawName);
/*     */         
/* 106 */         int depth = elements.size();
/*     */         
/* 108 */         if (depth > 0) {
/*     */           
/* 110 */           Xpp3Dom parent = elements.get(depth - 1);
/*     */           
/* 112 */           parent.addChild(childConfiguration);
/*     */         } 
/*     */         
/* 115 */         elements.add(childConfiguration);
/*     */         
/* 117 */         if (parser.isEmptyElementTag()) {
/*     */           
/* 119 */           values.add(null);
/*     */         }
/*     */         else {
/*     */           
/* 123 */           values.add(new StringBuffer());
/*     */         } 
/*     */         
/* 126 */         int attributesSize = parser.getAttributeCount();
/*     */         
/* 128 */         for (int i = 0; i < attributesSize; i++)
/*     */         {
/* 130 */           String name = parser.getAttributeName(i);
/*     */           
/* 132 */           String value = parser.getAttributeValue(i);
/*     */           
/* 134 */           childConfiguration.setAttribute(name, value);
/*     */         }
/*     */       
/* 137 */       } else if (eventType == 4) {
/*     */         
/* 139 */         int depth = values.size() - 1;
/*     */         
/* 141 */         StringBuffer valueBuffer = values.get(depth);
/*     */         
/* 143 */         String text = parser.getText();
/*     */         
/* 145 */         if (trim)
/*     */         {
/* 147 */           text = text.trim();
/*     */         }
/*     */         
/* 150 */         valueBuffer.append(text);
/*     */       }
/* 152 */       else if (eventType == 3) {
/*     */         
/* 154 */         int depth = elements.size() - 1;
/*     */         
/* 156 */         Xpp3Dom finishedConfiguration = elements.remove(depth);
/*     */ 
/*     */         
/* 159 */         Object accumulatedValue = values.remove(depth);
/*     */         
/* 161 */         if (finishedConfiguration.getChildCount() == 0)
/*     */         {
/* 163 */           if (accumulatedValue == null) {
/*     */             
/* 165 */             finishedConfiguration.setValue(null);
/*     */           }
/*     */           else {
/*     */             
/* 169 */             finishedConfiguration.setValue(accumulatedValue.toString());
/*     */           } 
/*     */         }
/*     */         
/* 173 */         if (depth == 0)
/*     */         {
/* 175 */           return finishedConfiguration;
/*     */         }
/*     */       } 
/*     */       
/* 179 */       eventType = parser.next();
/*     */     } 
/*     */     
/* 182 */     throw new IllegalStateException("End of document found before returning to 0 depth");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\Xpp3DomBuilder.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */