package io.undertow.server.protocol.ajp;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.security.impl.ExternalAuthenticationMechanism;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.BadRequestException;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.ParameterLimitException;
import io.undertow.util.URLUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AjpRequestParser {
   private final String encoding;
   private final boolean doDecode;
   private final boolean allowEncodedSlash;
   private final int maxParameters;
   private final int maxHeaders;
   private StringBuilder decodeBuffer;
   private final boolean allowUnescapedCharactersInUrl;
   private final Pattern allowedRequestAttributesPattern;
   private static final HttpString[] HTTP_HEADERS;
   public static final int FORWARD_REQUEST = 2;
   public static final int CPONG = 9;
   public static final int CPING = 10;
   public static final int SHUTDOWN = 7;
   private static final HttpString[] HTTP_METHODS = new HttpString[28];
   private static final String[] ATTRIBUTES;
   private static final Set<String> ATTR_SET;
   public static final String QUERY_STRING = "query_string";
   public static final String SSL_CERT = "ssl_cert";
   public static final String CONTEXT = "context";
   public static final String SERVLET_PATH = "servlet_path";
   public static final String REMOTE_USER = "remote_user";
   public static final String AUTH_TYPE = "auth_type";
   public static final String ROUTE = "route";
   public static final String SSL_CIPHER = "ssl_cipher";
   public static final String SSL_SESSION = "ssl_session";
   public static final String REQ_ATTRIBUTE = "req_attribute";
   public static final String SSL_KEY_SIZE = "ssl_key_size";
   public static final String SECRET = "secret";
   public static final String STORED_METHOD = "stored_method";
   public static final String AJP_REMOTE_PORT = "AJP_REMOTE_PORT";
   public static final int STRING_LENGTH_MASK = Integer.MIN_VALUE;

   public AjpRequestParser(String encoding, boolean doDecode, int maxParameters, int maxHeaders, boolean allowEncodedSlash, boolean allowUnescapedCharactersInUrl) {
      this(encoding, doDecode, maxParameters, maxHeaders, allowEncodedSlash, allowUnescapedCharactersInUrl, (String)null);
   }

   public AjpRequestParser(String encoding, boolean doDecode, int maxParameters, int maxHeaders, boolean allowEncodedSlash, boolean allowUnescapedCharactersInUrl, String allowedRequestAttributesPattern) {
      this.encoding = encoding;
      this.doDecode = doDecode;
      this.maxParameters = maxParameters;
      this.maxHeaders = maxHeaders;
      this.allowEncodedSlash = allowEncodedSlash;
      this.allowUnescapedCharactersInUrl = allowUnescapedCharactersInUrl;
      if (allowedRequestAttributesPattern != null && !allowedRequestAttributesPattern.isEmpty()) {
         this.allowedRequestAttributesPattern = Pattern.compile(allowedRequestAttributesPattern);
      } else {
         this.allowedRequestAttributesPattern = null;
      }

   }

   public void parse(ByteBuffer buf, AjpRequestParseState state, HttpServerExchange exchange) throws IOException, BadRequestException {
      if (buf.hasRemaining()) {
         String resultAsQueryString;
         int val;
         StringHolder result;
         label317: {
            label318: {
               IntegerHolder result;
               label319: {
                  label320: {
                     label321: {
                        label306: {
                           label322: {
                              label323: {
                                 switch (state.state) {
                                    case 0:
                                       result = this.parse16BitInteger(buf, state);
                                       if (!result.readComplete) {
                                          return;
                                       }

                                       if (result.value != 4660) {
                                          throw new BadRequestException(UndertowMessages.MESSAGES.wrongMagicNumber(result.value));
                                       }
                                    case 2:
                                       result = this.parse16BitInteger(buf, state);
                                       if (!result.readComplete) {
                                          state.state = 2;
                                          return;
                                       }

                                       state.dataSize = result.value;
                                    case 3:
                                       if (!buf.hasRemaining()) {
                                          state.state = 3;
                                          return;
                                       }

                                       val = buf.get();
                                       state.prefix = (byte)val;
                                       if (val != 2) {
                                          state.state = 15;
                                          return;
                                       }
                                    case 4:
                                       if (!buf.hasRemaining()) {
                                          state.state = 4;
                                          return;
                                       }

                                       val = buf.get();
                                       if (val > 0 && val < 28) {
                                          exchange.setRequestMethod(HTTP_METHODS[val]);
                                       } else if ((val & 255) != 255) {
                                          throw new BadRequestException("Unknown method type " + val);
                                       }
                                    case 5:
                                       result = this.parseString(buf, state, AjpRequestParser.StringType.OTHER);
                                       if (!result.readComplete) {
                                          state.state = 5;
                                          return;
                                       }

                                       exchange.setProtocol(HttpString.tryFromString(result.value));
                                       break;
                                    case 1:
                                    default:
                                       state.state = 15;
                                       return;
                                    case 6:
                                       break;
                                    case 7:
                                       break label323;
                                    case 8:
                                       break label322;
                                    case 9:
                                       break label306;
                                    case 10:
                                       break label321;
                                    case 11:
                                       break label320;
                                    case 12:
                                       break label319;
                                    case 13:
                                       break label318;
                                    case 14:
                                       break label317;
                                 }

                                 result = this.parseString(buf, state, AjpRequestParser.StringType.URL);
                                 if (!result.readComplete) {
                                    state.state = 6;
                                    return;
                                 }

                                 int colon = result.value.indexOf(59);
                                 if (colon == -1) {
                                    resultAsQueryString = this.decode(result.value, result.containsUrlCharacters);
                                    if (result.containsUnencodedCharacters) {
                                       exchange.setRequestURI(resultAsQueryString);
                                    } else {
                                       exchange.setRequestURI(result.value);
                                    }

                                    exchange.setRequestPath(resultAsQueryString);
                                    exchange.setRelativePath(resultAsQueryString);
                                 } else {
                                    StringBuffer resBuffer = new StringBuffer();
                                    int pathParamParsingIndex = 0;

                                    String res;
                                    try {
                                       do {
                                          res = result.value.substring(pathParamParsingIndex, colon);
                                          resBuffer.append(this.decode(res, result.containsUrlCharacters));
                                          pathParamParsingIndex = colon + 1 + URLUtils.parsePathParams(result.value.substring(colon + 1), exchange, this.encoding, this.doDecode && result.containsUrlCharacters, this.maxParameters);
                                          colon = result.value.indexOf(59, pathParamParsingIndex + 1);
                                       } while(pathParamParsingIndex < result.value.length() && colon != -1);
                                    } catch (ParameterLimitException var10) {
                                       UndertowLogger.REQUEST_IO_LOGGER.failedToParseRequest(var10);
                                       state.badRequest = true;
                                    }

                                    if (pathParamParsingIndex < result.value.length()) {
                                       res = result.value.substring(pathParamParsingIndex);
                                       resBuffer.append(this.decode(res, result.containsUrlCharacters));
                                    }

                                    res = resBuffer.toString();
                                    if (result.containsUnencodedCharacters) {
                                       exchange.setRequestURI(res);
                                    } else {
                                       exchange.setRequestURI(result.value);
                                    }

                                    exchange.setRequestPath(res);
                                    exchange.setRelativePath(res);
                                 }
                              }

                              result = this.parseString(buf, state, AjpRequestParser.StringType.OTHER);
                              if (!result.readComplete) {
                                 state.state = 7;
                                 return;
                              }

                              state.remoteAddress = result.value;
                           }

                           result = this.parseString(buf, state, AjpRequestParser.StringType.OTHER);
                           if (!result.readComplete) {
                              state.state = 8;
                              return;
                           }
                        }

                        result = this.parseString(buf, state, AjpRequestParser.StringType.OTHER);
                        if (!result.readComplete) {
                           state.state = 9;
                           return;
                        }

                        state.serverAddress = result.value;
                     }

                     result = this.parse16BitInteger(buf, state);
                     if (!result.readComplete) {
                        state.state = 10;
                        return;
                     }

                     state.serverPort = result.value;
                  }

                  if (!buf.hasRemaining()) {
                     state.state = 11;
                     return;
                  }

                  val = buf.get();
                  if (val != 0) {
                     exchange.setRequestScheme("https");
                  } else {
                     exchange.setRequestScheme("http");
                  }
               }

               result = this.parse16BitInteger(buf, state);
               if (!result.readComplete) {
                  state.state = 12;
                  return;
               }

               state.numHeaders = result.value;
               if (state.numHeaders > this.maxHeaders) {
                  UndertowLogger.REQUEST_IO_LOGGER.failedToParseRequest(new BadRequestException(UndertowMessages.MESSAGES.tooManyHeaders(this.maxHeaders)));
                  state.badRequest = true;
               }
            }

            for(val = state.readHeaders; val < state.numHeaders; ++val) {
               StringHolder result;
               if (state.currentHeader == null) {
                  result = this.parseString(buf, state, AjpRequestParser.StringType.HEADER);
                  if (!result.readComplete) {
                     state.state = 13;
                     state.readHeaders = val;
                     return;
                  }

                  if (result.header != null) {
                     state.currentHeader = result.header;
                  } else {
                     state.currentHeader = HttpString.tryFromString(result.value);
                     Connectors.verifyToken(state.currentHeader);
                  }
               }

               result = this.parseString(buf, state, AjpRequestParser.StringType.OTHER);
               if (!result.readComplete) {
                  state.state = 13;
                  state.readHeaders = val;
                  return;
               }

               if (!state.badRequest) {
                  exchange.getRequestHeaders().add(state.currentHeader, result.value);
               }

               state.currentHeader = null;
            }
         }

         while(true) {
            if (state.currentAttribute == null && state.currentIntegerPart == -1) {
               if (!buf.hasRemaining()) {
                  state.state = 14;
                  return;
               }

               val = 255 & buf.get();
               if (val == 255) {
                  state.state = 15;
                  return;
               }

               if (val == 10) {
                  state.currentIntegerPart = 1;
               } else {
                  if (val == 0 || val >= ATTRIBUTES.length) {
                     continue;
                  }

                  state.currentAttribute = ATTRIBUTES[val];
               }
            }

            if (state.currentIntegerPart == 1) {
               result = this.parseString(buf, state, AjpRequestParser.StringType.OTHER);
               if (!result.readComplete) {
                  state.state = 14;
                  return;
               }

               state.currentAttribute = result.value;
               state.currentIntegerPart = -1;
            }

            boolean decodingAlreadyDone = false;
            String result;
            if (state.currentAttribute.equals("ssl_key_size")) {
               IntegerHolder resultHolder = this.parse16BitInteger(buf, state);
               if (!resultHolder.readComplete) {
                  state.state = 14;
                  return;
               }

               result = Integer.toString(resultHolder.value);
            } else {
               StringHolder resultHolder = this.parseString(buf, state, state.currentAttribute.equals("query_string") ? AjpRequestParser.StringType.QUERY_STRING : AjpRequestParser.StringType.OTHER);
               if (!resultHolder.readComplete) {
                  state.state = 14;
                  return;
               }

               if (resultHolder.containsUnencodedCharacters) {
                  result = this.decode(resultHolder.value, true);
                  decodingAlreadyDone = true;
               } else {
                  result = resultHolder.value;
               }
            }

            if (state.currentAttribute.equals("query_string")) {
               resultAsQueryString = result == null ? "" : result;
               exchange.setQueryString(resultAsQueryString);

               try {
                  URLUtils.parseQueryString(resultAsQueryString, exchange, this.encoding, this.doDecode && !decodingAlreadyDone, this.maxParameters);
               } catch (IllegalArgumentException | ParameterLimitException var9) {
                  UndertowLogger.REQUEST_IO_LOGGER.failedToParseRequest(var9);
                  state.badRequest = true;
               }
            } else if (state.currentAttribute.equals("remote_user")) {
               exchange.putAttachment(ExternalAuthenticationMechanism.EXTERNAL_PRINCIPAL, result);
               exchange.putAttachment(HttpServerExchange.REMOTE_USER, result);
            } else if (state.currentAttribute.equals("auth_type")) {
               exchange.putAttachment(ExternalAuthenticationMechanism.EXTERNAL_AUTHENTICATION_TYPE, result);
            } else if (state.currentAttribute.equals("stored_method")) {
               HttpString requestMethod = new HttpString(result);
               Connectors.verifyToken(requestMethod);
               exchange.setRequestMethod(requestMethod);
            } else if (state.currentAttribute.equals("AJP_REMOTE_PORT")) {
               state.remotePort = Integer.parseInt(result);
            } else if (state.currentAttribute.equals("ssl_session")) {
               state.sslSessionId = result;
            } else if (state.currentAttribute.equals("ssl_cipher")) {
               state.sslCipher = result;
            } else if (state.currentAttribute.equals("ssl_cert")) {
               state.sslCert = result;
            } else if (state.currentAttribute.equals("ssl_key_size")) {
               state.sslKeySize = result;
            } else {
               if (state.attributes == null) {
                  state.attributes = new TreeMap();
               }

               if (ATTR_SET.contains(state.currentAttribute)) {
                  state.attributes.put(state.currentAttribute, result);
               } else if (this.allowedRequestAttributesPattern != null) {
                  Matcher m = this.allowedRequestAttributesPattern.matcher(state.currentAttribute);
                  if (m.matches()) {
                     state.attributes.put(state.currentAttribute, result);
                  }
               }
            }

            state.currentAttribute = null;
         }
      }
   }

   private String decode(String url, boolean containsUrlCharacters) throws UnsupportedEncodingException {
      if (this.doDecode && containsUrlCharacters) {
         try {
            if (this.decodeBuffer == null) {
               this.decodeBuffer = new StringBuilder();
            }

            return URLUtils.decode(url, this.encoding, this.allowEncodedSlash, false, this.decodeBuffer);
         } catch (Exception var4) {
            throw UndertowMessages.MESSAGES.failedToDecodeURL(url, this.encoding, var4);
         }
      } else {
         return url;
      }
   }

   protected HttpString headers(int offset) {
      return HTTP_HEADERS[offset];
   }

   protected IntegerHolder parse16BitInteger(ByteBuffer buf, AjpRequestParseState state) {
      if (!buf.hasRemaining()) {
         return new IntegerHolder(-1, false);
      } else {
         int number = state.currentIntegerPart;
         if (number == -1) {
            number = buf.get() & 255;
         }

         if (buf.hasRemaining()) {
            byte b = buf.get();
            int result = ((255 & number) << 8) + (b & 255);
            state.currentIntegerPart = -1;
            return new IntegerHolder(result, true);
         } else {
            state.currentIntegerPart = number;
            return new IntegerHolder(-1, false);
         }
      }
   }

   protected StringHolder parseString(ByteBuffer buf, AjpRequestParseState state, StringType type) throws UnsupportedEncodingException, BadRequestException {
      boolean containsUrlCharacters = state.containsUrlCharacters;
      boolean containsUnencodedUrlCharacters = state.containsUnencodedUrlCharacters;
      if (!buf.hasRemaining()) {
         return new StringHolder((String)null, false, false, false);
      } else {
         int stringLength = state.stringLength;
         int length;
         byte c;
         if (stringLength == -1) {
            length = buf.get() & 255;
            if (!buf.hasRemaining()) {
               state.stringLength = length | Integer.MIN_VALUE;
               return new StringHolder((String)null, false, false, false);
            }

            c = buf.get();
            stringLength = ((255 & length) << 8) + (c & 255);
         } else if ((stringLength & Integer.MIN_VALUE) != 0) {
            length = stringLength & Integer.MAX_VALUE;
            stringLength = ((255 & length) << 8) + (buf.get() & 255);
         }

         if (type == AjpRequestParser.StringType.HEADER && (stringLength & '\uff00') != 0) {
            state.stringLength = -1;
            return new StringHolder(this.headers(stringLength & 255));
         } else if (stringLength == 65535) {
            state.stringLength = -1;
            return new StringHolder((String)null, true, false, false);
         } else {
            for(length = state.getCurrentStringLength(); length < stringLength; ++length) {
               if (!buf.hasRemaining()) {
                  state.stringLength = stringLength;
                  state.containsUrlCharacters = containsUrlCharacters;
                  state.containsUnencodedUrlCharacters = containsUnencodedUrlCharacters;
                  return new StringHolder((String)null, false, false, false);
               }

               c = buf.get();
               if (type != AjpRequestParser.StringType.QUERY_STRING || c != 43 && c != 37 && c >= 0) {
                  if (type == AjpRequestParser.StringType.URL && (c == 37 || c < 0)) {
                     if (c < 0) {
                        if (!this.allowUnescapedCharactersInUrl) {
                           throw new BadRequestException();
                        }

                        containsUnencodedUrlCharacters = true;
                     }

                     containsUrlCharacters = true;
                  }
               } else {
                  if (c < 0) {
                     if (!this.allowUnescapedCharactersInUrl) {
                        throw new BadRequestException();
                     }

                     containsUnencodedUrlCharacters = true;
                  }

                  containsUrlCharacters = true;
               }

               state.addStringByte(c);
            }

            if (buf.hasRemaining()) {
               buf.get();
               String value = state.getStringAndClear();
               state.stringLength = -1;
               state.containsUrlCharacters = false;
               state.containsUnencodedUrlCharacters = containsUnencodedUrlCharacters;
               return new StringHolder(value, true, containsUrlCharacters, containsUnencodedUrlCharacters);
            } else {
               state.stringLength = stringLength;
               state.containsUrlCharacters = containsUrlCharacters;
               state.containsUnencodedUrlCharacters = containsUnencodedUrlCharacters;
               return new StringHolder((String)null, false, false, false);
            }
         }
      }
   }

   static {
      HTTP_METHODS[1] = Methods.OPTIONS;
      HTTP_METHODS[2] = Methods.GET;
      HTTP_METHODS[3] = Methods.HEAD;
      HTTP_METHODS[4] = Methods.POST;
      HTTP_METHODS[5] = Methods.PUT;
      HTTP_METHODS[6] = Methods.DELETE;
      HTTP_METHODS[7] = Methods.TRACE;
      HTTP_METHODS[8] = Methods.PROPFIND;
      HTTP_METHODS[9] = Methods.PROPPATCH;
      HTTP_METHODS[10] = Methods.MKCOL;
      HTTP_METHODS[11] = Methods.COPY;
      HTTP_METHODS[12] = Methods.MOVE;
      HTTP_METHODS[13] = Methods.LOCK;
      HTTP_METHODS[14] = Methods.UNLOCK;
      HTTP_METHODS[15] = Methods.ACL;
      HTTP_METHODS[16] = Methods.REPORT;
      HTTP_METHODS[17] = Methods.VERSION_CONTROL;
      HTTP_METHODS[18] = Methods.CHECKIN;
      HTTP_METHODS[19] = Methods.CHECKOUT;
      HTTP_METHODS[20] = Methods.UNCHECKOUT;
      HTTP_METHODS[21] = Methods.SEARCH;
      HTTP_METHODS[22] = Methods.MKWORKSPACE;
      HTTP_METHODS[23] = Methods.UPDATE;
      HTTP_METHODS[24] = Methods.LABEL;
      HTTP_METHODS[25] = Methods.MERGE;
      HTTP_METHODS[26] = Methods.BASELINE_CONTROL;
      HTTP_METHODS[27] = Methods.MKACTIVITY;
      HTTP_HEADERS = new HttpString[15];
      HTTP_HEADERS[1] = Headers.ACCEPT;
      HTTP_HEADERS[2] = Headers.ACCEPT_CHARSET;
      HTTP_HEADERS[3] = Headers.ACCEPT_ENCODING;
      HTTP_HEADERS[4] = Headers.ACCEPT_LANGUAGE;
      HTTP_HEADERS[5] = Headers.AUTHORIZATION;
      HTTP_HEADERS[6] = Headers.CONNECTION;
      HTTP_HEADERS[7] = Headers.CONTENT_TYPE;
      HTTP_HEADERS[8] = Headers.CONTENT_LENGTH;
      HTTP_HEADERS[9] = Headers.COOKIE;
      HTTP_HEADERS[10] = Headers.COOKIE2;
      HTTP_HEADERS[11] = Headers.HOST;
      HTTP_HEADERS[12] = Headers.PRAGMA;
      HTTP_HEADERS[13] = Headers.REFERER;
      HTTP_HEADERS[14] = Headers.USER_AGENT;
      ATTRIBUTES = new String[14];
      ATTRIBUTES[1] = "context";
      ATTRIBUTES[2] = "servlet_path";
      ATTRIBUTES[3] = "remote_user";
      ATTRIBUTES[4] = "auth_type";
      ATTRIBUTES[5] = "query_string";
      ATTRIBUTES[6] = "route";
      ATTRIBUTES[7] = "ssl_cert";
      ATTRIBUTES[8] = "ssl_cipher";
      ATTRIBUTES[9] = "ssl_session";
      ATTRIBUTES[10] = "req_attribute";
      ATTRIBUTES[11] = "ssl_key_size";
      ATTRIBUTES[12] = "secret";
      ATTRIBUTES[13] = "stored_method";
      ATTR_SET = new HashSet(Arrays.asList(ATTRIBUTES));
   }

   static enum StringType {
      HEADER,
      URL,
      QUERY_STRING,
      OTHER;
   }

   protected static class StringHolder {
      public final String value;
      public final HttpString header;
      final boolean readComplete;
      final boolean containsUrlCharacters;
      final boolean containsUnencodedCharacters;

      private StringHolder(String value, boolean readComplete, boolean containsUrlCharacters, boolean containsUnencodedCharacters) {
         this.value = value;
         this.readComplete = readComplete;
         this.containsUrlCharacters = containsUrlCharacters;
         this.containsUnencodedCharacters = containsUnencodedCharacters;
         this.header = null;
      }

      private StringHolder(HttpString value) {
         this.value = null;
         this.readComplete = true;
         this.header = value;
         this.containsUrlCharacters = false;
         this.containsUnencodedCharacters = false;
      }

      // $FF: synthetic method
      StringHolder(String x0, boolean x1, boolean x2, boolean x3, Object x4) {
         this(x0, x1, x2, x3);
      }

      // $FF: synthetic method
      StringHolder(HttpString x0, Object x1) {
         this(x0);
      }
   }

   protected static class IntegerHolder {
      public final int value;
      public final boolean readComplete;

      private IntegerHolder(int value, boolean readComplete) {
         this.value = value;
         this.readComplete = readComplete;
      }

      // $FF: synthetic method
      IntegerHolder(int x0, boolean x1, Object x2) {
         this(x0, x1);
      }
   }
}
