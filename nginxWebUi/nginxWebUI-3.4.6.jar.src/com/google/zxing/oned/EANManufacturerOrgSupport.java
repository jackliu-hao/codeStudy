/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class EANManufacturerOrgSupport
/*     */ {
/*  33 */   private final List<int[]> ranges = (List)new ArrayList<>();
/*  34 */   private final List<String> countryIdentifiers = new ArrayList<>();
/*     */   
/*     */   String lookupCountryIdentifier(String productCode) {
/*  37 */     initIfNeeded();
/*  38 */     int prefix = Integer.parseInt(productCode.substring(0, 3));
/*  39 */     int max = this.ranges.size();
/*  40 */     for (int i = 0; i < max; i++) {
/*     */       
/*  42 */       int range[], start = (range = this.ranges.get(i))[0];
/*  43 */       if (prefix < start) {
/*  44 */         return null;
/*     */       }
/*  46 */       int end = (range.length == 1) ? start : range[1];
/*  47 */       if (prefix <= end) {
/*  48 */         return this.countryIdentifiers.get(i);
/*     */       }
/*     */     } 
/*  51 */     return null;
/*     */   }
/*     */   
/*     */   private void add(int[] range, String id) {
/*  55 */     this.ranges.add(range);
/*  56 */     this.countryIdentifiers.add(id);
/*     */   }
/*     */   
/*     */   private synchronized void initIfNeeded() {
/*  60 */     if (!this.ranges.isEmpty()) {
/*     */       return;
/*     */     }
/*  63 */     add(new int[] { 0, 19 }, "US/CA");
/*  64 */     add(new int[] { 30, 39 }, "US");
/*  65 */     add(new int[] { 60, 139 }, "US/CA");
/*  66 */     add(new int[] { 300, 379 }, "FR");
/*  67 */     add(new int[] { 380 }, "BG");
/*  68 */     add(new int[] { 383 }, "SI");
/*  69 */     add(new int[] { 385 }, "HR");
/*  70 */     add(new int[] { 387 }, "BA");
/*  71 */     add(new int[] { 400, 440 }, "DE");
/*  72 */     add(new int[] { 450, 459 }, "JP");
/*  73 */     add(new int[] { 460, 469 }, "RU");
/*  74 */     add(new int[] { 471 }, "TW");
/*  75 */     add(new int[] { 474 }, "EE");
/*  76 */     add(new int[] { 475 }, "LV");
/*  77 */     add(new int[] { 476 }, "AZ");
/*  78 */     add(new int[] { 477 }, "LT");
/*  79 */     add(new int[] { 478 }, "UZ");
/*  80 */     add(new int[] { 479 }, "LK");
/*  81 */     add(new int[] { 480 }, "PH");
/*  82 */     add(new int[] { 481 }, "BY");
/*  83 */     add(new int[] { 482 }, "UA");
/*  84 */     add(new int[] { 484 }, "MD");
/*  85 */     add(new int[] { 485 }, "AM");
/*  86 */     add(new int[] { 486 }, "GE");
/*  87 */     add(new int[] { 487 }, "KZ");
/*  88 */     add(new int[] { 489 }, "HK");
/*  89 */     add(new int[] { 490, 499 }, "JP");
/*  90 */     add(new int[] { 500, 509 }, "GB");
/*  91 */     add(new int[] { 520 }, "GR");
/*  92 */     add(new int[] { 528 }, "LB");
/*  93 */     add(new int[] { 529 }, "CY");
/*  94 */     add(new int[] { 531 }, "MK");
/*  95 */     add(new int[] { 535 }, "MT");
/*  96 */     add(new int[] { 539 }, "IE");
/*  97 */     add(new int[] { 540, 549 }, "BE/LU");
/*  98 */     add(new int[] { 560 }, "PT");
/*  99 */     add(new int[] { 569 }, "IS");
/* 100 */     add(new int[] { 570, 579 }, "DK");
/* 101 */     add(new int[] { 590 }, "PL");
/* 102 */     add(new int[] { 594 }, "RO");
/* 103 */     add(new int[] { 599 }, "HU");
/* 104 */     add(new int[] { 600, 601 }, "ZA");
/* 105 */     add(new int[] { 603 }, "GH");
/* 106 */     add(new int[] { 608 }, "BH");
/* 107 */     add(new int[] { 609 }, "MU");
/* 108 */     add(new int[] { 611 }, "MA");
/* 109 */     add(new int[] { 613 }, "DZ");
/* 110 */     add(new int[] { 616 }, "KE");
/* 111 */     add(new int[] { 618 }, "CI");
/* 112 */     add(new int[] { 619 }, "TN");
/* 113 */     add(new int[] { 621 }, "SY");
/* 114 */     add(new int[] { 622 }, "EG");
/* 115 */     add(new int[] { 624 }, "LY");
/* 116 */     add(new int[] { 625 }, "JO");
/* 117 */     add(new int[] { 626 }, "IR");
/* 118 */     add(new int[] { 627 }, "KW");
/* 119 */     add(new int[] { 628 }, "SA");
/* 120 */     add(new int[] { 629 }, "AE");
/* 121 */     add(new int[] { 640, 649 }, "FI");
/* 122 */     add(new int[] { 690, 695 }, "CN");
/* 123 */     add(new int[] { 700, 709 }, "NO");
/* 124 */     add(new int[] { 729 }, "IL");
/* 125 */     add(new int[] { 730, 739 }, "SE");
/* 126 */     add(new int[] { 740 }, "GT");
/* 127 */     add(new int[] { 741 }, "SV");
/* 128 */     add(new int[] { 742 }, "HN");
/* 129 */     add(new int[] { 743 }, "NI");
/* 130 */     add(new int[] { 744 }, "CR");
/* 131 */     add(new int[] { 745 }, "PA");
/* 132 */     add(new int[] { 746 }, "DO");
/* 133 */     add(new int[] { 750 }, "MX");
/* 134 */     add(new int[] { 754, 755 }, "CA");
/* 135 */     add(new int[] { 759 }, "VE");
/* 136 */     add(new int[] { 760, 769 }, "CH");
/* 137 */     add(new int[] { 770 }, "CO");
/* 138 */     add(new int[] { 773 }, "UY");
/* 139 */     add(new int[] { 775 }, "PE");
/* 140 */     add(new int[] { 777 }, "BO");
/* 141 */     add(new int[] { 779 }, "AR");
/* 142 */     add(new int[] { 780 }, "CL");
/* 143 */     add(new int[] { 784 }, "PY");
/* 144 */     add(new int[] { 785 }, "PE");
/* 145 */     add(new int[] { 786 }, "EC");
/* 146 */     add(new int[] { 789, 790 }, "BR");
/* 147 */     add(new int[] { 800, 839 }, "IT");
/* 148 */     add(new int[] { 840, 849 }, "ES");
/* 149 */     add(new int[] { 850 }, "CU");
/* 150 */     add(new int[] { 858 }, "SK");
/* 151 */     add(new int[] { 859 }, "CZ");
/* 152 */     add(new int[] { 860 }, "YU");
/* 153 */     add(new int[] { 865 }, "MN");
/* 154 */     add(new int[] { 867 }, "KP");
/* 155 */     add(new int[] { 868, 869 }, "TR");
/* 156 */     add(new int[] { 870, 879 }, "NL");
/* 157 */     add(new int[] { 880 }, "KR");
/* 158 */     add(new int[] { 885 }, "TH");
/* 159 */     add(new int[] { 888 }, "SG");
/* 160 */     add(new int[] { 890 }, "IN");
/* 161 */     add(new int[] { 893 }, "VN");
/* 162 */     add(new int[] { 896 }, "PK");
/* 163 */     add(new int[] { 899 }, "ID");
/* 164 */     add(new int[] { 900, 919 }, "AT");
/* 165 */     add(new int[] { 930, 939 }, "AU");
/* 166 */     add(new int[] { 940, 949 }, "AZ");
/* 167 */     add(new int[] { 955 }, "MY");
/* 168 */     add(new int[] { 958 }, "MO");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\EANManufacturerOrgSupport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */