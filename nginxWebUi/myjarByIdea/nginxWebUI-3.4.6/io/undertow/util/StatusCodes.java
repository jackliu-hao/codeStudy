package io.undertow.util;

public class StatusCodes {
   private static final int SIZE = 735;
   private static final Entry[] TABLE = new Entry[735];
   public static final int CONTINUE = 100;
   public static final int SWITCHING_PROTOCOLS = 101;
   public static final int PROCESSING = 102;
   public static final int OK = 200;
   public static final int CREATED = 201;
   public static final int ACCEPTED = 202;
   public static final int NON_AUTHORITATIVE_INFORMATION = 203;
   public static final int NO_CONTENT = 204;
   public static final int RESET_CONTENT = 205;
   public static final int PARTIAL_CONTENT = 206;
   public static final int MULTI_STATUS = 207;
   public static final int ALREADY_REPORTED = 208;
   public static final int IM_USED = 226;
   public static final int MULTIPLE_CHOICES = 300;
   public static final int MOVED_PERMANENTLY = 301;
   /** @deprecated */
   @Deprecated
   public static final int MOVED_PERMENANTLY = 301;
   public static final int FOUND = 302;
   public static final int SEE_OTHER = 303;
   public static final int NOT_MODIFIED = 304;
   public static final int USE_PROXY = 305;
   public static final int TEMPORARY_REDIRECT = 307;
   public static final int PERMANENT_REDIRECT = 308;
   public static final int BAD_REQUEST = 400;
   public static final int UNAUTHORIZED = 401;
   public static final int PAYMENT_REQUIRED = 402;
   public static final int FORBIDDEN = 403;
   public static final int NOT_FOUND = 404;
   public static final int METHOD_NOT_ALLOWED = 405;
   public static final int NOT_ACCEPTABLE = 406;
   public static final int PROXY_AUTHENTICATION_REQUIRED = 407;
   public static final int REQUEST_TIME_OUT = 408;
   public static final int CONFLICT = 409;
   public static final int GONE = 410;
   public static final int LENGTH_REQUIRED = 411;
   public static final int PRECONDITION_FAILED = 412;
   public static final int REQUEST_ENTITY_TOO_LARGE = 413;
   public static final int REQUEST_URI_TOO_LARGE = 414;
   public static final int UNSUPPORTED_MEDIA_TYPE = 415;
   public static final int REQUEST_RANGE_NOT_SATISFIABLE = 416;
   public static final int EXPECTATION_FAILED = 417;
   public static final int UNPROCESSABLE_ENTITY = 422;
   public static final int LOCKED = 423;
   public static final int FAILED_DEPENDENCY = 424;
   public static final int UPGRADE_REQUIRED = 426;
   public static final int PRECONDITION_REQUIRED = 428;
   public static final int TOO_MANY_REQUESTS = 429;
   public static final int REQUEST_HEADER_FIELDS_TOO_LARGE = 431;
   public static final int INTERNAL_SERVER_ERROR = 500;
   public static final int NOT_IMPLEMENTED = 501;
   public static final int BAD_GATEWAY = 502;
   public static final int SERVICE_UNAVAILABLE = 503;
   public static final int GATEWAY_TIME_OUT = 504;
   public static final int HTTP_VERSION_NOT_SUPPORTED = 505;
   public static final int INSUFFICIENT_STORAGE = 507;
   public static final int LOOP_DETECTED = 508;
   public static final int NOT_EXTENDED = 510;
   public static final int NETWORK_AUTHENTICATION_REQUIRED = 511;
   public static final String CONTINUE_STRING = "Continue";
   public static final String SWITCHING_PROTOCOLS_STRING = "Switching Protocols";
   public static final String PROCESSING_STRING = "Processing";
   public static final String OK_STRING = "OK";
   public static final String CREATED_STRING = "Created";
   public static final String ACCEPTED_STRING = "Accepted";
   public static final String NON_AUTHORITATIVE_INFORMATION_STRING = "Non-Authoritative Information";
   public static final String NO_CONTENT_STRING = "No Content";
   public static final String RESET_CONTENT_STRING = "Reset Content";
   public static final String PARTIAL_CONTENT_STRING = "Partial Content";
   public static final String MULTI_STATUS_STRING = "Multi-Status";
   public static final String ALREADY_REPORTED_STRING = "Already Reported";
   public static final String IM_USED_STRING = "IM Used";
   public static final String MULTIPLE_CHOICES_STRING = "Multiple Choices";
   public static final String MOVED_PERMANENTLY_STRING = "Moved Permanently";
   public static final String FOUND_STRING = "Found";
   public static final String SEE_OTHER_STRING = "See Other";
   public static final String NOT_MODIFIED_STRING = "Not Modified";
   public static final String USE_PROXY_STRING = "Use Proxy";
   public static final String TEMPORARY_REDIRECT_STRING = "Temporary Redirect";
   public static final String PERMANENT_REDIRECT_STRING = "Permanent Redirect";
   public static final String BAD_REQUEST_STRING = "Bad Request";
   public static final String UNAUTHORIZED_STRING = "Unauthorized";
   public static final String PAYMENT_REQUIRED_STRING = "Payment Required";
   public static final String FORBIDDEN_STRING = "Forbidden";
   public static final String NOT_FOUND_STRING = "Not Found";
   public static final String METHOD_NOT_ALLOWED_STRING = "Method Not Allowed";
   public static final String NOT_ACCEPTABLE_STRING = "Not Acceptable";
   public static final String PROXY_AUTHENTICATION_REQUIRED_STRING = "Proxy Authentication Required";
   public static final String REQUEST_TIME_OUT_STRING = "Request Time-out";
   public static final String CONFLICT_STRING = "Conflict";
   public static final String GONE_STRING = "Gone";
   public static final String LENGTH_REQUIRED_STRING = "Length Required";
   public static final String PRECONDITION_FAILED_STRING = "Precondition Failed";
   public static final String REQUEST_ENTITY_TOO_LARGE_STRING = "Request Entity Too Large";
   public static final String REQUEST_URI_TOO_LARGE_STRING = "Request-URI Too Large";
   public static final String UNSUPPORTED_MEDIA_TYPE_STRING = "Unsupported Media Type";
   public static final String REQUEST_RANGE_NOT_SATISFIABLE_STRING = "Requested range not satisfiable";
   public static final String EXPECTATION_FAILED_STRING = "Expectation Failed";
   public static final String UNPROCESSABLE_ENTITY_STRING = "Unprocessable Entity";
   public static final String LOCKED_STRING = "Locked";
   public static final String FAILED_DEPENDENCY_STRING = "Failed Dependency";
   public static final String UPGRADE_REQUIRED_STRING = "Upgrade Required";
   public static final String PRECONDITION_REQUIRED_STRING = "Precondition Required";
   public static final String TOO_MANY_REQUESTS_STRING = "Too Many Requests";
   public static final String REQUEST_HEADER_FIELDS_TOO_LARGE_STRING = "Request Header Fields Too Large";
   public static final String INTERNAL_SERVER_ERROR_STRING = "Internal Server Error";
   public static final String NOT_IMPLEMENTED_STRING = "Not Implemented";
   public static final String BAD_GATEWAY_STRING = "Bad Gateway";
   public static final String SERVICE_UNAVAILABLE_STRING = "Service Unavailable";
   public static final String GATEWAY_TIME_OUT_STRING = "Gateway Time-out";
   public static final String HTTP_VERSION_NOT_SUPPORTED_STRING = "HTTP Version not supported";
   public static final String INSUFFICIENT_STORAGE_STRING = "Insufficient Storage";
   public static final String LOOP_DETECTED_STRING = "Loop Detected";
   public static final String NOT_EXTENDED_STRING = "Not Extended";
   public static final String NETWORK_AUTHENTICATION_REQUIRED_STRING = "Network Authentication Required";

   private static void putCode(int code, String reason) {
      Entry e = new Entry(reason, code);
      int h = code & 735;
      if (TABLE[h] != null) {
         throw new IllegalArgumentException("hash collision");
      } else {
         TABLE[h] = e;
      }
   }

   private StatusCodes() {
   }

   public static final String getReason(int code) {
      int hash = code & 735;
      if (hash == 735) {
         return "Unknown";
      } else {
         Entry result = TABLE[hash];
         return result != null && result.code == code ? result.reason : "Unknown";
      }
   }

   static {
      putCode(100, "Continue");
      putCode(101, "Switching Protocols");
      putCode(102, "Processing");
      putCode(200, "OK");
      putCode(201, "Created");
      putCode(202, "Accepted");
      putCode(203, "Non-Authoritative Information");
      putCode(204, "No Content");
      putCode(205, "Reset Content");
      putCode(206, "Partial Content");
      putCode(207, "Multi-Status");
      putCode(208, "Already Reported");
      putCode(226, "IM Used");
      putCode(300, "Multiple Choices");
      putCode(301, "Moved Permanently");
      putCode(302, "Found");
      putCode(303, "See Other");
      putCode(304, "Not Modified");
      putCode(305, "Use Proxy");
      putCode(307, "Temporary Redirect");
      putCode(308, "Permanent Redirect");
      putCode(400, "Bad Request");
      putCode(401, "Unauthorized");
      putCode(402, "Payment Required");
      putCode(403, "Forbidden");
      putCode(404, "Not Found");
      putCode(405, "Method Not Allowed");
      putCode(406, "Not Acceptable");
      putCode(407, "Proxy Authentication Required");
      putCode(408, "Request Time-out");
      putCode(409, "Conflict");
      putCode(410, "Gone");
      putCode(411, "Length Required");
      putCode(412, "Precondition Failed");
      putCode(413, "Request Entity Too Large");
      putCode(414, "Request-URI Too Large");
      putCode(415, "Unsupported Media Type");
      putCode(416, "Requested range not satisfiable");
      putCode(417, "Expectation Failed");
      putCode(422, "Unprocessable Entity");
      putCode(423, "Locked");
      putCode(424, "Failed Dependency");
      putCode(426, "Upgrade Required");
      putCode(428, "Precondition Required");
      putCode(429, "Too Many Requests");
      putCode(431, "Request Header Fields Too Large");
      putCode(500, "Internal Server Error");
      putCode(501, "Not Implemented");
      putCode(502, "Bad Gateway");
      putCode(503, "Service Unavailable");
      putCode(504, "Gateway Time-out");
      putCode(505, "HTTP Version not supported");
      putCode(507, "Insufficient Storage");
      putCode(508, "Loop Detected");
      putCode(510, "Not Extended");
      putCode(511, "Network Authentication Required");
   }

   private static final class Entry {
      final String reason;
      final int code;

      private Entry(String reason, int code) {
         this.reason = reason;
         this.code = code;
      }

      // $FF: synthetic method
      Entry(String x0, int x1, Object x2) {
         this(x0, x1);
      }
   }
}
