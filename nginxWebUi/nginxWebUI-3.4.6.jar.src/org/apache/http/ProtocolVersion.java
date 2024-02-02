/*     */ package org.apache.http;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class ProtocolVersion
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   private static final long serialVersionUID = 8950662842175091068L;
/*     */   protected final String protocol;
/*     */   protected final int major;
/*     */   protected final int minor;
/*     */   
/*     */   public ProtocolVersion(String protocol, int major, int minor) {
/*  72 */     this.protocol = (String)Args.notNull(protocol, "Protocol name");
/*  73 */     this.major = Args.notNegative(major, "Protocol major version");
/*  74 */     this.minor = Args.notNegative(minor, "Protocol minor version");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getProtocol() {
/*  83 */     return this.protocol;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getMajor() {
/*  92 */     return this.major;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getMinor() {
/* 101 */     return this.minor;
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
/*     */   public ProtocolVersion forVersion(int major, int minor) {
/* 123 */     if (major == this.major && minor == this.minor) {
/* 124 */       return this;
/*     */     }
/*     */ 
/*     */     
/* 128 */     return new ProtocolVersion(this.protocol, major, minor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 139 */     return this.protocol.hashCode() ^ this.major * 100000 ^ this.minor;
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
/*     */   public final boolean equals(Object obj) {
/* 158 */     if (this == obj) {
/* 159 */       return true;
/*     */     }
/* 161 */     if (!(obj instanceof ProtocolVersion)) {
/* 162 */       return false;
/*     */     }
/* 164 */     ProtocolVersion that = (ProtocolVersion)obj;
/*     */     
/* 166 */     return (this.protocol.equals(that.protocol) && this.major == that.major && this.minor == that.minor);
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
/*     */   public boolean isComparable(ProtocolVersion that) {
/* 183 */     return (that != null && this.protocol.equals(that.protocol));
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
/*     */   public int compareToVersion(ProtocolVersion that) {
/* 204 */     Args.notNull(that, "Protocol version");
/* 205 */     Args.check(this.protocol.equals(that.protocol), "Versions for different protocols cannot be compared: %s %s", new Object[] { this, that });
/*     */     
/* 207 */     int delta = getMajor() - that.getMajor();
/* 208 */     if (delta == 0) {
/* 209 */       delta = getMinor() - that.getMinor();
/*     */     }
/* 211 */     return delta;
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
/*     */   public final boolean greaterEquals(ProtocolVersion version) {
/* 226 */     return (isComparable(version) && compareToVersion(version) >= 0);
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
/*     */   public final boolean lessEquals(ProtocolVersion version) {
/* 241 */     return (isComparable(version) && compareToVersion(version) <= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 252 */     StringBuilder buffer = new StringBuilder();
/* 253 */     buffer.append(this.protocol);
/* 254 */     buffer.append('/');
/* 255 */     buffer.append(Integer.toString(this.major));
/* 256 */     buffer.append('.');
/* 257 */     buffer.append(Integer.toString(this.minor));
/* 258 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 263 */     return super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\ProtocolVersion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */