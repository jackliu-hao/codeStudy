package cn.hutool.http;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class HttpBase<T> {
   protected static final Charset DEFAULT_CHARSET;
   public static final String HTTP_1_0 = "HTTP/1.0";
   public static final String HTTP_1_1 = "HTTP/1.1";
   protected Map<String, List<String>> headers = new HashMap();
   protected Charset charset;
   protected String httpVersion;
   protected byte[] bodyBytes;

   public HttpBase() {
      this.charset = DEFAULT_CHARSET;
      this.httpVersion = "HTTP/1.1";
   }

   public String header(String name) {
      List<String> values = this.headerList(name);
      return CollectionUtil.isEmpty(values) ? null : (String)values.get(0);
   }

   public List<String> headerList(String name) {
      if (StrUtil.isBlank(name)) {
         return null;
      } else {
         CaseInsensitiveMap<String, List<String>> headersIgnoreCase = new CaseInsensitiveMap(this.headers);
         return (List)headersIgnoreCase.get(name.trim());
      }
   }

   public String header(Header name) {
      return null == name ? null : this.header(name.toString());
   }

   public T header(String name, String value, boolean isOverride) {
      if (null != name && null != value) {
         List<String> values = (List)this.headers.get(name.trim());
         if (!isOverride && !CollectionUtil.isEmpty(values)) {
            values.add(value.trim());
         } else {
            ArrayList<String> valueList = new ArrayList();
            valueList.add(value);
            this.headers.put(name.trim(), valueList);
         }
      }

      return this;
   }

   public T header(Header name, String value, boolean isOverride) {
      return this.header(name.toString(), value, isOverride);
   }

   public T header(Header name, String value) {
      return this.header(name.toString(), value, true);
   }

   public T header(String name, String value) {
      return this.header(name, value, true);
   }

   public T headerMap(Map<String, String> headers, boolean isOverride) {
      if (MapUtil.isEmpty(headers)) {
         return this;
      } else {
         Iterator var3 = headers.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var3.next();
            this.header((String)entry.getKey(), StrUtil.nullToEmpty((CharSequence)entry.getValue()), isOverride);
         }

         return this;
      }
   }

   public T header(Map<String, List<String>> headers) {
      return this.header(headers, false);
   }

   public T header(Map<String, List<String>> headers, boolean isOverride) {
      if (MapUtil.isEmpty(headers)) {
         return this;
      } else {
         Iterator var4 = headers.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry)var4.next();
            String name = (String)entry.getKey();
            Iterator var6 = ((List)entry.getValue()).iterator();

            while(var6.hasNext()) {
               String value = (String)var6.next();
               this.header(name, StrUtil.nullToEmpty(value), isOverride);
            }
         }

         return this;
      }
   }

   public T addHeaders(Map<String, String> headers) {
      if (MapUtil.isEmpty(headers)) {
         return this;
      } else {
         Iterator var2 = headers.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var2.next();
            this.header((String)entry.getKey(), StrUtil.nullToEmpty((CharSequence)entry.getValue()), false);
         }

         return this;
      }
   }

   public T removeHeader(String name) {
      if (name != null) {
         this.headers.remove(name.trim());
      }

      return this;
   }

   public T removeHeader(Header name) {
      return this.removeHeader(name.toString());
   }

   public Map<String, List<String>> headers() {
      return Collections.unmodifiableMap(this.headers);
   }

   public T clearHeaders() {
      this.headers.clear();
      return this;
   }

   public String httpVersion() {
      return this.httpVersion;
   }

   public T httpVersion(String httpVersion) {
      this.httpVersion = httpVersion;
      return this;
   }

   public byte[] bodyBytes() {
      return this.bodyBytes;
   }

   public String charset() {
      return this.charset.name();
   }

   public T charset(String charset) {
      if (StrUtil.isNotBlank(charset)) {
         this.charset(Charset.forName(charset));
      }

      return this;
   }

   public T charset(Charset charset) {
      if (null != charset) {
         this.charset = charset;
      }

      return this;
   }

   public String toString() {
      StringBuilder sb = StrUtil.builder();
      sb.append("Request Headers: ").append("\r\n");
      Iterator var2 = this.headers.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, List<String>> entry = (Map.Entry)var2.next();
         sb.append("    ").append((String)entry.getKey()).append(": ").append(CollUtil.join((Iterable)((Iterable)entry.getValue()), ",")).append("\r\n");
      }

      sb.append("Request Body: ").append("\r\n");
      sb.append("    ").append(StrUtil.str(this.bodyBytes, this.charset)).append("\r\n");
      return sb.toString();
   }

   static {
      DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
   }
}
