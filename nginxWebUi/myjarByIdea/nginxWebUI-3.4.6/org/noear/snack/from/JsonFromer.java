package org.noear.snack.from;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.noear.snack.ONode;
import org.noear.snack.OValue;
import org.noear.snack.core.Context;
import org.noear.snack.core.Feature;
import org.noear.snack.core.exts.CharBuffer;
import org.noear.snack.core.exts.CharReader;
import org.noear.snack.core.exts.ThData;
import org.noear.snack.core.utils.IOUtil;
import org.noear.snack.exception.SnackException;

public class JsonFromer implements Fromer {
   private static final ThData<CharBuffer> tlBuilder = new ThData(() -> {
      return new CharBuffer();
   });

   public void handle(Context ctx) throws IOException {
      ctx.target = this.do_handle(ctx, (String)ctx.source);
   }

   private ONode do_handle(Context ctx, String text) throws IOException {
      if (text == null) {
         return new ONode(ctx.options);
      } else {
         text = text.trim();
         int len = text.length();
         ONode node;
         if (len == 0) {
            node = new ONode(ctx.options);
         } else {
            char prefix = text.charAt(0);
            char suffix = text.charAt(text.length() - 1);
            if ((prefix != '{' || suffix != '}') && (prefix != '[' || suffix != ']')) {
               if (len >= 2 && (prefix == '"' && suffix == '"' || prefix == '\'' && suffix == '\'')) {
                  node = this.analyse_val(ctx, text.substring(1, len - 1), true, false);
               } else if (prefix != '<' && len < 40) {
                  node = this.analyse_val(ctx, text, false, true);
               } else {
                  node = new ONode(ctx.options);
                  node.val().setString(text);
               }
            } else {
               CharBuffer sBuf = null;
               if (ctx.options.hasFeature(Feature.DisThreadLocal)) {
                  sBuf = new CharBuffer();
               } else {
                  sBuf = (CharBuffer)tlBuilder.get();
                  sBuf.setLength(0);
               }

               node = new ONode(ctx.options);
               this.analyse(ctx, new CharReader(text), sBuf, node);
            }
         }

         return node;
      }
   }

   public void analyse(Context ctx, CharReader sr, CharBuffer sBuf, ONode p) throws IOException {
      String name = null;
      boolean read_space1 = false;

      while(sr.read()) {
         char c = sr.value();
         switch (c) {
            case '"':
               this.scanString(sr, sBuf, '"');
               if (this.analyse_buf(ctx, p, name, sBuf)) {
                  name = null;
               }
               break;
            case '\'':
               this.scanString(sr, sBuf, '\'');
               if (this.analyse_buf(ctx, p, name, sBuf)) {
                  name = null;
               }
               break;
            case ',':
               if (sBuf.length() > 0 && this.analyse_buf(ctx, p, name, sBuf)) {
                  name = null;
               }
               break;
            case ':':
               name = sBuf.toString();
               sBuf.setLength(0);
               break;
            case '[':
               if (p.isObject()) {
                  this.analyse(ctx, sr, sBuf, p.getNew(name).asArray());
                  name = null;
               } else if (p.isArray()) {
                  this.analyse(ctx, sr, sBuf, p.addNew().asArray());
               } else {
                  this.analyse(ctx, sr, sBuf, p.asArray());
               }
               break;
            case ']':
               if (sBuf.length() > 0) {
                  this.analyse_buf(ctx, p, name, sBuf);
               }

               return;
            case '{':
               if (p.isObject()) {
                  this.analyse(ctx, sr, sBuf, p.getNew(name).asObject());
                  name = null;
               } else if (p.isArray()) {
                  this.analyse(ctx, sr, sBuf, p.addNew().asObject());
               } else {
                  this.analyse(ctx, sr, sBuf, p.asObject());
               }
               break;
            case '}':
               if (sBuf.length() > 0) {
                  this.analyse_buf(ctx, p, name, sBuf);
               }

               return;
            default:
               if (sBuf.length() == 0) {
                  if (c > ' ') {
                     sBuf.append(c);
                     if (c == 'n') {
                        read_space1 = true;
                     }
                  }
               } else if (c > ' ') {
                  sBuf.append(c);
               } else if (c == ' ' && read_space1) {
                  read_space1 = false;
                  sBuf.append(c);
               }
         }
      }

   }

   private boolean analyse_buf(Context ctx, ONode p, String name, CharBuffer sBuf) {
      if (p.isObject()) {
         if (name != null) {
            p.setNode(name, this.analyse_val(ctx, sBuf));
            sBuf.setLength(0);
            return true;
         }
      } else if (p.isArray()) {
         p.addNode(this.analyse_val(ctx, sBuf));
         sBuf.setLength(0);
      }

      return false;
   }

   private void scanString(CharReader sr, CharBuffer sBuf, char quote) throws IOException {
      sBuf.isString = true;

      while(true) {
         while(true) {
            while(sr.read()) {
               char c = sr.value();
               if (quote == c) {
                  return;
               }

               if ('\\' == c) {
                  c = sr.next();
                  if ('t' != c && 'r' != c && 'n' != c && 'f' != c && 'b' != c && '"' != c && '\'' != c && '/' != c && (c < '0' || c > '7')) {
                     int val;
                     if ('x' == c) {
                        val = sr.next();
                        char x2 = sr.next();
                        int val = IOUtil.DIGITS_MARK[val] * 16 + IOUtil.DIGITS_MARK[x2];
                        sBuf.append((char)val);
                     } else if ('u' == c) {
                        int val = 0;
                        c = sr.next();
                        val = (val << 4) + IOUtil.DIGITS_MARK[c];
                        c = sr.next();
                        val = (val << 4) + IOUtil.DIGITS_MARK[c];
                        c = sr.next();
                        val = (val << 4) + IOUtil.DIGITS_MARK[c];
                        c = sr.next();
                        val = (val << 4) + IOUtil.DIGITS_MARK[c];
                        sBuf.append((char)val);
                     } else {
                        sBuf.append('\\');
                        sBuf.append(c);
                     }
                  } else {
                     sBuf.append(IOUtil.CHARS_MARK_REV[c]);
                  }
               } else {
                  sBuf.append(c);
               }
            }

            return;
         }
      }
   }

   private ONode analyse_val(Context ctx, CharBuffer sBuf) {
      if (!sBuf.isString) {
         sBuf.trimLast();
      }

      return this.analyse_val(ctx, sBuf.toString(), sBuf.isString, false);
   }

   private ONode analyse_val(Context ctx, String sval, boolean isString, boolean isNoterr) {
      ONode orst = null;
      if (isString) {
         if (ctx.options.hasFeature(Feature.StringJsonToNode) && (sval.startsWith("{") && sval.endsWith("}") || sval.startsWith("[") && sval.endsWith("]"))) {
            orst = ONode.loadStr(sval, ctx.options);
         }

         if (orst == null) {
            orst = new ONode(ctx.options);
            orst.val().setString(sval);
         }
      } else {
         orst = new ONode(ctx.options);
         OValue oval = orst.val();
         char c = sval.charAt(0);
         int len = sval.length();
         if (c == 't' && len == 4) {
            oval.setBool(true);
         } else if (c == 'f' && len == 5) {
            oval.setBool(false);
         } else if (c == 'n') {
            if (len == 4) {
               oval.setNull();
            } else if (sval.indexOf(68) == 4) {
               long ticks = Long.parseLong(sval.substring(9, sval.length() - 1));
               oval.setDate(new Date(ticks));
            }
         } else if (c == 'N' && len == 3) {
            oval.setNull();
         } else if (c == 'u' && len == 9) {
            oval.setNull();
         } else if ((c < '0' || c > '9') && c != '-') {
            if (!isNoterr) {
               throw new SnackException("Format error!");
            }

            oval.setString(sval);
         } else if (sval.length() > 16) {
            if (sval.indexOf(46) > 0) {
               oval.setNumber(new BigDecimal(sval));
            } else {
               oval.setNumber(new BigInteger(sval));
            }
         } else if (sval.indexOf(46) > 0) {
            oval.setNumber(Double.parseDouble(sval));
         } else {
            oval.setNumber(Long.parseLong(sval));
         }
      }

      return orst;
   }
}
