/*     */ package cn.hutool.http.webservice;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.XmlUtil;
/*     */ import cn.hutool.http.HttpBase;
/*     */ import cn.hutool.http.HttpGlobalConfig;
/*     */ import cn.hutool.http.HttpRequest;
/*     */ import cn.hutool.http.HttpResponse;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.MessageFactory;
/*     */ import javax.xml.soap.MimeHeaders;
/*     */ import javax.xml.soap.Name;
/*     */ import javax.xml.soap.SOAPBodyElement;
/*     */ import javax.xml.soap.SOAPElement;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import javax.xml.soap.SOAPHeaderElement;
/*     */ import javax.xml.soap.SOAPMessage;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SoapClient
/*     */   extends HttpBase<SoapClient>
/*     */ {
/*     */   private static final String CONTENT_TYPE_SOAP11_TEXT_XML = "text/xml;charset=";
/*     */   private static final String CONTENT_TYPE_SOAP12_SOAP_XML = "application/soap+xml;charset=";
/*     */   private String url;
/*  72 */   private int connectionTimeout = HttpGlobalConfig.getTimeout();
/*     */ 
/*     */ 
/*     */   
/*  76 */   private int readTimeout = HttpGlobalConfig.getTimeout();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MessageFactory factory;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SOAPMessage message;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SOAPBodyElement methodEle;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String namespaceURI;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final SoapProtocol protocol;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SoapClient create(String url) {
/* 108 */     return new SoapClient(url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SoapClient create(String url, SoapProtocol protocol) {
/* 119 */     return new SoapClient(url, protocol);
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
/*     */   public static SoapClient create(String url, SoapProtocol protocol, String namespaceURI) {
/* 132 */     return new SoapClient(url, protocol, namespaceURI);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SoapClient(String url) {
/* 141 */     this(url, SoapProtocol.SOAP_1_1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SoapClient(String url, SoapProtocol protocol) {
/* 151 */     this(url, protocol, (String)null);
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
/*     */   public SoapClient(String url, SoapProtocol protocol, String namespaceURI) {
/* 163 */     this.url = url;
/* 164 */     this.namespaceURI = namespaceURI;
/* 165 */     this.protocol = protocol;
/* 166 */     init(protocol);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SoapClient init(SoapProtocol protocol) {
/*     */     try {
/* 178 */       this.factory = MessageFactory.newInstance(protocol.getValue());
/*     */       
/* 180 */       this.message = this.factory.createMessage();
/* 181 */     } catch (SOAPException e) {
/* 182 */       throw new SoapRuntimeException(e);
/*     */     } 
/*     */     
/* 185 */     return this;
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
/*     */   public SoapClient reset() {
/*     */     try {
/* 199 */       this.message = this.factory.createMessage();
/* 200 */     } catch (SOAPException e) {
/* 201 */       throw new SoapRuntimeException(e);
/*     */     } 
/* 203 */     this.methodEle = null;
/*     */     
/* 205 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SoapClient setCharset(Charset charset) {
/* 216 */     return charset(charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public SoapClient charset(Charset charset) {
/* 221 */     super.charset(charset);
/*     */     try {
/* 223 */       this.message.setProperty("javax.xml.soap.character-set-encoding", charset());
/* 224 */       this.message.setProperty("javax.xml.soap.write-xml-declaration", "true");
/* 225 */     } catch (SOAPException sOAPException) {}
/*     */ 
/*     */ 
/*     */     
/* 229 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SoapClient setUrl(String url) {
/* 239 */     this.url = url;
/* 240 */     return this;
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
/*     */   public SOAPHeaderElement addSOAPHeader(QName name, String actorURI, String roleUri, Boolean mustUnderstand, Boolean relay) {
/* 255 */     SOAPHeaderElement ele = addSOAPHeader(name);
/*     */     try {
/* 257 */       if (StrUtil.isNotBlank(roleUri)) {
/* 258 */         ele.setRole(roleUri);
/*     */       }
/* 260 */       if (null != relay) {
/* 261 */         ele.setRelay(relay.booleanValue());
/*     */       }
/* 263 */     } catch (SOAPException e) {
/* 264 */       throw new SoapRuntimeException(e);
/*     */     } 
/*     */     
/* 267 */     if (StrUtil.isNotBlank(actorURI)) {
/* 268 */       ele.setActor(actorURI);
/*     */     }
/* 270 */     if (null != mustUnderstand) {
/* 271 */       ele.setMustUnderstand(mustUnderstand.booleanValue());
/*     */     }
/*     */     
/* 274 */     return ele;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SOAPHeaderElement addSOAPHeader(String localName) {
/* 285 */     return addSOAPHeader(new QName(localName));
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
/*     */   public SOAPHeaderElement addSOAPHeader(String localName, String value) {
/* 297 */     SOAPHeaderElement soapHeaderElement = addSOAPHeader(localName);
/* 298 */     soapHeaderElement.setTextContent(value);
/* 299 */     return soapHeaderElement;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SOAPHeaderElement addSOAPHeader(QName name) {
/*     */     SOAPHeaderElement ele;
/*     */     try {
/* 312 */       ele = this.message.getSOAPHeader().addHeaderElement(name);
/* 313 */     } catch (SOAPException e) {
/* 314 */       throw new SoapRuntimeException(e);
/*     */     } 
/* 316 */     return ele;
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
/*     */   public SoapClient setMethod(Name name, Map<String, Object> params, boolean useMethodPrefix) {
/* 328 */     return setMethod(new QName(name.getURI(), name.getLocalName(), name.getPrefix()), params, useMethodPrefix);
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
/*     */   public SoapClient setMethod(QName name, Map<String, Object> params, boolean useMethodPrefix) {
/* 340 */     setMethod(name);
/* 341 */     String prefix = useMethodPrefix ? name.getPrefix() : null;
/* 342 */     SOAPBodyElement methodEle = this.methodEle;
/* 343 */     for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)MapUtil.wrap(params)) {
/* 344 */       setParam((SOAPElement)methodEle, entry.getKey(), entry.getValue(), prefix);
/*     */     }
/*     */     
/* 347 */     return this;
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
/*     */   public SoapClient setMethod(String methodName) {
/* 359 */     return setMethod(methodName, (String)ObjectUtil.defaultIfNull(this.namespaceURI, ""));
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
/*     */   public SoapClient setMethod(String methodName, String namespaceURI) {
/*     */     QName qName;
/* 372 */     List<String> methodNameList = StrUtil.split(methodName, ':');
/*     */     
/* 374 */     if (2 == methodNameList.size()) {
/* 375 */       qName = new QName(namespaceURI, methodNameList.get(1), methodNameList.get(0));
/*     */     } else {
/* 377 */       qName = new QName(namespaceURI, methodName);
/*     */     } 
/* 379 */     return setMethod(qName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SoapClient setMethod(QName name) {
/*     */     try {
/* 390 */       this.methodEle = this.message.getSOAPBody().addBodyElement(name);
/* 391 */     } catch (SOAPException e) {
/* 392 */       throw new SoapRuntimeException(e);
/*     */     } 
/*     */     
/* 395 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SoapClient setParam(String name, Object value) {
/* 406 */     return setParam(name, value, true);
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
/*     */   public SoapClient setParam(String name, Object value, boolean useMethodPrefix) {
/* 418 */     setParam((SOAPElement)this.methodEle, name, value, useMethodPrefix ? this.methodEle.getPrefix() : null);
/* 419 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SoapClient setParams(Map<String, Object> params) {
/* 430 */     return setParams(params, true);
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
/*     */   public SoapClient setParams(Map<String, Object> params, boolean useMethodPrefix) {
/* 442 */     for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)MapUtil.wrap(params)) {
/* 443 */       setParam(entry.getKey(), entry.getValue(), useMethodPrefix);
/*     */     }
/* 445 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SOAPBodyElement getMethodEle() {
/* 456 */     return this.methodEle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SOAPMessage getMessage() {
/* 466 */     return this.message;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMsgStr(boolean pretty) {
/* 476 */     return SoapUtil.toString(this.message, pretty, this.charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SoapClient write(OutputStream out) {
/*     */     try {
/* 488 */       this.message.writeTo(out);
/* 489 */     } catch (SOAPException|IOException e) {
/* 490 */       throw new SoapRuntimeException(e);
/*     */     } 
/* 492 */     return this;
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
/*     */   public SoapClient timeout(int milliseconds) {
/* 510 */     setConnectionTimeout(milliseconds);
/* 511 */     setReadTimeout(milliseconds);
/* 512 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SoapClient setConnectionTimeout(int milliseconds) {
/* 523 */     this.connectionTimeout = milliseconds;
/* 524 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SoapClient setReadTimeout(int milliseconds) {
/* 535 */     this.readTimeout = milliseconds;
/* 536 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SOAPMessage sendForMessage() {
/* 545 */     HttpResponse res = sendForResponse();
/* 546 */     MimeHeaders headers = new MimeHeaders();
/* 547 */     for (Map.Entry<String, List<String>> entry : (Iterable<Map.Entry<String, List<String>>>)res.headers().entrySet()) {
/* 548 */       if (StrUtil.isNotEmpty(entry.getKey())) {
/* 549 */         headers.setHeader(entry.getKey(), (String)CollUtil.get(entry.getValue(), 0));
/*     */       }
/*     */     } 
/*     */     try {
/* 553 */       return this.factory.createMessage(headers, res.bodyStream());
/* 554 */     } catch (IOException|SOAPException e) {
/* 555 */       throw new SoapRuntimeException(e);
/*     */     } finally {
/* 557 */       IoUtil.close((Closeable)res);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String send() {
/* 567 */     return send(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String send(boolean pretty) {
/* 577 */     String body = sendForResponse().body();
/* 578 */     return pretty ? XmlUtil.format(body) : body;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse sendForResponse() {
/* 589 */     return ((HttpRequest)HttpRequest.post(this.url)
/* 590 */       .setFollowRedirects(true)
/* 591 */       .setConnectionTimeout(this.connectionTimeout)
/* 592 */       .setReadTimeout(this.readTimeout)
/* 593 */       .contentType(getXmlContentType())
/* 594 */       .header(headers()))
/* 595 */       .body(getMsgStr(false))
/* 596 */       .executeAsync();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getXmlContentType() {
/* 605 */     switch (this.protocol) {
/*     */       case SOAP_1_1:
/* 607 */         return "text/xml;charset=".concat(this.charset.toString());
/*     */       case SOAP_1_2:
/* 609 */         return "application/soap+xml;charset=".concat(this.charset.toString());
/*     */     } 
/* 611 */     throw new SoapRuntimeException("Unsupported protocol: {}", new Object[] { this.protocol });
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
/*     */   private static SOAPElement setParam(SOAPElement ele, String name, Object value, String prefix) {
/*     */     SOAPElement childEle;
/*     */     try {
/* 628 */       if (StrUtil.isNotBlank(prefix)) {
/* 629 */         childEle = ele.addChildElement(name, prefix);
/*     */       } else {
/* 631 */         childEle = ele.addChildElement(name);
/*     */       } 
/* 633 */     } catch (SOAPException e) {
/* 634 */       throw new SoapRuntimeException(e);
/*     */     } 
/*     */     
/* 637 */     if (null != value) {
/* 638 */       if (value instanceof SOAPElement) {
/*     */         
/*     */         try {
/* 641 */           ele.addChildElement((SOAPElement)value);
/* 642 */         } catch (SOAPException e) {
/* 643 */           throw new SoapRuntimeException(e);
/*     */         } 
/* 645 */       } else if (value instanceof Map) {
/*     */ 
/*     */         
/* 648 */         for (Object obj : ((Map)value).entrySet()) {
/* 649 */           Map.Entry entry = (Map.Entry)obj;
/* 650 */           setParam(childEle, entry.getKey().toString(), entry.getValue(), prefix);
/*     */         } 
/*     */       } else {
/*     */         
/* 654 */         childEle.setValue(value.toString());
/*     */       } 
/*     */     }
/*     */     
/* 658 */     return childEle;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\webservice\SoapClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */