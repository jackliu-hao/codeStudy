/*     */ package com.mysql.cj.util;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Set;
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
/*     */ public enum SearchMode
/*     */ {
/*  43 */   ALLOW_BACKSLASH_ESCAPE,
/*     */ 
/*     */ 
/*     */   
/*  47 */   SKIP_BETWEEN_MARKERS,
/*     */ 
/*     */ 
/*     */   
/*  51 */   SKIP_BLOCK_COMMENTS,
/*     */ 
/*     */ 
/*     */   
/*  55 */   SKIP_LINE_COMMENTS,
/*     */ 
/*     */ 
/*     */   
/*  59 */   SKIP_MYSQL_MARKERS,
/*     */ 
/*     */ 
/*     */   
/*  63 */   SKIP_HINT_BLOCKS,
/*     */ 
/*     */ 
/*     */   
/*  67 */   SKIP_WHITE_SPACE,
/*     */ 
/*     */ 
/*     */   
/*  71 */   VOID;
/*     */   public static final Set<SearchMode> __FULL;
/*     */   public static final Set<SearchMode> __BSE_MRK_COM_MYM_HNT_WS;
/*     */   public static final Set<SearchMode> __MRK_COM_MYM_HNT_WS;
/*     */   public static final Set<SearchMode> __BSE_COM_MYM_HNT_WS;
/*     */   public static final Set<SearchMode> __COM_MYM_HNT_WS;
/*     */   public static final Set<SearchMode> __BSE_MRK_WS;
/*     */   public static final Set<SearchMode> __MRK_WS;
/*     */   public static final Set<SearchMode> __NONE;
/*     */   
/*     */   static {
/*  82 */     __FULL = Collections.unmodifiableSet(EnumSet.allOf(SearchMode.class));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  88 */     __BSE_MRK_COM_MYM_HNT_WS = Collections.unmodifiableSet(EnumSet.of(ALLOW_BACKSLASH_ESCAPE, new SearchMode[] { SKIP_BETWEEN_MARKERS, SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  95 */     __MRK_COM_MYM_HNT_WS = Collections.unmodifiableSet(EnumSet.of(SKIP_BETWEEN_MARKERS, new SearchMode[] { SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE }));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 100 */     __BSE_COM_MYM_HNT_WS = Collections.unmodifiableSet(
/* 101 */         EnumSet.of(ALLOW_BACKSLASH_ESCAPE, new SearchMode[] { SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 107 */     __COM_MYM_HNT_WS = Collections.unmodifiableSet(EnumSet.of(SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 112 */     __BSE_MRK_WS = Collections.unmodifiableSet(EnumSet.of(ALLOW_BACKSLASH_ESCAPE, SKIP_BETWEEN_MARKERS, SKIP_WHITE_SPACE));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 117 */     __MRK_WS = Collections.unmodifiableSet(EnumSet.of(SKIP_BETWEEN_MARKERS, SKIP_WHITE_SPACE));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 123 */     __NONE = Collections.unmodifiableSet(EnumSet.of(VOID));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\SearchMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */