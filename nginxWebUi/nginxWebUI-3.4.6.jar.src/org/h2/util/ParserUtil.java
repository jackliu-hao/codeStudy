/*     */ package org.h2.util;
/*     */ 
/*     */ import java.util.HashMap;
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
/*     */ public class ParserUtil
/*     */ {
/*     */   public static final int KEYWORD = 1;
/*     */   public static final int IDENTIFIER = 2;
/*     */   public static final int ALL = 3;
/*     */   public static final int AND = 4;
/*     */   public static final int ANY = 5;
/*     */   public static final int ARRAY = 6;
/*     */   public static final int AS = 7;
/*     */   public static final int ASYMMETRIC = 8;
/*     */   public static final int AUTHORIZATION = 9;
/*     */   public static final int BETWEEN = 10;
/*     */   public static final int CASE = 11;
/*     */   public static final int CAST = 12;
/*     */   public static final int CHECK = 13;
/*     */   public static final int CONSTRAINT = 14;
/*     */   public static final int CROSS = 15;
/*     */   public static final int CURRENT_CATALOG = 16;
/*     */   public static final int CURRENT_DATE = 17;
/*     */   public static final int CURRENT_PATH = 18;
/*     */   public static final int CURRENT_ROLE = 19;
/*     */   public static final int CURRENT_SCHEMA = 20;
/*     */   public static final int CURRENT_TIME = 21;
/*     */   public static final int CURRENT_TIMESTAMP = 22;
/*     */   public static final int CURRENT_USER = 23;
/*     */   public static final int DAY = 24;
/*     */   public static final int DEFAULT = 25;
/*     */   public static final int DISTINCT = 26;
/*     */   public static final int ELSE = 27;
/*     */   public static final int END = 28;
/*     */   public static final int EXCEPT = 29;
/*     */   public static final int EXISTS = 30;
/*     */   public static final int FALSE = 31;
/*     */   public static final int FETCH = 32;
/*     */   public static final int FOR = 33;
/*     */   public static final int FOREIGN = 34;
/*     */   public static final int FROM = 35;
/*     */   public static final int FULL = 36;
/*     */   public static final int GROUP = 37;
/*     */   public static final int HAVING = 38;
/*     */   public static final int HOUR = 39;
/*     */   public static final int IF = 40;
/*     */   public static final int IN = 41;
/*     */   public static final int INNER = 42;
/*     */   public static final int INTERSECT = 43;
/*     */   public static final int INTERVAL = 44;
/*     */   public static final int IS = 45;
/*     */   public static final int JOIN = 46;
/*     */   public static final int KEY = 47;
/*     */   public static final int LEFT = 48;
/*     */   public static final int LIKE = 49;
/*     */   public static final int LIMIT = 50;
/*     */   public static final int LOCALTIME = 51;
/*     */   public static final int LOCALTIMESTAMP = 52;
/*     */   public static final int MINUS = 53;
/*     */   public static final int MINUTE = 54;
/*     */   public static final int MONTH = 55;
/*     */   public static final int NATURAL = 56;
/*     */   public static final int NOT = 57;
/*     */   public static final int NULL = 58;
/*     */   public static final int OFFSET = 59;
/*     */   public static final int ON = 60;
/*     */   public static final int OR = 61;
/*     */   public static final int ORDER = 62;
/*     */   public static final int PRIMARY = 63;
/*     */   public static final int QUALIFY = 64;
/*     */   public static final int RIGHT = 65;
/*     */   public static final int ROW = 66;
/*     */   public static final int ROWNUM = 67;
/*     */   public static final int SECOND = 68;
/*     */   public static final int SELECT = 69;
/*     */   public static final int SESSION_USER = 70;
/*     */   public static final int SET = 71;
/*     */   public static final int SOME = 72;
/*     */   public static final int SYMMETRIC = 73;
/*     */   public static final int SYSTEM_USER = 74;
/*     */   public static final int TABLE = 75;
/*     */   public static final int TO = 76;
/*     */   public static final int TRUE = 77;
/*     */   public static final int UESCAPE = 78;
/*     */   public static final int UNION = 79;
/*     */   public static final int UNIQUE = 80;
/*     */   public static final int UNKNOWN = 81;
/*     */   public static final int USER = 82;
/*     */   public static final int USING = 83;
/*     */   public static final int VALUE = 84;
/*     */   public static final int VALUES = 85;
/*     */   public static final int WHEN = 86;
/*     */   public static final int WHERE = 87;
/*     */   public static final int WINDOW = 88;
/*     */   public static final int WITH = 89;
/*     */   public static final int YEAR = 90;
/*     */   public static final int _ROWID_ = 91;
/*     */   public static final int FIRST_KEYWORD = 3;
/*     */   public static final int LAST_KEYWORD = 91;
/*     */   private static final HashMap<String, Integer> KEYWORDS;
/*     */   
/*     */   static {
/* 484 */     HashMap<Object, Object> hashMap = new HashMap<>(256);
/* 485 */     hashMap.put("ALL", Integer.valueOf(3));
/* 486 */     hashMap.put("AND", Integer.valueOf(4));
/* 487 */     hashMap.put("ANY", Integer.valueOf(5));
/* 488 */     hashMap.put("ARRAY", Integer.valueOf(6));
/* 489 */     hashMap.put("AS", Integer.valueOf(7));
/* 490 */     hashMap.put("ASYMMETRIC", Integer.valueOf(8));
/* 491 */     hashMap.put("AUTHORIZATION", Integer.valueOf(9));
/* 492 */     hashMap.put("BETWEEN", Integer.valueOf(10));
/* 493 */     hashMap.put("CASE", Integer.valueOf(11));
/* 494 */     hashMap.put("CAST", Integer.valueOf(12));
/* 495 */     hashMap.put("CHECK", Integer.valueOf(13));
/* 496 */     hashMap.put("CONSTRAINT", Integer.valueOf(14));
/* 497 */     hashMap.put("CROSS", Integer.valueOf(15));
/* 498 */     hashMap.put("CURRENT_CATALOG", Integer.valueOf(16));
/* 499 */     hashMap.put("CURRENT_DATE", Integer.valueOf(17));
/* 500 */     hashMap.put("CURRENT_PATH", Integer.valueOf(18));
/* 501 */     hashMap.put("CURRENT_ROLE", Integer.valueOf(19));
/* 502 */     hashMap.put("CURRENT_SCHEMA", Integer.valueOf(20));
/* 503 */     hashMap.put("CURRENT_TIME", Integer.valueOf(21));
/* 504 */     hashMap.put("CURRENT_TIMESTAMP", Integer.valueOf(22));
/* 505 */     hashMap.put("CURRENT_USER", Integer.valueOf(23));
/* 506 */     hashMap.put("DAY", Integer.valueOf(24));
/* 507 */     hashMap.put("DEFAULT", Integer.valueOf(25));
/* 508 */     hashMap.put("DISTINCT", Integer.valueOf(26));
/* 509 */     hashMap.put("ELSE", Integer.valueOf(27));
/* 510 */     hashMap.put("END", Integer.valueOf(28));
/* 511 */     hashMap.put("EXCEPT", Integer.valueOf(29));
/* 512 */     hashMap.put("EXISTS", Integer.valueOf(30));
/* 513 */     hashMap.put("FALSE", Integer.valueOf(31));
/* 514 */     hashMap.put("FETCH", Integer.valueOf(32));
/* 515 */     hashMap.put("FOR", Integer.valueOf(33));
/* 516 */     hashMap.put("FOREIGN", Integer.valueOf(34));
/* 517 */     hashMap.put("FROM", Integer.valueOf(35));
/* 518 */     hashMap.put("FULL", Integer.valueOf(36));
/* 519 */     hashMap.put("GROUP", Integer.valueOf(37));
/* 520 */     hashMap.put("HAVING", Integer.valueOf(38));
/* 521 */     hashMap.put("HOUR", Integer.valueOf(39));
/* 522 */     hashMap.put("IF", Integer.valueOf(40));
/* 523 */     hashMap.put("IN", Integer.valueOf(41));
/* 524 */     hashMap.put("INNER", Integer.valueOf(42));
/* 525 */     hashMap.put("INTERSECT", Integer.valueOf(43));
/* 526 */     hashMap.put("INTERVAL", Integer.valueOf(44));
/* 527 */     hashMap.put("IS", Integer.valueOf(45));
/* 528 */     hashMap.put("JOIN", Integer.valueOf(46));
/* 529 */     hashMap.put("KEY", Integer.valueOf(47));
/* 530 */     hashMap.put("LEFT", Integer.valueOf(48));
/* 531 */     hashMap.put("LIKE", Integer.valueOf(49));
/* 532 */     hashMap.put("LIMIT", Integer.valueOf(50));
/* 533 */     hashMap.put("LOCALTIME", Integer.valueOf(51));
/* 534 */     hashMap.put("LOCALTIMESTAMP", Integer.valueOf(52));
/* 535 */     hashMap.put("MINUS", Integer.valueOf(53));
/* 536 */     hashMap.put("MINUTE", Integer.valueOf(54));
/* 537 */     hashMap.put("MONTH", Integer.valueOf(55));
/* 538 */     hashMap.put("NATURAL", Integer.valueOf(56));
/* 539 */     hashMap.put("NOT", Integer.valueOf(57));
/* 540 */     hashMap.put("NULL", Integer.valueOf(58));
/* 541 */     hashMap.put("OFFSET", Integer.valueOf(59));
/* 542 */     hashMap.put("ON", Integer.valueOf(60));
/* 543 */     hashMap.put("OR", Integer.valueOf(61));
/* 544 */     hashMap.put("ORDER", Integer.valueOf(62));
/* 545 */     hashMap.put("PRIMARY", Integer.valueOf(63));
/* 546 */     hashMap.put("QUALIFY", Integer.valueOf(64));
/* 547 */     hashMap.put("RIGHT", Integer.valueOf(65));
/* 548 */     hashMap.put("ROW", Integer.valueOf(66));
/* 549 */     hashMap.put("ROWNUM", Integer.valueOf(67));
/* 550 */     hashMap.put("SECOND", Integer.valueOf(68));
/* 551 */     hashMap.put("SELECT", Integer.valueOf(69));
/* 552 */     hashMap.put("SESSION_USER", Integer.valueOf(70));
/* 553 */     hashMap.put("SET", Integer.valueOf(71));
/* 554 */     hashMap.put("SOME", Integer.valueOf(72));
/* 555 */     hashMap.put("SYMMETRIC", Integer.valueOf(73));
/* 556 */     hashMap.put("SYSTEM_USER", Integer.valueOf(74));
/* 557 */     hashMap.put("TABLE", Integer.valueOf(75));
/* 558 */     hashMap.put("TO", Integer.valueOf(76));
/* 559 */     hashMap.put("TRUE", Integer.valueOf(77));
/* 560 */     hashMap.put("UESCAPE", Integer.valueOf(78));
/* 561 */     hashMap.put("UNION", Integer.valueOf(79));
/* 562 */     hashMap.put("UNIQUE", Integer.valueOf(80));
/* 563 */     hashMap.put("UNKNOWN", Integer.valueOf(81));
/* 564 */     hashMap.put("USER", Integer.valueOf(82));
/* 565 */     hashMap.put("USING", Integer.valueOf(83));
/* 566 */     hashMap.put("VALUE", Integer.valueOf(84));
/* 567 */     hashMap.put("VALUES", Integer.valueOf(85));
/* 568 */     hashMap.put("WHEN", Integer.valueOf(86));
/* 569 */     hashMap.put("WHERE", Integer.valueOf(87));
/* 570 */     hashMap.put("WINDOW", Integer.valueOf(88));
/* 571 */     hashMap.put("WITH", Integer.valueOf(89));
/* 572 */     hashMap.put("YEAR", Integer.valueOf(90));
/* 573 */     hashMap.put("_ROWID_", Integer.valueOf(91));
/*     */     
/* 575 */     hashMap.put("BOTH", Integer.valueOf(1));
/* 576 */     hashMap.put("FILTER", Integer.valueOf(1));
/* 577 */     hashMap.put("GROUPS", Integer.valueOf(1));
/* 578 */     hashMap.put("ILIKE", Integer.valueOf(1));
/* 579 */     hashMap.put("LEADING", Integer.valueOf(1));
/* 580 */     hashMap.put("OVER", Integer.valueOf(1));
/* 581 */     hashMap.put("PARTITION", Integer.valueOf(1));
/* 582 */     hashMap.put("RANGE", Integer.valueOf(1));
/* 583 */     hashMap.put("REGEXP", Integer.valueOf(1));
/* 584 */     hashMap.put("ROWS", Integer.valueOf(1));
/* 585 */     hashMap.put("TOP", Integer.valueOf(1));
/* 586 */     hashMap.put("TRAILING", Integer.valueOf(1));
/* 587 */     KEYWORDS = (HashMap)hashMap;
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
/*     */   public static StringBuilder quoteIdentifier(StringBuilder paramStringBuilder, String paramString, int paramInt) {
/* 604 */     if (paramString == null) {
/* 605 */       return paramStringBuilder.append("\"\"");
/*     */     }
/* 607 */     if ((paramInt & 0x1) != 0 && isSimpleIdentifier(paramString, false, false)) {
/* 608 */       return paramStringBuilder.append(paramString);
/*     */     }
/* 610 */     return StringUtils.quoteIdentifier(paramStringBuilder, paramString);
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
/*     */   public static boolean isKeyword(String paramString, boolean paramBoolean) {
/* 622 */     return (getTokenType(paramString, paramBoolean, false) != 2);
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
/*     */   public static boolean isSimpleIdentifier(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
/* 635 */     if (paramBoolean1 && paramBoolean2) {
/* 636 */       throw new IllegalArgumentException("databaseToUpper && databaseToLower");
/*     */     }
/* 638 */     int i = paramString.length();
/* 639 */     if (i == 0 || !checkLetter(paramBoolean1, paramBoolean2, paramString.charAt(0))) {
/* 640 */       return false;
/*     */     }
/* 642 */     for (byte b = 1; b < i; b++) {
/* 643 */       char c = paramString.charAt(b);
/* 644 */       if (c != '_' && (c < '0' || c > '9') && !checkLetter(paramBoolean1, paramBoolean2, c)) {
/* 645 */         return false;
/*     */       }
/*     */     } 
/* 648 */     return (getTokenType(paramString, !paramBoolean1, true) == 2);
/*     */   }
/*     */   
/*     */   private static boolean checkLetter(boolean paramBoolean1, boolean paramBoolean2, char paramChar) {
/* 652 */     if (paramBoolean1) {
/* 653 */       if (paramChar < 'A' || paramChar > 'Z') {
/* 654 */         return false;
/*     */       }
/* 656 */     } else if (paramBoolean2) {
/* 657 */       if (paramChar < 'a' || paramChar > 'z') {
/* 658 */         return false;
/*     */       }
/*     */     }
/* 661 */     else if ((paramChar < 'A' || paramChar > 'Z') && (paramChar < 'a' || paramChar > 'z')) {
/* 662 */       return false;
/*     */     } 
/*     */     
/* 665 */     return true;
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
/*     */   public static int getTokenType(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
/* 680 */     int i = paramString.length();
/* 681 */     if (i <= 1 || i > 17) {
/* 682 */       return 2;
/*     */     }
/* 684 */     if (paramBoolean1) {
/* 685 */       paramString = StringUtils.toUpperEnglish(paramString);
/*     */     }
/* 687 */     Integer integer = KEYWORDS.get(paramString);
/* 688 */     if (integer == null) {
/* 689 */       return 2;
/*     */     }
/* 691 */     int j = integer.intValue();
/* 692 */     return (j == 1 && !paramBoolean2) ? 2 : j;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\ParserUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */