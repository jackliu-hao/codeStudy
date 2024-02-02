package io.undertow.util;

import io.undertow.server.Connectors;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Methods {
   public static final String OPTIONS_STRING = "OPTIONS";
   public static final String GET_STRING = "GET";
   public static final String HEAD_STRING = "HEAD";
   public static final String POST_STRING = "POST";
   public static final String PUT_STRING = "PUT";
   public static final String DELETE_STRING = "DELETE";
   public static final String TRACE_STRING = "TRACE";
   public static final String CONNECT_STRING = "CONNECT";
   public static final String PATCH_STRING = "PATCH";
   public static final String PROPFIND_STRING = "PROPFIND";
   public static final String PROPPATCH_STRING = "PROPPATCH";
   public static final String MKCOL_STRING = "MKCOL";
   public static final String COPY_STRING = "COPY";
   public static final String MOVE_STRING = "MOVE";
   public static final String LOCK_STRING = "LOCK";
   public static final String UNLOCK_STRING = "UNLOCK";
   public static final String ACL_STRING = "ACL";
   public static final String REPORT_STRING = "REPORT";
   public static final String VERSION_CONTROL_STRING = "VERSION-CONTROL";
   public static final String CHECKIN_STRING = "CHECKIN";
   public static final String CHECKOUT_STRING = "CHECKOUT";
   public static final String UNCHECKOUT_STRING = "UNCHECKOUT";
   public static final String SEARCH_STRING = "SEARCH";
   public static final String MKWORKSPACE_STRING = "MKWORKSPACE";
   public static final String UPDATE_STRING = "UPDATE";
   public static final String LABEL_STRING = "LABEL";
   public static final String MERGE_STRING = "MERGE";
   public static final String BASELINE_CONTROL_STRING = "BASELINE_CONTROL";
   public static final String MKACTIVITY_STRING = "MKACTIVITY";
   public static final HttpString OPTIONS = new HttpString("OPTIONS");
   public static final HttpString GET = new HttpString("GET");
   public static final HttpString HEAD = new HttpString("HEAD");
   public static final HttpString POST = new HttpString("POST");
   public static final HttpString PUT = new HttpString("PUT");
   public static final HttpString DELETE = new HttpString("DELETE");
   public static final HttpString TRACE = new HttpString("TRACE");
   public static final HttpString CONNECT = new HttpString("CONNECT");
   public static final HttpString PATCH = new HttpString("PATCH");
   public static final HttpString PROPFIND = new HttpString("PROPFIND");
   public static final HttpString PROPPATCH = new HttpString("PROPPATCH");
   public static final HttpString MKCOL = new HttpString("MKCOL");
   public static final HttpString COPY = new HttpString("COPY");
   public static final HttpString MOVE = new HttpString("MOVE");
   public static final HttpString LOCK = new HttpString("LOCK");
   public static final HttpString UNLOCK = new HttpString("UNLOCK");
   public static final HttpString ACL = new HttpString("ACL");
   public static final HttpString REPORT = new HttpString("REPORT");
   public static final HttpString VERSION_CONTROL = new HttpString("VERSION-CONTROL");
   public static final HttpString CHECKIN = new HttpString("CHECKIN");
   public static final HttpString CHECKOUT = new HttpString("CHECKOUT");
   public static final HttpString UNCHECKOUT = new HttpString("UNCHECKOUT");
   public static final HttpString SEARCH = new HttpString("SEARCH");
   public static final HttpString MKWORKSPACE = new HttpString("MKWORKSPACE");
   public static final HttpString UPDATE = new HttpString("UPDATE");
   public static final HttpString LABEL = new HttpString("LABEL");
   public static final HttpString MERGE = new HttpString("MERGE");
   public static final HttpString BASELINE_CONTROL = new HttpString("BASELINE_CONTROL");
   public static final HttpString MKACTIVITY = new HttpString("MKACTIVITY");
   private static final Map<String, HttpString> METHODS;

   private Methods() {
   }

   private static void putString(Map<String, HttpString> methods, HttpString options) {
      methods.put(options.toString(), options);
   }

   public static HttpString fromString(String method) {
      HttpString res = (HttpString)METHODS.get(method);
      if (res == null) {
         HttpString httpString = new HttpString(method);
         Connectors.verifyToken(httpString);
         return httpString;
      } else {
         return res;
      }
   }

   static {
      Map<String, HttpString> methods = new HashMap();
      putString(methods, OPTIONS);
      putString(methods, GET);
      putString(methods, HEAD);
      putString(methods, POST);
      putString(methods, PUT);
      putString(methods, DELETE);
      putString(methods, TRACE);
      putString(methods, CONNECT);
      putString(methods, PATCH);
      putString(methods, PROPFIND);
      putString(methods, PROPPATCH);
      putString(methods, MKCOL);
      putString(methods, COPY);
      putString(methods, MOVE);
      putString(methods, LOCK);
      putString(methods, UNLOCK);
      putString(methods, ACL);
      putString(methods, REPORT);
      putString(methods, VERSION_CONTROL);
      putString(methods, CHECKIN);
      putString(methods, CHECKOUT);
      putString(methods, UNCHECKOUT);
      putString(methods, SEARCH);
      putString(methods, MKWORKSPACE);
      putString(methods, UPDATE);
      putString(methods, LABEL);
      putString(methods, MERGE);
      putString(methods, BASELINE_CONTROL);
      putString(methods, MKACTIVITY);
      METHODS = Collections.unmodifiableMap(methods);
   }
}
