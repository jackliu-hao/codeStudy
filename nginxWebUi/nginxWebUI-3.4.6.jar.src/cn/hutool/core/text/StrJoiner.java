/*     */ package cn.hutool.core.text;
/*     */ 
/*     */ import cn.hutool.core.collection.ArrayIter;
/*     */ import cn.hutool.core.collection.IterUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ import java.util.function.Function;
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
/*     */ public class StrJoiner
/*     */   implements Appendable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private Appendable appendable;
/*     */   private CharSequence delimiter;
/*     */   private CharSequence prefix;
/*     */   private CharSequence suffix;
/*     */   private boolean wrapElement;
/*  38 */   private NullMode nullMode = NullMode.NULL_STRING;
/*     */   
/*  40 */   private String emptyResult = "";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasContent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StrJoiner of(StrJoiner joiner) {
/*  53 */     StrJoiner joinerNew = new StrJoiner(joiner.delimiter, joiner.prefix, joiner.suffix);
/*  54 */     joinerNew.wrapElement = joiner.wrapElement;
/*  55 */     joinerNew.nullMode = joiner.nullMode;
/*  56 */     joinerNew.emptyResult = joiner.emptyResult;
/*     */     
/*  58 */     return joinerNew;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StrJoiner of(CharSequence delimiter) {
/*  68 */     return new StrJoiner(delimiter);
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
/*     */   public static StrJoiner of(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
/*  80 */     return new StrJoiner(delimiter, prefix, suffix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrJoiner(CharSequence delimiter) {
/*  89 */     this(null, delimiter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrJoiner(Appendable appendable, CharSequence delimiter) {
/*  99 */     this(appendable, delimiter, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrJoiner(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
/* 110 */     this(null, delimiter, prefix, suffix);
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
/*     */   public StrJoiner(Appendable appendable, CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
/* 123 */     if (null != appendable) {
/* 124 */       this.appendable = appendable;
/* 125 */       checkHasContent(appendable);
/*     */     } 
/*     */     
/* 128 */     this.delimiter = delimiter;
/* 129 */     this.prefix = prefix;
/* 130 */     this.suffix = suffix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrJoiner setDelimiter(CharSequence delimiter) {
/* 140 */     this.delimiter = delimiter;
/* 141 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrJoiner setPrefix(CharSequence prefix) {
/* 151 */     this.prefix = prefix;
/* 152 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrJoiner setSuffix(CharSequence suffix) {
/* 162 */     this.suffix = suffix;
/* 163 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrJoiner setWrapElement(boolean wrapElement) {
/* 173 */     this.wrapElement = wrapElement;
/* 174 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrJoiner setNullMode(NullMode nullMode) {
/* 184 */     this.nullMode = nullMode;
/* 185 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrJoiner setEmptyResult(String emptyResult) {
/* 195 */     this.emptyResult = emptyResult;
/* 196 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrJoiner append(Object obj) {
/* 206 */     if (null == obj) {
/* 207 */       append((CharSequence)null);
/* 208 */     } else if (ArrayUtil.isArray(obj)) {
/* 209 */       append((Iterator<?>)new ArrayIter(obj));
/* 210 */     } else if (obj instanceof Iterator) {
/* 211 */       append((Iterator)obj);
/* 212 */     } else if (obj instanceof Iterable) {
/* 213 */       append(((Iterable)obj).iterator());
/*     */     } else {
/* 215 */       append(ObjectUtil.toString(obj));
/*     */     } 
/* 217 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> StrJoiner append(T[] array) {
/* 228 */     if (null == array) {
/* 229 */       return this;
/*     */     }
/* 231 */     return append((Iterator<?>)new ArrayIter((Object[])array));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> StrJoiner append(Iterator<T> iterator) {
/* 242 */     if (null != iterator) {
/* 243 */       while (iterator.hasNext()) {
/* 244 */         append(iterator.next());
/*     */       }
/*     */     }
/* 247 */     return this;
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
/*     */   public <T> StrJoiner append(T[] array, Function<T, ? extends CharSequence> toStrFunc) {
/* 259 */     return append((Iterator<T>)new ArrayIter((Object[])array), toStrFunc);
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
/*     */   public <E> StrJoiner append(Iterable<E> iterable, Function<? super E, ? extends CharSequence> toStrFunc) {
/* 271 */     return append(IterUtil.getIter(iterable), toStrFunc);
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
/*     */   public <E> StrJoiner append(Iterator<E> iterator, Function<? super E, ? extends CharSequence> toStrFunc) {
/* 283 */     if (null != iterator) {
/* 284 */       while (iterator.hasNext()) {
/* 285 */         append(toStrFunc.apply(iterator.next()));
/*     */       }
/*     */     }
/* 288 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public StrJoiner append(CharSequence csq) {
/* 293 */     return append(csq, 0, StrUtil.length(csq));
/*     */   }
/*     */ 
/*     */   
/*     */   public StrJoiner append(CharSequence csq, int startInclude, int endExclude) {
/* 298 */     if (null == csq) {
/* 299 */       switch (this.nullMode) {
/*     */         case IGNORE:
/* 301 */           return this;
/*     */         case TO_EMPTY:
/* 303 */           csq = "";
/*     */           break;
/*     */         case NULL_STRING:
/* 306 */           csq = "null";
/* 307 */           endExclude = "null".length();
/*     */           break;
/*     */       } 
/*     */     }
/*     */     try {
/* 312 */       Appendable appendable = prepare();
/* 313 */       if (this.wrapElement && StrUtil.isNotEmpty(this.prefix)) {
/* 314 */         appendable.append(this.prefix);
/*     */       }
/* 316 */       appendable.append(csq, startInclude, endExclude);
/* 317 */       if (this.wrapElement && StrUtil.isNotEmpty(this.suffix)) {
/* 318 */         appendable.append(this.suffix);
/*     */       }
/* 320 */     } catch (IOException e) {
/* 321 */       throw new IORuntimeException(e);
/*     */     } 
/* 323 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public StrJoiner append(char c) {
/* 328 */     return append(String.valueOf(c));
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
/*     */   public StrJoiner merge(StrJoiner strJoiner) {
/* 340 */     if (null != strJoiner && null != strJoiner.appendable) {
/* 341 */       String otherStr = strJoiner.toString();
/* 342 */       if (strJoiner.wrapElement) {
/* 343 */         append(otherStr);
/*     */       } else {
/* 345 */         append(otherStr, this.prefix.length(), otherStr.length());
/*     */       } 
/*     */     } 
/* 348 */     return this;
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
/*     */   public int length() {
/* 360 */     return (this.appendable != null) ? (this.appendable.toString().length() + this.suffix.length()) : ((null == this.emptyResult) ? -1 : this.emptyResult
/* 361 */       .length());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 366 */     if (null == this.appendable) {
/* 367 */       return this.emptyResult;
/*     */     }
/*     */     
/* 370 */     String result = this.appendable.toString();
/* 371 */     if (false == this.wrapElement && StrUtil.isNotEmpty(this.suffix)) {
/* 372 */       result = result + this.suffix;
/*     */     }
/* 374 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum NullMode
/*     */   {
/* 384 */     IGNORE,
/*     */ 
/*     */ 
/*     */     
/* 388 */     TO_EMPTY,
/*     */ 
/*     */ 
/*     */     
/* 392 */     NULL_STRING;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Appendable prepare() throws IOException {
/* 402 */     if (this.hasContent) {
/* 403 */       this.appendable.append(this.delimiter);
/*     */     } else {
/* 405 */       if (null == this.appendable) {
/* 406 */         this.appendable = new StringBuilder();
/*     */       }
/* 408 */       if (false == this.wrapElement && StrUtil.isNotEmpty(this.prefix)) {
/* 409 */         this.appendable.append(this.prefix);
/*     */       }
/* 411 */       this.hasContent = true;
/*     */     } 
/* 413 */     return this.appendable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkHasContent(Appendable appendable) {
/* 422 */     if (appendable instanceof CharSequence) {
/* 423 */       CharSequence charSequence = (CharSequence)appendable;
/* 424 */       if (charSequence.length() > 0 && StrUtil.endWith(charSequence, this.delimiter)) {
/* 425 */         this.hasContent = true;
/*     */       }
/*     */     } else {
/* 428 */       String initStr = appendable.toString();
/* 429 */       if (StrUtil.isNotEmpty(initStr) && false == StrUtil.endWith(initStr, this.delimiter))
/* 430 */         this.hasContent = true; 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\StrJoiner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */