package io.undertow.client.http;

import io.undertow.util.BadRequestException;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.Protocols;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

abstract class HttpResponseParser {
   public static final HttpResponseParser INSTANCE;
   private static final int NORMAL = 0;
   private static final int WHITESPACE = 1;
   private static final int BEGIN_LINE_END = 2;
   private static final int LINE_END = 3;
   private static final int AWAIT_DATA_END = 4;

   abstract void handleHttpVersion(ByteBuffer var1, ResponseParseState var2, HttpResponseBuilder var3) throws BadRequestException;

   abstract void handleHeader(ByteBuffer var1, ResponseParseState var2, HttpResponseBuilder var3) throws BadRequestException;

   public void handle(ByteBuffer buffer, ResponseParseState currentState, HttpResponseBuilder builder) throws BadRequestException {
      if (currentState.state == 0) {
         this.handleHttpVersion(buffer, currentState, builder);
         if (!buffer.hasRemaining()) {
            return;
         }
      }

      if (currentState.state == 1) {
         this.handleStatusCode(buffer, currentState, builder);
         if (!buffer.hasRemaining()) {
            return;
         }
      }

      if (currentState.state == 2) {
         this.handleReasonPhrase(buffer, currentState, builder);
         if (!buffer.hasRemaining()) {
            return;
         }
      }

      if (currentState.state == 3) {
         this.handleAfterReasonPhrase(buffer, currentState, builder);
         if (!buffer.hasRemaining()) {
            return;
         }
      }

      while(currentState.state != 6) {
         if (currentState.state == 4) {
            this.handleHeader(buffer, currentState, builder);
            if (!buffer.hasRemaining()) {
               return;
            }
         }

         if (currentState.state == 5) {
            this.handleHeaderValue(buffer, currentState, builder);
            if (!buffer.hasRemaining()) {
               return;
            }
         }
      }

   }

   final void handleStatusCode(ByteBuffer buffer, ResponseParseState state, HttpResponseBuilder builder) {
      StringBuilder stringBuilder = state.stringBuilder;

      while(buffer.hasRemaining()) {
         char next = (char)buffer.get();
         if (next == ' ' || next == '\t') {
            builder.setStatusCode(Integer.parseInt(stringBuilder.toString()));
            state.state = 2;
            state.stringBuilder.setLength(0);
            state.parseState = 0;
            state.pos = 0;
            state.nextHeader = null;
            return;
         }

         stringBuilder.append(next);
      }

   }

   final void handleReasonPhrase(ByteBuffer buffer, ResponseParseState state, HttpResponseBuilder builder) {
      StringBuilder stringBuilder = state.stringBuilder;

      while(buffer.hasRemaining()) {
         char next = (char)buffer.get();
         if (next == '\n' || next == '\r') {
            builder.setReasonPhrase(stringBuilder.toString());
            state.state = 3;
            state.stringBuilder.setLength(0);
            state.parseState = 0;
            state.leftOver = (byte)next;
            state.pos = 0;
            state.nextHeader = null;
            return;
         }

         stringBuilder.append(next);
      }

   }

   final void handleHeaderValue(ByteBuffer buffer, ResponseParseState state, HttpResponseBuilder builder) {
      StringBuilder stringBuilder = state.stringBuilder;
      if (stringBuilder == null) {
         stringBuilder = new StringBuilder();
         state.parseState = 0;
      }

      int parseState = state.parseState;

      while(buffer.hasRemaining()) {
         byte next = buffer.get();
         switch (parseState) {
            case 0:
               if (next == 13) {
                  parseState = 2;
               } else if (next == 10) {
                  parseState = 3;
               } else {
                  if (next != 32 && next != 9) {
                     stringBuilder.append((char)next);
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

                  stringBuilder.append((char)next);
                  parseState = 0;
               }
               break;
            case 2:
            case 3:
               if (next == 10 && parseState == 2) {
                  parseState = 3;
               } else {
                  if (next != 9 && next != 32) {
                     HttpString nextStandardHeader = state.nextHeader;
                     String headerValue = stringBuilder.toString();
                     builder.getResponseHeaders().add(nextStandardHeader, headerValue);
                     state.nextHeader = null;
                     state.leftOver = next;
                     state.stringBuilder.setLength(0);
                     if (next != 13) {
                        state.state = 4;
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
               state.state = 6;
               return;
         }
      }

      state.parseState = parseState;
   }

   protected void handleAfterReasonPhrase(ByteBuffer buffer, ResponseParseState state, HttpResponseBuilder builder) {
      boolean newLine = state.leftOver == 10;

      while(buffer.hasRemaining()) {
         byte next = buffer.get();
         if (newLine) {
            if (next == 10) {
               state.state = 6;
               return;
            }

            state.state = 4;
            state.leftOver = next;
            return;
         }

         if (next == 10) {
            newLine = true;
         } else if (next != 13 && next != 32 && next != 9) {
            state.state = 4;
            state.leftOver = next;
            return;
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
               field.setAccessible(true);
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
         Class<?> cls = Class.forName(HttpResponseParser.class.getName() + "$$generated", false, HttpResponseParser.class.getClassLoader());
         INSTANCE = (HttpResponseParser)cls.newInstance();
      } catch (Exception var1) {
         throw new RuntimeException(var1);
      }
   }
}
