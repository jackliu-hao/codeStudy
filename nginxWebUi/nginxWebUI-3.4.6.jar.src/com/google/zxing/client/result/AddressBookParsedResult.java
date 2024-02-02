/*     */ package com.google.zxing.client.result;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class AddressBookParsedResult
/*     */   extends ParsedResult
/*     */ {
/*     */   private final String[] names;
/*     */   private final String[] nicknames;
/*     */   private final String pronunciation;
/*     */   private final String[] phoneNumbers;
/*     */   private final String[] phoneTypes;
/*     */   private final String[] emails;
/*     */   private final String[] emailTypes;
/*     */   private final String instantMessenger;
/*     */   private final String note;
/*     */   private final String[] addresses;
/*     */   private final String[] addressTypes;
/*     */   private final String org;
/*     */   private final String birthday;
/*     */   private final String title;
/*     */   private final String[] urls;
/*     */   private final String[] geo;
/*     */   
/*     */   public AddressBookParsedResult(String[] names, String[] phoneNumbers, String[] phoneTypes, String[] emails, String[] emailTypes, String[] addresses, String[] addressTypes) {
/*  51 */     this(names, (String[])null, (String)null, phoneNumbers, phoneTypes, emails, emailTypes, (String)null, (String)null, addresses, addressTypes, (String)null, (String)null, (String)null, (String[])null, (String[])null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AddressBookParsedResult(String[] names, String[] nicknames, String pronunciation, String[] phoneNumbers, String[] phoneTypes, String[] emails, String[] emailTypes, String instantMessenger, String note, String[] addresses, String[] addressTypes, String org, String birthday, String title, String[] urls, String[] geo) {
/*  85 */     super(ParsedResultType.ADDRESSBOOK);
/*  86 */     this.names = names;
/*  87 */     this.nicknames = nicknames;
/*  88 */     this.pronunciation = pronunciation;
/*  89 */     this.phoneNumbers = phoneNumbers;
/*  90 */     this.phoneTypes = phoneTypes;
/*  91 */     this.emails = emails;
/*  92 */     this.emailTypes = emailTypes;
/*  93 */     this.instantMessenger = instantMessenger;
/*  94 */     this.note = note;
/*  95 */     this.addresses = addresses;
/*  96 */     this.addressTypes = addressTypes;
/*  97 */     this.org = org;
/*  98 */     this.birthday = birthday;
/*  99 */     this.title = title;
/* 100 */     this.urls = urls;
/* 101 */     this.geo = geo;
/*     */   }
/*     */   
/*     */   public String[] getNames() {
/* 105 */     return this.names;
/*     */   }
/*     */   
/*     */   public String[] getNicknames() {
/* 109 */     return this.nicknames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPronunciation() {
/* 119 */     return this.pronunciation;
/*     */   }
/*     */   
/*     */   public String[] getPhoneNumbers() {
/* 123 */     return this.phoneNumbers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getPhoneTypes() {
/* 131 */     return this.phoneTypes;
/*     */   }
/*     */   
/*     */   public String[] getEmails() {
/* 135 */     return this.emails;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getEmailTypes() {
/* 143 */     return this.emailTypes;
/*     */   }
/*     */   
/*     */   public String getInstantMessenger() {
/* 147 */     return this.instantMessenger;
/*     */   }
/*     */   
/*     */   public String getNote() {
/* 151 */     return this.note;
/*     */   }
/*     */   
/*     */   public String[] getAddresses() {
/* 155 */     return this.addresses;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getAddressTypes() {
/* 163 */     return this.addressTypes;
/*     */   }
/*     */   
/*     */   public String getTitle() {
/* 167 */     return this.title;
/*     */   }
/*     */   
/*     */   public String getOrg() {
/* 171 */     return this.org;
/*     */   }
/*     */   
/*     */   public String[] getURLs() {
/* 175 */     return this.urls;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBirthday() {
/* 182 */     return this.birthday;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getGeo() {
/* 189 */     return this.geo;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayResult() {
/* 194 */     StringBuilder result = new StringBuilder(100);
/* 195 */     maybeAppend(this.names, result);
/* 196 */     maybeAppend(this.nicknames, result);
/* 197 */     maybeAppend(this.pronunciation, result);
/* 198 */     maybeAppend(this.title, result);
/* 199 */     maybeAppend(this.org, result);
/* 200 */     maybeAppend(this.addresses, result);
/* 201 */     maybeAppend(this.phoneNumbers, result);
/* 202 */     maybeAppend(this.emails, result);
/* 203 */     maybeAppend(this.instantMessenger, result);
/* 204 */     maybeAppend(this.urls, result);
/* 205 */     maybeAppend(this.birthday, result);
/* 206 */     maybeAppend(this.geo, result);
/* 207 */     maybeAppend(this.note, result);
/* 208 */     return result.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\AddressBookParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */