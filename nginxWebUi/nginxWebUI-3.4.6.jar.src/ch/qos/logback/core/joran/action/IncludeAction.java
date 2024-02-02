/*     */ package ch.qos.logback.core.joran.action;
/*     */ 
/*     */ import ch.qos.logback.core.joran.event.SaxEvent;
/*     */ import ch.qos.logback.core.joran.event.SaxEventRecorder;
/*     */ import ch.qos.logback.core.joran.spi.ActionException;
/*     */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*     */ import ch.qos.logback.core.joran.spi.JoranException;
/*     */ import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
/*     */ import ch.qos.logback.core.util.Loader;
/*     */ import ch.qos.logback.core.util.OptionHelper;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.List;
/*     */ import org.xml.sax.Attributes;
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
/*     */ public class IncludeAction
/*     */   extends Action
/*     */ {
/*     */   private static final String INCLUDED_TAG = "included";
/*     */   private static final String FILE_ATTR = "file";
/*     */   private static final String URL_ATTR = "url";
/*     */   private static final String RESOURCE_ATTR = "resource";
/*     */   private static final String OPTIONAL_ATTR = "optional";
/*     */   private String attributeInUse;
/*     */   private boolean optional;
/*     */   
/*     */   public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
/*  49 */     SaxEventRecorder recorder = new SaxEventRecorder(this.context);
/*     */     
/*  51 */     this.attributeInUse = null;
/*  52 */     this.optional = OptionHelper.toBoolean(attributes.getValue("optional"), false);
/*     */     
/*  54 */     if (!checkAttributes(attributes)) {
/*     */       return;
/*     */     }
/*     */     
/*  58 */     InputStream in = getInputStream(ec, attributes);
/*     */     
/*     */     try {
/*  61 */       if (in != null) {
/*  62 */         parseAndRecord(in, recorder);
/*     */         
/*  64 */         trimHeadAndTail(recorder);
/*     */ 
/*     */         
/*  67 */         ec.getJoranInterpreter().getEventPlayer().addEventsDynamically(recorder.saxEventList, 2);
/*     */       } 
/*  69 */     } catch (JoranException e) {
/*  70 */       addError("Error while parsing  " + this.attributeInUse, (Throwable)e);
/*     */     } finally {
/*  72 */       close(in);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void close(InputStream in) {
/*  78 */     if (in != null) {
/*     */       try {
/*  80 */         in.close();
/*  81 */       } catch (IOException iOException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean checkAttributes(Attributes attributes) {
/*  87 */     String fileAttribute = attributes.getValue("file");
/*  88 */     String urlAttribute = attributes.getValue("url");
/*  89 */     String resourceAttribute = attributes.getValue("resource");
/*     */     
/*  91 */     int count = 0;
/*     */     
/*  93 */     if (!OptionHelper.isEmpty(fileAttribute)) {
/*  94 */       count++;
/*     */     }
/*  96 */     if (!OptionHelper.isEmpty(urlAttribute)) {
/*  97 */       count++;
/*     */     }
/*  99 */     if (!OptionHelper.isEmpty(resourceAttribute)) {
/* 100 */       count++;
/*     */     }
/*     */     
/* 103 */     if (count == 0) {
/* 104 */       addError("One of \"path\", \"resource\" or \"url\" attributes must be set.");
/* 105 */       return false;
/* 106 */     }  if (count > 1) {
/* 107 */       addError("Only one of \"file\", \"url\" or \"resource\" attributes should be set.");
/* 108 */       return false;
/* 109 */     }  if (count == 1) {
/* 110 */       return true;
/*     */     }
/* 112 */     throw new IllegalStateException("Count value [" + count + "] is not expected");
/*     */   }
/*     */   
/*     */   URL attributeToURL(String urlAttribute) {
/*     */     try {
/* 117 */       return new URL(urlAttribute);
/* 118 */     } catch (MalformedURLException mue) {
/* 119 */       String errMsg = "URL [" + urlAttribute + "] is not well formed.";
/* 120 */       addError(errMsg, mue);
/* 121 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   InputStream openURL(URL url) {
/*     */     try {
/* 127 */       return url.openStream();
/* 128 */     } catch (IOException e) {
/* 129 */       optionalWarning("Failed to open [" + url.toString() + "]");
/* 130 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   URL resourceAsURL(String resourceAttribute) {
/* 135 */     URL url = Loader.getResourceBySelfClassLoader(resourceAttribute);
/* 136 */     if (url == null) {
/* 137 */       optionalWarning("Could not find resource corresponding to [" + resourceAttribute + "]");
/* 138 */       return null;
/*     */     } 
/* 140 */     return url;
/*     */   }
/*     */   
/*     */   private void optionalWarning(String msg) {
/* 144 */     if (!this.optional) {
/* 145 */       addWarn(msg);
/*     */     }
/*     */   }
/*     */   
/*     */   URL filePathAsURL(String path) {
/* 150 */     URI uri = (new File(path)).toURI();
/*     */     try {
/* 152 */       return uri.toURL();
/* 153 */     } catch (MalformedURLException e) {
/*     */       
/* 155 */       e.printStackTrace();
/* 156 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   URL getInputURL(InterpretationContext ec, Attributes attributes) {
/* 161 */     String fileAttribute = attributes.getValue("file");
/* 162 */     String urlAttribute = attributes.getValue("url");
/* 163 */     String resourceAttribute = attributes.getValue("resource");
/*     */     
/* 165 */     if (!OptionHelper.isEmpty(fileAttribute)) {
/* 166 */       this.attributeInUse = ec.subst(fileAttribute);
/* 167 */       return filePathAsURL(this.attributeInUse);
/*     */     } 
/*     */     
/* 170 */     if (!OptionHelper.isEmpty(urlAttribute)) {
/* 171 */       this.attributeInUse = ec.subst(urlAttribute);
/* 172 */       return attributeToURL(this.attributeInUse);
/*     */     } 
/*     */     
/* 175 */     if (!OptionHelper.isEmpty(resourceAttribute)) {
/* 176 */       this.attributeInUse = ec.subst(resourceAttribute);
/* 177 */       return resourceAsURL(this.attributeInUse);
/*     */     } 
/*     */     
/* 180 */     throw new IllegalStateException("A URL stream should have been returned");
/*     */   }
/*     */ 
/*     */   
/*     */   InputStream getInputStream(InterpretationContext ec, Attributes attributes) {
/* 185 */     URL inputURL = getInputURL(ec, attributes);
/* 186 */     if (inputURL == null) {
/* 187 */       return null;
/*     */     }
/* 189 */     ConfigurationWatchListUtil.addToWatchList(this.context, inputURL);
/* 190 */     return openURL(inputURL);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void trimHeadAndTail(SaxEventRecorder recorder) {
/* 197 */     List<SaxEvent> saxEventList = recorder.saxEventList;
/*     */     
/* 199 */     if (saxEventList.size() == 0) {
/*     */       return;
/*     */     }
/*     */     
/* 203 */     SaxEvent first = saxEventList.get(0);
/* 204 */     if (first != null && first.qName.equalsIgnoreCase("included")) {
/* 205 */       saxEventList.remove(0);
/*     */     }
/*     */     
/* 208 */     SaxEvent last = saxEventList.get(recorder.saxEventList.size() - 1);
/* 209 */     if (last != null && last.qName.equalsIgnoreCase("included")) {
/* 210 */       saxEventList.remove(recorder.saxEventList.size() - 1);
/*     */     }
/*     */   }
/*     */   
/*     */   private void parseAndRecord(InputStream inputSource, SaxEventRecorder recorder) throws JoranException {
/* 215 */     recorder.setContext(this.context);
/* 216 */     recorder.recordEvents(inputSource);
/*     */   }
/*     */   
/*     */   public void end(InterpretationContext ec, String name) throws ActionException {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\IncludeAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */