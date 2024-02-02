package io.undertow.server.protocol.http;

import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.BadRequestException;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.Protocols;
import io.undertow.util.URLUtils;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.xnio.OptionMap;

public abstract class HttpRequestParser {
   private static final byte[] HTTP;
   public static final int HTTP_LENGTH;
   private final int maxParameters;
   private final int maxHeaders;
   private final boolean allowEncodedSlash;
   private final boolean decode;
   private final String charset;
   private final int maxCachedHeaderSize;
   private final boolean allowUnescapedCharactersInUrl;
   private static final boolean[] ALLOWED_TARGET_CHARACTER = new boolean[256];
   private static final int START = 0;
   private static final int FIRST_COLON = 1;
   private static final int FIRST_SLASH = 2;
   private static final int SECOND_SLASH = 3;
   private static final int IN_PATH = 4;
   private static final int HOST_DONE = 5;
   private static final int NORMAL = 0;
   private static final int WHITESPACE = 1;
   private static final int BEGIN_LINE_END = 2;
   private static final int LINE_END = 3;
   private static final int AWAIT_DATA_END = 4;

   public static boolean isTargetCharacterAllowed(char c) {
      return ALLOWED_TARGET_CHARACTER[c];
   }

   public HttpRequestParser(OptionMap options) {
      this.maxParameters = options.get(UndertowOptions.MAX_PARAMETERS, 1000);
      this.maxHeaders = options.get(UndertowOptions.MAX_HEADERS, 200);
      this.allowEncodedSlash = options.get(UndertowOptions.ALLOW_ENCODED_SLASH, false);
      this.decode = options.get(UndertowOptions.DECODE_URL, true);
      this.charset = (String)options.get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name());
      this.maxCachedHeaderSize = options.get(UndertowOptions.MAX_CACHED_HEADER_SIZE, 150);
      this.allowUnescapedCharactersInUrl = options.get(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, false);
   }

   public static final HttpRequestParser instance(OptionMap options) {
      try {
         Class<?> cls = Class.forName(HttpRequestParser.class.getName() + "$$generated", false, HttpRequestParser.class.getClassLoader());
         Constructor<?> ctor = cls.getConstructor(OptionMap.class);
         return (HttpRequestParser)ctor.newInstance(options);
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   public void handle(ByteBuffer buffer, ParseState currentState, HttpServerExchange builder) throws BadRequestException {
      if (currentState.state != 0) {
         this.handleStateful(buffer, currentState, builder);
      } else {
         int position = buffer.position();
         if (buffer.remaining() > 3 && buffer.get(position) == 71 && buffer.get(position + 1) == 69 && buffer.get(position + 2) == 84 && buffer.get(position + 3) == 32) {
            buffer.position(position + 4);
            builder.setRequestMethod(Methods.GET);
            currentState.state = 1;
         } else {
            try {
               this.handleHttpVerb(buffer, currentState, builder);
            } catch (IllegalArgumentException var10) {
               throw new BadRequestException(var10);
            }
         }

         this.handlePath(buffer, currentState, builder);
         boolean failed = false;
         if (buffer.remaining() > HTTP_LENGTH + 3) {
            int pos = buffer.position();

            for(int i = 0; i < HTTP_LENGTH; ++i) {
               if (HTTP[i] != buffer.get(pos + i)) {
                  failed = true;
                  break;
               }
            }

            if (!failed) {
               byte b = buffer.get(pos + HTTP_LENGTH);
               byte b2 = buffer.get(pos + HTTP_LENGTH + 1);
               byte b3 = buffer.get(pos + HTTP_LENGTH + 2);
               if (b2 == 13 && b3 == 10) {
                  if (b == 49) {
                     builder.setProtocol(Protocols.HTTP_1_1);
                     buffer.position(pos + HTTP_LENGTH + 3);
                     currentState.state = 6;
                  } else if (b == 48) {
                     builder.setProtocol(Protocols.HTTP_1_0);
                     buffer.position(pos + HTTP_LENGTH + 3);
                     currentState.state = 6;
                  } else {
                     failed = true;
                  }
               } else {
                  failed = true;
               }
            }
         } else {
            failed = true;
         }

         if (failed) {
            this.handleHttpVersion(buffer, currentState, builder);
            this.handleAfterVersion(buffer, currentState);
         }

         while(currentState.state != 8 && buffer.hasRemaining()) {
            this.handleHeader(buffer, currentState, builder);
            if (currentState.state == 7) {
               this.handleHeaderValue(buffer, currentState, builder);
            }
         }

      }
   }

   private void handleStateful(ByteBuffer buffer, ParseState currentState, HttpServerExchange builder) throws BadRequestException {
      if (currentState.state == 1) {
         this.handlePath(buffer, currentState, builder);
         if (!buffer.hasRemaining()) {
            return;
         }
      }

      if (currentState.state == 3) {
         this.handleQueryParameters(buffer, currentState, builder);
         if (!buffer.hasRemaining()) {
            return;
         }
      }

      if (currentState.state == 2) {
         this.handlePathParameters(buffer, currentState, builder);
         if (!buffer.hasRemaining()) {
            return;
         }

         if (currentState.state == 1) {
            this.handlePath(buffer, currentState, builder);
            if (!buffer.hasRemaining()) {
               return;
            }
         }
      }

      if (currentState.state == 4) {
         this.handleHttpVersion(buffer, currentState, builder);
         if (!buffer.hasRemaining()) {
            return;
         }
      }

      if (currentState.state == 5) {
         this.handleAfterVersion(buffer, currentState);
         if (!buffer.hasRemaining()) {
            return;
         }
      }

      while(currentState.state != 8) {
         if (currentState.state == 6) {
            this.handleHeader(buffer, currentState, builder);
            if (!buffer.hasRemaining()) {
               return;
            }
         }

         if (currentState.state == 7) {
            this.handleHeaderValue(buffer, currentState, builder);
            if (!buffer.hasRemaining()) {
               return;
            }
         }
      }

   }

   abstract void handleHttpVerb(ByteBuffer var1, ParseState var2, HttpServerExchange var3) throws BadRequestException;

   abstract void handleHttpVersion(ByteBuffer var1, ParseState var2, HttpServerExchange var3) throws BadRequestException;

   abstract void handleHeader(ByteBuffer var1, ParseState var2, HttpServerExchange var3) throws BadRequestException;

   final void handlePath(ByteBuffer buffer, ParseState state, HttpServerExchange exchange) throws BadRequestException {
      StringBuilder stringBuilder = state.stringBuilder;
      int parseState = state.parseState;
      int canonicalPathStart = state.pos;
      boolean urlDecodeRequired = state.urlDecodeRequired;

      while(true) {
         while(buffer.hasRemaining()) {
            char next = (char)(buffer.get() & 255);
            if (!this.allowUnescapedCharactersInUrl && !ALLOWED_TARGET_CHARACTER[next]) {
               throw new BadRequestException(UndertowMessages.MESSAGES.invalidCharacterInRequestTarget(next));
            }

            if (next != ' ' && next != '\t') {
               if (next == '\r' || next == '\n') {
                  throw UndertowMessages.MESSAGES.failedToParsePath();
               }

               if (next == '?' && (parseState == 0 || parseState == 5 || parseState == 4)) {
                  this.beginQueryParameters(buffer, state, exchange, stringBuilder, parseState, canonicalPathStart, urlDecodeRequired);
                  return;
               }

               if (next == ';') {
                  state.parseState = parseState;
                  state.urlDecodeRequired = urlDecodeRequired;
                  state.pos = canonicalPathStart;
                  state.canonicalPath.append(stringBuilder.substring(canonicalPathStart));
                  this.handlePathParameters(buffer, state, exchange);
                  if (state.state != 1) {
                     return;
                  }

                  canonicalPathStart = stringBuilder.length();
                  stringBuilder.append('/');
               } else {
                  if (this.decode && (next == '%' || next > 127)) {
                     urlDecodeRequired = true;
                  } else if (next == ':' && parseState == 0) {
                     parseState = 1;
                  } else if (next == '/' && parseState == 1) {
                     parseState = 2;
                  } else if (next == '/' && parseState == 2) {
                     parseState = 3;
                  } else if (next == '/' && parseState == 3) {
                     parseState = 5;
                     canonicalPathStart = stringBuilder.length();
                  } else if (parseState != 1 && parseState != 2) {
                     if (next == '/' && parseState != 5) {
                        parseState = 4;
                     }
                  } else {
                     parseState = 4;
                  }

                  stringBuilder.append(next);
               }
            } else if (stringBuilder.length() != 0) {
               String path = stringBuilder.toString();
               this.parsePathComplete(state, exchange, canonicalPathStart, parseState, urlDecodeRequired, path);
               exchange.setQueryString("");
               state.state = 4;
               return;
            }
         }

         state.parseState = parseState;
         state.pos = canonicalPathStart;
         state.urlDecodeRequired = urlDecodeRequired;
         return;
      }
   }

   private void parsePathComplete(ParseState state, HttpServerExchange exchange, int canonicalPathStart, int parseState, boolean urlDecodeRequired, String path) {
      if (parseState == 3) {
         exchange.setRequestPath("/");
         exchange.setRelativePath("/");
         exchange.setRequestURI(path, true);
      } else if (parseState < 5 && state.canonicalPath.length() == 0) {
         String decodedPath = this.decode(path, urlDecodeRequired, state, this.allowEncodedSlash, false);
         exchange.setRequestPath(decodedPath);
         exchange.setRelativePath(decodedPath);
         exchange.setRequestURI(path, false);
      } else {
         this.handleFullUrl(state, exchange, canonicalPathStart, urlDecodeRequired, path, parseState);
      }

      state.stringBuilder.setLength(0);
      state.canonicalPath.setLength(0);
      state.parseState = 0;
      state.pos = 0;
      state.urlDecodeRequired = false;
   }

   private void beginQueryParameters(ByteBuffer buffer, ParseState state, HttpServerExchange exchange, StringBuilder stringBuilder, int parseState, int canonicalPathStart, boolean urlDecodeRequired) throws BadRequestException {
      String path = stringBuilder.toString();
      this.parsePathComplete(state, exchange, canonicalPathStart, parseState, urlDecodeRequired, path);
      state.state = 3;
      this.handleQueryParameters(buffer, state, exchange);
   }

   private void handleFullUrl(ParseState state, HttpServerExchange exchange, int canonicalPathStart, boolean urlDecodeRequired, String path, int parseState) {
      state.canonicalPath.append(path.substring(canonicalPathStart));
      String thePath = this.decode(state.canonicalPath.toString(), urlDecodeRequired, state, this.allowEncodedSlash, false);
      exchange.setRequestPath(thePath);
      exchange.setRelativePath(thePath);
      exchange.setRequestURI(path, parseState == 5);
   }

   final void handleQueryParameters(ByteBuffer buffer, ParseState state, HttpServerExchange exchange) throws BadRequestException {
      StringBuilder stringBuilder = state.stringBuilder;
      int queryParamPos = state.pos;
      int mapCount = state.mapCount;
      boolean urlDecodeRequired = state.urlDecodeRequired;
      String nextQueryParam = state.nextQueryParam;

      while(buffer.hasRemaining()) {
         char next = (char)(buffer.get() & 255);
         if (!this.allowUnescapedCharactersInUrl && !ALLOWED_TARGET_CHARACTER[next]) {
            throw new BadRequestException(UndertowMessages.MESSAGES.invalidCharacterInRequestTarget(next));
         }

         if (next != ' ' && next != '\t') {
            if (next != '\r' && next != '\n') {
               if (!this.decode || next != '+' && next != '%' && next <= 127) {
                  if (next == '=' && nextQueryParam == null) {
                     nextQueryParam = this.decode(stringBuilder.substring(queryParamPos), urlDecodeRequired, state, true, true);
                     urlDecodeRequired = false;
                     queryParamPos = stringBuilder.length() + 1;
                  } else if (next == '&' && nextQueryParam == null) {
                     ++mapCount;
                     if (mapCount >= this.maxParameters) {
                        throw UndertowMessages.MESSAGES.tooManyQueryParameters(this.maxParameters);
                     }

                     if (queryParamPos != stringBuilder.length()) {
                        exchange.addQueryParam(this.decode(stringBuilder.substring(queryParamPos), urlDecodeRequired, state, true, true), "");
                     }

                     urlDecodeRequired = false;
                     queryParamPos = stringBuilder.length() + 1;
                  } else if (next == '&') {
                     ++mapCount;
                     if (mapCount >= this.maxParameters) {
                        throw UndertowMessages.MESSAGES.tooManyQueryParameters(this.maxParameters);
                     }

                     exchange.addQueryParam(nextQueryParam, this.decode(stringBuilder.substring(queryParamPos), urlDecodeRequired, state, true, true));
                     urlDecodeRequired = false;
                     queryParamPos = stringBuilder.length() + 1;
                     nextQueryParam = null;
                  }
               } else {
                  urlDecodeRequired = true;
               }

               stringBuilder.append(next);
               continue;
            }

            throw UndertowMessages.MESSAGES.failedToParsePath();
         }

         String queryString = stringBuilder.toString();
         exchange.setQueryString(queryString);
         if (nextQueryParam == null) {
            if (queryParamPos != stringBuilder.length()) {
               exchange.addQueryParam(this.decode(stringBuilder.substring(queryParamPos), urlDecodeRequired, state, true, true), "");
            }
         } else {
            exchange.addQueryParam(nextQueryParam, this.decode(stringBuilder.substring(queryParamPos), urlDecodeRequired, state, true, true));
         }

         state.state = 4;
         state.stringBuilder.setLength(0);
         state.pos = 0;
         state.nextQueryParam = null;
         state.urlDecodeRequired = false;
         state.mapCount = 0;
         return;
      }

      state.pos = queryParamPos;
      state.nextQueryParam = nextQueryParam;
      state.urlDecodeRequired = urlDecodeRequired;
      state.mapCount = mapCount;
   }

   private String decode(String value, boolean urlDecodeRequired, ParseState state, boolean allowEncodedSlash, boolean formEncoded) {
      return urlDecodeRequired ? URLUtils.decode(value, this.charset, allowEncodedSlash, formEncoded, state.decodeBuffer) : value;
   }

   final void handlePathParameters(ByteBuffer buffer, ParseState state, HttpServerExchange exchange) throws BadRequestException {
      state.state = 2;
      boolean urlDecodeRequired = state.urlDecodeRequired;
      String param = state.nextQueryParam;
      StringBuilder stringBuilder = state.stringBuilder;
      stringBuilder.append(";");
      int pos = stringBuilder.length();

      while(buffer.hasRemaining()) {
         char next = (char)(buffer.get() & 255);
         if (!this.allowUnescapedCharactersInUrl && !ALLOWED_TARGET_CHARACTER[next]) {
            throw new BadRequestException(UndertowMessages.MESSAGES.invalidCharacterInRequestTarget(next));
         }

         if (next != ' ' && next != '\t' && next != '?') {
            if (next != '\r' && next != '\n') {
               if (next == '/') {
                  this.handleParsedParam(param, stringBuilder.substring(pos), exchange, urlDecodeRequired, state);
                  state.pos = stringBuilder.length();
                  state.state = 1;
                  state.nextQueryParam = null;
                  return;
               }

               if (this.decode && (next == '+' || next == '%' || next > 127)) {
                  urlDecodeRequired = true;
               }

               if (next == '=' && param == null) {
                  param = this.decode(stringBuilder.substring(pos), urlDecodeRequired, state, true, true);
                  urlDecodeRequired = false;
                  pos = stringBuilder.length() + 1;
               } else if (next == ';') {
                  this.handleParsedParam(param, stringBuilder.substring(pos), exchange, urlDecodeRequired, state);
                  param = null;
                  pos = stringBuilder.length() + 1;
               } else if (next == ',') {
                  if (param == null) {
                     throw UndertowMessages.MESSAGES.failedToParsePath();
                  }

                  this.handleParsedParam(param, stringBuilder.substring(pos), exchange, urlDecodeRequired, state);
                  pos = stringBuilder.length() + 1;
               }

               stringBuilder.append(next);
               continue;
            }

            throw UndertowMessages.MESSAGES.failedToParsePath();
         }

         this.handleParsedParam(param, stringBuilder.substring(pos), exchange, urlDecodeRequired, state);
         String path = stringBuilder.toString();
         this.parsePathComplete(state, exchange, path.length(), state.parseState, urlDecodeRequired, path);
         state.state = 4;
         state.nextQueryParam = null;
         if (next == '?') {
            state.state = 3;
            this.handleQueryParameters(buffer, state, exchange);
         } else {
            exchange.setQueryString("");
         }

         return;
      }

      state.urlDecodeRequired = urlDecodeRequired;
      state.pos = pos;
      state.urlDecodeRequired = urlDecodeRequired;
      state.nextQueryParam = param;
   }

   private void handleParsedParam(String previouslyParsedParam, String parsedParam, HttpServerExchange exchange, boolean urlDecodeRequired, ParseState state) throws BadRequestException {
      if (previouslyParsedParam == null) {
         exchange.addPathParam(this.decode(parsedParam, urlDecodeRequired, state, true, true), "");
      } else {
         exchange.addPathParam(previouslyParsedParam, this.decode(parsedParam, urlDecodeRequired, state, true, true));
      }

   }

   final void handleHeaderValue(ByteBuffer buffer, ParseState state, HttpServerExchange builder) throws BadRequestException {
      HttpString headerName = state.nextHeader;
      StringBuilder stringBuilder = state.stringBuilder;
      CacheMap<HttpString, String> headerValuesCache = state.headerValuesCache;
      if (headerName != null && stringBuilder.length() == 0 && headerValuesCache != null) {
         String existing = (String)headerValuesCache.get(headerName);
         if (existing != null && this.handleCachedHeader(existing, buffer, state, builder)) {
            return;
         }
      }

      this.handleHeaderValueCacheMiss(buffer, state, builder, headerName, headerValuesCache, stringBuilder);
   }

   private void handleHeaderValueCacheMiss(ByteBuffer buffer, ParseState state, HttpServerExchange builder, HttpString headerName, CacheMap<HttpString, String> headerValuesCache, StringBuilder stringBuilder) throws BadRequestException {
      int parseState = state.parseState;

      byte next;
      while(buffer.hasRemaining() && parseState == 0) {
         next = buffer.get();
         if (next == 13) {
            parseState = 2;
         } else if (next == 10) {
            parseState = 3;
         } else if (next != 32 && next != 9) {
            stringBuilder.append((char)(next & 255));
         } else {
            parseState = 1;
         }
      }

      while(buffer.hasRemaining()) {
         next = buffer.get();
         switch (parseState) {
            case 0:
               if (next == 13) {
                  parseState = 2;
               } else if (next == 10) {
                  parseState = 3;
               } else {
                  if (next != 32 && next != 9) {
                     stringBuilder.append((char)(next & 255));
                     continue;
                  }

                  parseState = 1;
               }
               break;
            case 1:
               if (next == 13) {
                  parseState = 2;
               } else if (next == 10) {
                  parseState = 3;
               } else if (next != 32 && next != 9) {
                  if (stringBuilder.length() > 0) {
                     stringBuilder.append(' ');
                  }

                  stringBuilder.append((char)(next & 255));
                  parseState = 0;
               }
               break;
            case 2:
            case 3:
               if (next == 10 && parseState == 2) {
                  parseState = 3;
               } else {
                  if (next != 9 && next != 32) {
                     String headerValue = stringBuilder.toString();
                     if (++state.mapCount > this.maxHeaders) {
                        throw new BadRequestException(UndertowMessages.MESSAGES.tooManyHeaders(this.maxHeaders));
                     }

                     builder.getRequestHeaders().add(headerName, headerValue);
                     if (headerValuesCache != null && headerName.length() + headerValue.length() < this.maxCachedHeaderSize) {
                        headerValuesCache.put(headerName, headerValue);
                     }

                     state.nextHeader = null;
                     state.leftOver = next;
                     state.stringBuilder.setLength(0);
                     if (next != 13) {
                        if (next == 10) {
                           state.state = 8;
                           return;
                        }

                        state.state = 6;
                        state.parseState = 0;
                        return;
                     }

                     parseState = 4;
                     continue;
                  }

                  parseState = 1;
               }
               break;
            case 4:
               state.state = 8;
               return;
         }
      }

      state.parseState = parseState;
   }

   protected boolean handleCachedHeader(String existing, ByteBuffer buffer, ParseState state, HttpServerExchange builder) throws BadRequestException {
      int pos;
      for(pos = buffer.position(); pos < buffer.limit() && buffer.get(pos) == 32; ++pos) {
      }

      if (existing.length() + 3 + pos > buffer.limit()) {
         return false;
      } else {
         int i;
         byte next;
         for(i = 0; i < existing.length(); ++i) {
            next = buffer.get(pos + i);
            if (next != existing.charAt(i)) {
               return false;
            }
         }

         if (buffer.get(pos + i++) != 13) {
            return false;
         } else if (buffer.get(pos + i++) != 10) {
            return false;
         } else {
            next = buffer.get(pos + i);
            if (next != 9 && next != 32) {
               buffer.position(pos + i);
               if (++state.mapCount > this.maxHeaders) {
                  throw new BadRequestException(UndertowMessages.MESSAGES.tooManyHeaders(this.maxHeaders));
               } else {
                  builder.getRequestHeaders().add(state.nextHeader, existing);
                  state.nextHeader = null;
                  state.state = 6;
                  state.parseState = 0;
                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected void handleAfterVersion(ByteBuffer buffer, ParseState state) throws BadRequestException {
      boolean newLine;
      for(newLine = state.leftOver == 10; buffer.hasRemaining(); newLine = true) {
         byte next = buffer.get();
         if (newLine) {
            if (next == 10) {
               state.state = 8;
               return;
            }

            state.state = 6;
            state.leftOver = next;
            return;
         }

         if (next != 10) {
            if (next != 13 && next != 32 && next != 9) {
               state.state = 6;
               state.leftOver = next;
               return;
            }

            throw UndertowMessages.MESSAGES.badRequest();
         }
      }

      if (newLine) {
         state.leftOver = 10;
      }

   }

   protected static Map<String, HttpString> httpStrings() {
      Map<String, HttpString> results = new HashMap();
      Class[] classs = new Class[]{Headers.class, Methods.class, Protocols.class};
      Class[] var2 = classs;
      int var3 = classs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Class<?> c = var2[var4];
         Field[] var6 = c.getDeclaredFields();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Field field = var6[var8];
            if (field.getType().equals(HttpString.class)) {
               HttpString result = null;

               try {
                  result = (HttpString)field.get((Object)null);
                  results.put(result.toString(), result);
               } catch (IllegalAccessException var12) {
                  throw new RuntimeException(var12);
               }
            }
         }
      }

      return results;
   }

   static {
      try {
         HTTP = "HTTP/1.".getBytes("ASCII");
         HTTP_LENGTH = HTTP.length;
      } catch (UnsupportedEncodingException var1) {
         throw new RuntimeException(var1);
      }

      for(int i = 0; i < 256; ++i) {
         if (i >= 32 && i <= 126) {
            switch ((char)i) {
               case '"':
               case '#':
               case '<':
               case '>':
               case '\\':
               case '^':
               case '`':
               case '{':
               case '|':
               case '}':
                  ALLOWED_TARGET_CHARACTER[i] = false;
                  break;
               default:
                  ALLOWED_TARGET_CHARACTER[i] = true;
            }
         } else {
            ALLOWED_TARGET_CHARACTER[i] = false;
         }
      }

   }
}
