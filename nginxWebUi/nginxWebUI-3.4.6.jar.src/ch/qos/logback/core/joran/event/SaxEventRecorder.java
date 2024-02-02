/*     */ package ch.qos.logback.core.joran.event;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.joran.spi.ElementPath;
/*     */ import ch.qos.logback.core.joran.spi.JoranException;
/*     */ import ch.qos.logback.core.spi.ContextAware;
/*     */ import ch.qos.logback.core.spi.ContextAwareImpl;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.Locator;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
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
/*     */ public class SaxEventRecorder
/*     */   extends DefaultHandler
/*     */   implements ContextAware
/*     */ {
/*     */   final ContextAwareImpl cai;
/*     */   public List<SaxEvent> saxEventList;
/*     */   Locator locator;
/*     */   ElementPath globalElementPath;
/*     */   
/*     */   public SaxEventRecorder(Context context) {
/*  48 */     this.saxEventList = new ArrayList<SaxEvent>();
/*     */     
/*  50 */     this.globalElementPath = new ElementPath();
/*     */     this.cai = new ContextAwareImpl(context, this);
/*     */   } public final void recordEvents(InputStream inputStream) throws JoranException {
/*  53 */     recordEvents(new InputSource(inputStream));
/*     */   }
/*     */   
/*     */   public List<SaxEvent> recordEvents(InputSource inputSource) throws JoranException {
/*  57 */     SAXParser saxParser = buildSaxParser();
/*     */     try {
/*  59 */       saxParser.parse(inputSource, this);
/*  60 */       return this.saxEventList;
/*  61 */     } catch (IOException ie) {
/*  62 */       handleError("I/O error occurred while parsing xml file", ie);
/*  63 */     } catch (SAXException se) {
/*     */       
/*  65 */       throw new JoranException("Problem parsing XML document. See previously reported errors.", se);
/*  66 */     } catch (Exception ex) {
/*  67 */       handleError("Unexpected exception while parsing XML document.", ex);
/*     */     } 
/*  69 */     throw new IllegalStateException("This point can never be reached");
/*     */   }
/*     */   
/*     */   private void handleError(String errMsg, Throwable t) throws JoranException {
/*  73 */     addError(errMsg, t);
/*  74 */     throw new JoranException(errMsg, t);
/*     */   }
/*     */   
/*     */   private SAXParser buildSaxParser() throws JoranException {
/*     */     try {
/*  79 */       SAXParserFactory spf = SAXParserFactory.newInstance();
/*  80 */       spf.setValidating(false);
/*     */       
/*  82 */       spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
/*  83 */       spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
/*  84 */       spf.setNamespaceAware(true);
/*  85 */       return spf.newSAXParser();
/*  86 */     } catch (Exception pce) {
/*  87 */       String errMsg = "Parser configuration error occurred";
/*  88 */       addError(errMsg, pce);
/*  89 */       throw new JoranException(errMsg, pce);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void startDocument() {}
/*     */   
/*     */   public Locator getLocator() {
/*  97 */     return this.locator;
/*     */   }
/*     */   
/*     */   public void setDocumentLocator(Locator l) {
/* 101 */     this.locator = l;
/*     */   }
/*     */ 
/*     */   
/*     */   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
/* 106 */     String tagName = getTagName(localName, qName);
/* 107 */     this.globalElementPath.push(tagName);
/* 108 */     ElementPath current = this.globalElementPath.duplicate();
/* 109 */     this.saxEventList.add(new StartEvent(current, namespaceURI, localName, qName, atts, getLocator()));
/*     */   }
/*     */   
/*     */   public void characters(char[] ch, int start, int length) {
/* 113 */     String bodyStr = new String(ch, start, length);
/* 114 */     SaxEvent lastEvent = getLastEvent();
/* 115 */     if (lastEvent instanceof BodyEvent) {
/* 116 */       BodyEvent be = (BodyEvent)lastEvent;
/* 117 */       be.append(bodyStr);
/*     */     
/*     */     }
/* 120 */     else if (!isSpaceOnly(bodyStr)) {
/* 121 */       this.saxEventList.add(new BodyEvent(bodyStr, getLocator()));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isSpaceOnly(String bodyStr) {
/* 127 */     String bodyTrimmed = bodyStr.trim();
/* 128 */     return (bodyTrimmed.length() == 0);
/*     */   }
/*     */   
/*     */   SaxEvent getLastEvent() {
/* 132 */     if (this.saxEventList.isEmpty()) {
/* 133 */       return null;
/*     */     }
/* 135 */     int size = this.saxEventList.size();
/* 136 */     return this.saxEventList.get(size - 1);
/*     */   }
/*     */   
/*     */   public void endElement(String namespaceURI, String localName, String qName) {
/* 140 */     this.saxEventList.add(new EndEvent(namespaceURI, localName, qName, getLocator()));
/* 141 */     this.globalElementPath.pop();
/*     */   }
/*     */   
/*     */   String getTagName(String localName, String qName) {
/* 145 */     String tagName = localName;
/* 146 */     if (tagName == null || tagName.length() < 1) {
/* 147 */       tagName = qName;
/*     */     }
/* 149 */     return tagName;
/*     */   }
/*     */   
/*     */   public void error(SAXParseException spe) throws SAXException {
/* 153 */     addError("XML_PARSING - Parsing error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
/* 154 */     addError(spe.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public void fatalError(SAXParseException spe) throws SAXException {
/* 159 */     addError("XML_PARSING - Parsing fatal error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
/* 160 */     addError(spe.toString());
/*     */   }
/*     */   
/*     */   public void warning(SAXParseException spe) throws SAXException {
/* 164 */     addWarn("XML_PARSING - Parsing warning on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber(), spe);
/*     */   }
/*     */   
/*     */   public void addError(String msg) {
/* 168 */     this.cai.addError(msg);
/*     */   }
/*     */   
/*     */   public void addError(String msg, Throwable ex) {
/* 172 */     this.cai.addError(msg, ex);
/*     */   }
/*     */   
/*     */   public void addInfo(String msg) {
/* 176 */     this.cai.addInfo(msg);
/*     */   }
/*     */   
/*     */   public void addInfo(String msg, Throwable ex) {
/* 180 */     this.cai.addInfo(msg, ex);
/*     */   }
/*     */   
/*     */   public void addStatus(Status status) {
/* 184 */     this.cai.addStatus(status);
/*     */   }
/*     */   
/*     */   public void addWarn(String msg) {
/* 188 */     this.cai.addWarn(msg);
/*     */   }
/*     */   
/*     */   public void addWarn(String msg, Throwable ex) {
/* 192 */     this.cai.addWarn(msg, ex);
/*     */   }
/*     */   
/*     */   public Context getContext() {
/* 196 */     return this.cai.getContext();
/*     */   }
/*     */   
/*     */   public void setContext(Context context) {
/* 200 */     this.cai.setContext(context);
/*     */   }
/*     */   
/*     */   public List<SaxEvent> getSaxEventList() {
/* 204 */     return this.saxEventList;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\event\SaxEventRecorder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */