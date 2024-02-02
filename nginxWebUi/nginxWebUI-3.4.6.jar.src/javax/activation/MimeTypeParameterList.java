/*     */ package javax.activation;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MimeTypeParameterList
/*     */ {
/*     */   private Hashtable parameters;
/*     */   private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";
/*     */   
/*     */   public MimeTypeParameterList() {
/*  53 */     this.parameters = new Hashtable();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MimeTypeParameterList(String parameterList) throws MimeTypeParseException {
/*  63 */     this.parameters = new Hashtable();
/*     */ 
/*     */     
/*  66 */     parse(parameterList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void parse(String parameterList) throws MimeTypeParseException {
/*  75 */     if (parameterList == null) {
/*     */       return;
/*     */     }
/*  78 */     int length = parameterList.length();
/*  79 */     if (length <= 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  84 */     int i = skipWhiteSpace(parameterList, 0); char c;
/*  85 */     for (; i < length && (c = parameterList.charAt(i)) == ';'; 
/*  86 */       i = skipWhiteSpace(parameterList, i)) {
/*     */       String value;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  92 */       i++;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  97 */       i = skipWhiteSpace(parameterList, i);
/*     */ 
/*     */       
/* 100 */       if (i >= length) {
/*     */         return;
/*     */       }
/*     */       
/* 104 */       int lastIndex = i;
/* 105 */       while (i < length && isTokenChar(parameterList.charAt(i))) {
/* 106 */         i++;
/*     */       }
/* 108 */       String name = parameterList.substring(lastIndex, i).toLowerCase();
/*     */ 
/*     */       
/* 111 */       i = skipWhiteSpace(parameterList, i);
/*     */       
/* 113 */       if (i >= length || parameterList.charAt(i) != '=') {
/* 114 */         throw new MimeTypeParseException("Couldn't find the '=' that separates a parameter name from its value.");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 119 */       i++;
/* 120 */       i = skipWhiteSpace(parameterList, i);
/*     */       
/* 122 */       if (i >= length) {
/* 123 */         throw new MimeTypeParseException("Couldn't find a value for parameter named " + name);
/*     */       }
/*     */ 
/*     */       
/* 127 */       c = parameterList.charAt(i);
/* 128 */       if (c == '"') {
/*     */         
/* 130 */         i++;
/* 131 */         if (i >= length) {
/* 132 */           throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
/*     */         }
/*     */         
/* 135 */         lastIndex = i;
/*     */ 
/*     */         
/* 138 */         while (i < length) {
/* 139 */           c = parameterList.charAt(i);
/* 140 */           if (c == '"')
/*     */             break; 
/* 142 */           if (c == '\\')
/*     */           {
/*     */ 
/*     */             
/* 146 */             i++;
/*     */           }
/* 148 */           i++;
/*     */         } 
/* 150 */         if (c != '"') {
/* 151 */           throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
/*     */         }
/*     */         
/* 154 */         value = unquote(parameterList.substring(lastIndex, i));
/*     */         
/* 156 */         i++;
/* 157 */       } else if (isTokenChar(c)) {
/*     */ 
/*     */         
/* 160 */         lastIndex = i;
/* 161 */         while (i < length && isTokenChar(parameterList.charAt(i)))
/* 162 */           i++; 
/* 163 */         value = parameterList.substring(lastIndex, i);
/*     */       } else {
/*     */         
/* 166 */         throw new MimeTypeParseException("Unexpected character encountered at index " + i);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 171 */       this.parameters.put(name, value);
/*     */     } 
/* 173 */     if (i < length) {
/* 174 */       throw new MimeTypeParseException("More characters encountered in input than expected.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 185 */     return this.parameters.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 194 */     return this.parameters.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String get(String name) {
/* 205 */     return (String)this.parameters.get(name.trim().toLowerCase());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(String name, String value) {
/* 216 */     this.parameters.put(name.trim().toLowerCase(), value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(String name) {
/* 225 */     this.parameters.remove(name.trim().toLowerCase());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getNames() {
/* 234 */     return this.parameters.keys();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 241 */     StringBuffer buffer = new StringBuffer();
/* 242 */     buffer.ensureCapacity(this.parameters.size() * 16);
/*     */ 
/*     */     
/* 245 */     Enumeration keys = this.parameters.keys();
/* 246 */     while (keys.hasMoreElements()) {
/* 247 */       String key = keys.nextElement();
/* 248 */       buffer.append("; ");
/* 249 */       buffer.append(key);
/* 250 */       buffer.append('=');
/* 251 */       buffer.append(quote((String)this.parameters.get(key)));
/*     */     } 
/*     */     
/* 254 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isTokenChar(char c) {
/* 263 */     return (c > ' ' && c < '' && "()<>@,;:/[]?=\\\"".indexOf(c) < 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int skipWhiteSpace(String rawdata, int i) {
/* 271 */     int length = rawdata.length();
/* 272 */     while (i < length && Character.isWhitespace(rawdata.charAt(i)))
/* 273 */       i++; 
/* 274 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String quote(String value) {
/* 281 */     boolean needsQuotes = false;
/*     */ 
/*     */     
/* 284 */     int length = value.length();
/* 285 */     for (int i = 0; i < length && !needsQuotes; i++) {
/* 286 */       needsQuotes = !isTokenChar(value.charAt(i));
/*     */     }
/*     */     
/* 289 */     if (needsQuotes) {
/* 290 */       StringBuffer buffer = new StringBuffer();
/* 291 */       buffer.ensureCapacity((int)(length * 1.5D));
/*     */ 
/*     */       
/* 294 */       buffer.append('"');
/*     */ 
/*     */       
/* 297 */       for (int j = 0; j < length; j++) {
/* 298 */         char c = value.charAt(j);
/* 299 */         if (c == '\\' || c == '"')
/* 300 */           buffer.append('\\'); 
/* 301 */         buffer.append(c);
/*     */       } 
/*     */ 
/*     */       
/* 305 */       buffer.append('"');
/*     */       
/* 307 */       return buffer.toString();
/*     */     } 
/* 309 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String unquote(String value) {
/* 318 */     int valueLength = value.length();
/* 319 */     StringBuffer buffer = new StringBuffer();
/* 320 */     buffer.ensureCapacity(valueLength);
/*     */     
/* 322 */     boolean escaped = false;
/* 323 */     for (int i = 0; i < valueLength; i++) {
/* 324 */       char currentChar = value.charAt(i);
/* 325 */       if (!escaped && currentChar != '\\') {
/* 326 */         buffer.append(currentChar);
/* 327 */       } else if (escaped) {
/* 328 */         buffer.append(currentChar);
/* 329 */         escaped = false;
/*     */       } else {
/* 331 */         escaped = true;
/*     */       } 
/*     */     } 
/*     */     
/* 335 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\MimeTypeParameterList.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */