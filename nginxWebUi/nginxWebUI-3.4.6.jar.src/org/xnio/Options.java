/*     */ package org.xnio;
/*     */ 
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import org.xnio.sasl.SaslQop;
/*     */ import org.xnio.sasl.SaslStrength;
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
/*     */ public final class Options
/*     */ {
/*  44 */   public static final Option<Boolean> ALLOW_BLOCKING = Option.simple(Options.class, "ALLOW_BLOCKING", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   public static final Option<Boolean> MULTICAST = Option.simple(Options.class, "MULTICAST", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   public static final Option<Boolean> BROADCAST = Option.simple(Options.class, "BROADCAST", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   public static final Option<Boolean> CLOSE_ABORT = Option.simple(Options.class, "CLOSE_ABORT", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   public static final Option<Integer> RECEIVE_BUFFER = Option.simple(Options.class, "RECEIVE_BUFFER", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   public static final Option<Boolean> REUSE_ADDRESSES = Option.simple(Options.class, "REUSE_ADDRESSES", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   public static final Option<Integer> SEND_BUFFER = Option.simple(Options.class, "SEND_BUFFER", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  90 */   public static final Option<Boolean> TCP_NODELAY = Option.simple(Options.class, "TCP_NODELAY", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  95 */   public static final Option<Integer> MULTICAST_TTL = Option.simple(Options.class, "MULTICAST_TTL", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 100 */   public static final Option<Integer> IP_TRAFFIC_CLASS = Option.simple(Options.class, "IP_TRAFFIC_CLASS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 106 */   public static final Option<Boolean> TCP_OOB_INLINE = Option.simple(Options.class, "TCP_OOB_INLINE", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 112 */   public static final Option<Boolean> KEEP_ALIVE = Option.simple(Options.class, "KEEP_ALIVE", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 117 */   public static final Option<Integer> BACKLOG = Option.simple(Options.class, "BACKLOG", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 123 */   public static final Option<Integer> READ_TIMEOUT = Option.simple(Options.class, "READ_TIMEOUT", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 129 */   public static final Option<Integer> WRITE_TIMEOUT = Option.simple(Options.class, "WRITE_TIMEOUT", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 136 */   public static final Option<Integer> MAX_INBOUND_MESSAGE_SIZE = Option.simple(Options.class, "MAX_INBOUND_MESSAGE_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 143 */   public static final Option<Integer> MAX_OUTBOUND_MESSAGE_SIZE = Option.simple(Options.class, "MAX_OUTBOUND_MESSAGE_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 152 */   public static final Option<Boolean> SSL_ENABLED = Option.simple(Options.class, "SSL_ENABLED", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 159 */   public static final Option<SslClientAuthMode> SSL_CLIENT_AUTH_MODE = Option.simple(Options.class, "SSL_CLIENT_AUTH_MODE", SslClientAuthMode.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 167 */   public static final Option<Sequence<String>> SSL_ENABLED_CIPHER_SUITES = Option.sequence(Options.class, "SSL_ENABLED_CIPHER_SUITES", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 174 */   public static final Option<Sequence<String>> SSL_SUPPORTED_CIPHER_SUITES = Option.sequence(Options.class, "SSL_SUPPORTED_CIPHER_SUITES", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 182 */   public static final Option<Sequence<String>> SSL_ENABLED_PROTOCOLS = Option.sequence(Options.class, "SSL_ENABLED_PROTOCOLS", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 189 */   public static final Option<Sequence<String>> SSL_SUPPORTED_PROTOCOLS = Option.sequence(Options.class, "SSL_SUPPORTED_PROTOCOLS", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 196 */   public static final Option<String> SSL_PROVIDER = Option.simple(Options.class, "SSL_PROVIDER", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 203 */   public static final Option<String> SSL_PROTOCOL = Option.simple(Options.class, "SSL_PROTOCOL", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 210 */   public static final Option<Boolean> SSL_ENABLE_SESSION_CREATION = Option.simple(Options.class, "SSL_ENABLE_SESSION_CREATION", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 218 */   public static final Option<Boolean> SSL_USE_CLIENT_MODE = Option.simple(Options.class, "SSL_USE_CLIENT_MODE", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 225 */   public static final Option<Integer> SSL_CLIENT_SESSION_CACHE_SIZE = Option.simple(Options.class, "SSL_CLIENT_SESSION_CACHE_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 232 */   public static final Option<Integer> SSL_CLIENT_SESSION_TIMEOUT = Option.simple(Options.class, "SSL_CLIENT_SESSION_TIMEOUT", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 239 */   public static final Option<Integer> SSL_SERVER_SESSION_CACHE_SIZE = Option.simple(Options.class, "SSL_SERVER_SESSION_CACHE_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 246 */   public static final Option<Integer> SSL_SERVER_SESSION_TIMEOUT = Option.simple(Options.class, "SSL_SERVER_SESSION_TIMEOUT", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 253 */   public static final Option<Sequence<Class<? extends KeyManager>>> SSL_JSSE_KEY_MANAGER_CLASSES = Option.typeSequence(Options.class, "SSL_JSSE_KEY_MANAGER_CLASSES", KeyManager.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 260 */   public static final Option<Sequence<Class<? extends TrustManager>>> SSL_JSSE_TRUST_MANAGER_CLASSES = Option.typeSequence(Options.class, "SSL_JSSE_TRUST_MANAGER_CLASSES", TrustManager.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 267 */   public static final Option<OptionMap> SSL_RNG_OPTIONS = Option.simple(Options.class, "SSL_RNG_OPTIONS", OptionMap.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 274 */   public static final Option<Integer> SSL_PACKET_BUFFER_SIZE = Option.simple(Options.class, "SSL_PACKET_BUFFER_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 281 */   public static final Option<Integer> SSL_APPLICATION_BUFFER_SIZE = Option.simple(Options.class, "SSL_APPLICATION_BUFFER_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 288 */   public static final Option<Integer> SSL_PACKET_BUFFER_REGION_SIZE = Option.simple(Options.class, "SSL_PACKET_BUFFER_REGION_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 295 */   public static final Option<Integer> SSL_APPLICATION_BUFFER_REGION_SIZE = Option.simple(Options.class, "SSL_APPLICATION_BUFFER_REGION_SIZE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 302 */   public static final Option<Boolean> SSL_STARTTLS = Option.simple(Options.class, "SSL_STARTTLS", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 309 */   public static final Option<String> SSL_PEER_HOST_NAME = Option.simple(Options.class, "SSL_PEER_HOST_NAME", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 315 */   public static final Option<Integer> SSL_PEER_PORT = Option.simple(Options.class, "SSL_PEER_PORT", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 321 */   public static final Option<Boolean> SSL_NON_BLOCKING_KEY_MANAGER = Option.simple(Options.class, "SSL_NON_BLOCKING_KEY_MANAGER", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 327 */   public static final Option<Boolean> SSL_NON_BLOCKING_TRUST_MANAGER = Option.simple(Options.class, "SSL_NON_BLOCKING_TRUST_MANAGER", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 334 */   public static final Option<Boolean> USE_DIRECT_BUFFERS = Option.simple(Options.class, "USE_DIRECT_BUFFERS", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 340 */   public static final Option<Boolean> SECURE = Option.simple(Options.class, "SECURE", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 347 */   public static final Option<Boolean> SASL_POLICY_FORWARD_SECRECY = Option.simple(Options.class, "SASL_POLICY_FORWARD_SECRECY", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 354 */   public static final Option<Boolean> SASL_POLICY_NOACTIVE = Option.simple(Options.class, "SASL_POLICY_NOACTIVE", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 361 */   public static final Option<Boolean> SASL_POLICY_NOANONYMOUS = Option.simple(Options.class, "SASL_POLICY_NOANONYMOUS", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 368 */   public static final Option<Boolean> SASL_POLICY_NODICTIONARY = Option.simple(Options.class, "SASL_POLICY_NODICTIONARY", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 375 */   public static final Option<Boolean> SASL_POLICY_NOPLAINTEXT = Option.simple(Options.class, "SASL_POLICY_NOPLAINTEXT", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 382 */   public static final Option<Boolean> SASL_POLICY_PASS_CREDENTIALS = Option.simple(Options.class, "SASL_POLICY_PASS_CREDENTIALS", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 389 */   public static final Option<Sequence<SaslQop>> SASL_QOP = Option.sequence(Options.class, "SASL_QOP", SaslQop.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 396 */   public static final Option<SaslStrength> SASL_STRENGTH = Option.simple(Options.class, "SASL_STRENGTH", SaslStrength.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 403 */   public static final Option<Boolean> SASL_SERVER_AUTH = Option.simple(Options.class, "SASL_SERVER_AUTH", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 410 */   public static final Option<Boolean> SASL_REUSE = Option.simple(Options.class, "SASL_REUSE", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 415 */   public static final Option<Sequence<String>> SASL_MECHANISMS = Option.sequence(Options.class, "SASL_MECHANISMS", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 420 */   public static final Option<Sequence<String>> SASL_DISALLOWED_MECHANISMS = Option.sequence(Options.class, "SASL_DISALLOWED_MECHANISMS", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 425 */   public static final Option<Sequence<Property>> SASL_PROPERTIES = Option.sequence(Options.class, "SASL_PROPERTIES", Property.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 430 */   public static final Option<FileAccess> FILE_ACCESS = Option.simple(Options.class, "FILE_ACCESS", FileAccess.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 436 */   public static final Option<Boolean> FILE_APPEND = Option.simple(Options.class, "FILE_APPEND", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 442 */   public static final Option<Boolean> FILE_CREATE = Option.simple(Options.class, "FILE_CREATE", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 447 */   public static final Option<Long> STACK_SIZE = Option.simple(Options.class, "STACK_SIZE", Long.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 453 */   public static final Option<String> WORKER_NAME = Option.simple(Options.class, "WORKER_NAME", String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 458 */   public static final Option<Integer> THREAD_PRIORITY = Option.simple(Options.class, "THREAD_PRIORITY", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 463 */   public static final Option<Boolean> THREAD_DAEMON = Option.simple(Options.class, "THREAD_DAEMON", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 468 */   public static final Option<Integer> WORKER_IO_THREADS = Option.simple(Options.class, "WORKER_IO_THREADS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 474 */   public static final Option<Integer> WORKER_READ_THREADS = Option.simple(Options.class, "WORKER_READ_THREADS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 480 */   public static final Option<Integer> WORKER_WRITE_THREADS = Option.simple(Options.class, "WORKER_WRITE_THREADS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 487 */   public static final Option<Boolean> SPLIT_READ_WRITE_THREADS = Option.simple(Options.class, "SPLIT_READ_WRITE_THREADS", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 494 */   public static final Option<Boolean> WORKER_ESTABLISH_WRITING = Option.simple(Options.class, "WORKER_ESTABLISH_WRITING", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 505 */   public static final Option<Integer> WORKER_ACCEPT_THREADS = Option.simple(Options.class, "WORKER_ACCEPT_THREADS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 510 */   public static final Option<Integer> WORKER_TASK_CORE_THREADS = Option.simple(Options.class, "WORKER_TASK_CORE_THREADS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 515 */   public static final Option<Integer> WORKER_TASK_MAX_THREADS = Option.simple(Options.class, "WORKER_TASK_MAX_THREADS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 520 */   public static final Option<Integer> WORKER_TASK_KEEPALIVE = Option.simple(Options.class, "WORKER_TASK_KEEPALIVE", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 525 */   public static final Option<Integer> WORKER_TASK_LIMIT = Option.simple(Options.class, "WORKER_TASK_LIMIT", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 532 */   public static final Option<Boolean> CORK = Option.simple(Options.class, "CORK", Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 538 */   public static final Option<Integer> CONNECTION_HIGH_WATER = Option.simple(Options.class, "CONNECTION_HIGH_WATER", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 544 */   public static final Option<Integer> CONNECTION_LOW_WATER = Option.simple(Options.class, "CONNECTION_LOW_WATER", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 549 */   public static final Option<Integer> COMPRESSION_LEVEL = Option.simple(Options.class, "COMPRESSION_LEVEL", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 554 */   public static final Option<CompressionType> COMPRESSION_TYPE = Option.simple(Options.class, "COMPRESSION_TYPE", CompressionType.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 560 */   public static final Option<Integer> BALANCING_TOKENS = Option.simple(Options.class, "BALANCING_TOKENS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 565 */   public static final Option<Integer> BALANCING_CONNECTIONS = Option.simple(Options.class, "BALANCING_CONNECTIONS", Integer.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 570 */   public static final Option<Integer> WATCHER_POLL_INTERVAL = Option.simple(Options.class, "WATCHER_POLL_INTERVAL", Integer.class);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Options.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */