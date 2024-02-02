package io.undertow;

import org.xnio.Option;

public class UndertowOptions {
   public static final Option<Integer> MAX_HEADER_SIZE = Option.simple(UndertowOptions.class, "MAX_HEADER_SIZE", Integer.class);
   public static final int DEFAULT_MAX_HEADER_SIZE = 1048576;
   public static final Option<Long> MAX_ENTITY_SIZE = Option.simple(UndertowOptions.class, "MAX_ENTITY_SIZE", Long.class);
   public static final Option<Long> MULTIPART_MAX_ENTITY_SIZE = Option.simple(UndertowOptions.class, "MULTIPART_MAX_ENTITY_SIZE", Long.class);
   public static final long DEFAULT_MAX_ENTITY_SIZE = -1L;
   public static final Option<Boolean> BUFFER_PIPELINED_DATA = Option.simple(UndertowOptions.class, "BUFFER_PIPELINED_DATA", Boolean.class);
   public static final Option<Integer> IDLE_TIMEOUT = Option.simple(UndertowOptions.class, "IDLE_TIMEOUT", Integer.class);
   public static final Option<Integer> REQUEST_PARSE_TIMEOUT = Option.simple(UndertowOptions.class, "REQUEST_PARSE_TIMEOUT", Integer.class);
   public static final Option<Integer> NO_REQUEST_TIMEOUT = Option.simple(UndertowOptions.class, "NO_REQUEST_TIMEOUT", Integer.class);
   public static final int DEFAULT_MAX_PARAMETERS = 1000;
   public static final Option<Integer> MAX_PARAMETERS = Option.simple(UndertowOptions.class, "MAX_PARAMETERS", Integer.class);
   public static final int DEFAULT_MAX_HEADERS = 200;
   public static final Option<Integer> MAX_HEADERS = Option.simple(UndertowOptions.class, "MAX_HEADERS", Integer.class);
   public static final Option<Integer> MAX_COOKIES = Option.simple(UndertowOptions.class, "MAX_COOKIES", Integer.class);
   public static final Option<Boolean> ALLOW_ENCODED_SLASH = Option.simple(UndertowOptions.class, "ALLOW_ENCODED_SLASH", Boolean.class);
   public static final Option<Boolean> DECODE_URL = Option.simple(UndertowOptions.class, "DECODE_URL", Boolean.class);
   public static final Option<String> URL_CHARSET = Option.simple(UndertowOptions.class, "URL_CHARSET", String.class);
   public static final Option<Boolean> ALWAYS_SET_KEEP_ALIVE = Option.simple(UndertowOptions.class, "ALWAYS_SET_KEEP_ALIVE", Boolean.class);
   public static final Option<Boolean> ALWAYS_SET_DATE = Option.simple(UndertowOptions.class, "ALWAYS_SET_DATE", Boolean.class);
   public static final Option<Integer> MAX_BUFFERED_REQUEST_SIZE = Option.simple(UndertowOptions.class, "MAX_BUFFERED_REQUEST_SIZE", Integer.class);
   public static final int DEFAULT_MAX_BUFFERED_REQUEST_SIZE = 16384;
   public static final Option<Boolean> RECORD_REQUEST_START_TIME = Option.simple(UndertowOptions.class, "RECORD_REQUEST_START_TIME", Boolean.class);
   public static final Option<Boolean> ALLOW_EQUALS_IN_COOKIE_VALUE = Option.simple(UndertowOptions.class, "ALLOW_EQUALS_IN_COOKIE_VALUE", Boolean.class);
   public static final Option<Boolean> ENABLE_RFC6265_COOKIE_VALIDATION = Option.simple(UndertowOptions.class, "ENABLE_RFC6265_COOKIE_VALIDATION", Boolean.class);
   public static final boolean DEFAULT_ENABLE_RFC6265_COOKIE_VALIDATION = false;
   /** @deprecated */
   @Deprecated
   public static final Option<Boolean> ENABLE_SPDY = Option.simple(UndertowOptions.class, "ENABLE_SPDY", Boolean.class);
   public static final Option<Boolean> ENABLE_HTTP2 = Option.simple(UndertowOptions.class, "ENABLE_HTTP2", Boolean.class);
   public static final Option<Boolean> ENABLE_STATISTICS = Option.simple(UndertowOptions.class, "ENABLE_STATISTICS", Boolean.class);
   /** @deprecated */
   @Deprecated
   public static final Option<Boolean> ENABLE_CONNECTOR_STATISTICS;
   public static final Option<Boolean> ALLOW_UNKNOWN_PROTOCOLS;
   public static final Option<Integer> HTTP2_SETTINGS_HEADER_TABLE_SIZE;
   public static final int HTTP2_SETTINGS_HEADER_TABLE_SIZE_DEFAULT = 4096;
   public static final Option<Boolean> HTTP2_SETTINGS_ENABLE_PUSH;
   public static final Option<Integer> HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS;
   public static final Option<Integer> HTTP2_SETTINGS_INITIAL_WINDOW_SIZE;
   public static final Option<Integer> HTTP2_SETTINGS_MAX_FRAME_SIZE;
   /** @deprecated */
   @Deprecated
   public static final Option<Integer> HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE;
   public static final Option<Integer> HTTP2_PADDING_SIZE;
   public static final Option<Integer> HTTP2_HUFFMAN_CACHE_SIZE;
   public static final Option<Integer> MAX_CONCURRENT_REQUESTS_PER_CONNECTION;
   public static final Option<Integer> MAX_QUEUED_READ_BUFFERS;
   public static final Option<Integer> MAX_AJP_PACKET_SIZE;
   public static final Option<Boolean> REQUIRE_HOST_HTTP11;
   public static final int DEFAULT_MAX_CACHED_HEADER_SIZE = 150;
   public static final Option<Integer> MAX_CACHED_HEADER_SIZE;
   public static final int DEFAULT_HTTP_HEADERS_CACHE_SIZE = 15;
   public static final Option<Integer> HTTP_HEADERS_CACHE_SIZE;
   public static final Option<Boolean> SSL_USER_CIPHER_SUITES_ORDER;
   public static final Option<Boolean> ALLOW_UNESCAPED_CHARACTERS_IN_URL;
   public static final Option<Integer> SHUTDOWN_TIMEOUT;
   public static final Option<String> ENDPOINT_IDENTIFICATION_ALGORITHM;
   public static final Option<Integer> QUEUED_FRAMES_HIGH_WATER_MARK;
   public static final Option<Integer> QUEUED_FRAMES_LOW_WATER_MARK;
   public static final Option<String> AJP_ALLOWED_REQUEST_ATTRIBUTES_PATTERN;

   private UndertowOptions() {
   }

   static {
      ENABLE_CONNECTOR_STATISTICS = ENABLE_STATISTICS;
      ALLOW_UNKNOWN_PROTOCOLS = Option.simple(UndertowOptions.class, "ALLOW_UNKNOWN_PROTOCOLS", Boolean.class);
      HTTP2_SETTINGS_HEADER_TABLE_SIZE = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_HEADER_TABLE_SIZE", Integer.class);
      HTTP2_SETTINGS_ENABLE_PUSH = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_ENABLE_PUSH", Boolean.class);
      HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS", Integer.class);
      HTTP2_SETTINGS_INITIAL_WINDOW_SIZE = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_INITIAL_WINDOW_SIZE", Integer.class);
      HTTP2_SETTINGS_MAX_FRAME_SIZE = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_MAX_FRAME_SIZE", Integer.class);
      HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE", Integer.class);
      HTTP2_PADDING_SIZE = Option.simple(UndertowOptions.class, "HTTP2_PADDING_SIZE", Integer.class);
      HTTP2_HUFFMAN_CACHE_SIZE = Option.simple(UndertowOptions.class, "HTTP2_HUFFMAN_CACHE_SIZE", Integer.class);
      MAX_CONCURRENT_REQUESTS_PER_CONNECTION = Option.simple(UndertowOptions.class, "MAX_CONCURRENT_REQUESTS_PER_CONNECTION", Integer.class);
      MAX_QUEUED_READ_BUFFERS = Option.simple(UndertowOptions.class, "MAX_QUEUED_READ_BUFFERS", Integer.class);
      MAX_AJP_PACKET_SIZE = Option.simple(UndertowOptions.class, "MAX_AJP_PACKET_SIZE", Integer.class);
      REQUIRE_HOST_HTTP11 = Option.simple(UndertowOptions.class, "REQUIRE_HOST_HTTP11", Boolean.class);
      MAX_CACHED_HEADER_SIZE = Option.simple(UndertowOptions.class, "MAX_CACHED_HEADER_SIZE", Integer.class);
      HTTP_HEADERS_CACHE_SIZE = Option.simple(UndertowOptions.class, "HTTP_HEADERS_CACHE_SIZE", Integer.class);
      SSL_USER_CIPHER_SUITES_ORDER = Option.simple(UndertowOptions.class, "SSL_USER_CIPHER_SUITES_ORDER", Boolean.class);
      ALLOW_UNESCAPED_CHARACTERS_IN_URL = Option.simple(UndertowOptions.class, "ALLOW_UNESCAPED_CHARACTERS_IN_URL", Boolean.class);
      SHUTDOWN_TIMEOUT = Option.simple(UndertowOptions.class, "SHUTDOWN_TIMEOUT", Integer.class);
      ENDPOINT_IDENTIFICATION_ALGORITHM = Option.simple(UndertowOptions.class, "ENDPOINT_IDENTIFICATION_ALGORITHM", String.class);
      QUEUED_FRAMES_HIGH_WATER_MARK = Option.simple(UndertowOptions.class, "QUEUED_FRAMES_HIGH_WATER_MARK", Integer.class);
      QUEUED_FRAMES_LOW_WATER_MARK = Option.simple(UndertowOptions.class, "QUEUED_FRAMES_LOW_WATER_MARK", Integer.class);
      AJP_ALLOWED_REQUEST_ATTRIBUTES_PATTERN = Option.simple(UndertowOptions.class, "AJP_ALLOWED_REQUEST_ATTRIBUTES_PATTERN", String.class);
   }
}
