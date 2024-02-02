package org.apache.http.entity;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.TextUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public final class ContentType implements Serializable {
   private static final long serialVersionUID = -7768694718232371896L;
   public static final ContentType APPLICATION_ATOM_XML;
   public static final ContentType APPLICATION_FORM_URLENCODED;
   public static final ContentType APPLICATION_JSON;
   public static final ContentType APPLICATION_OCTET_STREAM;
   public static final ContentType APPLICATION_SOAP_XML;
   public static final ContentType APPLICATION_SVG_XML;
   public static final ContentType APPLICATION_XHTML_XML;
   public static final ContentType APPLICATION_XML;
   public static final ContentType IMAGE_BMP;
   public static final ContentType IMAGE_GIF;
   public static final ContentType IMAGE_JPEG;
   public static final ContentType IMAGE_PNG;
   public static final ContentType IMAGE_SVG;
   public static final ContentType IMAGE_TIFF;
   public static final ContentType IMAGE_WEBP;
   public static final ContentType MULTIPART_FORM_DATA;
   public static final ContentType TEXT_HTML;
   public static final ContentType TEXT_PLAIN;
   public static final ContentType TEXT_XML;
   public static final ContentType WILDCARD;
   private static final Map<String, ContentType> CONTENT_TYPE_MAP;
   public static final ContentType DEFAULT_TEXT;
   public static final ContentType DEFAULT_BINARY;
   private final String mimeType;
   private final Charset charset;
   private final NameValuePair[] params;

   ContentType(String mimeType, Charset charset) {
      this.mimeType = mimeType;
      this.charset = charset;
      this.params = null;
   }

   ContentType(String mimeType, Charset charset, NameValuePair[] params) {
      this.mimeType = mimeType;
      this.charset = charset;
      this.params = params;
   }

   public String getMimeType() {
      return this.mimeType;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public String getParameter(String name) {
      Args.notEmpty((CharSequence)name, "Parameter name");
      if (this.params == null) {
         return null;
      } else {
         NameValuePair[] arr$ = this.params;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            NameValuePair param = arr$[i$];
            if (param.getName().equalsIgnoreCase(name)) {
               return param.getValue();
            }
         }

         return null;
      }
   }

   public String toString() {
      CharArrayBuffer buf = new CharArrayBuffer(64);
      buf.append(this.mimeType);
      if (this.params != null) {
         buf.append("; ");
         BasicHeaderValueFormatter.INSTANCE.formatParameters(buf, this.params, false);
      } else if (this.charset != null) {
         buf.append("; charset=");
         buf.append(this.charset.name());
      }

      return buf.toString();
   }

   private static boolean valid(String s) {
      for(int i = 0; i < s.length(); ++i) {
         char ch = s.charAt(i);
         if (ch == '"' || ch == ',' || ch == ';') {
            return false;
         }
      }

      return true;
   }

   public static ContentType create(String mimeType, Charset charset) {
      String normalizedMimeType = ((String)Args.notBlank(mimeType, "MIME type")).toLowerCase(Locale.ROOT);
      Args.check(valid(normalizedMimeType), "MIME type may not contain reserved characters");
      return new ContentType(normalizedMimeType, charset);
   }

   public static ContentType create(String mimeType) {
      return create(mimeType, (Charset)null);
   }

   public static ContentType create(String mimeType, String charset) throws UnsupportedCharsetException {
      return create(mimeType, !TextUtils.isBlank(charset) ? Charset.forName(charset) : null);
   }

   private static ContentType create(HeaderElement helem, boolean strict) {
      return create(helem.getName(), helem.getParameters(), strict);
   }

   private static ContentType create(String mimeType, NameValuePair[] params, boolean strict) {
      Charset charset = null;
      NameValuePair[] arr$ = params;
      int len$ = params.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NameValuePair param = arr$[i$];
         if (param.getName().equalsIgnoreCase("charset")) {
            String s = param.getValue();
            if (TextUtils.isBlank(s)) {
               break;
            }

            try {
               charset = Charset.forName(s);
               break;
            } catch (UnsupportedCharsetException var10) {
               if (!strict) {
                  break;
               }

               throw var10;
            }
         }
      }

      return new ContentType(mimeType, charset, params != null && params.length > 0 ? params : null);
   }

   public static ContentType create(String mimeType, NameValuePair... params) throws UnsupportedCharsetException {
      String type = ((String)Args.notBlank(mimeType, "MIME type")).toLowerCase(Locale.ROOT);
      Args.check(valid(type), "MIME type may not contain reserved characters");
      return create(mimeType, params, true);
   }

   public static ContentType parse(String s) throws ParseException, UnsupportedCharsetException {
      Args.notNull(s, "Content type");
      CharArrayBuffer buf = new CharArrayBuffer(s.length());
      buf.append(s);
      ParserCursor cursor = new ParserCursor(0, s.length());
      HeaderElement[] elements = BasicHeaderValueParser.INSTANCE.parseElements(buf, cursor);
      if (elements.length > 0) {
         return create(elements[0], true);
      } else {
         throw new ParseException("Invalid content type: " + s);
      }
   }

   public static ContentType get(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
      if (entity == null) {
         return null;
      } else {
         Header header = entity.getContentType();
         if (header != null) {
            HeaderElement[] elements = header.getElements();
            if (elements.length > 0) {
               return create(elements[0], true);
            }
         }

         return null;
      }
   }

   public static ContentType getLenient(HttpEntity entity) {
      if (entity == null) {
         return null;
      } else {
         Header header = entity.getContentType();
         if (header != null) {
            try {
               HeaderElement[] elements = header.getElements();
               if (elements.length > 0) {
                  return create(elements[0], false);
               }
            } catch (ParseException var3) {
               return null;
            }
         }

         return null;
      }
   }

   public static ContentType getOrDefault(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
      ContentType contentType = get(entity);
      return contentType != null ? contentType : DEFAULT_TEXT;
   }

   public static ContentType getLenientOrDefault(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
      ContentType contentType = get(entity);
      return contentType != null ? contentType : DEFAULT_TEXT;
   }

   public static ContentType getByMimeType(String mimeType) {
      return mimeType == null ? null : (ContentType)CONTENT_TYPE_MAP.get(mimeType);
   }

   public ContentType withCharset(Charset charset) {
      return create(this.getMimeType(), charset);
   }

   public ContentType withCharset(String charset) {
      return create(this.getMimeType(), charset);
   }

   public ContentType withParameters(NameValuePair... params) throws UnsupportedCharsetException {
      if (params.length == 0) {
         return this;
      } else {
         Map<String, String> paramMap = new LinkedHashMap();
         NameValuePair[] arr$;
         int len$;
         int i$;
         NameValuePair param;
         if (this.params != null) {
            arr$ = this.params;
            len$ = arr$.length;

            for(i$ = 0; i$ < len$; ++i$) {
               param = arr$[i$];
               paramMap.put(param.getName(), param.getValue());
            }
         }

         arr$ = params;
         len$ = params.length;

         for(i$ = 0; i$ < len$; ++i$) {
            param = arr$[i$];
            paramMap.put(param.getName(), param.getValue());
         }

         List<NameValuePair> newParams = new ArrayList(paramMap.size() + 1);
         if (this.charset != null && !paramMap.containsKey("charset")) {
            newParams.add(new BasicNameValuePair("charset", this.charset.name()));
         }

         Iterator i$ = paramMap.entrySet().iterator();

         while(i$.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)i$.next();
            newParams.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue()));
         }

         return create(this.getMimeType(), (NameValuePair[])newParams.toArray(new NameValuePair[newParams.size()]), true);
      }
   }

   static {
      APPLICATION_ATOM_XML = create("application/atom+xml", Consts.ISO_8859_1);
      APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded", Consts.ISO_8859_1);
      APPLICATION_JSON = create("application/json", Consts.UTF_8);
      APPLICATION_OCTET_STREAM = create("application/octet-stream", (Charset)null);
      APPLICATION_SOAP_XML = create("application/soap+xml", Consts.UTF_8);
      APPLICATION_SVG_XML = create("application/svg+xml", Consts.ISO_8859_1);
      APPLICATION_XHTML_XML = create("application/xhtml+xml", Consts.ISO_8859_1);
      APPLICATION_XML = create("application/xml", Consts.ISO_8859_1);
      IMAGE_BMP = create("image/bmp");
      IMAGE_GIF = create("image/gif");
      IMAGE_JPEG = create("image/jpeg");
      IMAGE_PNG = create("image/png");
      IMAGE_SVG = create("image/svg+xml");
      IMAGE_TIFF = create("image/tiff");
      IMAGE_WEBP = create("image/webp");
      MULTIPART_FORM_DATA = create("multipart/form-data", Consts.ISO_8859_1);
      TEXT_HTML = create("text/html", Consts.ISO_8859_1);
      TEXT_PLAIN = create("text/plain", Consts.ISO_8859_1);
      TEXT_XML = create("text/xml", Consts.ISO_8859_1);
      WILDCARD = create("*/*", (Charset)null);
      ContentType[] contentTypes = new ContentType[]{APPLICATION_ATOM_XML, APPLICATION_FORM_URLENCODED, APPLICATION_JSON, APPLICATION_SVG_XML, APPLICATION_XHTML_XML, APPLICATION_XML, IMAGE_BMP, IMAGE_GIF, IMAGE_JPEG, IMAGE_PNG, IMAGE_SVG, IMAGE_TIFF, IMAGE_WEBP, MULTIPART_FORM_DATA, TEXT_HTML, TEXT_PLAIN, TEXT_XML};
      HashMap<String, ContentType> map = new HashMap();
      ContentType[] arr$ = contentTypes;
      int len$ = contentTypes.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         ContentType contentType = arr$[i$];
         map.put(contentType.getMimeType(), contentType);
      }

      CONTENT_TYPE_MAP = Collections.unmodifiableMap(map);
      DEFAULT_TEXT = TEXT_PLAIN;
      DEFAULT_BINARY = APPLICATION_OCTET_STREAM;
   }
}
