/*     */ package org.noear.snack.from;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Date;
/*     */ import org.noear.snack.ONode;
/*     */ import org.noear.snack.OValue;
/*     */ import org.noear.snack.core.Context;
/*     */ import org.noear.snack.core.Feature;
/*     */ import org.noear.snack.core.exts.CharBuffer;
/*     */ import org.noear.snack.core.exts.CharReader;
/*     */ import org.noear.snack.core.exts.ThData;
/*     */ import org.noear.snack.core.utils.IOUtil;
/*     */ import org.noear.snack.exception.SnackException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonFromer
/*     */   implements Fromer
/*     */ {
/*  24 */   private static final ThData<CharBuffer> tlBuilder = new ThData(() -> new CharBuffer());
/*     */ 
/*     */   
/*     */   public void handle(Context ctx) throws IOException {
/*  28 */     ctx.target = do_handle(ctx, (String)ctx.source);
/*     */   }
/*     */   private ONode do_handle(Context ctx, String text) throws IOException {
/*     */     ONode node;
/*  32 */     if (text == null) {
/*  33 */       return new ONode(ctx.options);
/*     */     }
/*  35 */     text = text.trim();
/*     */ 
/*     */     
/*  38 */     int len = text.length();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  43 */     if (len == 0) {
/*  44 */       node = new ONode(ctx.options);
/*     */     } else {
/*  46 */       char prefix = text.charAt(0);
/*  47 */       char suffix = text.charAt(text.length() - 1);
/*     */       
/*  49 */       if ((prefix == '{' && suffix == '}') || (prefix == '[' && suffix == ']')) {
/*     */ 
/*     */ 
/*     */         
/*  53 */         CharBuffer sBuf = null;
/*  54 */         if (ctx.options.hasFeature(Feature.DisThreadLocal)) {
/*  55 */           sBuf = new CharBuffer();
/*     */         } else {
/*  57 */           sBuf = (CharBuffer)tlBuilder.get();
/*  58 */           sBuf.setLength(0);
/*     */         } 
/*     */         
/*  61 */         node = new ONode(ctx.options);
/*  62 */         analyse(ctx, new CharReader(text), sBuf, node);
/*     */       }
/*  64 */       else if (len >= 2 && ((prefix == '"' && suffix == '"') || (prefix == '\'' && suffix == '\''))) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  69 */         node = analyse_val(ctx, text.substring(1, len - 1), true, false);
/*  70 */       } else if (prefix != '<' && len < 40) {
/*     */ 
/*     */         
/*  73 */         node = analyse_val(ctx, text, false, true);
/*     */       } else {
/*     */         
/*  76 */         node = new ONode(ctx.options);
/*  77 */         node.val().setString(text);
/*     */       } 
/*     */     } 
/*     */     
/*  81 */     return node;
/*     */   }
/*     */   
/*     */   public void analyse(Context ctx, CharReader sr, CharBuffer sBuf, ONode p) throws IOException {
/*  85 */     String name = null;
/*     */     
/*  87 */     boolean read_space1 = false;
/*     */     
/*  89 */     while (sr.read()) {
/*  90 */       char c = sr.value();
/*     */ 
/*     */       
/*  93 */       switch (c) {
/*     */         case '"':
/*  95 */           scanString(sr, sBuf, '"');
/*  96 */           if (analyse_buf(ctx, p, name, sBuf)) {
/*  97 */             name = null;
/*     */           }
/*     */           continue;
/*     */         
/*     */         case '\'':
/* 102 */           scanString(sr, sBuf, '\'');
/* 103 */           if (analyse_buf(ctx, p, name, sBuf)) {
/* 104 */             name = null;
/*     */           }
/*     */           continue;
/*     */         
/*     */         case '{':
/* 109 */           if (p.isObject()) {
/* 110 */             analyse(ctx, sr, sBuf, p.getNew(name).asObject());
/* 111 */             name = null; continue;
/* 112 */           }  if (p.isArray()) {
/* 113 */             analyse(ctx, sr, sBuf, p.addNew().asObject()); continue;
/*     */           } 
/* 115 */           analyse(ctx, sr, sBuf, p.asObject());
/*     */           continue;
/*     */ 
/*     */         
/*     */         case '[':
/* 120 */           if (p.isObject()) {
/* 121 */             analyse(ctx, sr, sBuf, p.getNew(name).asArray());
/* 122 */             name = null; continue;
/* 123 */           }  if (p.isArray()) {
/* 124 */             analyse(ctx, sr, sBuf, p.addNew().asArray()); continue;
/*     */           } 
/* 126 */           analyse(ctx, sr, sBuf, p.asArray());
/*     */           continue;
/*     */ 
/*     */ 
/*     */         
/*     */         case ':':
/* 132 */           name = sBuf.toString();
/* 133 */           sBuf.setLength(0);
/*     */           continue;
/*     */         
/*     */         case ',':
/* 137 */           if (sBuf.length() > 0 && 
/* 138 */             analyse_buf(ctx, p, name, sBuf)) {
/* 139 */             name = null;
/*     */           }
/*     */           continue;
/*     */ 
/*     */         
/*     */         case '}':
/* 145 */           if (sBuf.length() > 0) {
/* 146 */             analyse_buf(ctx, p, name, sBuf);
/*     */           }
/*     */           return;
/*     */         
/*     */         case ']':
/* 151 */           if (sBuf.length() > 0) {
/* 152 */             analyse_buf(ctx, p, name, sBuf);
/*     */           }
/*     */           return;
/*     */       } 
/*     */       
/* 157 */       if (sBuf.length() == 0) {
/* 158 */         if (c > ' ') {
/* 159 */           sBuf.append(c);
/*     */           
/* 161 */           if (c == 'n')
/* 162 */             read_space1 = true; 
/*     */         } 
/*     */         continue;
/*     */       } 
/* 166 */       if (c > ' ') {
/* 167 */         sBuf.append(c); continue;
/* 168 */       }  if (c == ' ' && 
/* 169 */         read_space1) {
/* 170 */         read_space1 = false;
/* 171 */         sBuf.append(c);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean analyse_buf(Context ctx, ONode p, String name, CharBuffer sBuf) {
/* 181 */     if (p.isObject()) {
/* 182 */       if (name != null) {
/* 183 */         p.setNode(name, analyse_val(ctx, sBuf));
/* 184 */         sBuf.setLength(0);
/* 185 */         return true;
/*     */       } 
/* 187 */     } else if (p.isArray()) {
/* 188 */       p.addNode(analyse_val(ctx, sBuf));
/* 189 */       sBuf.setLength(0);
/*     */     } 
/* 191 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void scanString(CharReader sr, CharBuffer sBuf, char quote) throws IOException {
/* 196 */     sBuf.isString = true;
/*     */     
/* 198 */     while (sr.read()) {
/* 199 */       char c = sr.value();
/*     */       
/* 201 */       if (quote == c) {
/*     */         return;
/*     */       }
/*     */       
/* 205 */       if ('\\' == c) {
/* 206 */         c = sr.next();
/*     */         
/* 208 */         if ('t' == c || 'r' == c || 'n' == c || 'f' == c || 'b' == c || '"' == c || '\'' == c || '/' == c || (c >= '0' && c <= '7')) {
/* 209 */           sBuf.append(IOUtil.CHARS_MARK_REV[c]);
/*     */           
/*     */           continue;
/*     */         } 
/* 213 */         if ('x' == c) {
/*     */           
/* 215 */           char x1 = sr.next();
/* 216 */           char x2 = sr.next();
/*     */           
/* 218 */           int val = IOUtil.DIGITS_MARK[x1] * 16 + IOUtil.DIGITS_MARK[x2];
/* 219 */           sBuf.append((char)val);
/*     */           
/*     */           continue;
/*     */         } 
/* 223 */         if ('u' == c) {
/* 224 */           int val = 0;
/*     */           
/* 226 */           c = sr.next();
/* 227 */           val = (val << 4) + IOUtil.DIGITS_MARK[c];
/* 228 */           c = sr.next();
/* 229 */           val = (val << 4) + IOUtil.DIGITS_MARK[c];
/* 230 */           c = sr.next();
/* 231 */           val = (val << 4) + IOUtil.DIGITS_MARK[c];
/* 232 */           c = sr.next();
/* 233 */           val = (val << 4) + IOUtil.DIGITS_MARK[c];
/* 234 */           sBuf.append((char)val);
/*     */           
/*     */           continue;
/*     */         } 
/* 238 */         sBuf.append('\\');
/* 239 */         sBuf.append(c); continue;
/*     */       } 
/* 241 */       sBuf.append(c);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private ONode analyse_val(Context ctx, CharBuffer sBuf) {
/* 247 */     if (!sBuf.isString) {
/* 248 */       sBuf.trimLast();
/*     */     }
/* 250 */     return analyse_val(ctx, sBuf.toString(), sBuf.isString, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ONode analyse_val(Context ctx, String sval, boolean isString, boolean isNoterr) {
/* 257 */     ONode orst = null;
/*     */     
/* 259 */     if (isString) {
/* 260 */       if (ctx.options.hasFeature(Feature.StringJsonToNode) && ((
/* 261 */         sval.startsWith("{") && sval.endsWith("}")) || (sval
/* 262 */         .startsWith("[") && sval.endsWith("]")))) {
/* 263 */         orst = ONode.loadStr(sval, ctx.options);
/*     */       }
/*     */ 
/*     */       
/* 267 */       if (orst == null) {
/* 268 */         orst = new ONode(ctx.options);
/* 269 */         orst.val().setString(sval);
/*     */       } 
/*     */     } else {
/* 272 */       orst = new ONode(ctx.options);
/* 273 */       OValue oval = orst.val();
/*     */       
/* 275 */       char c = sval.charAt(0);
/* 276 */       int len = sval.length();
/*     */       
/* 278 */       if (c == 't' && len == 4) {
/* 279 */         oval.setBool(true);
/* 280 */       } else if (c == 'f' && len == 5) {
/* 281 */         oval.setBool(false);
/* 282 */       } else if (c == 'n') {
/* 283 */         if (len == 4) {
/* 284 */           oval.setNull();
/* 285 */         } else if (sval.indexOf('D') == 4) {
/* 286 */           long ticks = Long.parseLong(sval.substring(9, sval.length() - 1));
/* 287 */           oval.setDate(new Date(ticks));
/*     */         } 
/* 289 */       } else if (c == 'N' && len == 3) {
/* 290 */         oval.setNull();
/* 291 */       } else if (c == 'u' && len == 9) {
/* 292 */         oval.setNull();
/* 293 */       } else if ((c >= '0' && c <= '9') || c == '-') {
/* 294 */         if (sval.length() > 16) {
/* 295 */           if (sval.indexOf('.') > 0) {
/* 296 */             oval.setNumber(new BigDecimal(sval));
/*     */           } else {
/* 298 */             oval.setNumber(new BigInteger(sval));
/*     */           }
/*     */         
/* 301 */         } else if (sval.indexOf('.') > 0) {
/* 302 */           oval.setNumber(Double.valueOf(Double.parseDouble(sval)));
/*     */         } else {
/* 304 */           oval.setNumber(Long.valueOf(Long.parseLong(sval)));
/*     */         }
/*     */       
/*     */       }
/* 308 */       else if (isNoterr) {
/* 309 */         oval.setString(sval);
/*     */       } else {
/* 311 */         throw new SnackException("Format error!");
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 316 */     return orst;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\from\JsonFromer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */