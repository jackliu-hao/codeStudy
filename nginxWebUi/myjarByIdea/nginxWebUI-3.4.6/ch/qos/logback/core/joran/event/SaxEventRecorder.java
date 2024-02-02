package ch.qos.logback.core.joran.event;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareImpl;
import ch.qos.logback.core.status.Status;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxEventRecorder extends DefaultHandler implements ContextAware {
   final ContextAwareImpl cai;
   public List<SaxEvent> saxEventList = new ArrayList();
   Locator locator;
   ElementPath globalElementPath = new ElementPath();

   public SaxEventRecorder(Context context) {
      this.cai = new ContextAwareImpl(context, this);
   }

   public final void recordEvents(InputStream inputStream) throws JoranException {
      this.recordEvents(new InputSource(inputStream));
   }

   public List<SaxEvent> recordEvents(InputSource inputSource) throws JoranException {
      SAXParser saxParser = this.buildSaxParser();

      try {
         saxParser.parse(inputSource, this);
         return this.saxEventList;
      } catch (IOException var4) {
         this.handleError("I/O error occurred while parsing xml file", var4);
      } catch (SAXException var5) {
         throw new JoranException("Problem parsing XML document. See previously reported errors.", var5);
      } catch (Exception var6) {
         this.handleError("Unexpected exception while parsing XML document.", var6);
      }

      throw new IllegalStateException("This point can never be reached");
   }

   private void handleError(String errMsg, Throwable t) throws JoranException {
      this.addError(errMsg, t);
      throw new JoranException(errMsg, t);
   }

   private SAXParser buildSaxParser() throws JoranException {
      try {
         SAXParserFactory spf = SAXParserFactory.newInstance();
         spf.setValidating(false);
         spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
         spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
         spf.setNamespaceAware(true);
         return spf.newSAXParser();
      } catch (Exception var3) {
         String errMsg = "Parser configuration error occurred";
         this.addError(errMsg, var3);
         throw new JoranException(errMsg, var3);
      }
   }

   public void startDocument() {
   }

   public Locator getLocator() {
      return this.locator;
   }

   public void setDocumentLocator(Locator l) {
      this.locator = l;
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
      String tagName = this.getTagName(localName, qName);
      this.globalElementPath.push(tagName);
      ElementPath current = this.globalElementPath.duplicate();
      this.saxEventList.add(new StartEvent(current, namespaceURI, localName, qName, atts, this.getLocator()));
   }

   public void characters(char[] ch, int start, int length) {
      String bodyStr = new String(ch, start, length);
      SaxEvent lastEvent = this.getLastEvent();
      if (lastEvent instanceof BodyEvent) {
         BodyEvent be = (BodyEvent)lastEvent;
         be.append(bodyStr);
      } else if (!this.isSpaceOnly(bodyStr)) {
         this.saxEventList.add(new BodyEvent(bodyStr, this.getLocator()));
      }

   }

   boolean isSpaceOnly(String bodyStr) {
      String bodyTrimmed = bodyStr.trim();
      return bodyTrimmed.length() == 0;
   }

   SaxEvent getLastEvent() {
      if (this.saxEventList.isEmpty()) {
         return null;
      } else {
         int size = this.saxEventList.size();
         return (SaxEvent)this.saxEventList.get(size - 1);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName) {
      this.saxEventList.add(new EndEvent(namespaceURI, localName, qName, this.getLocator()));
      this.globalElementPath.pop();
   }

   String getTagName(String localName, String qName) {
      String tagName = localName;
      if (localName == null || localName.length() < 1) {
         tagName = qName;
      }

      return tagName;
   }

   public void error(SAXParseException spe) throws SAXException {
      this.addError("XML_PARSING - Parsing error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
      this.addError(spe.toString());
   }

   public void fatalError(SAXParseException spe) throws SAXException {
      this.addError("XML_PARSING - Parsing fatal error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
      this.addError(spe.toString());
   }

   public void warning(SAXParseException spe) throws SAXException {
      this.addWarn("XML_PARSING - Parsing warning on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber(), spe);
   }

   public void addError(String msg) {
      this.cai.addError(msg);
   }

   public void addError(String msg, Throwable ex) {
      this.cai.addError(msg, ex);
   }

   public void addInfo(String msg) {
      this.cai.addInfo(msg);
   }

   public void addInfo(String msg, Throwable ex) {
      this.cai.addInfo(msg, ex);
   }

   public void addStatus(Status status) {
      this.cai.addStatus(status);
   }

   public void addWarn(String msg) {
      this.cai.addWarn(msg);
   }

   public void addWarn(String msg, Throwable ex) {
      this.cai.addWarn(msg, ex);
   }

   public Context getContext() {
      return this.cai.getContext();
   }

   public void setContext(Context context) {
      this.cai.setContext(context);
   }

   public List<SaxEvent> getSaxEventList() {
      return this.saxEventList;
   }
}
