/*     */ package io.undertow;
/*     */ 
/*     */ import org.xnio.Option;
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
/*     */ public class UndertowOptions
/*     */ {
/*  30 */   public static final Option<Integer> MAX_HEADER_SIZE = Option.simple(UndertowOptions.class, "MAX_HEADER_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_MAX_HEADER_SIZE = 1048576;
/*     */ 
/*     */ 
/*     */   
/*  39 */   public static final Option<Long> MAX_ENTITY_SIZE = Option.simple(UndertowOptions.class, "MAX_ENTITY_SIZE", Long.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   public static final Option<Long> MULTIPART_MAX_ENTITY_SIZE = Option.simple(UndertowOptions.class, "MULTIPART_MAX_ENTITY_SIZE", Long.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final long DEFAULT_MAX_ENTITY_SIZE = -1L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   public static final Option<Boolean> BUFFER_PIPELINED_DATA = Option.simple(UndertowOptions.class, "BUFFER_PIPELINED_DATA", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   public static final Option<Integer> IDLE_TIMEOUT = Option.simple(UndertowOptions.class, "IDLE_TIMEOUT", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   public static final Option<Integer> REQUEST_PARSE_TIMEOUT = Option.simple(UndertowOptions.class, "REQUEST_PARSE_TIMEOUT", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   public static final Option<Integer> NO_REQUEST_TIMEOUT = Option.simple(UndertowOptions.class, "NO_REQUEST_TIMEOUT", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_MAX_PARAMETERS = 1000;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   public static final Option<Integer> MAX_PARAMETERS = Option.simple(UndertowOptions.class, "MAX_PARAMETERS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_MAX_HEADERS = 200;
/*     */ 
/*     */ 
/*     */   
/*  98 */   public static final Option<Integer> MAX_HEADERS = Option.simple(UndertowOptions.class, "MAX_HEADERS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 106 */   public static final Option<Integer> MAX_COOKIES = Option.simple(UndertowOptions.class, "MAX_COOKIES", Integer.class);
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
/* 118 */   public static final Option<Boolean> ALLOW_ENCODED_SLASH = Option.simple(UndertowOptions.class, "ALLOW_ENCODED_SLASH", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 126 */   public static final Option<Boolean> DECODE_URL = Option.simple(UndertowOptions.class, "DECODE_URL", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 135 */   public static final Option<String> URL_CHARSET = Option.simple(UndertowOptions.class, "URL_CHARSET", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 143 */   public static final Option<Boolean> ALWAYS_SET_KEEP_ALIVE = Option.simple(UndertowOptions.class, "ALWAYS_SET_KEEP_ALIVE", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 151 */   public static final Option<Boolean> ALWAYS_SET_DATE = Option.simple(UndertowOptions.class, "ALWAYS_SET_DATE", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 161 */   public static final Option<Integer> MAX_BUFFERED_REQUEST_SIZE = Option.simple(UndertowOptions.class, "MAX_BUFFERED_REQUEST_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_MAX_BUFFERED_REQUEST_SIZE = 16384;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 172 */   public static final Option<Boolean> RECORD_REQUEST_START_TIME = Option.simple(UndertowOptions.class, "RECORD_REQUEST_START_TIME", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 181 */   public static final Option<Boolean> ALLOW_EQUALS_IN_COOKIE_VALUE = Option.simple(UndertowOptions.class, "ALLOW_EQUALS_IN_COOKIE_VALUE", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 188 */   public static final Option<Boolean> ENABLE_RFC6265_COOKIE_VALIDATION = Option.simple(UndertowOptions.class, "ENABLE_RFC6265_COOKIE_VALIDATION", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final boolean DEFAULT_ENABLE_RFC6265_COOKIE_VALIDATION = false;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 198 */   public static final Option<Boolean> ENABLE_SPDY = Option.simple(UndertowOptions.class, "ENABLE_SPDY", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 203 */   public static final Option<Boolean> ENABLE_HTTP2 = Option.simple(UndertowOptions.class, "ENABLE_HTTP2", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 212 */   public static final Option<Boolean> ENABLE_STATISTICS = Option.simple(UndertowOptions.class, "ENABLE_STATISTICS", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 220 */   public static final Option<Boolean> ENABLE_CONNECTOR_STATISTICS = ENABLE_STATISTICS;
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
/* 235 */   public static final Option<Boolean> ALLOW_UNKNOWN_PROTOCOLS = Option.simple(UndertowOptions.class, "ALLOW_UNKNOWN_PROTOCOLS", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 240 */   public static final Option<Integer> HTTP2_SETTINGS_HEADER_TABLE_SIZE = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_HEADER_TABLE_SIZE", Integer.class);
/*     */ 
/*     */   
/*     */   public static final int HTTP2_SETTINGS_HEADER_TABLE_SIZE_DEFAULT = 4096;
/*     */ 
/*     */   
/* 246 */   public static final Option<Boolean> HTTP2_SETTINGS_ENABLE_PUSH = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_ENABLE_PUSH", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 251 */   public static final Option<Integer> HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS", Integer.class);
/*     */   
/* 253 */   public static final Option<Integer> HTTP2_SETTINGS_INITIAL_WINDOW_SIZE = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_INITIAL_WINDOW_SIZE", Integer.class);
/* 254 */   public static final Option<Integer> HTTP2_SETTINGS_MAX_FRAME_SIZE = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_MAX_FRAME_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 262 */   public static final Option<Integer> HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE = Option.simple(UndertowOptions.class, "HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 267 */   public static final Option<Integer> HTTP2_PADDING_SIZE = Option.simple(UndertowOptions.class, "HTTP2_PADDING_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 273 */   public static final Option<Integer> HTTP2_HUFFMAN_CACHE_SIZE = Option.simple(UndertowOptions.class, "HTTP2_HUFFMAN_CACHE_SIZE", Integer.class);
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
/* 287 */   public static final Option<Integer> MAX_CONCURRENT_REQUESTS_PER_CONNECTION = Option.simple(UndertowOptions.class, "MAX_CONCURRENT_REQUESTS_PER_CONNECTION", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 292 */   public static final Option<Integer> MAX_QUEUED_READ_BUFFERS = Option.simple(UndertowOptions.class, "MAX_QUEUED_READ_BUFFERS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 297 */   public static final Option<Integer> MAX_AJP_PACKET_SIZE = Option.simple(UndertowOptions.class, "MAX_AJP_PACKET_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 302 */   public static final Option<Boolean> REQUIRE_HOST_HTTP11 = Option.simple(UndertowOptions.class, "REQUIRE_HOST_HTTP11", Boolean.class);
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_MAX_CACHED_HEADER_SIZE = 150;
/*     */ 
/*     */   
/* 309 */   public static final Option<Integer> MAX_CACHED_HEADER_SIZE = Option.simple(UndertowOptions.class, "MAX_CACHED_HEADER_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_HTTP_HEADERS_CACHE_SIZE = 15;
/*     */ 
/*     */   
/* 316 */   public static final Option<Integer> HTTP_HEADERS_CACHE_SIZE = Option.simple(UndertowOptions.class, "HTTP_HEADERS_CACHE_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 321 */   public static final Option<Boolean> SSL_USER_CIPHER_SUITES_ORDER = Option.simple(UndertowOptions.class, "SSL_USER_CIPHER_SUITES_ORDER", Boolean.class);
/*     */ 
/*     */   
/* 324 */   public static final Option<Boolean> ALLOW_UNESCAPED_CHARACTERS_IN_URL = Option.simple(UndertowOptions.class, "ALLOW_UNESCAPED_CHARACTERS_IN_URL", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 332 */   public static final Option<Integer> SHUTDOWN_TIMEOUT = Option.simple(UndertowOptions.class, "SHUTDOWN_TIMEOUT", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 339 */   public static final Option<String> ENDPOINT_IDENTIFICATION_ALGORITHM = Option.simple(UndertowOptions.class, "ENDPOINT_IDENTIFICATION_ALGORITHM", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 347 */   public static final Option<Integer> QUEUED_FRAMES_HIGH_WATER_MARK = Option.simple(UndertowOptions.class, "QUEUED_FRAMES_HIGH_WATER_MARK", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 354 */   public static final Option<Integer> QUEUED_FRAMES_LOW_WATER_MARK = Option.simple(UndertowOptions.class, "QUEUED_FRAMES_LOW_WATER_MARK", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 363 */   public static final Option<String> AJP_ALLOWED_REQUEST_ATTRIBUTES_PATTERN = Option.simple(UndertowOptions.class, "AJP_ALLOWED_REQUEST_ATTRIBUTES_PATTERN", String.class);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\UndertowOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */