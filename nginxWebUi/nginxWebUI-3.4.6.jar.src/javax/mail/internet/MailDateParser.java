/*     */ package javax.mail.internet;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class MailDateParser
/*     */ {
/* 465 */   int index = 0;
/* 466 */   char[] orig = null;
/*     */   
/*     */   public MailDateParser(char[] orig, int index) {
/* 469 */     this.orig = orig;
/* 470 */     this.index = index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void skipUntilNumber() throws ParseException {
/*     */     try {
/*     */       while (true) {
/* 482 */         switch (this.orig[this.index]) {
/*     */           case '0':
/*     */           case '1':
/*     */           case '2':
/*     */           case '3':
/*     */           case '4':
/*     */           case '5':
/*     */           case '6':
/*     */           case '7':
/*     */           case '8':
/*     */           case '9':
/*     */             return;
/*     */         } 
/*     */         
/* 496 */         this.index++;
/*     */       }
/*     */     
/*     */     }
/* 500 */     catch (ArrayIndexOutOfBoundsException e) {
/* 501 */       throw new ParseException("No Number Found", this.index);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void skipWhiteSpace() {
/* 509 */     int len = this.orig.length;
/* 510 */     while (this.index < len) {
/* 511 */       switch (this.orig[this.index]) {
/*     */         case '\t':
/*     */         case '\n':
/*     */         case '\r':
/*     */         case ' ':
/* 516 */           this.index++;
/*     */           continue;
/*     */       } 
/*     */       return;
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
/*     */   public int peekChar() throws ParseException {
/* 531 */     if (this.index < this.orig.length) {
/* 532 */       return this.orig[this.index];
/*     */     }
/* 534 */     throw new ParseException("No more characters", this.index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void skipChar(char c) throws ParseException {
/* 542 */     if (this.index < this.orig.length) {
/* 543 */       if (this.orig[this.index] == c) {
/* 544 */         this.index++;
/*     */       } else {
/* 546 */         throw new ParseException("Wrong char", this.index);
/*     */       } 
/*     */     } else {
/* 549 */       throw new ParseException("No more characters", this.index);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean skipIfChar(char c) throws ParseException {
/* 558 */     if (this.index < this.orig.length) {
/* 559 */       if (this.orig[this.index] == c) {
/* 560 */         this.index++;
/* 561 */         return true;
/*     */       } 
/* 563 */       return false;
/*     */     } 
/*     */     
/* 566 */     throw new ParseException("No more characters", this.index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int parseNumber() throws ParseException {
/* 577 */     int length = this.orig.length;
/* 578 */     boolean gotNum = false;
/* 579 */     int result = 0;
/*     */     
/* 581 */     while (this.index < length) {
/* 582 */       switch (this.orig[this.index]) {
/*     */         case '0':
/* 584 */           result *= 10;
/* 585 */           gotNum = true;
/*     */           break;
/*     */         
/*     */         case '1':
/* 589 */           result = result * 10 + 1;
/* 590 */           gotNum = true;
/*     */           break;
/*     */         
/*     */         case '2':
/* 594 */           result = result * 10 + 2;
/* 595 */           gotNum = true;
/*     */           break;
/*     */         
/*     */         case '3':
/* 599 */           result = result * 10 + 3;
/* 600 */           gotNum = true;
/*     */           break;
/*     */         
/*     */         case '4':
/* 604 */           result = result * 10 + 4;
/* 605 */           gotNum = true;
/*     */           break;
/*     */         
/*     */         case '5':
/* 609 */           result = result * 10 + 5;
/* 610 */           gotNum = true;
/*     */           break;
/*     */         
/*     */         case '6':
/* 614 */           result = result * 10 + 6;
/* 615 */           gotNum = true;
/*     */           break;
/*     */         
/*     */         case '7':
/* 619 */           result = result * 10 + 7;
/* 620 */           gotNum = true;
/*     */           break;
/*     */         
/*     */         case '8':
/* 624 */           result = result * 10 + 8;
/* 625 */           gotNum = true;
/*     */           break;
/*     */         
/*     */         case '9':
/* 629 */           result = result * 10 + 9;
/* 630 */           gotNum = true;
/*     */           break;
/*     */         
/*     */         default:
/* 634 */           if (gotNum) {
/* 635 */             return result;
/*     */           }
/* 637 */           throw new ParseException("No Number found", this.index);
/*     */       } 
/*     */       
/* 640 */       this.index++;
/*     */     } 
/*     */ 
/*     */     
/* 644 */     if (gotNum) {
/* 645 */       return result;
/*     */     }
/*     */     
/* 648 */     throw new ParseException("No Number found", this.index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int parseMonth() throws ParseException {
/*     */     try {
/*     */       char curr;
/* 661 */       switch (this.orig[this.index++]) {
/*     */         
/*     */         case 'J':
/*     */         case 'j':
/* 665 */           switch (this.orig[this.index++]) {
/*     */             case 'A':
/*     */             case 'a':
/* 668 */               curr = this.orig[this.index++];
/* 669 */               if (curr == 'N' || curr == 'n') {
/* 670 */                 return 0;
/*     */               }
/*     */               break;
/*     */             
/*     */             case 'U':
/*     */             case 'u':
/* 676 */               curr = this.orig[this.index++];
/* 677 */               if (curr == 'N' || curr == 'n')
/* 678 */                 return 5; 
/* 679 */               if (curr == 'L' || curr == 'l') {
/* 680 */                 return 6;
/*     */               }
/*     */               break;
/*     */           } 
/*     */           
/*     */           break;
/*     */         case 'F':
/*     */         case 'f':
/* 688 */           curr = this.orig[this.index++];
/* 689 */           if (curr == 'E' || curr == 'e') {
/* 690 */             curr = this.orig[this.index++];
/* 691 */             if (curr == 'B' || curr == 'b') {
/* 692 */               return 1;
/*     */             }
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 'M':
/*     */         case 'm':
/* 699 */           curr = this.orig[this.index++];
/* 700 */           if (curr == 'A' || curr == 'a') {
/* 701 */             curr = this.orig[this.index++];
/* 702 */             if (curr == 'R' || curr == 'r')
/* 703 */               return 2; 
/* 704 */             if (curr == 'Y' || curr == 'y') {
/* 705 */               return 4;
/*     */             }
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 'A':
/*     */         case 'a':
/* 712 */           curr = this.orig[this.index++];
/* 713 */           if (curr == 'P' || curr == 'p') {
/* 714 */             curr = this.orig[this.index++];
/* 715 */             if (curr == 'R' || curr == 'r')
/* 716 */               return 3;  break;
/*     */           } 
/* 718 */           if (curr == 'U' || curr == 'u') {
/* 719 */             curr = this.orig[this.index++];
/* 720 */             if (curr == 'G' || curr == 'g') {
/* 721 */               return 7;
/*     */             }
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 'S':
/*     */         case 's':
/* 728 */           curr = this.orig[this.index++];
/* 729 */           if (curr == 'E' || curr == 'e') {
/* 730 */             curr = this.orig[this.index++];
/* 731 */             if (curr == 'P' || curr == 'p') {
/* 732 */               return 8;
/*     */             }
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 'O':
/*     */         case 'o':
/* 739 */           curr = this.orig[this.index++];
/* 740 */           if (curr == 'C' || curr == 'c') {
/* 741 */             curr = this.orig[this.index++];
/* 742 */             if (curr == 'T' || curr == 't') {
/* 743 */               return 9;
/*     */             }
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 'N':
/*     */         case 'n':
/* 750 */           curr = this.orig[this.index++];
/* 751 */           if (curr == 'O' || curr == 'o') {
/* 752 */             curr = this.orig[this.index++];
/* 753 */             if (curr == 'V' || curr == 'v') {
/* 754 */               return 10;
/*     */             }
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 'D':
/*     */         case 'd':
/* 761 */           curr = this.orig[this.index++];
/* 762 */           if (curr == 'E' || curr == 'e') {
/* 763 */             curr = this.orig[this.index++];
/* 764 */             if (curr == 'C' || curr == 'c') {
/* 765 */               return 11;
/*     */             }
/*     */           } 
/*     */           break;
/*     */       } 
/* 770 */     } catch (ArrayIndexOutOfBoundsException e) {}
/*     */ 
/*     */     
/* 773 */     throw new ParseException("Bad Month", this.index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int parseTimeZone() throws ParseException {
/* 782 */     if (this.index >= this.orig.length) {
/* 783 */       throw new ParseException("No more characters", this.index);
/*     */     }
/* 785 */     char test = this.orig[this.index];
/* 786 */     if (test == '+' || test == '-') {
/* 787 */       return parseNumericTimeZone();
/*     */     }
/* 789 */     return parseAlphaTimeZone();
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
/*     */   public int parseNumericTimeZone() throws ParseException {
/* 805 */     boolean switchSign = false;
/* 806 */     char first = this.orig[this.index++];
/* 807 */     if (first == '+') {
/* 808 */       switchSign = true;
/* 809 */     } else if (first != '-') {
/* 810 */       throw new ParseException("Bad Numeric TimeZone", this.index);
/*     */     } 
/*     */     
/* 813 */     int oindex = this.index;
/* 814 */     int tz = parseNumber();
/* 815 */     if (tz >= 2400)
/* 816 */       throw new ParseException("Numeric TimeZone out of range", oindex); 
/* 817 */     int offset = tz / 100 * 60 + tz % 100;
/* 818 */     if (switchSign) {
/* 819 */       return -offset;
/*     */     }
/* 821 */     return offset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int parseAlphaTimeZone() throws ParseException {
/* 831 */     int result = 0;
/* 832 */     boolean foundCommon = false;
/*     */     
/*     */     try {
/*     */       char curr;
/* 836 */       switch (this.orig[this.index++]) {
/*     */         case 'U':
/*     */         case 'u':
/* 839 */           curr = this.orig[this.index++];
/* 840 */           if (curr == 'T' || curr == 't') {
/* 841 */             result = 0;
/*     */             break;
/*     */           } 
/* 844 */           throw new ParseException("Bad Alpha TimeZone", this.index);
/*     */         
/*     */         case 'G':
/*     */         case 'g':
/* 848 */           curr = this.orig[this.index++];
/* 849 */           if (curr == 'M' || curr == 'm') {
/* 850 */             curr = this.orig[this.index++];
/* 851 */             if (curr == 'T' || curr == 't') {
/* 852 */               result = 0;
/*     */               break;
/*     */             } 
/*     */           } 
/* 856 */           throw new ParseException("Bad Alpha TimeZone", this.index);
/*     */         
/*     */         case 'E':
/*     */         case 'e':
/* 860 */           result = 300;
/* 861 */           foundCommon = true;
/*     */           break;
/*     */         
/*     */         case 'C':
/*     */         case 'c':
/* 866 */           result = 360;
/* 867 */           foundCommon = true;
/*     */           break;
/*     */         
/*     */         case 'M':
/*     */         case 'm':
/* 872 */           result = 420;
/* 873 */           foundCommon = true;
/*     */           break;
/*     */         
/*     */         case 'P':
/*     */         case 'p':
/* 878 */           result = 480;
/* 879 */           foundCommon = true;
/*     */           break;
/*     */         
/*     */         default:
/* 883 */           throw new ParseException("Bad Alpha TimeZone", this.index);
/*     */       } 
/* 885 */     } catch (ArrayIndexOutOfBoundsException e) {
/* 886 */       throw new ParseException("Bad Alpha TimeZone", this.index);
/*     */     } 
/*     */     
/* 889 */     if (foundCommon) {
/* 890 */       char c = this.orig[this.index++];
/* 891 */       if (c == 'S' || c == 's') {
/* 892 */         c = this.orig[this.index++];
/* 893 */         if (c != 'T' && c != 't') {
/* 894 */           throw new ParseException("Bad Alpha TimeZone", this.index);
/*     */         }
/* 896 */       } else if (c == 'D' || c == 'd') {
/* 897 */         c = this.orig[this.index++];
/* 898 */         if (c == 'T' || c != 't') {
/*     */           
/* 900 */           result -= 60;
/*     */         } else {
/* 902 */           throw new ParseException("Bad Alpha TimeZone", this.index);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 907 */     return result;
/*     */   }
/*     */   
/*     */   int getIndex() {
/* 911 */     return this.index;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\MailDateParser.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */