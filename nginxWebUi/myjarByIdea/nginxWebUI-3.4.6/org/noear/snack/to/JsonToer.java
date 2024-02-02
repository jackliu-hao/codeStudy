package org.noear.snack.to;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import org.noear.snack.ONode;
import org.noear.snack.ONodeData;
import org.noear.snack.OValue;
import org.noear.snack.core.Context;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.snack.core.exts.ThData;
import org.noear.snack.core.utils.DateUtil;
import org.noear.snack.core.utils.IOUtil;
import org.noear.snack.core.utils.TypeUtil;

public class JsonToer implements Toer {
   private static final ThData<StringBuilder> tlBuilder = new ThData(() -> {
      return new StringBuilder(5120);
   });

   public void handle(Context ctx) {
      ONode o = (ONode)ctx.source;
      if (null != o) {
         StringBuilder sb = null;
         if (ctx.options.hasFeature(Feature.DisThreadLocal)) {
            sb = new StringBuilder(5120);
         } else {
            sb = (StringBuilder)tlBuilder.get();
            sb.setLength(0);
         }

         this.analyse(ctx.options, o, sb);
         ctx.target = sb.toString();
      }

   }

   public void analyse(Options opts, ONode o, StringBuilder sb) {
      if (o != null) {
         switch (o.nodeType()) {
            case Value:
               this.writeValue(opts, sb, o.nodeData());
               break;
            case Array:
               this.writeArray(opts, sb, o.nodeData());
               break;
            case Object:
               this.writeObject(opts, sb, o.nodeData());
               break;
            default:
               sb.append("null");
         }

      }
   }

   private void writeArray(Options opts, StringBuilder sBuf, ONodeData d) {
      sBuf.append("[");
      Iterator<ONode> iterator = d.array.iterator();

      while(iterator.hasNext()) {
         ONode sub = (ONode)iterator.next();
         this.analyse(opts, sub, sBuf);
         if (iterator.hasNext()) {
            sBuf.append(",");
         }
      }

      sBuf.append("]");
   }

   private void writeObject(Options opts, StringBuilder sBuf, ONodeData d) {
      sBuf.append("{");
      Iterator<String> itr = d.object.keySet().iterator();

      while(itr.hasNext()) {
         String k = (String)itr.next();
         this.writeName(opts, sBuf, k);
         sBuf.append(":");
         this.analyse(opts, (ONode)d.object.get(k), sBuf);
         if (itr.hasNext()) {
            sBuf.append(",");
         }
      }

      sBuf.append("}");
   }

   private void writeValue(Options opts, StringBuilder sBuf, ONodeData d) {
      OValue v = d.value;
      switch (v.type()) {
         case Null:
            sBuf.append("null");
            break;
         case String:
            this.writeValString(opts, sBuf, v.getRawString(), true);
            break;
         case DateTime:
            this.writeValDate(opts, sBuf, v.getRawDate());
            break;
         case Boolean:
            this.writeValBool(opts, sBuf, v.getRawBoolean());
            break;
         case Number:
            this.writeValNumber(opts, sBuf, v.getRawNumber());
            break;
         default:
            sBuf.append(v.getString());
      }

   }

   private void writeName(Options opts, StringBuilder sBuf, String val) {
      if (opts.hasFeature(Feature.QuoteFieldNames)) {
         if (opts.hasFeature(Feature.UseSingleQuotes)) {
            sBuf.append("'").append(val).append("'");
         } else {
            sBuf.append("\"").append(val).append("\"");
         }
      } else {
         sBuf.append(val);
      }

   }

   private void writeValDate(Options opts, StringBuilder sBuf, Date val) {
      if (opts.hasFeature(Feature.WriteDateUseTicks)) {
         sBuf.append(val.getTime());
      } else if (opts.hasFeature(Feature.WriteDateUseFormat)) {
         String valStr = DateUtil.format(val, opts.getDateFormat(), opts.getTimeZone());
         this.writeValString(opts, sBuf, valStr, false);
      } else {
         sBuf.append("new Date(").append(val.getTime()).append(")");
      }

   }

   private void writeValBool(Options opts, StringBuilder sBuf, Boolean val) {
      if (opts.hasFeature(Feature.WriteBoolUse01)) {
         sBuf.append(val ? 1 : 0);
      } else {
         sBuf.append(val ? "true" : "false");
      }

   }

   private void writeValNumber(Options opts, StringBuilder sBuf, Number val) {
      String sVal;
      if (val instanceof BigInteger) {
         BigInteger v = (BigInteger)val;
         sVal = v.toString();
         if (opts.hasFeature(Feature.WriteNumberUseString)) {
            this.writeValString(opts, sBuf, sVal, false);
         } else if (sVal.length() > 16 && (v.compareTo(TypeUtil.INT_LOW) < 0 || v.compareTo(TypeUtil.INT_HIGH) > 0) && opts.hasFeature(Feature.BrowserCompatible)) {
            this.writeValString(opts, sBuf, sVal, false);
         } else {
            sBuf.append(sVal);
         }

      } else if (!(val instanceof BigDecimal)) {
         if (opts.hasFeature(Feature.WriteNumberUseString)) {
            this.writeValString(opts, sBuf, val.toString(), false);
         } else {
            sBuf.append(val.toString());
         }

      } else {
         BigDecimal v = (BigDecimal)val;
         sVal = v.toPlainString();
         if (opts.hasFeature(Feature.WriteNumberUseString)) {
            this.writeValString(opts, sBuf, sVal, false);
         } else if (sVal.length() > 16 && (v.compareTo(TypeUtil.DEC_LOW) < 0 || v.compareTo(TypeUtil.DEC_HIGH) > 0) && opts.hasFeature(Feature.BrowserCompatible)) {
            this.writeValString(opts, sBuf, sVal, false);
         } else {
            sBuf.append(sVal);
         }

      }
   }

   private void writeValString(Options opts, StringBuilder sBuf, String val, boolean isStr) {
      boolean useSingleQuotes = opts.hasFeature(Feature.UseSingleQuotes);
      char quote = useSingleQuotes ? 39 : 34;
      sBuf.append((char)quote);
      if (isStr) {
         boolean isCompatible = opts.hasFeature(Feature.BrowserCompatible);
         boolean isSecure = opts.hasFeature(Feature.BrowserSecure);
         boolean isTransfer = opts.hasFeature(Feature.TransferCompatible);
         int i = 0;

         for(int len = val.length(); i < len; ++i) {
            char c = val.charAt(i);
            if (c == quote || c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == '\b' || c >= 0 && c <= 7) {
               sBuf.append("\\");
               sBuf.append(IOUtil.CHARS_MARK[c]);
            } else if (!isSecure || c != '(' && c != ')' && c != '<' && c != '>') {
               if (isTransfer && c == '\\') {
                  sBuf.append("\\");
                  sBuf.append(IOUtil.CHARS_MARK[c]);
               } else {
                  if (isCompatible) {
                     if (c == '\\') {
                        sBuf.append("\\");
                        sBuf.append(IOUtil.CHARS_MARK[c]);
                        continue;
                     }

                     if (c < ' ') {
                        sBuf.append('\\');
                        sBuf.append('u');
                        sBuf.append('0');
                        sBuf.append('0');
                        sBuf.append(IOUtil.DIGITS[c >>> 4 & 15]);
                        sBuf.append(IOUtil.DIGITS[c & 15]);
                        continue;
                     }

                     if (c >= 127) {
                        sBuf.append('\\');
                        sBuf.append('u');
                        sBuf.append(IOUtil.DIGITS[c >>> 12 & 15]);
                        sBuf.append(IOUtil.DIGITS[c >>> 8 & 15]);
                        sBuf.append(IOUtil.DIGITS[c >>> 4 & 15]);
                        sBuf.append(IOUtil.DIGITS[c & 15]);
                        continue;
                     }
                  }

                  sBuf.append(c);
               }
            } else {
               sBuf.append('\\');
               sBuf.append('u');
               sBuf.append(IOUtil.DIGITS[c >>> 12 & 15]);
               sBuf.append(IOUtil.DIGITS[c >>> 8 & 15]);
               sBuf.append(IOUtil.DIGITS[c >>> 4 & 15]);
               sBuf.append(IOUtil.DIGITS[c & 15]);
            }
         }
      } else {
         sBuf.append(val);
      }

      sBuf.append((char)quote);
   }
}
