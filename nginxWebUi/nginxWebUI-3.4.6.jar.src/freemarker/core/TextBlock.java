/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.utility.CollectionUtils;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.IOException;
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
/*     */ public final class TextBlock
/*     */   extends TemplateElement
/*     */ {
/*     */   private char[] text;
/*     */   private final boolean unparsed;
/*     */   
/*     */   public TextBlock(String text) {
/*  43 */     this(text, false);
/*     */   }
/*     */   
/*     */   public TextBlock(String text, boolean unparsed) {
/*  47 */     this(text.toCharArray(), unparsed);
/*     */   }
/*     */   
/*     */   TextBlock(char[] text, boolean unparsed) {
/*  51 */     this.text = text;
/*  52 */     this.unparsed = unparsed;
/*     */   }
/*     */   
/*     */   void replaceText(String text) {
/*  56 */     this.text = text.toCharArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateElement[] accept(Environment env) throws IOException {
/*  67 */     env.getOut().write(this.text);
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  73 */     if (canonical) {
/*  74 */       String text = new String(this.text);
/*  75 */       if (this.unparsed) {
/*  76 */         return "<#noparse>" + text + "</#noparse>";
/*     */       }
/*  78 */       return text;
/*     */     } 
/*  80 */     return "text " + StringUtil.jQuote(new String(this.text));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  86 */     return "#text";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  91 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/*  96 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/*  97 */     return new String(this.text);
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 102 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 103 */     return ParameterRole.CONTENT;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement postParseCleanup(boolean stripWhitespace) {
/* 108 */     if (this.text.length == 0) return this; 
/* 109 */     int openingCharsToStrip = 0, trailingCharsToStrip = 0;
/* 110 */     boolean deliberateLeftTrim = deliberateLeftTrim();
/* 111 */     boolean deliberateRightTrim = deliberateRightTrim();
/* 112 */     if (!stripWhitespace || this.text.length == 0) {
/* 113 */       return this;
/*     */     }
/* 115 */     TemplateElement parentElement = getParentElement();
/* 116 */     if (isTopLevelTextIfParentIs(parentElement) && previousSibling() == null) {
/* 117 */       return this;
/*     */     }
/* 119 */     if (!deliberateLeftTrim) {
/* 120 */       trailingCharsToStrip = trailingCharsToStrip();
/*     */     }
/* 122 */     if (!deliberateRightTrim) {
/* 123 */       openingCharsToStrip = openingCharsToStrip();
/*     */     }
/* 125 */     if (openingCharsToStrip == 0 && trailingCharsToStrip == 0) {
/* 126 */       return this;
/*     */     }
/* 128 */     this.text = substring(this.text, openingCharsToStrip, this.text.length - trailingCharsToStrip);
/* 129 */     if (openingCharsToStrip > 0) {
/* 130 */       this.beginLine++;
/* 131 */       this.beginColumn = 1;
/*     */     } 
/* 133 */     if (trailingCharsToStrip > 0) {
/* 134 */       this.endColumn = 0;
/*     */     }
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean deliberateLeftTrim() {
/* 144 */     boolean result = false;
/* 145 */     TemplateElement elem = nextTerminalNode();
/* 146 */     for (; elem != null && elem.beginLine == this.endLine; 
/* 147 */       elem = elem.nextTerminalNode()) {
/* 148 */       if (elem instanceof TrimInstruction) {
/* 149 */         TrimInstruction ti = (TrimInstruction)elem;
/* 150 */         if (!ti.left && !ti.right) {
/* 151 */           result = true;
/*     */         }
/* 153 */         if (ti.left) {
/* 154 */           result = true;
/* 155 */           int lastNewLineIndex = lastNewLineIndex();
/* 156 */           if (lastNewLineIndex >= 0 || this.beginColumn == 1) {
/* 157 */             char[] firstPart = substring(this.text, 0, lastNewLineIndex + 1);
/* 158 */             char[] lastLine = substring(this.text, 1 + lastNewLineIndex);
/* 159 */             if (StringUtil.isTrimmableToEmpty(lastLine)) {
/* 160 */               this.text = firstPart;
/* 161 */               this.endColumn = 0;
/*     */             } else {
/* 163 */               int i = 0;
/* 164 */               while (Character.isWhitespace(lastLine[i])) {
/* 165 */                 i++;
/*     */               }
/* 167 */               char[] printablePart = substring(lastLine, i);
/* 168 */               this.text = concat(firstPart, printablePart);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 174 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean deliberateRightTrim() {
/* 182 */     boolean result = false;
/* 183 */     TemplateElement elem = prevTerminalNode();
/* 184 */     for (; elem != null && elem.endLine == this.beginLine; 
/* 185 */       elem = elem.prevTerminalNode()) {
/* 186 */       if (elem instanceof TrimInstruction) {
/* 187 */         TrimInstruction ti = (TrimInstruction)elem;
/* 188 */         if (!ti.left && !ti.right) {
/* 189 */           result = true;
/*     */         }
/* 191 */         if (ti.right) {
/* 192 */           result = true;
/* 193 */           int firstLineIndex = firstNewLineIndex() + 1;
/* 194 */           if (firstLineIndex == 0) {
/* 195 */             return false;
/*     */           }
/* 197 */           if (this.text.length > firstLineIndex && this.text[firstLineIndex - 1] == '\r' && this.text[firstLineIndex] == '\n')
/*     */           {
/*     */             
/* 200 */             firstLineIndex++;
/*     */           }
/* 202 */           char[] trailingPart = substring(this.text, firstLineIndex);
/* 203 */           char[] openingPart = substring(this.text, 0, firstLineIndex);
/* 204 */           if (StringUtil.isTrimmableToEmpty(openingPart)) {
/* 205 */             this.text = trailingPart;
/* 206 */             this.beginLine++;
/* 207 */             this.beginColumn = 1;
/*     */           } else {
/* 209 */             int lastNonWS = openingPart.length - 1;
/* 210 */             while (Character.isWhitespace(this.text[lastNonWS])) {
/* 211 */               lastNonWS--;
/*     */             }
/* 213 */             char[] printablePart = substring(this.text, 0, lastNonWS + 1);
/* 214 */             if (StringUtil.isTrimmableToEmpty(trailingPart)) {
/*     */               
/* 216 */               boolean trimTrailingPart = true;
/* 217 */               TemplateElement te = nextTerminalNode();
/* 218 */               for (; te != null && te.beginLine == this.endLine; 
/* 219 */                 te = te.nextTerminalNode()) {
/* 220 */                 if (te.heedsOpeningWhitespace()) {
/* 221 */                   trimTrailingPart = false;
/*     */                 }
/* 223 */                 if (te instanceof TrimInstruction && ((TrimInstruction)te).left) {
/* 224 */                   trimTrailingPart = true;
/*     */                   break;
/*     */                 } 
/*     */               } 
/* 228 */               if (trimTrailingPart) trailingPart = CollectionUtils.EMPTY_CHAR_ARRAY; 
/*     */             } 
/* 230 */             this.text = concat(printablePart, trailingPart);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 235 */     return result;
/*     */   }
/*     */   
/*     */   private int firstNewLineIndex() {
/* 239 */     char[] text = this.text;
/* 240 */     for (int i = 0; i < text.length; i++) {
/* 241 */       char c = text[i];
/* 242 */       if (c == '\r' || c == '\n') {
/* 243 */         return i;
/*     */       }
/*     */     } 
/* 246 */     return -1;
/*     */   }
/*     */   
/*     */   private int lastNewLineIndex() {
/* 250 */     char[] text = this.text;
/* 251 */     for (int i = text.length - 1; i >= 0; i--) {
/* 252 */       char c = text[i];
/* 253 */       if (c == '\r' || c == '\n') {
/* 254 */         return i;
/*     */       }
/*     */     } 
/* 257 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int openingCharsToStrip() {
/* 265 */     int newlineIndex = firstNewLineIndex();
/* 266 */     if (newlineIndex == -1 && this.beginColumn != 1) {
/* 267 */       return 0;
/*     */     }
/* 269 */     newlineIndex++;
/* 270 */     if (this.text.length > newlineIndex && 
/* 271 */       newlineIndex > 0 && this.text[newlineIndex - 1] == '\r' && this.text[newlineIndex] == '\n') {
/* 272 */       newlineIndex++;
/*     */     }
/*     */     
/* 275 */     if (!StringUtil.isTrimmableToEmpty(this.text, 0, newlineIndex)) {
/* 276 */       return 0;
/*     */     }
/*     */ 
/*     */     
/* 280 */     TemplateElement elem = prevTerminalNode();
/* 281 */     for (; elem != null && elem.endLine == this.beginLine; 
/* 282 */       elem = elem.prevTerminalNode()) {
/* 283 */       if (elem.heedsOpeningWhitespace()) {
/* 284 */         return 0;
/*     */       }
/*     */     } 
/* 287 */     return newlineIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int trailingCharsToStrip() {
/* 295 */     int lastNewlineIndex = lastNewLineIndex();
/* 296 */     if (lastNewlineIndex == -1 && this.beginColumn != 1) {
/* 297 */       return 0;
/*     */     }
/* 299 */     if (!StringUtil.isTrimmableToEmpty(this.text, lastNewlineIndex + 1)) {
/* 300 */       return 0;
/*     */     }
/*     */ 
/*     */     
/* 304 */     TemplateElement elem = nextTerminalNode();
/* 305 */     for (; elem != null && elem.beginLine == this.endLine; 
/* 306 */       elem = elem.nextTerminalNode()) {
/* 307 */       if (elem.heedsTrailingWhitespace()) {
/* 308 */         return 0;
/*     */       }
/*     */     } 
/* 311 */     return this.text.length - lastNewlineIndex + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean heedsTrailingWhitespace() {
/* 316 */     if (isIgnorable(true)) {
/* 317 */       return false;
/*     */     }
/* 319 */     for (int i = 0; i < this.text.length; i++) {
/* 320 */       char c = this.text[i];
/* 321 */       if (c == '\n' || c == '\r') {
/* 322 */         return false;
/*     */       }
/* 324 */       if (!Character.isWhitespace(c)) {
/* 325 */         return true;
/*     */       }
/*     */     } 
/* 328 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean heedsOpeningWhitespace() {
/* 333 */     if (isIgnorable(true)) {
/* 334 */       return false;
/*     */     }
/* 336 */     for (int i = this.text.length - 1; i >= 0; i--) {
/* 337 */       char c = this.text[i];
/* 338 */       if (c == '\n' || c == '\r') {
/* 339 */         return false;
/*     */       }
/* 341 */       if (!Character.isWhitespace(c)) {
/* 342 */         return true;
/*     */       }
/*     */     } 
/* 345 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isIgnorable(boolean stripWhitespace) {
/* 350 */     if (this.text == null || this.text.length == 0) {
/* 351 */       return true;
/*     */     }
/* 353 */     if (stripWhitespace) {
/* 354 */       if (!StringUtil.isTrimmableToEmpty(this.text)) {
/* 355 */         return false;
/*     */       }
/* 357 */       TemplateElement parentElement = getParentElement();
/* 358 */       boolean atTopLevel = isTopLevelTextIfParentIs(parentElement);
/* 359 */       TemplateElement prevSibling = previousSibling();
/* 360 */       TemplateElement nextSibling = nextSibling();
/* 361 */       return (((prevSibling == null && atTopLevel) || nonOutputtingType(prevSibling)) && ((nextSibling == null && atTopLevel) || 
/* 362 */         nonOutputtingType(nextSibling)));
/*     */     } 
/* 364 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isTopLevelTextIfParentIs(TemplateElement parentElement) {
/* 369 */     return (parentElement == null || (parentElement
/* 370 */       .getParentElement() == null && parentElement instanceof MixedContent));
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean nonOutputtingType(TemplateElement element) {
/* 375 */     return (element instanceof Macro || element instanceof Assignment || element instanceof AssignmentInstruction || element instanceof PropertySetting || element instanceof LibraryLoad || element instanceof Comment);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static char[] substring(char[] c, int from, int to) {
/* 384 */     char[] c2 = new char[to - from];
/* 385 */     System.arraycopy(c, from, c2, 0, c2.length);
/* 386 */     return c2;
/*     */   }
/*     */   
/*     */   private static char[] substring(char[] c, int from) {
/* 390 */     return substring(c, from, c.length);
/*     */   }
/*     */   
/*     */   private static char[] concat(char[] c1, char[] c2) {
/* 394 */     char[] c = new char[c1.length + c2.length];
/* 395 */     System.arraycopy(c1, 0, c, 0, c1.length);
/* 396 */     System.arraycopy(c2, 0, c, c1.length, c2.length);
/* 397 */     return c;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isOutputCacheable() {
/* 402 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 407 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TextBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */