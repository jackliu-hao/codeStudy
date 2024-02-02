/*     */ package javax.servlet.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Hashtable;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.servlet.ServletInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class HttpUtils
/*     */ {
/*     */   private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
/*  39 */   private static ResourceBundle lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Hashtable<String, String[]> parseQueryString(String s) {
/*  78 */     String[] valArray = null;
/*     */     
/*  80 */     if (s == null) {
/*  81 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/*  84 */     Hashtable<String, String[]> ht = (Hashtable)new Hashtable<>();
/*  85 */     StringBuilder sb = new StringBuilder();
/*  86 */     StringTokenizer st = new StringTokenizer(s, "&");
/*  87 */     while (st.hasMoreTokens()) {
/*  88 */       String pair = st.nextToken();
/*  89 */       int pos = pair.indexOf('=');
/*  90 */       if (pos == -1)
/*     */       {
/*     */         
/*  93 */         throw new IllegalArgumentException();
/*     */       }
/*  95 */       String key = parseName(pair.substring(0, pos), sb);
/*  96 */       String val = parseName(pair.substring(pos + 1, pair.length()), sb);
/*  97 */       if (ht.containsKey(key)) {
/*  98 */         String[] oldVals = ht.get(key);
/*  99 */         valArray = new String[oldVals.length + 1];
/* 100 */         for (int i = 0; i < oldVals.length; i++) {
/* 101 */           valArray[i] = oldVals[i];
/*     */         }
/* 103 */         valArray[oldVals.length] = val;
/*     */       } else {
/* 105 */         valArray = new String[1];
/* 106 */         valArray[0] = val;
/*     */       } 
/* 108 */       ht.put(key, valArray);
/*     */     } 
/*     */     
/* 111 */     return ht;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Hashtable<String, String[]> parsePostData(int len, ServletInputStream in) {
/* 155 */     if (len <= 0)
/*     */     {
/* 157 */       return (Hashtable)new Hashtable<>();
/*     */     }
/*     */     
/* 160 */     if (in == null) {
/* 161 */       throw new IllegalArgumentException();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 167 */     byte[] postedBytes = new byte[len];
/*     */     try {
/* 169 */       int offset = 0;
/*     */       
/*     */       do {
/* 172 */         int inputLen = in.read(postedBytes, offset, len - offset);
/* 173 */         if (inputLen <= 0) {
/* 174 */           String msg = lStrings.getString("err.io.short_read");
/* 175 */           throw new IllegalArgumentException(msg);
/*     */         } 
/* 177 */         offset += inputLen;
/* 178 */       } while (len - offset > 0);
/*     */     }
/* 180 */     catch (IOException e) {
/* 181 */       throw new IllegalArgumentException(e.getMessage());
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 190 */       String postedBody = new String(postedBytes, 0, len, "8859_1");
/* 191 */       return parseQueryString(postedBody);
/* 192 */     } catch (UnsupportedEncodingException e) {
/*     */ 
/*     */       
/* 195 */       throw new IllegalArgumentException(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String parseName(String s, StringBuilder sb) {
/* 204 */     sb.setLength(0);
/* 205 */     for (int i = 0; i < s.length(); i++) {
/* 206 */       char c = s.charAt(i);
/* 207 */       switch (c) {
/*     */         case '+':
/* 209 */           sb.append(' ');
/*     */           break;
/*     */         case '%':
/*     */           try {
/* 213 */             sb.append((char)Integer.parseInt(s.substring(i + 1, i + 3), 16));
/*     */             
/* 215 */             i += 2;
/* 216 */           } catch (NumberFormatException e) {
/*     */ 
/*     */             
/* 219 */             throw new IllegalArgumentException();
/* 220 */           } catch (StringIndexOutOfBoundsException e) {
/* 221 */             String rest = s.substring(i);
/* 222 */             sb.append(rest);
/* 223 */             if (rest.length() == 2) {
/* 224 */               i++;
/*     */             }
/*     */           } 
/*     */           break;
/*     */         default:
/* 229 */           sb.append(c);
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 234 */     return sb.toString();
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
/*     */   public static StringBuffer getRequestURL(HttpServletRequest req) {
/* 260 */     StringBuffer url = new StringBuffer();
/* 261 */     String scheme = req.getScheme();
/* 262 */     int port = req.getServerPort();
/* 263 */     String urlPath = req.getRequestURI();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 268 */     url.append(scheme);
/* 269 */     url.append("://");
/* 270 */     url.append(req.getServerName());
/* 271 */     if ((scheme.equals("http") && port != 80) || (scheme
/* 272 */       .equals("https") && port != 443)) {
/* 273 */       url.append(':');
/* 274 */       url.append(req.getServerPort());
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 280 */     url.append(urlPath);
/*     */     
/* 282 */     return url;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */