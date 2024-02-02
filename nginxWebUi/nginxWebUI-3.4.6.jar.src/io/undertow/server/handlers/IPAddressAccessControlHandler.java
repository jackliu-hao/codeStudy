/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.NetworkUtils;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.stream.Collectors;
/*     */ import org.xnio.Bits;
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
/*     */ public class IPAddressAccessControlHandler
/*     */   implements HttpHandler
/*     */ {
/*  57 */   private static final Pattern IP4_EXACT = Pattern.compile("(?:\\d{1,3}\\.){3}\\d{1,3}");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  62 */   private static final Pattern IP4_WILDCARD = Pattern.compile("(?:(?:\\d{1,3}|\\*)\\.){3}(?:\\d{1,3}|\\*)");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   private static final Pattern IP4_SLASH = Pattern.compile("(?:\\d{1,3}\\.){3}\\d{1,3}\\/\\d\\d?");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   private static final Pattern IP6_EXACT = Pattern.compile("^(?:([0-9a-fA-F]{1,4}:){7,7}(?:[0-9a-fA-F]){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,7}(?:(:))|(?:([0-9a-fA-F]{1,4}:)){1,6}(?:(:[0-9a-fA-F]){1,4})|(?:([0-9a-fA-F]{1,4}:)){1,5}(?:(:[0-9a-fA-F]{1,4})){1,2}|(?:([0-9a-fA-F]{1,4}:)){1,4}(?:(:[0-9a-fA-F]{1,4})){1,3}|(?:([0-9a-fA-F]{1,4}:)){1,3}(?:(:[0-9a-fA-F]{1,4})){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,2}(?:(:[0-9a-fA-F]{1,4})){1,5}|(?:([0-9a-fA-F]{1,4}:))(?:(:[0-9a-fA-F]{1,4})){1,6}|(?:(:))(?:((:[0-9a-fA-F]{1,4}){1,7}|(?:(:)))))$");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   private static final Pattern IP6_WILDCARD = Pattern.compile("(?:(?:[a-zA-Z0-9]{1,4}|\\*):){7}(?:[a-zA-Z0-9]{1,4}|\\*)");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   private static final Pattern IP6_SLASH = Pattern.compile("(?:[a-zA-Z0-9]{1,4}:){7}[a-zA-Z0-9]{1,4}\\/\\d{1,3}");
/*     */   
/*     */   private volatile HttpHandler next;
/*     */   private volatile boolean defaultAllow = false;
/*     */   private final int denyResponseCode;
/*  87 */   private final List<PeerMatch> ipv6acl = new CopyOnWriteArrayList<>();
/*  88 */   private final List<PeerMatch> ipv4acl = new CopyOnWriteArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  93 */   private static final boolean traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
/*  94 */   private static final boolean debugEnabled = UndertowLogger.PREDICATE_LOGGER.isDebugEnabled();
/*     */ 
/*     */   
/*     */   public IPAddressAccessControlHandler(HttpHandler next) {
/*  98 */     this(next, 403);
/*     */   }
/*     */   
/*     */   public IPAddressAccessControlHandler(HttpHandler next, int denyResponseCode) {
/* 102 */     this.next = next;
/* 103 */     this.denyResponseCode = denyResponseCode;
/*     */   }
/*     */   
/*     */   public IPAddressAccessControlHandler() {
/* 107 */     this.next = ResponseCodeHandler.HANDLE_404;
/* 108 */     this.denyResponseCode = 403;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 113 */     InetSocketAddress peer = exchange.getSourceAddress();
/* 114 */     if (isAllowed(peer.getAddress())) {
/* 115 */       this.next.handleRequest(exchange);
/*     */     } else {
/* 117 */       if (debugEnabled) {
/* 118 */         UndertowLogger.PREDICATE_LOGGER.debugf("Access to [%s] blocked from %s.", exchange, peer.getHostString());
/*     */       }
/* 120 */       exchange.setStatusCode(this.denyResponseCode);
/* 121 */       exchange.endExchange();
/*     */     } 
/*     */   }
/*     */   
/*     */   boolean isAllowed(InetAddress address) {
/* 126 */     if (address instanceof java.net.Inet4Address) {
/* 127 */       for (PeerMatch rule : this.ipv4acl) {
/* 128 */         if (traceEnabled) {
/* 129 */           UndertowLogger.PREDICATE_LOGGER.tracef("Comparing rule [%s] to IPv4 address %s.", rule.toPredicateString(), address.getHostAddress());
/*     */         }
/* 131 */         if (rule.matches(address)) {
/* 132 */           return !rule.isDeny();
/*     */         }
/*     */       } 
/* 135 */     } else if (address instanceof java.net.Inet6Address) {
/* 136 */       for (PeerMatch rule : this.ipv6acl) {
/* 137 */         if (traceEnabled) {
/* 138 */           UndertowLogger.PREDICATE_LOGGER.tracef("Comparing rule [%s] to IPv6 address %s.", rule.toPredicateString(), address.getHostAddress());
/*     */         }
/* 140 */         if (rule.matches(address)) {
/* 141 */           return !rule.isDeny();
/*     */         }
/*     */       } 
/*     */     } 
/* 145 */     return this.defaultAllow;
/*     */   }
/*     */   
/*     */   public int getDenyResponseCode() {
/* 149 */     return this.denyResponseCode;
/*     */   }
/*     */   
/*     */   public boolean isDefaultAllow() {
/* 153 */     return this.defaultAllow;
/*     */   }
/*     */   
/*     */   public IPAddressAccessControlHandler setDefaultAllow(boolean defaultAllow) {
/* 157 */     this.defaultAllow = defaultAllow;
/* 158 */     return this;
/*     */   }
/*     */   
/*     */   public HttpHandler getNext() {
/* 162 */     return this.next;
/*     */   }
/*     */   
/*     */   public IPAddressAccessControlHandler setNext(HttpHandler next) {
/* 166 */     this.next = next;
/* 167 */     return this;
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
/*     */   public IPAddressAccessControlHandler addAllow(String peer) {
/* 185 */     return addRule(peer, false);
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
/*     */   public IPAddressAccessControlHandler addDeny(String peer) {
/* 203 */     return addRule(peer, true);
/*     */   }
/*     */   
/*     */   public IPAddressAccessControlHandler clearRules() {
/* 207 */     this.ipv4acl.clear();
/* 208 */     this.ipv6acl.clear();
/* 209 */     return this;
/*     */   }
/*     */   
/*     */   private IPAddressAccessControlHandler addRule(String peer, boolean deny) {
/* 213 */     if (IP4_EXACT.matcher(peer).matches()) {
/* 214 */       addIpV4ExactMatch(peer, deny);
/* 215 */     } else if (IP4_WILDCARD.matcher(peer).matches()) {
/* 216 */       addIpV4WildcardMatch(peer, deny);
/* 217 */     } else if (IP4_SLASH.matcher(peer).matches()) {
/* 218 */       addIpV4SlashPrefix(peer, deny);
/* 219 */     } else if (IP6_EXACT.matcher(peer).matches()) {
/* 220 */       addIpV6ExactMatch(peer, deny);
/* 221 */     } else if (IP6_WILDCARD.matcher(peer).matches()) {
/* 222 */       addIpV6WildcardMatch(peer, deny);
/* 223 */     } else if (IP6_SLASH.matcher(peer).matches()) {
/* 224 */       addIpV6SlashPrefix(peer, deny);
/*     */     } else {
/* 226 */       throw UndertowMessages.MESSAGES.notAValidIpPattern(peer);
/*     */     } 
/* 228 */     return this;
/*     */   }
/*     */   
/*     */   private void addIpV6SlashPrefix(String peer, boolean deny) {
/* 232 */     String[] components = peer.split("\\/");
/* 233 */     String[] parts = components[0].split("\\:");
/* 234 */     int maskLen = Integer.parseInt(components[1]);
/* 235 */     assert parts.length == 8;
/*     */     
/* 237 */     byte[] pattern = new byte[16];
/* 238 */     byte[] mask = new byte[16];
/*     */     int i;
/* 240 */     for (i = 0; i < 8; i++) {
/* 241 */       int val = Integer.parseInt(parts[i], 16);
/* 242 */       pattern[i * 2] = (byte)(val >> 8);
/* 243 */       pattern[i * 2 + 1] = (byte)(val & 0xFF);
/*     */     } 
/* 245 */     for (i = 0; i < 16; i++) {
/* 246 */       if (maskLen > 8) {
/* 247 */         mask[i] = -1;
/* 248 */         maskLen -= 8;
/* 249 */       } else if (maskLen != 0) {
/* 250 */         mask[i] = (byte)(Bits.intBitMask(8 - maskLen, 7) & 0xFF);
/* 251 */         maskLen = 0;
/*     */       } else {
/*     */         break;
/*     */       } 
/*     */     } 
/* 256 */     this.ipv6acl.add(new PrefixIpV6PeerMatch(deny, peer, mask, pattern));
/*     */   }
/*     */   
/*     */   private void addIpV4SlashPrefix(String peer, boolean deny) {
/* 260 */     String[] components = peer.split("\\/");
/* 261 */     String[] parts = components[0].split("\\.");
/* 262 */     int maskLen = Integer.parseInt(components[1]);
/* 263 */     int mask = Bits.intBitMask(32 - maskLen, 31);
/* 264 */     int prefix = 0;
/* 265 */     for (int i = 0; i < 4; i++) {
/* 266 */       prefix <<= 8;
/* 267 */       String part = parts[i];
/* 268 */       int no = Integer.parseInt(part);
/* 269 */       prefix |= no;
/*     */     } 
/* 271 */     this.ipv4acl.add(new PrefixIpV4PeerMatch(deny, peer, mask, prefix));
/*     */   }
/*     */   
/*     */   private void addIpV6WildcardMatch(String peer, boolean deny) {
/* 275 */     byte[] pattern = new byte[16];
/* 276 */     byte[] mask = new byte[16];
/* 277 */     String[] parts = peer.split("\\:");
/* 278 */     assert parts.length == 8;
/* 279 */     for (int i = 0; i < 8; i++) {
/* 280 */       if (!parts[i].equals("*")) {
/* 281 */         int val = Integer.parseInt(parts[i], 16);
/* 282 */         pattern[i * 2] = (byte)(val >> 8);
/* 283 */         pattern[i * 2 + 1] = (byte)(val & 0xFF);
/* 284 */         mask[i * 2] = -1;
/* 285 */         mask[i * 2 + 1] = -1;
/*     */       } 
/*     */     } 
/* 288 */     this.ipv6acl.add(new PrefixIpV6PeerMatch(deny, peer, mask, pattern));
/*     */   }
/*     */   
/*     */   private void addIpV4WildcardMatch(String peer, boolean deny) {
/* 292 */     String[] parts = peer.split("\\.");
/* 293 */     int mask = 0;
/* 294 */     int prefix = 0;
/* 295 */     for (int i = 0; i < 4; i++) {
/* 296 */       mask <<= 8;
/* 297 */       prefix <<= 8;
/* 298 */       String part = parts[i];
/* 299 */       if (!part.equals("*")) {
/* 300 */         int no = Integer.parseInt(part);
/* 301 */         mask |= 0xFF;
/* 302 */         prefix |= no;
/*     */       } 
/*     */     } 
/* 305 */     this.ipv4acl.add(new PrefixIpV4PeerMatch(deny, peer, mask, prefix));
/*     */   }
/*     */ 
/*     */   
/*     */   private void addIpV6ExactMatch(String peer, boolean deny) {
/*     */     try {
/* 311 */       byte[] bytes = NetworkUtils.parseIpv6AddressToBytes(peer);
/* 312 */       this.ipv6acl.add(new ExactIpV6PeerMatch(deny, peer, bytes));
/* 313 */     } catch (IOException e) {
/* 314 */       throw UndertowMessages.MESSAGES.invalidACLAddress(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addIpV4ExactMatch(String peer, boolean deny) {
/* 319 */     String[] parts = peer.split("\\.");
/* 320 */     byte[] bytes = { (byte)Integer.parseInt(parts[0]), (byte)Integer.parseInt(parts[1]), (byte)Integer.parseInt(parts[2]), (byte)Integer.parseInt(parts[3]) };
/* 321 */     this.ipv4acl.add(new ExactIpV4PeerMatch(deny, peer, bytes));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 327 */     String predicate = "ip-access-control( default-allow=" + this.defaultAllow + ", acl={ ";
/* 328 */     List<PeerMatch> acl = new ArrayList<>();
/* 329 */     acl.addAll(this.ipv4acl);
/* 330 */     acl.addAll(this.ipv6acl);
/*     */     
/* 332 */     predicate = predicate + (String)acl.stream().map(s -> "'" + s.toPredicateString() + "'").collect(Collectors.joining(", "));
/* 333 */     predicate = predicate + " }";
/* 334 */     if (this.denyResponseCode != 403) {
/* 335 */       predicate = predicate + ", failure-status=" + this.denyResponseCode;
/*     */     }
/* 337 */     predicate = predicate + " )";
/* 338 */     return predicate;
/*     */   }
/*     */   
/*     */   static abstract class PeerMatch
/*     */   {
/*     */     private final boolean deny;
/*     */     private final String pattern;
/*     */     
/*     */     protected PeerMatch(boolean deny, String pattern) {
/* 347 */       this.deny = deny;
/* 348 */       this.pattern = pattern;
/*     */     }
/*     */     
/*     */     abstract boolean matches(InetAddress param1InetAddress);
/*     */     
/*     */     boolean isDeny() {
/* 354 */       return this.deny;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 359 */       return getClass().getSimpleName() + "{deny=" + this.deny + ", pattern='" + this.pattern + '\'' + '}';
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toPredicateString() {
/* 366 */       return this.pattern + " " + (this.deny ? "deny" : "allow");
/*     */     }
/*     */   }
/*     */   
/*     */   static class ExactIpV4PeerMatch
/*     */     extends PeerMatch {
/*     */     private final byte[] address;
/*     */     
/*     */     protected ExactIpV4PeerMatch(boolean deny, String pattern, byte[] address) {
/* 375 */       super(deny, pattern);
/* 376 */       this.address = address;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean matches(InetAddress address) {
/* 381 */       return Arrays.equals(address.getAddress(), this.address);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ExactIpV6PeerMatch
/*     */     extends PeerMatch {
/*     */     private final byte[] address;
/*     */     
/*     */     protected ExactIpV6PeerMatch(boolean deny, String pattern, byte[] address) {
/* 390 */       super(deny, pattern);
/* 391 */       this.address = address;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean matches(InetAddress address) {
/* 396 */       return Arrays.equals(address.getAddress(), this.address);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class PrefixIpV4PeerMatch
/*     */     extends PeerMatch {
/*     */     private final int mask;
/*     */     private final int prefix;
/*     */     
/*     */     protected PrefixIpV4PeerMatch(boolean deny, String pattern, int mask, int prefix) {
/* 406 */       super(deny, pattern);
/* 407 */       this.mask = mask;
/* 408 */       this.prefix = prefix;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean matches(InetAddress address) {
/* 413 */       byte[] bytes = address.getAddress();
/* 414 */       if (bytes == null) {
/* 415 */         return false;
/*     */       }
/* 417 */       int addressInt = (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | bytes[3] & 0xFF;
/* 418 */       return ((addressInt & this.mask) == this.prefix);
/*     */     }
/*     */   }
/*     */   
/*     */   static class PrefixIpV6PeerMatch
/*     */     extends PeerMatch {
/*     */     private final byte[] mask;
/*     */     private final byte[] prefix;
/*     */     
/*     */     protected PrefixIpV6PeerMatch(boolean deny, String pattern, byte[] mask, byte[] prefix) {
/* 428 */       super(deny, pattern);
/* 429 */       this.mask = mask;
/* 430 */       this.prefix = prefix;
/* 431 */       assert mask.length == prefix.length;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean matches(InetAddress address) {
/* 436 */       byte[] bytes = address.getAddress();
/* 437 */       if (bytes == null) {
/* 438 */         return false;
/*     */       }
/* 440 */       if (bytes.length != this.mask.length) {
/* 441 */         return false;
/*     */       }
/* 443 */       for (int i = 0; i < this.mask.length; i++) {
/* 444 */         if ((bytes[i] & this.mask[i]) != this.prefix[i]) {
/* 445 */           return false;
/*     */         }
/*     */       } 
/* 448 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 456 */       return "ip-access-control";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 461 */       Map<String, Class<?>> params = new HashMap<>();
/* 462 */       params.put("acl", String[].class);
/* 463 */       params.put("failure-status", int.class);
/* 464 */       params.put("default-allow", boolean.class);
/* 465 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 470 */       return Collections.singleton("acl");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 475 */       return "acl";
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 481 */       String[] acl = (String[])config.get("acl");
/* 482 */       Boolean defaultAllow = (Boolean)config.get("default-allow");
/* 483 */       Integer failureStatus = (Integer)config.get("failure-status");
/*     */       
/* 485 */       List<IPAddressAccessControlHandler.Holder> peerMatches = new ArrayList<>();
/* 486 */       for (String rule : acl) {
/* 487 */         String[] parts = rule.split(" ");
/* 488 */         if (parts.length != 2) {
/* 489 */           throw UndertowMessages.MESSAGES.invalidAclRule(rule);
/*     */         }
/* 491 */         if (parts[1].trim().equals("allow")) {
/* 492 */           peerMatches.add(new IPAddressAccessControlHandler.Holder(parts[0].trim(), false));
/* 493 */         } else if (parts[1].trim().equals("deny")) {
/* 494 */           peerMatches.add(new IPAddressAccessControlHandler.Holder(parts[0].trim(), true));
/*     */         } else {
/* 496 */           throw UndertowMessages.MESSAGES.invalidAclRule(rule);
/*     */         } 
/*     */       } 
/* 499 */       return new IPAddressAccessControlHandler.Wrapper(peerMatches, (defaultAllow == null) ? false : defaultAllow.booleanValue(), (failureStatus == null) ? 403 : failureStatus.intValue());
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final List<IPAddressAccessControlHandler.Holder> peerMatches;
/*     */     private final boolean defaultAllow;
/*     */     private final int failureStatus;
/*     */     
/*     */     private Wrapper(List<IPAddressAccessControlHandler.Holder> peerMatches, boolean defaultAllow, int failureStatus) {
/* 511 */       this.peerMatches = peerMatches;
/* 512 */       this.defaultAllow = defaultAllow;
/* 513 */       this.failureStatus = failureStatus;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 518 */       IPAddressAccessControlHandler res = new IPAddressAccessControlHandler(handler, this.failureStatus);
/* 519 */       for (IPAddressAccessControlHandler.Holder match : this.peerMatches) {
/* 520 */         if (match.deny) {
/* 521 */           res.addDeny(match.rule); continue;
/*     */         } 
/* 523 */         res.addAllow(match.rule);
/*     */       } 
/*     */       
/* 526 */       res.setDefaultAllow(this.defaultAllow);
/* 527 */       return res;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Holder
/*     */   {
/*     */     final String rule;
/*     */     final boolean deny;
/*     */     
/*     */     private Holder(String rule, boolean deny) {
/* 537 */       this.rule = rule;
/* 538 */       this.deny = deny;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\IPAddressAccessControlHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */