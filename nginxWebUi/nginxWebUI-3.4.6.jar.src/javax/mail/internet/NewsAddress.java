/*     */ package javax.mail.internet;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import javax.mail.Address;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NewsAddress
/*     */   extends Address
/*     */ {
/*     */   protected String newsgroup;
/*     */   protected String host;
/*     */   private static final long serialVersionUID = -4203797299824684143L;
/*     */   
/*     */   public NewsAddress() {}
/*     */   
/*     */   public NewsAddress(String newsgroup) {
/*  73 */     this(newsgroup, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NewsAddress(String newsgroup, String host) {
/*  83 */     this.newsgroup = newsgroup;
/*  84 */     this.host = host;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getType() {
/*  92 */     return "news";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNewsgroup(String newsgroup) {
/* 101 */     this.newsgroup = newsgroup;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getNewsgroup() {
/* 110 */     return this.newsgroup;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHost(String host) {
/* 119 */     this.host = host;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost() {
/* 128 */     return this.host;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 137 */     return this.newsgroup;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object a) {
/* 144 */     if (!(a instanceof NewsAddress)) {
/* 145 */       return false;
/*     */     }
/* 147 */     NewsAddress s = (NewsAddress)a;
/* 148 */     return (this.newsgroup.equals(s.newsgroup) && ((this.host == null && s.host == null) || (this.host != null && s.host != null && this.host.equalsIgnoreCase(s.host))));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 157 */     int hash = 0;
/* 158 */     if (this.newsgroup != null)
/* 159 */       hash += this.newsgroup.hashCode(); 
/* 160 */     if (this.host != null)
/* 161 */       hash += this.host.toLowerCase(Locale.ENGLISH).hashCode(); 
/* 162 */     return hash;
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
/*     */   public static String toString(Address[] addresses) {
/* 178 */     if (addresses == null || addresses.length == 0) {
/* 179 */       return null;
/*     */     }
/* 181 */     StringBuffer s = new StringBuffer(((NewsAddress)addresses[0]).toString());
/*     */     
/* 183 */     for (int i = 1; i < addresses.length; i++) {
/* 184 */       s.append(",").append(((NewsAddress)addresses[i]).toString());
/*     */     }
/* 186 */     return s.toString();
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
/*     */   public static NewsAddress[] parse(String newsgroups) throws AddressException {
/* 200 */     StringTokenizer st = new StringTokenizer(newsgroups, ",");
/* 201 */     Vector nglist = new Vector();
/* 202 */     while (st.hasMoreTokens()) {
/* 203 */       String ng = st.nextToken();
/* 204 */       nglist.addElement(new NewsAddress(ng));
/*     */     } 
/* 206 */     int size = nglist.size();
/* 207 */     NewsAddress[] na = new NewsAddress[size];
/* 208 */     if (size > 0)
/* 209 */       nglist.copyInto((Object[])na); 
/* 210 */     return na;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\NewsAddress.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */