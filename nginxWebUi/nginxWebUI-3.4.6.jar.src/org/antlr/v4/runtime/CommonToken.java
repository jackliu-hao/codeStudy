/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.antlr.v4.runtime.misc.Interval;
/*     */ import org.antlr.v4.runtime.misc.Pair;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CommonToken
/*     */   implements WritableToken, Serializable
/*     */ {
/*  42 */   protected static final Pair<TokenSource, CharStream> EMPTY_SOURCE = new Pair<TokenSource, CharStream>(null, null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int line;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   protected int charPositionInLine = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   protected int channel = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Pair<TokenSource, CharStream> source;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String text;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   protected int index = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int start;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int stop;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommonToken(int type) {
/* 112 */     this.type = type;
/* 113 */     this.source = EMPTY_SOURCE;
/*     */   }
/*     */   
/*     */   public CommonToken(Pair<TokenSource, CharStream> source, int type, int channel, int start, int stop) {
/* 117 */     this.source = source;
/* 118 */     this.type = type;
/* 119 */     this.channel = channel;
/* 120 */     this.start = start;
/* 121 */     this.stop = stop;
/* 122 */     if (source.a != null) {
/* 123 */       this.line = ((TokenSource)source.a).getLine();
/* 124 */       this.charPositionInLine = ((TokenSource)source.a).getCharPositionInLine();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommonToken(int type, String text) {
/* 136 */     this.type = type;
/* 137 */     this.channel = 0;
/* 138 */     this.text = text;
/* 139 */     this.source = EMPTY_SOURCE;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommonToken(Token oldToken) {
/* 156 */     this.type = oldToken.getType();
/* 157 */     this.line = oldToken.getLine();
/* 158 */     this.index = oldToken.getTokenIndex();
/* 159 */     this.charPositionInLine = oldToken.getCharPositionInLine();
/* 160 */     this.channel = oldToken.getChannel();
/* 161 */     this.start = oldToken.getStartIndex();
/* 162 */     this.stop = oldToken.getStopIndex();
/*     */     
/* 164 */     if (oldToken instanceof CommonToken) {
/* 165 */       this.text = ((CommonToken)oldToken).text;
/* 166 */       this.source = ((CommonToken)oldToken).source;
/*     */     } else {
/*     */       
/* 169 */       this.text = oldToken.getText();
/* 170 */       this.source = new Pair<TokenSource, CharStream>(oldToken.getTokenSource(), oldToken.getInputStream());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 176 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLine(int line) {
/* 181 */     this.line = line;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getText() {
/* 186 */     if (this.text != null) {
/* 187 */       return this.text;
/*     */     }
/*     */     
/* 190 */     CharStream input = getInputStream();
/* 191 */     if (input == null) return null; 
/* 192 */     int n = input.size();
/* 193 */     if (this.start < n && this.stop < n) {
/* 194 */       return input.getText(Interval.of(this.start, this.stop));
/*     */     }
/*     */     
/* 197 */     return "<EOF>";
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
/*     */ 
/*     */   
/*     */   public void setText(String text) {
/* 212 */     this.text = text;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLine() {
/* 217 */     return this.line;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCharPositionInLine() {
/* 222 */     return this.charPositionInLine;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharPositionInLine(int charPositionInLine) {
/* 227 */     this.charPositionInLine = charPositionInLine;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChannel() {
/* 232 */     return this.channel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setChannel(int channel) {
/* 237 */     this.channel = channel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setType(int type) {
/* 242 */     this.type = type;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStartIndex() {
/* 247 */     return this.start;
/*     */   }
/*     */   
/*     */   public void setStartIndex(int start) {
/* 251 */     this.start = start;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStopIndex() {
/* 256 */     return this.stop;
/*     */   }
/*     */   
/*     */   public void setStopIndex(int stop) {
/* 260 */     this.stop = stop;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTokenIndex() {
/* 265 */     return this.index;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTokenIndex(int index) {
/* 270 */     this.index = index;
/*     */   }
/*     */ 
/*     */   
/*     */   public TokenSource getTokenSource() {
/* 275 */     return (TokenSource)this.source.a;
/*     */   }
/*     */ 
/*     */   
/*     */   public CharStream getInputStream() {
/* 280 */     return (CharStream)this.source.b;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 285 */     String channelStr = "";
/* 286 */     if (this.channel > 0) {
/* 287 */       channelStr = ",channel=" + this.channel;
/*     */     }
/* 289 */     String txt = getText();
/* 290 */     if (txt != null) {
/* 291 */       txt = txt.replace("\n", "\\n");
/* 292 */       txt = txt.replace("\r", "\\r");
/* 293 */       txt = txt.replace("\t", "\\t");
/*     */     } else {
/*     */       
/* 296 */       txt = "<no text>";
/*     */     } 
/* 298 */     return "[@" + getTokenIndex() + "," + this.start + ":" + this.stop + "='" + txt + "',<" + this.type + ">" + channelStr + "," + this.line + ":" + getCharPositionInLine() + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\CommonToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */