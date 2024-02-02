/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import io.undertow.util.HttpString;
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
/*     */ class ParseState
/*     */ {
/*     */   public static final int VERB = 0;
/*     */   public static final int PATH = 1;
/*     */   public static final int PATH_PARAMETERS = 2;
/*     */   public static final int QUERY_PARAMETERS = 3;
/*     */   public static final int VERSION = 4;
/*     */   public static final int AFTER_VERSION = 5;
/*     */   public static final int HEADER = 6;
/*     */   public static final int HEADER_VALUE = 7;
/*     */   public static final int PARSE_COMPLETE = 8;
/*     */   int state;
/*     */   int parseState;
/*     */   HttpString current;
/*     */   byte[] currentBytes;
/*     */   int pos;
/*     */   boolean urlDecodeRequired = false;
/*  82 */   final StringBuilder stringBuilder = new StringBuilder();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  87 */   final StringBuilder canonicalPath = new StringBuilder();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   byte leftOver;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   HttpString nextHeader;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String nextQueryParam;
/*     */ 
/*     */ 
/*     */   
/*     */   int mapCount;
/*     */ 
/*     */ 
/*     */   
/* 110 */   final StringBuilder decodeBuffer = new StringBuilder();
/*     */ 
/*     */ 
/*     */   
/*     */   final CacheMap<HttpString, String> headerValuesCache;
/*     */ 
/*     */ 
/*     */   
/*     */   ParseState(int cacheSize) {
/* 119 */     this.parseState = 0;
/* 120 */     this.pos = 0;
/* 121 */     if (cacheSize <= 0) {
/* 122 */       this.headerValuesCache = null;
/*     */     } else {
/* 124 */       this.headerValuesCache = new CacheMap<>(cacheSize);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isComplete() {
/* 129 */     return (this.state == 8);
/*     */   }
/*     */   
/*     */   public final void parseComplete() {
/* 133 */     this.state = 8;
/*     */   }
/*     */   
/*     */   public void reset() {
/* 137 */     this.state = 0;
/* 138 */     this.parseState = 0;
/* 139 */     this.current = null;
/* 140 */     this.currentBytes = null;
/* 141 */     this.pos = 0;
/* 142 */     this.leftOver = 0;
/* 143 */     this.urlDecodeRequired = false;
/* 144 */     this.stringBuilder.setLength(0);
/* 145 */     this.nextHeader = null;
/* 146 */     this.nextQueryParam = null;
/* 147 */     this.mapCount = 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\ParseState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */