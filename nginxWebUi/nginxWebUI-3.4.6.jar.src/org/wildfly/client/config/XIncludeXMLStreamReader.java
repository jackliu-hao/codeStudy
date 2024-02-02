/*     */ package org.wildfly.client.config;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.IllegalCharsetNameException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.NoSuchElementException;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import org.wildfly.client.config._private.ConfigMessages;
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
/*     */ final class XIncludeXMLStreamReader
/*     */   extends AbstractDelegatingXMLStreamReader
/*     */ {
/*     */   static final String XINCLUDE_NS = "http://www.w3.org/2001/XInclude";
/*     */   private ConfigurationXMLStreamReader child;
/*     */   
/*     */   XIncludeXMLStreamReader(ConfigurationXMLStreamReader delegate) {
/*  46 */     super(true, delegate);
/*     */   }
/*     */   
/*     */   private ConfigurationXMLStreamReader getRawDelegate() {
/*  50 */     return super.getDelegate();
/*     */   }
/*     */   
/*     */   protected ConfigurationXMLStreamReader getDelegate() {
/*  54 */     ConfigurationXMLStreamReader child = this.child;
/*  55 */     return (child != null) ? child : getRawDelegate();
/*     */   }
/*     */   
/*     */   public void skipContent() throws ConfigXMLParseException {
/*  59 */     while (getDelegate().hasNext()) {
/*  60 */       switch (getDelegate().next()) {
/*     */         case 1:
/*  62 */           skipContent();
/*     */         case 2:
/*     */           return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int next() throws ConfigXMLParseException {
/*     */     int res;
/*  73 */     ConfigurationXMLStreamReader child = this.child;
/*  74 */     if (child != null) {
/*  75 */       if (child.hasNext()) {
/*  76 */         int next = child.next();
/*  77 */         if (next != 8) {
/*  78 */           return next;
/*     */         }
/*  80 */         child.close();
/*     */       } 
/*     */       
/*  83 */       this.child = null;
/*     */     } 
/*  85 */     ConfigurationXMLStreamReader delegate = getDelegate();
/*  86 */     if (!delegate.hasNext()) {
/*  87 */       throw new NoSuchElementException();
/*     */     }
/*     */     
/*     */     while (true) {
/*  91 */       res = delegate.next();
/*  92 */       if (res == 1) {
/*  93 */         String namespaceURI = delegate.getNamespaceURI();
/*  94 */         if ("http://www.w3.org/2001/XInclude".equals(namespaceURI)) {
/*  95 */           ConfigurationXMLStreamReader nested; boolean ok; switch (delegate.getLocalName()) {
/*     */             case "include":
/*  97 */               nested = processInclude();
/*  98 */               ok = false;
/*     */               
/* 100 */               try { if (nested != null && nested.hasNext())
/* 101 */                 { int eventType = nested.next();
/* 102 */                   if (eventType == 7)
/* 103 */                   { if (!nested.hasNext())
/*     */                     
/*     */                     { 
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
/* 118 */                       if (!ok)
/* 119 */                         try { nested.close(); }
/* 120 */                         catch (ConfigXMLParseException configXMLParseException) {}  continue; }  eventType = nested.next(); }  this.child = new XIncludeXMLStreamReader(nested); ok = true; return eventType; }  ok = true; } finally { if (!ok) try { nested.close(); } catch (ConfigXMLParseException configXMLParseException) {}
/*     */                  }
/*     */               
/*     */               continue;
/*     */           } 
/* 125 */           throw ConfigMessages.msg.unexpectedElement(delegate.getLocalName(), namespaceURI, getLocation());
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 130 */         return res;
/*     */       }  break;
/*     */     } 
/* 133 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ConfigurationXMLStreamReader processInclude() throws ConfigXMLParseException {
/* 139 */     ScopedXMLStreamReader includeElement = new ScopedXMLStreamReader(false, getRawDelegate());
/* 140 */     ConfigurationXMLStreamReader delegate = getDelegate();
/* 141 */     int attributeCount = delegate.getAttributeCount();
/* 142 */     URI href = null;
/* 143 */     Charset textCharset = StandardCharsets.UTF_8;
/* 144 */     boolean fallback = false;
/* 145 */     String accept = null;
/* 146 */     String acceptLanguage = null;
/* 147 */     boolean parseAsText = false;
/* 148 */     for (int i = 0; i < attributeCount; i++) {
/* 149 */       if (delegate.getAttributeNamespace(i) == null) {
/* 150 */         switch (delegate.getAttributeLocalName(i)) {
/*     */           case "href":
/*     */             try {
/* 153 */               href = new URI(delegate.getAttributeValue(i));
/* 154 */             } catch (URISyntaxException e) {
/* 155 */               throw new ConfigXMLParseException("Invalid include URI", getLocation(), e);
/*     */             } 
/* 157 */             if (href.getFragment() != null) {
/* 158 */               throw new ConfigXMLParseException("Invalid include URI: must not contain fragment identifier", getLocation());
/*     */             }
/* 160 */             fallback |= href.isOpaque();
/*     */             break;
/*     */           
/*     */           case "parse":
/* 164 */             switch (delegate.getAttributeValue(i)) { case "xml":
/* 165 */                 parseAsText = false; break;
/* 166 */               case "text": parseAsText = true; break; }
/* 167 */              throw new ConfigXMLParseException("Invalid include directive: unknown parse type (must be \"text\" or \"xml\")", getLocation());
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           case "xpointer":
/* 173 */             fallback = true;
/*     */             break;
/*     */           
/*     */           case "encoding":
/*     */             try {
/* 178 */               textCharset = Charset.forName(delegate.getAttributeValue(i));
/* 179 */             } catch (IllegalCharsetNameException|java.nio.charset.UnsupportedCharsetException e) {
/*     */               
/* 181 */               fallback = true;
/*     */             } 
/*     */             break;
/*     */ 
/*     */           
/*     */           case "accept":
/* 187 */             accept = delegate.getAttributeValue(i);
/*     */             break;
/*     */           
/*     */           case "accept-language":
/* 191 */             acceptLanguage = delegate.getAttributeValue(i);
/*     */             break;
/*     */         } 
/*     */ 
/*     */       
/*     */       }
/*     */     } 
/* 198 */     if (!fallback) {
/* 199 */       ConfigurationXMLStreamReader child; if (href == null) {
/* 200 */         throw delegate.missingRequiredAttribute(null, "href");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 206 */         if (!href.isAbsolute()) {
/* 207 */           href = getRawDelegate().getUri().resolve(href);
/*     */         }
/* 209 */         URL url = href.toURL();
/* 210 */         URLConnection connection = url.openConnection();
/* 211 */         connection.addRequestProperty("Accept", (accept != null) ? accept : (parseAsText ? "text/plain,text/*" : "application/xml,text/xml,application/*+xml,text/*+xml"));
/* 212 */         if (acceptLanguage != null) connection.addRequestProperty("Accept-Language", acceptLanguage); 
/* 213 */         InputStream inputStream = connection.getInputStream();
/*     */         try {
/* 215 */           if (parseAsText) {
/* 216 */             child = new TextXMLStreamReader(textCharset, inputStream, this, href);
/*     */           } else {
/* 218 */             child = new XIncludeXMLStreamReader(new BasicXMLStreamReader(getLocation(), getXmlInputFactory().createXMLStreamReader(inputStream), href, getXmlInputFactory(), inputStream));
/*     */           } 
/* 220 */         } catch (XMLStreamException e) {
/*     */           try {
/* 222 */             inputStream.close();
/* 223 */           } catch (Throwable e1) {
/* 224 */             e.addSuppressed(e1);
/*     */           } 
/* 226 */           throw ConfigXMLParseException.from(e, getUri(), getIncludedFrom());
/* 227 */         } catch (Throwable t) {
/*     */           try {
/* 229 */             inputStream.close();
/* 230 */           } catch (Throwable e1) {
/* 231 */             t.addSuppressed(e1);
/*     */           } 
/* 233 */           throw t;
/*     */         } 
/* 235 */       } catch (IOException e) {
/* 236 */         throw ConfigXMLParseException.from(e, getUri(), getIncludedFrom());
/*     */       } 
/*     */       
/*     */       try {
/* 240 */         getRawDelegate().skipContent();
/* 241 */         return child;
/* 242 */       } catch (Throwable t) {
/*     */         try {
/* 244 */           child.close();
/* 245 */         } catch (Throwable t2) {
/* 246 */           t.addSuppressed(t2);
/*     */         } 
/* 248 */         throw t;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 253 */     while (hasNext()) {
/* 254 */       int level; switch (super.next()) {
/*     */         case 1:
/* 256 */           if ("http://www.w3.org/2001/XInclude".equals(getNamespaceURI()) && "fallback".equals(getLocalName())) {
/* 257 */             return this.child = new ScopedXMLStreamReader(true, new DrainingXMLStreamReader(false, includeElement));
/*     */           }
/* 259 */           level = 0;
/* 260 */           while (hasNext()) {
/* 261 */             switch (super.next()) {
/*     */               case 1:
/* 263 */                 level++;
/*     */ 
/*     */               
/*     */               case 2:
/* 267 */                 if (level-- == 0) {
/*     */                   break;
/*     */                 }
/*     */             } 
/*     */ 
/*     */ 
/*     */           
/*     */           } 
/*     */ 
/*     */ 
/*     */         
/*     */         case 2:
/* 279 */           return null;
/*     */       } 
/*     */     
/*     */     } 
/* 283 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\XIncludeXMLStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */