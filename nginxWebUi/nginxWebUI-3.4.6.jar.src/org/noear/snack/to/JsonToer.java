/*     */ package org.noear.snack.to;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import org.noear.snack.ONode;
/*     */ import org.noear.snack.ONodeData;
/*     */ import org.noear.snack.ONodeType;
/*     */ import org.noear.snack.OValue;
/*     */ import org.noear.snack.OValueType;
/*     */ import org.noear.snack.core.Context;
/*     */ import org.noear.snack.core.Feature;
/*     */ import org.noear.snack.core.Options;
/*     */ import org.noear.snack.core.exts.ThData;
/*     */ import org.noear.snack.core.utils.DateUtil;
/*     */ import org.noear.snack.core.utils.IOUtil;
/*     */ import org.noear.snack.core.utils.TypeUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonToer
/*     */   implements Toer
/*     */ {
/*  25 */   private static final ThData<StringBuilder> tlBuilder = new ThData(() -> new StringBuilder(5120));
/*     */ 
/*     */   
/*     */   public void handle(Context ctx) {
/*  29 */     ONode o = (ONode)ctx.source;
/*     */     
/*  31 */     if (null != o) {
/*  32 */       StringBuilder sb = null;
/*  33 */       if (ctx.options.hasFeature(Feature.DisThreadLocal)) {
/*  34 */         sb = new StringBuilder(5120);
/*     */       } else {
/*  36 */         sb = (StringBuilder)tlBuilder.get();
/*  37 */         sb.setLength(0);
/*     */       } 
/*     */ 
/*     */       
/*  41 */       analyse(ctx.options, o, sb);
/*     */       
/*  43 */       ctx.target = sb.toString();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void analyse(Options opts, ONode o, StringBuilder sb) {
/*  48 */     if (o == null) {
/*     */       return;
/*     */     }
/*     */     
/*  52 */     switch (o.nodeType()) {
/*     */       case Null:
/*  54 */         writeValue(opts, sb, o.nodeData());
/*     */         return;
/*     */       
/*     */       case String:
/*  58 */         writeArray(opts, sb, o.nodeData());
/*     */         return;
/*     */       
/*     */       case DateTime:
/*  62 */         writeObject(opts, sb, o.nodeData());
/*     */         return;
/*     */     } 
/*     */     
/*  66 */     sb.append("null");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeArray(Options opts, StringBuilder sBuf, ONodeData d) {
/*  72 */     sBuf.append("[");
/*  73 */     Iterator<ONode> iterator = d.array.iterator();
/*  74 */     while (iterator.hasNext()) {
/*  75 */       ONode sub = iterator.next();
/*  76 */       analyse(opts, sub, sBuf);
/*  77 */       if (iterator.hasNext()) {
/*  78 */         sBuf.append(",");
/*     */       }
/*     */     } 
/*  81 */     sBuf.append("]");
/*     */   }
/*     */   
/*     */   private void writeObject(Options opts, StringBuilder sBuf, ONodeData d) {
/*  85 */     sBuf.append("{");
/*  86 */     Iterator<String> itr = d.object.keySet().iterator();
/*  87 */     while (itr.hasNext()) {
/*  88 */       String k = itr.next();
/*  89 */       writeName(opts, sBuf, k);
/*  90 */       sBuf.append(":");
/*  91 */       analyse(opts, (ONode)d.object.get(k), sBuf);
/*  92 */       if (itr.hasNext()) {
/*  93 */         sBuf.append(",");
/*     */       }
/*     */     } 
/*  96 */     sBuf.append("}");
/*     */   }
/*     */   
/*     */   private void writeValue(Options opts, StringBuilder sBuf, ONodeData d) {
/* 100 */     OValue v = d.value;
/* 101 */     switch (v.type()) {
/*     */       case Null:
/* 103 */         sBuf.append("null");
/*     */         return;
/*     */       
/*     */       case String:
/* 107 */         writeValString(opts, sBuf, v.getRawString(), true);
/*     */         return;
/*     */       
/*     */       case DateTime:
/* 111 */         writeValDate(opts, sBuf, v.getRawDate());
/*     */         return;
/*     */       
/*     */       case Boolean:
/* 115 */         writeValBool(opts, sBuf, Boolean.valueOf(v.getRawBoolean()));
/*     */         return;
/*     */       
/*     */       case Number:
/* 119 */         writeValNumber(opts, sBuf, v.getRawNumber());
/*     */         return;
/*     */     } 
/*     */     
/* 123 */     sBuf.append(v.getString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeName(Options opts, StringBuilder sBuf, String val) {
/* 129 */     if (opts.hasFeature(Feature.QuoteFieldNames)) {
/* 130 */       if (opts.hasFeature(Feature.UseSingleQuotes)) {
/* 131 */         sBuf.append("'").append(val).append("'");
/*     */       } else {
/* 133 */         sBuf.append("\"").append(val).append("\"");
/*     */       } 
/*     */     } else {
/* 136 */       sBuf.append(val);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeValDate(Options opts, StringBuilder sBuf, Date val) {
/* 141 */     if (opts.hasFeature(Feature.WriteDateUseTicks)) {
/* 142 */       sBuf.append(val.getTime());
/* 143 */     } else if (opts.hasFeature(Feature.WriteDateUseFormat)) {
/* 144 */       String valStr = DateUtil.format(val, opts.getDateFormat(), opts.getTimeZone());
/* 145 */       writeValString(opts, sBuf, valStr, false);
/*     */     } else {
/* 147 */       sBuf.append("new Date(").append(val.getTime()).append(")");
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeValBool(Options opts, StringBuilder sBuf, Boolean val) {
/* 152 */     if (opts.hasFeature(Feature.WriteBoolUse01)) {
/* 153 */       sBuf.append(val.booleanValue() ? 1 : 0);
/*     */     } else {
/* 155 */       sBuf.append(val.booleanValue() ? "true" : "false");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeValNumber(Options opts, StringBuilder sBuf, Number val) {
/* 161 */     if (val instanceof BigInteger) {
/* 162 */       BigInteger v = (BigInteger)val;
/* 163 */       String sVal = v.toString();
/*     */       
/* 165 */       if (opts.hasFeature(Feature.WriteNumberUseString)) {
/* 166 */         writeValString(opts, sBuf, sVal, false);
/*     */       
/*     */       }
/* 169 */       else if (sVal.length() > 16 && (v.compareTo(TypeUtil.INT_LOW) < 0 || v.compareTo(TypeUtil.INT_HIGH) > 0) && opts.hasFeature(Feature.BrowserCompatible)) {
/* 170 */         writeValString(opts, sBuf, sVal, false);
/*     */       } else {
/* 172 */         sBuf.append(sVal);
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 178 */     if (val instanceof BigDecimal) {
/* 179 */       BigDecimal v = (BigDecimal)val;
/* 180 */       String sVal = v.toPlainString();
/*     */       
/* 182 */       if (opts.hasFeature(Feature.WriteNumberUseString)) {
/* 183 */         writeValString(opts, sBuf, sVal, false);
/*     */       
/*     */       }
/* 186 */       else if (sVal.length() > 16 && (v.compareTo(TypeUtil.DEC_LOW) < 0 || v.compareTo(TypeUtil.DEC_HIGH) > 0) && opts.hasFeature(Feature.BrowserCompatible)) {
/* 187 */         writeValString(opts, sBuf, sVal, false);
/*     */       } else {
/* 189 */         sBuf.append(sVal);
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 195 */     if (opts.hasFeature(Feature.WriteNumberUseString)) {
/* 196 */       writeValString(opts, sBuf, val.toString(), false);
/*     */     } else {
/* 198 */       sBuf.append(val.toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeValString(Options opts, StringBuilder sBuf, String val, boolean isStr) {
/* 207 */     boolean useSingleQuotes = opts.hasFeature(Feature.UseSingleQuotes);
/* 208 */     char quote = useSingleQuotes ? '\'' : '"';
/* 209 */     sBuf.append(quote);
/*     */ 
/*     */ 
/*     */     
/* 213 */     if (isStr) {
/* 214 */       boolean isCompatible = opts.hasFeature(Feature.BrowserCompatible);
/* 215 */       boolean isSecure = opts.hasFeature(Feature.BrowserSecure);
/* 216 */       boolean isTransfer = opts.hasFeature(Feature.TransferCompatible);
/*     */       
/* 218 */       for (int i = 0, len = val.length(); i < len; i++) {
/* 219 */         char c = val.charAt(i);
/*     */ 
/*     */         
/* 222 */         if (c == quote || c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == '\b' || (c >= '\000' && c <= '\007')) {
/* 223 */           sBuf.append("\\");
/* 224 */           sBuf.append(IOUtil.CHARS_MARK[c]);
/*     */           
/*     */           continue;
/*     */         } 
/* 228 */         if (isSecure && (
/* 229 */           c == '(' || c == ')' || c == '<' || c == '>')) {
/* 230 */           sBuf.append('\\');
/* 231 */           sBuf.append('u');
/* 232 */           sBuf.append(IOUtil.DIGITS[c >>> 12 & 0xF]);
/* 233 */           sBuf.append(IOUtil.DIGITS[c >>> 8 & 0xF]);
/* 234 */           sBuf.append(IOUtil.DIGITS[c >>> 4 & 0xF]);
/* 235 */           sBuf.append(IOUtil.DIGITS[c & 0xF]);
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 240 */         if (isTransfer && 
/* 241 */           c == '\\') {
/* 242 */           sBuf.append("\\");
/* 243 */           sBuf.append(IOUtil.CHARS_MARK[c]);
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 248 */         if (isCompatible) {
/* 249 */           if (c == '\\') {
/* 250 */             sBuf.append("\\");
/* 251 */             sBuf.append(IOUtil.CHARS_MARK[c]);
/*     */             
/*     */             continue;
/*     */           } 
/*     */           
/* 256 */           if (c < ' ') {
/* 257 */             sBuf.append('\\');
/* 258 */             sBuf.append('u');
/* 259 */             sBuf.append('0');
/* 260 */             sBuf.append('0');
/* 261 */             sBuf.append(IOUtil.DIGITS[c >>> 4 & 0xF]);
/* 262 */             sBuf.append(IOUtil.DIGITS[c & 0xF]);
/*     */             
/*     */             continue;
/*     */           } 
/* 266 */           if (c >= '') {
/* 267 */             sBuf.append('\\');
/* 268 */             sBuf.append('u');
/* 269 */             sBuf.append(IOUtil.DIGITS[c >>> 12 & 0xF]);
/* 270 */             sBuf.append(IOUtil.DIGITS[c >>> 8 & 0xF]);
/* 271 */             sBuf.append(IOUtil.DIGITS[c >>> 4 & 0xF]);
/* 272 */             sBuf.append(IOUtil.DIGITS[c & 0xF]);
/*     */             
/*     */             continue;
/*     */           } 
/*     */         } 
/* 277 */         sBuf.append(c);
/*     */         continue;
/*     */       } 
/*     */     } else {
/* 281 */       sBuf.append(val);
/*     */     } 
/*     */ 
/*     */     
/* 285 */     sBuf.append(quote);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\to\JsonToer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */