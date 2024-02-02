package com.mysql.cj.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum SearchMode {
   ALLOW_BACKSLASH_ESCAPE,
   SKIP_BETWEEN_MARKERS,
   SKIP_BLOCK_COMMENTS,
   SKIP_LINE_COMMENTS,
   SKIP_MYSQL_MARKERS,
   SKIP_HINT_BLOCKS,
   SKIP_WHITE_SPACE,
   VOID;

   public static final Set<SearchMode> __FULL = Collections.unmodifiableSet(EnumSet.allOf(SearchMode.class));
   public static final Set<SearchMode> __BSE_MRK_COM_MYM_HNT_WS = Collections.unmodifiableSet(EnumSet.of(ALLOW_BACKSLASH_ESCAPE, SKIP_BETWEEN_MARKERS, SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE));
   public static final Set<SearchMode> __MRK_COM_MYM_HNT_WS = Collections.unmodifiableSet(EnumSet.of(SKIP_BETWEEN_MARKERS, SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE));
   public static final Set<SearchMode> __BSE_COM_MYM_HNT_WS = Collections.unmodifiableSet(EnumSet.of(ALLOW_BACKSLASH_ESCAPE, SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE));
   public static final Set<SearchMode> __COM_MYM_HNT_WS = Collections.unmodifiableSet(EnumSet.of(SKIP_BLOCK_COMMENTS, SKIP_LINE_COMMENTS, SKIP_MYSQL_MARKERS, SKIP_HINT_BLOCKS, SKIP_WHITE_SPACE));
   public static final Set<SearchMode> __BSE_MRK_WS = Collections.unmodifiableSet(EnumSet.of(ALLOW_BACKSLASH_ESCAPE, SKIP_BETWEEN_MARKERS, SKIP_WHITE_SPACE));
   public static final Set<SearchMode> __MRK_WS = Collections.unmodifiableSet(EnumSet.of(SKIP_BETWEEN_MARKERS, SKIP_WHITE_SPACE));
   public static final Set<SearchMode> __NONE = Collections.unmodifiableSet(EnumSet.of(VOID));
}
