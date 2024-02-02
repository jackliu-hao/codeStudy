/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.DateUtil;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class ISOLikeTemplateDateFormat
/*     */   extends TemplateDateFormat
/*     */ {
/*     */   private static final String XS_LESS_THAN_SECONDS_ACCURACY_ERROR_MESSAGE = "Less than seconds accuracy isn't allowed by the XML Schema format";
/*     */   private final ISOLikeTemplateDateFormatFactory factory;
/*     */   private final Environment env;
/*     */   protected final int dateType;
/*     */   protected final boolean zonelessInput;
/*     */   protected final TimeZone timeZone;
/*     */   protected final Boolean forceUTC;
/*     */   protected final Boolean showZoneOffset;
/*     */   protected final int accuracy;
/*     */   
/*     */   public ISOLikeTemplateDateFormat(String formatString, int parsingStart, int dateType, boolean zonelessInput, TimeZone timeZone, ISOLikeTemplateDateFormatFactory factory, Environment env) throws InvalidFormatParametersException, UnknownDateTypeFormattingUnsupportedException {
/*  58 */     this.factory = factory;
/*  59 */     this.env = env;
/*  60 */     if (dateType == 0) {
/*  61 */       throw new UnknownDateTypeFormattingUnsupportedException();
/*     */     }
/*     */     
/*  64 */     this.dateType = dateType;
/*  65 */     this.zonelessInput = zonelessInput;
/*     */     
/*  67 */     int ln = formatString.length();
/*  68 */     boolean afterSeparator = false;
/*  69 */     int i = parsingStart;
/*  70 */     int accuracy = 7;
/*  71 */     Boolean showZoneOffset = null;
/*  72 */     Boolean forceUTC = Boolean.FALSE;
/*  73 */     while (i < ln) {
/*  74 */       char c = formatString.charAt(i++);
/*  75 */       if (c == '_' || c == ' ') {
/*  76 */         afterSeparator = true; continue;
/*     */       } 
/*  78 */       if (!afterSeparator) {
/*  79 */         throw new InvalidFormatParametersException("Missing space or \"_\" before \"" + c + "\" (at char pos. " + i + ").");
/*     */       }
/*     */ 
/*     */       
/*  83 */       switch (c) {
/*     */         case 'h':
/*     */         case 'm':
/*     */         case 's':
/*  87 */           if (accuracy != 7) {
/*  88 */             throw new InvalidFormatParametersException("Character \"" + c + "\" is unexpected as accuracy was already specified earlier (at char pos. " + i + ").");
/*     */           }
/*     */ 
/*     */           
/*  92 */           switch (c) {
/*     */             case 'h':
/*  94 */               if (isXSMode()) {
/*  95 */                 throw new InvalidFormatParametersException("Less than seconds accuracy isn't allowed by the XML Schema format");
/*     */               }
/*     */               
/*  98 */               accuracy = 4;
/*     */               break;
/*     */             case 'm':
/* 101 */               if (i < ln && formatString.charAt(i) == 's') {
/* 102 */                 i++;
/* 103 */                 accuracy = 8; break;
/*     */               } 
/* 105 */               if (isXSMode()) {
/* 106 */                 throw new InvalidFormatParametersException("Less than seconds accuracy isn't allowed by the XML Schema format");
/*     */               }
/*     */               
/* 109 */               accuracy = 5;
/*     */               break;
/*     */             
/*     */             case 's':
/* 113 */               accuracy = 6;
/*     */               break;
/*     */           } 
/*     */           break;
/*     */         case 'f':
/* 118 */           if (i < ln && formatString.charAt(i) == 'u') {
/* 119 */             checkForceUTCNotSet(forceUTC);
/* 120 */             i++;
/* 121 */             forceUTC = Boolean.TRUE;
/*     */             break;
/*     */           } 
/*     */         
/*     */         case 'n':
/* 126 */           if (showZoneOffset != null) {
/* 127 */             throw new InvalidFormatParametersException("Character \"" + c + "\" is unexpected as zone offset visibility was already specified earlier. (at char pos. " + i + ").");
/*     */           }
/*     */ 
/*     */           
/* 131 */           switch (c) {
/*     */             case 'n':
/* 133 */               if (i < ln && formatString.charAt(i) == 'z') {
/* 134 */                 i++;
/* 135 */                 showZoneOffset = Boolean.FALSE; break;
/*     */               } 
/* 137 */               throw new InvalidFormatParametersException("\"n\" must be followed by \"z\" (at char pos. " + i + ").");
/*     */ 
/*     */ 
/*     */             
/*     */             case 'f':
/* 142 */               if (i < ln && formatString.charAt(i) == 'z') {
/* 143 */                 i++;
/* 144 */                 showZoneOffset = Boolean.TRUE; break;
/*     */               } 
/* 146 */               throw new InvalidFormatParametersException("\"f\" must be followed by \"z\" (at char pos. " + i + ").");
/*     */           } 
/*     */ 
/*     */           
/*     */           break;
/*     */         
/*     */         case 'u':
/* 153 */           checkForceUTCNotSet(forceUTC);
/* 154 */           forceUTC = null;
/*     */           break;
/*     */         default:
/* 157 */           throw new InvalidFormatParametersException("Unexpected character, " + 
/* 158 */               StringUtil.jQuote(String.valueOf(c)) + ". Expected the beginning of one of: h, m, s, ms, nz, fz, u (at char pos. " + i + ").");
/*     */       } 
/*     */ 
/*     */       
/* 162 */       afterSeparator = false;
/*     */     } 
/*     */ 
/*     */     
/* 166 */     this.accuracy = accuracy;
/* 167 */     this.showZoneOffset = showZoneOffset;
/* 168 */     this.forceUTC = forceUTC;
/* 169 */     this.timeZone = timeZone;
/*     */   }
/*     */   
/*     */   private void checkForceUTCNotSet(Boolean fourceUTC) throws InvalidFormatParametersException {
/* 173 */     if (fourceUTC != Boolean.FALSE) {
/* 174 */       throw new InvalidFormatParametersException("The UTC usage option was already set earlier.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final String formatToPlainText(TemplateDateModel dateModel) throws TemplateModelException {
/* 181 */     Date date = TemplateFormatUtil.getNonNullDate(dateModel);
/* 182 */     return format(date, (this.dateType != 1), (this.dateType != 2), (this.showZoneOffset == null) ? (!this.zonelessInput) : this.showZoneOffset
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 188 */         .booleanValue(), this.accuracy, ((this.forceUTC == null) ? !this.zonelessInput : this.forceUTC
/*     */         
/* 190 */         .booleanValue()) ? DateUtil.UTC : this.timeZone, this.factory
/* 191 */         .getISOBuiltInCalendar(this.env));
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
/*     */   public final Date parse(String s, int dateType) throws UnparsableValueException {
/* 204 */     DateUtil.CalendarFieldsToDateConverter calToDateConverter = this.factory.getCalendarFieldsToDateCalculator(this.env);
/* 205 */     TimeZone tz = (this.forceUTC != Boolean.FALSE) ? DateUtil.UTC : this.timeZone;
/*     */     try {
/* 207 */       if (dateType == 2)
/* 208 */         return parseDate(s, tz, calToDateConverter); 
/* 209 */       if (dateType == 1)
/* 210 */         return parseTime(s, tz, calToDateConverter); 
/* 211 */       if (dateType == 3) {
/* 212 */         return parseDateTime(s, tz, calToDateConverter);
/*     */       }
/* 214 */       throw new BugException("Unexpected date type: " + dateType);
/*     */     }
/* 216 */     catch (freemarker.template.utility.DateUtil.DateParseException e) {
/* 217 */       throw new UnparsableValueException(e.getMessage(), e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getDescription() {
/* 238 */     switch (this.dateType) { case 2:
/* 239 */         return getDateDescription();
/* 240 */       case 1: return getTimeDescription();
/* 241 */       case 3: return getDateTimeDescription(); }
/* 242 */      return "<error: wrong format dateType>";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isLocaleBound() {
/* 252 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTimeZoneBound() {
/* 257 */     return true;
/*     */   }
/*     */   
/*     */   protected abstract String format(Date paramDate, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt, TimeZone paramTimeZone, DateUtil.DateToISO8601CalendarFactory paramDateToISO8601CalendarFactory);
/*     */   
/*     */   protected abstract Date parseDate(String paramString, TimeZone paramTimeZone, DateUtil.CalendarFieldsToDateConverter paramCalendarFieldsToDateConverter) throws DateUtil.DateParseException;
/*     */   
/*     */   protected abstract Date parseTime(String paramString, TimeZone paramTimeZone, DateUtil.CalendarFieldsToDateConverter paramCalendarFieldsToDateConverter) throws DateUtil.DateParseException;
/*     */   
/*     */   protected abstract Date parseDateTime(String paramString, TimeZone paramTimeZone, DateUtil.CalendarFieldsToDateConverter paramCalendarFieldsToDateConverter) throws DateUtil.DateParseException;
/*     */   
/*     */   protected abstract String getDateDescription();
/*     */   
/*     */   protected abstract String getTimeDescription();
/*     */   
/*     */   protected abstract String getDateTimeDescription();
/*     */   
/*     */   protected abstract boolean isXSMode();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ISOLikeTemplateDateFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */