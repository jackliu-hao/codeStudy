/*     */ package com.mysql.cj.conf;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.UnsupportedConnectionStringException;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.util.SearchMode;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URI;
/*     */ import java.net.URLDecoder;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class ConnectionUrlParser
/*     */   implements DatabaseUrlContainer
/*     */ {
/*     */   private static final String DUMMY_SCHEMA = "cj://";
/*     */   private static final String USER_PASS_SEPARATOR = ":";
/*     */   private static final String USER_HOST_SEPARATOR = "@";
/*     */   private static final String HOSTS_SEPARATOR = ",";
/*     */   private static final String KEY_VALUE_HOST_INFO_OPENING_MARKER = "(";
/*     */   private static final String KEY_VALUE_HOST_INFO_CLOSING_MARKER = ")";
/*     */   private static final String HOSTS_LIST_OPENING_MARKERS = "[(";
/*     */   private static final String HOSTS_LIST_CLOSING_MARKERS = "])";
/*     */   private static final String ADDRESS_EQUALS_HOST_INFO_PREFIX = "ADDRESS=";
/*  90 */   private static final Pattern CONNECTION_STRING_PTRN = Pattern.compile("(?<scheme>[\\w\\+:%]+)\\s*(?://(?<authority>[^/?#]*))?\\s*(?:/(?!\\s*/)(?<path>[^?#]*))?(?:\\?(?!\\s*\\?)(?<query>[^#]*))?(?:\\s*#(?<fragment>.*))?");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  95 */   private static final Pattern SCHEME_PTRN = Pattern.compile("(?<scheme>[\\w\\+:%]+).*");
/*  96 */   private static final Pattern HOST_LIST_PTRN = Pattern.compile("^\\[(?<hosts>.*)\\]$");
/*  97 */   private static final Pattern GENERIC_HOST_PTRN = Pattern.compile("^(?<host>.*?)(?::(?<port>[^:]*))?$");
/*  98 */   private static final Pattern KEY_VALUE_HOST_PTRN = Pattern.compile("[,\\s]*(?<key>[\\w\\.\\-\\s%]*)(?:=(?<value>[^,]*))?");
/*  99 */   private static final Pattern ADDRESS_EQUALS_HOST_PTRN = Pattern.compile("\\s*\\(\\s*(?<key>[\\w\\.\\-%]+)?\\s*(?:=(?<value>[^)]*))?\\)\\s*");
/* 100 */   private static final Pattern PROPERTIES_PTRN = Pattern.compile("[&\\s]*(?<key>[\\w\\.\\-\\s%]*)(?:=(?<value>[^&]*))?");
/*     */   
/*     */   private final String baseConnectionString;
/*     */   
/*     */   private String scheme;
/*     */   private String authority;
/*     */   private String path;
/*     */   private String query;
/* 108 */   private List<HostInfo> parsedHosts = null;
/* 109 */   private Map<String, String> parsedProperties = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ConnectionUrlParser parseConnectionString(String connString) {
/* 119 */     return new ConnectionUrlParser(connString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ConnectionUrlParser(String connString) {
/* 129 */     if (connString == null) {
/* 130 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.0"));
/*     */     }
/* 132 */     if (!isConnectionStringSupported(connString)) {
/* 133 */       throw (UnsupportedConnectionStringException)ExceptionFactory.createException(UnsupportedConnectionStringException.class, 
/* 134 */           Messages.getString("ConnectionString.17", new String[] { connString }));
/*     */     }
/* 136 */     this.baseConnectionString = connString;
/* 137 */     parseConnectionString();
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
/*     */   public static boolean isConnectionStringSupported(String connString) {
/* 149 */     if (connString == null) {
/* 150 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.0"));
/*     */     }
/* 152 */     Matcher matcher = SCHEME_PTRN.matcher(connString);
/* 153 */     return (matcher.matches() && ConnectionUrl.Type.isSupported(decodeSkippingPlusSign(matcher.group("scheme"))));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseConnectionString() {
/* 160 */     String connString = this.baseConnectionString;
/* 161 */     Matcher matcher = CONNECTION_STRING_PTRN.matcher(connString);
/* 162 */     if (!matcher.matches()) {
/* 163 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.1"));
/*     */     }
/* 165 */     this.scheme = decodeSkippingPlusSign(matcher.group("scheme"));
/* 166 */     this.authority = matcher.group("authority");
/* 167 */     this.path = (matcher.group("path") == null) ? null : decode(matcher.group("path")).trim();
/* 168 */     this.query = matcher.group("query");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseAuthoritySection() {
/* 175 */     if (StringUtils.isNullOrEmpty(this.authority)) {
/*     */       
/* 177 */       this.parsedHosts.add(new HostInfo());
/*     */       
/*     */       return;
/*     */     } 
/* 181 */     List<String> authoritySegments = StringUtils.split(this.authority, ",", "[(", "])", true, SearchMode.__MRK_WS);
/*     */     
/* 183 */     for (String hi : authoritySegments) {
/* 184 */       parseAuthoritySegment(hi);
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseAuthoritySegment(String authSegment) {
/* 210 */     Pair<String, String> userHostInfoSplit = splitByUserInfoAndHostInfo(authSegment);
/* 211 */     String userInfo = StringUtils.safeTrim((String)userHostInfoSplit.left);
/* 212 */     String user = null;
/* 213 */     String password = null;
/* 214 */     if (!StringUtils.isNullOrEmpty(userInfo)) {
/* 215 */       Pair<String, String> userInfoPair = parseUserInfo(userInfo);
/* 216 */       user = decode(StringUtils.safeTrim((String)userInfoPair.left));
/* 217 */       password = decode(StringUtils.safeTrim((String)userInfoPair.right));
/*     */     } 
/* 219 */     String hostInfo = StringUtils.safeTrim((String)userHostInfoSplit.right);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 224 */     HostInfo hi = buildHostInfoForEmptyHost(user, password, hostInfo);
/* 225 */     if (hi != null) {
/* 226 */       this.parsedHosts.add(hi);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 233 */     hi = buildHostInfoResortingToUriParser(user, password, authSegment);
/* 234 */     if (hi != null) {
/* 235 */       this.parsedHosts.add(hi);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 242 */     List<HostInfo> hiList = buildHostInfoResortingToSubHostsListParser(user, password, hostInfo);
/* 243 */     if (hiList != null) {
/* 244 */       this.parsedHosts.addAll(hiList);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 251 */     hi = buildHostInfoResortingToKeyValueSyntaxParser(user, password, hostInfo);
/* 252 */     if (hi != null) {
/* 253 */       this.parsedHosts.add(hi);
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 261 */     hi = buildHostInfoResortingToAddressEqualsSyntaxParser(user, password, hostInfo);
/* 262 */     if (hi != null) {
/* 263 */       this.parsedHosts.add(hi);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 270 */     hi = buildHostInfoResortingToGenericSyntaxParser(user, password, hostInfo);
/* 271 */     if (hi != null) {
/* 272 */       this.parsedHosts.add(hi);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 279 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.2", new Object[] { authSegment }));
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
/*     */   private HostInfo buildHostInfoForEmptyHost(String user, String password, String hostInfo) {
/* 294 */     if (StringUtils.isNullOrEmpty(hostInfo)) {
/* 295 */       return new HostInfo(this, null, -1, user, password);
/*     */     }
/* 297 */     return null;
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
/*     */   private HostInfo buildHostInfoResortingToUriParser(String user, String password, String hostInfo) {
/* 313 */     String host = null;
/* 314 */     int port = -1;
/*     */     
/*     */     try {
/* 317 */       URI uri = URI.create("cj://" + hostInfo);
/* 318 */       if (uri.getHost() != null) {
/* 319 */         host = decode(uri.getHost());
/*     */       }
/* 321 */       if (uri.getPort() != -1) {
/* 322 */         port = uri.getPort();
/*     */       }
/* 324 */       if (uri.getUserInfo() != null)
/*     */       {
/* 326 */         return null;
/*     */       }
/* 328 */     } catch (IllegalArgumentException e) {
/*     */       
/* 330 */       return null;
/*     */     } 
/* 332 */     if (host != null || port != -1)
/*     */     {
/* 334 */       return new HostInfo(this, host, port, user, password);
/*     */     }
/* 336 */     return null;
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
/*     */   private List<HostInfo> buildHostInfoResortingToSubHostsListParser(String user, String password, String hostInfo) {
/* 351 */     Matcher matcher = HOST_LIST_PTRN.matcher(hostInfo);
/* 352 */     if (matcher.matches()) {
/* 353 */       String hosts = matcher.group("hosts");
/* 354 */       List<String> hostsList = StringUtils.split(hosts, ",", "[(", "])", true, SearchMode.__MRK_WS);
/*     */ 
/*     */       
/* 357 */       boolean maybeIPv6 = (hostsList.size() == 1 && ((String)hostsList.get(0)).matches("(?i)^[\\dabcdef:]+$"));
/* 358 */       List<HostInfo> hostInfoList = new ArrayList<>();
/* 359 */       for (String h : hostsList) {
/*     */         HostInfo hi;
/* 361 */         if ((hi = buildHostInfoForEmptyHost(user, password, h)) != null) {
/* 362 */           hostInfoList.add(hi); continue;
/* 363 */         }  if ((hi = buildHostInfoResortingToUriParser(user, password, h)) != null || (maybeIPv6 && (
/* 364 */           hi = buildHostInfoResortingToUriParser(user, password, "[" + h + "]")) != null)) {
/* 365 */           hostInfoList.add(hi); continue;
/* 366 */         }  if ((hi = buildHostInfoResortingToKeyValueSyntaxParser(user, password, h)) != null) {
/* 367 */           hostInfoList.add(hi); continue;
/* 368 */         }  if ((hi = buildHostInfoResortingToAddressEqualsSyntaxParser(user, password, h)) != null) {
/* 369 */           hostInfoList.add(hi); continue;
/* 370 */         }  if ((hi = buildHostInfoResortingToGenericSyntaxParser(user, password, h)) != null) {
/* 371 */           hostInfoList.add(hi); continue;
/*     */         } 
/* 373 */         return null;
/*     */       } 
/*     */       
/* 376 */       return hostInfoList;
/*     */     } 
/* 378 */     return null;
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
/*     */   private HostInfo buildHostInfoResortingToKeyValueSyntaxParser(String user, String password, String hostInfo) {
/* 393 */     if (!hostInfo.startsWith("(") || !hostInfo.endsWith(")"))
/*     */     {
/* 395 */       return null;
/*     */     }
/* 397 */     hostInfo = hostInfo.substring("(".length(), hostInfo.length() - ")".length());
/* 398 */     return new HostInfo(this, null, -1, user, password, processKeyValuePattern(KEY_VALUE_HOST_PTRN, hostInfo));
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
/*     */   private HostInfo buildHostInfoResortingToAddressEqualsSyntaxParser(String user, String password, String hostInfo) {
/* 413 */     int p = StringUtils.indexOfIgnoreCase(hostInfo, "ADDRESS=");
/* 414 */     if (p != 0)
/*     */     {
/* 416 */       return null;
/*     */     }
/* 418 */     hostInfo = hostInfo.substring(p + "ADDRESS=".length()).trim();
/* 419 */     return new HostInfo(this, null, -1, user, password, processKeyValuePattern(ADDRESS_EQUALS_HOST_PTRN, hostInfo));
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
/*     */   private HostInfo buildHostInfoResortingToGenericSyntaxParser(String user, String password, String hostInfo) {
/* 434 */     if ((splitByUserInfoAndHostInfo(hostInfo)).left != null)
/*     */     {
/* 436 */       return null;
/*     */     }
/* 438 */     Pair<String, Integer> hostPortPair = parseHostPortPair(hostInfo);
/* 439 */     String host = decode(StringUtils.safeTrim((String)hostPortPair.left));
/* 440 */     Integer port = (Integer)hostPortPair.right;
/* 441 */     return new HostInfo(this, StringUtils.isNullOrEmpty(host) ? null : host, port.intValue(), user, password);
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
/*     */   private Pair<String, String> splitByUserInfoAndHostInfo(String authSegment) {
/* 453 */     String userInfoPart = null;
/* 454 */     String hostInfoPart = authSegment;
/* 455 */     int p = authSegment.indexOf("@");
/* 456 */     if (p >= 0) {
/* 457 */       userInfoPart = authSegment.substring(0, p);
/* 458 */       hostInfoPart = authSegment.substring(p + "@".length());
/*     */     } 
/* 460 */     return new Pair<>(userInfoPart, hostInfoPart);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Pair<String, String> parseUserInfo(String userInfo) {
/* 471 */     if (StringUtils.isNullOrEmpty(userInfo)) {
/* 472 */       return null;
/*     */     }
/* 474 */     String[] userInfoParts = userInfo.split(":", 2);
/* 475 */     String userName = userInfoParts[0];
/* 476 */     String password = (userInfoParts.length > 1) ? userInfoParts[1] : null;
/* 477 */     return new Pair<>(userName, password);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Pair<String, Integer> parseHostPortPair(String hostInfo) {
/* 488 */     if (StringUtils.isNullOrEmpty(hostInfo)) {
/* 489 */       return null;
/*     */     }
/* 491 */     Matcher matcher = GENERIC_HOST_PTRN.matcher(hostInfo);
/* 492 */     if (matcher.matches()) {
/* 493 */       String host = matcher.group("host");
/* 494 */       String portAsString = decode(StringUtils.safeTrim(matcher.group("port")));
/* 495 */       Integer portAsInteger = Integer.valueOf(-1);
/* 496 */       if (!StringUtils.isNullOrEmpty(portAsString)) {
/*     */         try {
/* 498 */           portAsInteger = Integer.valueOf(Integer.parseInt(portAsString));
/* 499 */         } catch (NumberFormatException e) {
/* 500 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.3", new Object[] { hostInfo }), e);
/*     */         } 
/*     */       }
/*     */       
/* 504 */       return new Pair<>(host, portAsInteger);
/*     */     } 
/* 506 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.3", new Object[] { hostInfo }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseQuerySection() {
/* 513 */     if (StringUtils.isNullOrEmpty(this.query)) {
/* 514 */       this.parsedProperties = new HashMap<>();
/*     */       return;
/*     */     } 
/* 517 */     this.parsedProperties = processKeyValuePattern(PROPERTIES_PTRN, this.query);
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
/*     */   private Map<String, String> processKeyValuePattern(Pattern pattern, String input) {
/* 532 */     Matcher matcher = pattern.matcher(input);
/* 533 */     int p = 0;
/* 534 */     Map<String, String> kvMap = new HashMap<>();
/* 535 */     while (matcher.find()) {
/* 536 */       if (matcher.start() != p) {
/* 537 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 538 */             Messages.getString("ConnectionString.4", new Object[] { input.substring(p) }));
/*     */       }
/* 540 */       String key = decode(StringUtils.safeTrim(matcher.group("key")));
/* 541 */       String value = decode(StringUtils.safeTrim(matcher.group("value")));
/* 542 */       if (!StringUtils.isNullOrEmpty(key)) {
/* 543 */         kvMap.put(key, value);
/* 544 */       } else if (!StringUtils.isNullOrEmpty(value)) {
/* 545 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 546 */             Messages.getString("ConnectionString.4", new Object[] { input.substring(p) }));
/*     */       } 
/* 548 */       p = matcher.end();
/*     */     } 
/* 550 */     if (p != input.length()) {
/* 551 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.4", new Object[] { input.substring(p) }));
/*     */     }
/* 553 */     return kvMap;
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
/*     */   private static String decode(String text) {
/* 565 */     if (StringUtils.isNullOrEmpty(text)) {
/* 566 */       return text;
/*     */     }
/*     */     try {
/* 569 */       return URLDecoder.decode(text, StandardCharsets.UTF_8.name());
/* 570 */     } catch (UnsupportedEncodingException unsupportedEncodingException) {
/*     */ 
/*     */       
/* 573 */       return "";
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String decodeSkippingPlusSign(String text) {
/* 585 */     if (StringUtils.isNullOrEmpty(text)) {
/* 586 */       return text;
/*     */     }
/* 588 */     text = text.replace("+", "%2B");
/*     */     try {
/* 590 */       return URLDecoder.decode(text, StandardCharsets.UTF_8.name());
/* 591 */     } catch (UnsupportedEncodingException unsupportedEncodingException) {
/*     */ 
/*     */       
/* 594 */       return "";
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDatabaseUrl() {
/* 604 */     return this.baseConnectionString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getScheme() {
/* 613 */     return this.scheme;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAuthority() {
/* 622 */     return this.authority;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPath() {
/* 631 */     return this.path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getQuery() {
/* 640 */     return this.query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<HostInfo> getHosts() {
/* 649 */     if (this.parsedHosts == null) {
/* 650 */       this.parsedHosts = new ArrayList<>();
/* 651 */       parseAuthoritySection();
/*     */     } 
/* 653 */     return this.parsedHosts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getProperties() {
/* 662 */     if (this.parsedProperties == null) {
/* 663 */       parseQuerySection();
/*     */     }
/* 665 */     return Collections.unmodifiableMap(this.parsedProperties);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 675 */     StringBuilder asStr = new StringBuilder(super.toString());
/* 676 */     asStr.append(String.format(" :: {scheme: \"%s\", authority: \"%s\", path: \"%s\", query: \"%s\", parsedHosts: %s, parsedProperties: %s}", new Object[] { this.scheme, this.authority, this.path, this.query, this.parsedHosts, this.parsedProperties }));
/*     */     
/* 678 */     return asStr.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Pair<T, U>
/*     */   {
/*     */     public final T left;
/*     */ 
/*     */     
/*     */     public final U right;
/*     */ 
/*     */ 
/*     */     
/*     */     public Pair(T left, U right) {
/* 694 */       this.left = left;
/* 695 */       this.right = right;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 700 */       StringBuilder asStr = new StringBuilder(super.toString());
/* 701 */       asStr.append(String.format(" :: { left: %s, right: %s }", new Object[] { this.left, this.right }));
/* 702 */       return asStr.toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\ConnectionUrlParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */