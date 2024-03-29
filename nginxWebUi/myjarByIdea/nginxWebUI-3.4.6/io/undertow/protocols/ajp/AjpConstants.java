package io.undertow.protocols.ajp;

import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class AjpConstants {
   public static final int FRAME_TYPE_SEND_HEADERS = 4;
   public static final int FRAME_TYPE_REQUEST_BODY_CHUNK = 6;
   public static final int FRAME_TYPE_SEND_BODY_CHUNK = 3;
   public static final int FRAME_TYPE_END_RESPONSE = 5;
   public static final int FRAME_TYPE_CPONG = 9;
   public static final int FRAME_TYPE_CPING = 10;
   public static final int FRAME_TYPE_SHUTDOWN = 7;
   static final Map<HttpString, Integer> HEADER_MAP;
   static final Map<HttpString, Integer> HTTP_METHODS_MAP;
   static final HttpString[] HTTP_HEADERS_ARRAY;
   static final int ATTR_CONTEXT = 1;
   static final int ATTR_SERVLET_PATH = 2;
   static final int ATTR_REMOTE_USER = 3;
   static final int ATTR_AUTH_TYPE = 4;
   static final int ATTR_QUERY_STRING = 5;
   static final int ATTR_ROUTE = 6;
   static final int ATTR_SSL_CERT = 7;
   static final int ATTR_SSL_CIPHER = 8;
   static final int ATTR_SSL_SESSION = 9;
   static final int ATTR_REQ_ATTRIBUTE = 10;
   static final int ATTR_SSL_KEY_SIZE = 11;
   static final int ATTR_SECRET = 12;
   static final int ATTR_STORED_METHOD = 13;
   static final int ATTR_ARE_DONE = 255;

   static {
      Map<HttpString, Integer> headers = new HashMap();
      headers.put(Headers.ACCEPT, 40961);
      headers.put(Headers.ACCEPT_CHARSET, 40962);
      headers.put(Headers.ACCEPT_ENCODING, 40963);
      headers.put(Headers.ACCEPT_LANGUAGE, 40964);
      headers.put(Headers.AUTHORIZATION, 40965);
      headers.put(Headers.CONNECTION, 40966);
      headers.put(Headers.CONTENT_TYPE, 40967);
      headers.put(Headers.CONTENT_LENGTH, 40968);
      headers.put(Headers.COOKIE, 40969);
      headers.put(Headers.COOKIE2, 40970);
      headers.put(Headers.HOST, 40971);
      headers.put(Headers.PRAGMA, 40972);
      headers.put(Headers.REFERER, 40973);
      headers.put(Headers.USER_AGENT, 40974);
      HEADER_MAP = Collections.unmodifiableMap(headers);
      Map<HttpString, Integer> methods = new HashMap();
      methods.put(Methods.OPTIONS, 1);
      methods.put(Methods.GET, 2);
      methods.put(Methods.HEAD, 3);
      methods.put(Methods.POST, 4);
      methods.put(Methods.PUT, 5);
      methods.put(Methods.DELETE, 6);
      methods.put(Methods.TRACE, 7);
      methods.put(Methods.PROPFIND, 8);
      methods.put(Methods.PROPPATCH, 9);
      methods.put(Methods.MKCOL, 10);
      methods.put(Methods.COPY, 11);
      methods.put(Methods.MOVE, 12);
      methods.put(Methods.LOCK, 13);
      methods.put(Methods.UNLOCK, 14);
      methods.put(Methods.ACL, 15);
      methods.put(Methods.REPORT, 16);
      methods.put(Methods.VERSION_CONTROL, 17);
      methods.put(Methods.CHECKIN, 18);
      methods.put(Methods.CHECKOUT, 19);
      methods.put(Methods.UNCHECKOUT, 20);
      methods.put(Methods.SEARCH, 21);
      methods.put(Methods.MKWORKSPACE, 22);
      methods.put(Methods.UPDATE, 23);
      methods.put(Methods.LABEL, 24);
      methods.put(Methods.MERGE, 25);
      methods.put(Methods.BASELINE_CONTROL, 26);
      methods.put(Methods.MKACTIVITY, 27);
      HTTP_METHODS_MAP = Collections.unmodifiableMap(methods);
      HTTP_HEADERS_ARRAY = new HttpString[]{null, Headers.CONTENT_TYPE, Headers.CONTENT_LANGUAGE, Headers.CONTENT_LENGTH, Headers.DATE, Headers.LAST_MODIFIED, Headers.LOCATION, Headers.SET_COOKIE, Headers.SET_COOKIE2, Headers.SERVLET_ENGINE, Headers.STATUS, Headers.WWW_AUTHENTICATE};
   }
}
