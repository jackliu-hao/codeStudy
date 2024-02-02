package io.undertow.server.handlers.accesslog;

import io.undertow.UndertowLogger;
import io.undertow.Version;
import io.undertow.attribute.AuthenticationTypeExchangeAttribute;
import io.undertow.attribute.BytesSentAttribute;
import io.undertow.attribute.CompositeExchangeAttribute;
import io.undertow.attribute.ConstantExchangeAttribute;
import io.undertow.attribute.CookieAttribute;
import io.undertow.attribute.DateTimeAttribute;
import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeParser;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.attribute.LocalIPAttribute;
import io.undertow.attribute.QueryStringAttribute;
import io.undertow.attribute.QuotingExchangeAttribute;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.attribute.RemoteIPAttribute;
import io.undertow.attribute.RemoteUserAttribute;
import io.undertow.attribute.RequestHeaderAttribute;
import io.undertow.attribute.RequestMethodAttribute;
import io.undertow.attribute.RequestProtocolAttribute;
import io.undertow.attribute.RequestSchemeAttribute;
import io.undertow.attribute.RequestURLAttribute;
import io.undertow.attribute.ResponseCodeAttribute;
import io.undertow.attribute.ResponseHeaderAttribute;
import io.undertow.attribute.ResponseTimeAttribute;
import io.undertow.attribute.SecureExchangeAttribute;
import io.undertow.attribute.SubstituteEmptyWrapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExtendedAccessLogParser {
   private final ExchangeAttributeParser parser;

   public ExtendedAccessLogParser(ClassLoader classLoader) {
      this.parser = ExchangeAttributes.parser(classLoader, QuotingExchangeAttribute.WRAPPER);
   }

   public ExchangeAttribute parse(String pattern) {
      List<ExchangeAttribute> list = new ArrayList();
      PatternTokenizer tokenizer = new PatternTokenizer(pattern);

      try {
         tokenizer.getWhiteSpaces();
         if (tokenizer.isEnded()) {
            UndertowLogger.ROOT_LOGGER.extendedAccessLogEmptyPattern();
            return null;
         } else {
            for(String token = tokenizer.getToken(); token != null; token = tokenizer.getToken()) {
               if (UndertowLogger.ROOT_LOGGER.isDebugEnabled()) {
                  UndertowLogger.ROOT_LOGGER.debug("token = " + token);
               }

               ExchangeAttribute element = this.getLogElement(token, tokenizer);
               if (element == null) {
                  break;
               }

               list.add(element);
               String whiteSpaces = tokenizer.getWhiteSpaces();
               if (whiteSpaces.length() > 0) {
                  list.add(new ConstantExchangeAttribute(whiteSpaces));
               }

               if (tokenizer.isEnded()) {
                  break;
               }
            }

            if (UndertowLogger.ROOT_LOGGER.isDebugEnabled()) {
               UndertowLogger.ROOT_LOGGER.debug("finished decoding with element size of: " + list.size());
            }

            return new CompositeExchangeAttribute((ExchangeAttribute[])list.toArray(new ExchangeAttribute[list.size()]));
         }
      } catch (IOException var7) {
         UndertowLogger.ROOT_LOGGER.extendedAccessLogPatternParseError(var7);
         return null;
      }
   }

   protected ExchangeAttribute getLogElement(String token, PatternTokenizer tokenizer) throws IOException {
      if ("date".equals(token)) {
         return new DateTimeAttribute("yyyy-MM-dd", "GMT");
      } else {
         String nextToken;
         if ("time".equals(token)) {
            if (!tokenizer.hasSubToken()) {
               return new DateTimeAttribute("HH:mm:ss", "GMT");
            }

            nextToken = tokenizer.getToken();
            if ("taken".equals(nextToken)) {
               return new SubstituteEmptyWrapper.SubstituteEmptyAttribute(new ResponseTimeAttribute(TimeUnit.SECONDS), "-");
            }
         } else {
            if ("bytes".equals(token)) {
               return new BytesSentAttribute(true);
            }

            if ("cached".equals(token)) {
               return new ConstantExchangeAttribute("-");
            }

            if ("c".equals(token)) {
               nextToken = tokenizer.getToken();
               if ("ip".equals(nextToken)) {
                  return RemoteIPAttribute.INSTANCE;
               }

               if ("dns".equals(nextToken)) {
                  return new ExchangeAttribute() {
                     public String readAttribute(HttpServerExchange exchange) {
                        InetSocketAddress peerAddress = (InetSocketAddress)exchange.getConnection().getPeerAddress(InetSocketAddress.class);

                        try {
                           return peerAddress.getHostName();
                        } catch (Throwable var4) {
                           return peerAddress.getHostString();
                        }
                     }

                     public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
                        throw new ReadOnlyAttributeException();
                     }
                  };
               }
            } else if ("s".equals(token)) {
               nextToken = tokenizer.getToken();
               if ("ip".equals(nextToken)) {
                  return LocalIPAttribute.INSTANCE;
               }

               if ("dns".equals(nextToken)) {
                  return new ExchangeAttribute() {
                     public String readAttribute(HttpServerExchange exchange) {
                        try {
                           return ((InetSocketAddress)exchange.getConnection().getLocalAddress(InetSocketAddress.class)).getHostName();
                        } catch (Throwable var3) {
                           return "localhost";
                        }
                     }

                     public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
                        throw new ReadOnlyAttributeException();
                     }
                  };
               }
            } else {
               if ("cs".equals(token)) {
                  return this.getClientToServerElement(tokenizer);
               }

               if ("sc".equals(token)) {
                  return this.getServerToClientElement(tokenizer);
               }

               if ("sr".equals(token) || "rs".equals(token)) {
                  return this.getProxyElement(tokenizer);
               }

               if ("x".equals(token)) {
                  return this.getXParameterElement(tokenizer);
               }
            }
         }

         UndertowLogger.ROOT_LOGGER.extendedAccessLogUnknownToken(token);
         return null;
      }
   }

   protected ExchangeAttribute getClientToServerElement(PatternTokenizer tokenizer) throws IOException {
      String token;
      if (tokenizer.hasSubToken()) {
         token = tokenizer.getToken();
         if ("method".equals(token)) {
            return RequestMethodAttribute.INSTANCE;
         }

         if ("uri".equals(token)) {
            if (!tokenizer.hasSubToken()) {
               return new ExchangeAttribute() {
                  public String readAttribute(HttpServerExchange exchange) {
                     String query = exchange.getQueryString();
                     if (query.isEmpty()) {
                        return exchange.getRequestURI();
                     } else {
                        StringBuilder buf = new StringBuilder();
                        buf.append(exchange.getRequestURI());
                        buf.append('?');
                        buf.append(exchange.getQueryString());
                        return buf.toString();
                     }
                  }

                  public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
                     throw new ReadOnlyAttributeException();
                  }
               };
            }

            token = tokenizer.getToken();
            if ("stem".equals(token)) {
               return RequestURLAttribute.INSTANCE;
            }

            if ("query".equals(token)) {
               return new SubstituteEmptyWrapper.SubstituteEmptyAttribute(QueryStringAttribute.BARE_INSTANCE, "-");
            }
         }
      } else if (tokenizer.hasParameter()) {
         token = tokenizer.getParameter();
         if (token == null) {
            UndertowLogger.ROOT_LOGGER.extendedAccessLogMissingClosing();
            return null;
         }

         return new QuotingExchangeAttribute(new RequestHeaderAttribute(new HttpString(token)));
      }

      UndertowLogger.ROOT_LOGGER.extendedAccessLogCannotDecode(tokenizer.getRemains());
      return null;
   }

   protected ExchangeAttribute getServerToClientElement(PatternTokenizer tokenizer) throws IOException {
      String parameter;
      if (tokenizer.hasSubToken()) {
         parameter = tokenizer.getToken();
         if ("status".equals(parameter)) {
            return ResponseCodeAttribute.INSTANCE;
         }

         if ("comment".equals(parameter)) {
            return new ConstantExchangeAttribute("?");
         }
      } else if (tokenizer.hasParameter()) {
         parameter = tokenizer.getParameter();
         if (parameter == null) {
            UndertowLogger.ROOT_LOGGER.extendedAccessLogMissingClosing();
            return null;
         }

         return new QuotingExchangeAttribute(new ResponseHeaderAttribute(new HttpString(parameter)));
      }

      UndertowLogger.ROOT_LOGGER.extendedAccessLogCannotDecode(tokenizer.getRemains());
      return null;
   }

   protected ExchangeAttribute getProxyElement(PatternTokenizer tokenizer) throws IOException {
      String token = null;
      if (tokenizer.hasSubToken()) {
         tokenizer.getToken();
         return new ConstantExchangeAttribute("-");
      } else if (tokenizer.hasParameter()) {
         tokenizer.getParameter();
         return new ConstantExchangeAttribute("-");
      } else {
         UndertowLogger.ROOT_LOGGER.extendedAccessLogCannotDecode((String)token);
         return null;
      }
   }

   protected ExchangeAttribute getXParameterElement(PatternTokenizer tokenizer) throws IOException {
      if (!tokenizer.hasSubToken()) {
         UndertowLogger.ROOT_LOGGER.extendedAccessLogBadXParam();
         return null;
      } else {
         String token = tokenizer.getToken();
         if (!tokenizer.hasParameter()) {
            UndertowLogger.ROOT_LOGGER.extendedAccessLogBadXParam();
            return null;
         } else {
            final String parameter = tokenizer.getParameter();
            if (parameter == null) {
               UndertowLogger.ROOT_LOGGER.extendedAccessLogMissingClosing();
               return null;
            } else if ("A".equals(token)) {
               return new SubstituteEmptyWrapper.SubstituteEmptyAttribute(this.parser.parse("%{sc," + parameter + "}"), "-");
            } else if ("C".equals(token)) {
               return new SubstituteEmptyWrapper.SubstituteEmptyAttribute(new CookieAttribute(parameter), "-");
            } else if ("R".equals(token)) {
               return this.parser.parse("%{r," + parameter + "}");
            } else if ("S".equals(token)) {
               return new SubstituteEmptyWrapper.SubstituteEmptyAttribute(this.parser.parse("%{s," + parameter + "}"), "-");
            } else if ("H".equals(token)) {
               return this.getServletRequestElement(parameter);
            } else if ("P".equals(token)) {
               return new SubstituteEmptyWrapper.SubstituteEmptyAttribute(this.parser.parse("%{rp," + parameter + "}"), "-");
            } else if ("O".equals(token)) {
               return new QuotingExchangeAttribute(new ExchangeAttribute() {
                  public String readAttribute(HttpServerExchange exchange) {
                     HeaderValues values = exchange.getResponseHeaders().get(parameter);
                     if (values != null && values.size() > 0) {
                        StringBuilder buffer = new StringBuilder();

                        for(int i = 0; i < values.size(); ++i) {
                           String string = values.get(i);
                           buffer.append(string);
                           if (i + 1 < values.size()) {
                              buffer.append(",");
                           }
                        }

                        return buffer.toString();
                     } else {
                        return null;
                     }
                  }

                  public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
                     throw new ReadOnlyAttributeException();
                  }
               });
            } else {
               UndertowLogger.ROOT_LOGGER.extendedAccessLogCannotDecodeXParamValue(token);
               return null;
            }
         }
      }
   }

   protected ExchangeAttribute getServletRequestElement(String parameter) {
      if ("authType".equals(parameter)) {
         return new SubstituteEmptyWrapper.SubstituteEmptyAttribute(AuthenticationTypeExchangeAttribute.INSTANCE, "-");
      } else if ("remoteUser".equals(parameter)) {
         return new SubstituteEmptyWrapper.SubstituteEmptyAttribute(RemoteUserAttribute.INSTANCE, "-");
      } else if ("requestedSessionId".equals(parameter)) {
         return this.parser.parse("%{REQUESTED_SESSION_ID}");
      } else if ("requestedSessionIdFromCookie".equals(parameter)) {
         return this.parser.parse("%{REQUESTED_SESSION_ID_FROM_COOKIE}");
      } else if ("requestedSessionIdValid".equals(parameter)) {
         return this.parser.parse("%{REQUESTED_SESSION_ID_VALID}");
      } else if ("contentLength".equals(parameter)) {
         return new QuotingExchangeAttribute(new RequestHeaderAttribute(Headers.CONTENT_LENGTH));
      } else if ("characterEncoding".equals(parameter)) {
         return this.parser.parse("%{REQUEST_CHARACTER_ENCODING}");
      } else if ("locale".equals(parameter)) {
         return this.parser.parse("%{REQUEST_LOCALE}");
      } else if ("protocol".equals(parameter)) {
         return RequestProtocolAttribute.INSTANCE;
      } else if ("scheme".equals(parameter)) {
         return RequestSchemeAttribute.INSTANCE;
      } else if ("secure".equals(parameter)) {
         return SecureExchangeAttribute.INSTANCE;
      } else {
         UndertowLogger.ROOT_LOGGER.extendedAccessLogCannotDecodeXParamValue(parameter);
         return null;
      }
   }

   public static class ExtendedAccessLogHeaderGenerator implements LogFileHeaderGenerator {
      private final String pattern;

      public ExtendedAccessLogHeaderGenerator(String pattern) {
         this.pattern = pattern;
      }

      public String generateHeader() {
         StringBuilder sb = new StringBuilder();
         sb.append("#Fields: ");
         sb.append(this.pattern);
         sb.append(System.lineSeparator());
         sb.append("#Version: 2.0");
         sb.append(System.lineSeparator());
         sb.append("#Software: ");
         sb.append(Version.getFullVersionString());
         sb.append(System.lineSeparator());
         return sb.toString();
      }
   }

   private static class PatternTokenizer {
      private StringReader sr = null;
      private StringBuilder buf = new StringBuilder();
      private boolean ended = false;
      private boolean subToken;
      private boolean parameter;

      PatternTokenizer(String str) {
         this.sr = new StringReader(str);
      }

      public boolean hasSubToken() {
         return this.subToken;
      }

      public boolean hasParameter() {
         return this.parameter;
      }

      public String getToken() throws IOException {
         if (this.ended) {
            return null;
         } else {
            String result = null;
            this.subToken = false;
            this.parameter = false;

            for(int c = this.sr.read(); c != -1; c = this.sr.read()) {
               switch (c) {
                  case 32:
                     result = this.buf.toString();
                     this.buf = new StringBuilder();
                     this.buf.append((char)c);
                     return result;
                  case 40:
                     result = this.buf.toString();
                     this.buf = new StringBuilder();
                     this.parameter = true;
                     return result;
                  case 41:
                     result = this.buf.toString();
                     this.buf = new StringBuilder();
                     break;
                  case 45:
                     result = this.buf.toString();
                     this.buf = new StringBuilder();
                     this.subToken = true;
                     return result;
                  default:
                     this.buf.append((char)c);
               }
            }

            this.ended = true;
            if (this.buf.length() != 0) {
               return this.buf.toString();
            } else {
               return null;
            }
         }
      }

      public String getParameter() throws IOException {
         if (!this.parameter) {
            return null;
         } else {
            this.parameter = false;

            for(int c = this.sr.read(); c != -1; c = this.sr.read()) {
               if (c == 41) {
                  String result = this.buf.toString();
                  this.buf = new StringBuilder();
                  return result;
               }

               this.buf.append((char)c);
            }

            return null;
         }
      }

      public String getWhiteSpaces() throws IOException {
         if (this.isEnded()) {
            return "";
         } else {
            StringBuilder whiteSpaces = new StringBuilder();
            if (this.buf.length() > 0) {
               whiteSpaces.append(this.buf);
               this.buf = new StringBuilder();
            }

            int c;
            for(c = this.sr.read(); Character.isWhitespace((char)c); c = this.sr.read()) {
               whiteSpaces.append((char)c);
            }

            if (c == -1) {
               this.ended = true;
            } else {
               this.buf.append((char)c);
            }

            return whiteSpaces.toString();
         }
      }

      public boolean isEnded() {
         return this.ended;
      }

      public String getRemains() throws IOException {
         StringBuilder remains = new StringBuilder();

         for(int c = this.sr.read(); c != -1; c = this.sr.read()) {
            remains.append((char)c);
         }

         return remains.toString();
      }
   }
}
