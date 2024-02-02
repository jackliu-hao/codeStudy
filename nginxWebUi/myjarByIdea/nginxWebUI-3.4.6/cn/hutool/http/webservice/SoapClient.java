package cn.hutool.http.webservice;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpBase;
import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

public class SoapClient extends HttpBase<SoapClient> {
   private static final String CONTENT_TYPE_SOAP11_TEXT_XML = "text/xml;charset=";
   private static final String CONTENT_TYPE_SOAP12_SOAP_XML = "application/soap+xml;charset=";
   private String url;
   private int connectionTimeout;
   private int readTimeout;
   private MessageFactory factory;
   private SOAPMessage message;
   private SOAPBodyElement methodEle;
   private final String namespaceURI;
   private final SoapProtocol protocol;

   public static SoapClient create(String url) {
      return new SoapClient(url);
   }

   public static SoapClient create(String url, SoapProtocol protocol) {
      return new SoapClient(url, protocol);
   }

   public static SoapClient create(String url, SoapProtocol protocol, String namespaceURI) {
      return new SoapClient(url, protocol, namespaceURI);
   }

   public SoapClient(String url) {
      this(url, SoapProtocol.SOAP_1_1);
   }

   public SoapClient(String url, SoapProtocol protocol) {
      this(url, protocol, (String)null);
   }

   public SoapClient(String url, SoapProtocol protocol, String namespaceURI) {
      this.connectionTimeout = HttpGlobalConfig.getTimeout();
      this.readTimeout = HttpGlobalConfig.getTimeout();
      this.url = url;
      this.namespaceURI = namespaceURI;
      this.protocol = protocol;
      this.init(protocol);
   }

   public SoapClient init(SoapProtocol protocol) {
      try {
         this.factory = MessageFactory.newInstance(protocol.getValue());
         this.message = this.factory.createMessage();
         return this;
      } catch (SOAPException var3) {
         throw new SoapRuntimeException(var3);
      }
   }

   public SoapClient reset() {
      try {
         this.message = this.factory.createMessage();
      } catch (SOAPException var2) {
         throw new SoapRuntimeException(var2);
      }

      this.methodEle = null;
      return this;
   }

   public SoapClient setCharset(Charset charset) {
      return this.charset(charset);
   }

   public SoapClient charset(Charset charset) {
      super.charset(charset);

      try {
         this.message.setProperty("javax.xml.soap.character-set-encoding", this.charset());
         this.message.setProperty("javax.xml.soap.write-xml-declaration", "true");
      } catch (SOAPException var3) {
      }

      return this;
   }

   public SoapClient setUrl(String url) {
      this.url = url;
      return this;
   }

   public SOAPHeaderElement addSOAPHeader(QName name, String actorURI, String roleUri, Boolean mustUnderstand, Boolean relay) {
      SOAPHeaderElement ele = this.addSOAPHeader(name);

      try {
         if (StrUtil.isNotBlank(roleUri)) {
            ele.setRole(roleUri);
         }

         if (null != relay) {
            ele.setRelay(relay);
         }
      } catch (SOAPException var8) {
         throw new SoapRuntimeException(var8);
      }

      if (StrUtil.isNotBlank(actorURI)) {
         ele.setActor(actorURI);
      }

      if (null != mustUnderstand) {
         ele.setMustUnderstand(mustUnderstand);
      }

      return ele;
   }

   public SOAPHeaderElement addSOAPHeader(String localName) {
      return this.addSOAPHeader(new QName(localName));
   }

   public SOAPHeaderElement addSOAPHeader(String localName, String value) {
      SOAPHeaderElement soapHeaderElement = this.addSOAPHeader(localName);
      soapHeaderElement.setTextContent(value);
      return soapHeaderElement;
   }

   public SOAPHeaderElement addSOAPHeader(QName name) {
      try {
         SOAPHeaderElement ele = this.message.getSOAPHeader().addHeaderElement(name);
         return ele;
      } catch (SOAPException var4) {
         throw new SoapRuntimeException(var4);
      }
   }

   public SoapClient setMethod(Name name, Map<String, Object> params, boolean useMethodPrefix) {
      return this.setMethod(new QName(name.getURI(), name.getLocalName(), name.getPrefix()), params, useMethodPrefix);
   }

   public SoapClient setMethod(QName name, Map<String, Object> params, boolean useMethodPrefix) {
      this.setMethod(name);
      String prefix = useMethodPrefix ? name.getPrefix() : null;
      SOAPBodyElement methodEle = this.methodEle;
      Iterator var6 = MapUtil.wrap(params).iterator();

      while(var6.hasNext()) {
         Map.Entry<String, Object> entry = (Map.Entry)var6.next();
         setParam(methodEle, (String)entry.getKey(), entry.getValue(), prefix);
      }

      return this;
   }

   public SoapClient setMethod(String methodName) {
      return this.setMethod(methodName, (String)ObjectUtil.defaultIfNull(this.namespaceURI, (Object)""));
   }

   public SoapClient setMethod(String methodName, String namespaceURI) {
      List<String> methodNameList = StrUtil.split(methodName, ':');
      QName qName;
      if (2 == methodNameList.size()) {
         qName = new QName(namespaceURI, (String)methodNameList.get(1), (String)methodNameList.get(0));
      } else {
         qName = new QName(namespaceURI, methodName);
      }

      return this.setMethod(qName);
   }

   public SoapClient setMethod(QName name) {
      try {
         this.methodEle = this.message.getSOAPBody().addBodyElement(name);
         return this;
      } catch (SOAPException var3) {
         throw new SoapRuntimeException(var3);
      }
   }

   public SoapClient setParam(String name, Object value) {
      return this.setParam(name, value, true);
   }

   public SoapClient setParam(String name, Object value, boolean useMethodPrefix) {
      setParam(this.methodEle, name, value, useMethodPrefix ? this.methodEle.getPrefix() : null);
      return this;
   }

   public SoapClient setParams(Map<String, Object> params) {
      return this.setParams(params, true);
   }

   public SoapClient setParams(Map<String, Object> params, boolean useMethodPrefix) {
      Iterator var3 = MapUtil.wrap(params).iterator();

      while(var3.hasNext()) {
         Map.Entry<String, Object> entry = (Map.Entry)var3.next();
         this.setParam((String)entry.getKey(), entry.getValue(), useMethodPrefix);
      }

      return this;
   }

   public SOAPBodyElement getMethodEle() {
      return this.methodEle;
   }

   public SOAPMessage getMessage() {
      return this.message;
   }

   public String getMsgStr(boolean pretty) {
      return SoapUtil.toString(this.message, pretty, this.charset);
   }

   public SoapClient write(OutputStream out) {
      try {
         this.message.writeTo(out);
         return this;
      } catch (IOException | SOAPException var3) {
         throw new SoapRuntimeException(var3);
      }
   }

   public SoapClient timeout(int milliseconds) {
      this.setConnectionTimeout(milliseconds);
      this.setReadTimeout(milliseconds);
      return this;
   }

   public SoapClient setConnectionTimeout(int milliseconds) {
      this.connectionTimeout = milliseconds;
      return this;
   }

   public SoapClient setReadTimeout(int milliseconds) {
      this.readTimeout = milliseconds;
      return this;
   }

   public SOAPMessage sendForMessage() {
      HttpResponse res = this.sendForResponse();
      MimeHeaders headers = new MimeHeaders();
      Iterator var3 = res.headers().entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, List<String>> entry = (Map.Entry)var3.next();
         if (StrUtil.isNotEmpty((CharSequence)entry.getKey())) {
            headers.setHeader((String)entry.getKey(), (String)CollUtil.get((Collection)entry.getValue(), 0));
         }
      }

      SOAPMessage var10;
      try {
         var10 = this.factory.createMessage(headers, res.bodyStream());
      } catch (SOAPException | IOException var8) {
         throw new SoapRuntimeException(var8);
      } finally {
         IoUtil.close(res);
      }

      return var10;
   }

   public String send() {
      return this.send(false);
   }

   public String send(boolean pretty) {
      String body = this.sendForResponse().body();
      return pretty ? XmlUtil.format(body) : body;
   }

   public HttpResponse sendForResponse() {
      return ((HttpRequest)HttpRequest.post(this.url).setFollowRedirects(true).setConnectionTimeout(this.connectionTimeout).setReadTimeout(this.readTimeout).contentType(this.getXmlContentType()).header(this.headers())).body(this.getMsgStr(false)).executeAsync();
   }

   private String getXmlContentType() {
      switch (this.protocol) {
         case SOAP_1_1:
            return "text/xml;charset=".concat(this.charset.toString());
         case SOAP_1_2:
            return "application/soap+xml;charset=".concat(this.charset.toString());
         default:
            throw new SoapRuntimeException("Unsupported protocol: {}", new Object[]{this.protocol});
      }
   }

   private static SOAPElement setParam(SOAPElement ele, String name, Object value, String prefix) {
      SOAPElement childEle;
      try {
         if (StrUtil.isNotBlank(prefix)) {
            childEle = ele.addChildElement(name, prefix);
         } else {
            childEle = ele.addChildElement(name);
         }
      } catch (SOAPException var9) {
         throw new SoapRuntimeException(var9);
      }

      if (null != value) {
         if (value instanceof SOAPElement) {
            try {
               ele.addChildElement((SOAPElement)value);
            } catch (SOAPException var8) {
               throw new SoapRuntimeException(var8);
            }
         } else if (value instanceof Map) {
            Iterator var6 = ((Map)value).entrySet().iterator();

            while(var6.hasNext()) {
               Object obj = var6.next();
               Map.Entry entry = (Map.Entry)obj;
               setParam(childEle, entry.getKey().toString(), entry.getValue(), prefix);
            }
         } else {
            childEle.setValue(value.toString());
         }
      }

      return childEle;
   }
}
