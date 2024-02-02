/*     */ package org.wildfly.client.config;
/*     */ 
/*     */ import java.net.URI;
/*     */ import javax.xml.stream.Location;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class XMLLocation
/*     */   implements Location, Comparable<XMLLocation>
/*     */ {
/*  37 */   public static final XMLLocation UNKNOWN = new XMLLocation(null, -1, -1, -1);
/*     */ 
/*     */   
/*     */   private final XMLLocation includedFrom;
/*     */ 
/*     */   
/*     */   private final URI uri;
/*     */ 
/*     */   
/*     */   private final int lineNumber;
/*     */   
/*     */   private final int columnNumber;
/*     */   
/*     */   private final int characterOffset;
/*     */   
/*     */   private final String publicId;
/*     */   
/*     */   private final String systemId;
/*     */   
/*     */   private int hashCode;
/*     */ 
/*     */   
/*     */   public XMLLocation(XMLLocation includedFrom, URI uri, int lineNumber, int columnNumber, int characterOffset, String publicId, String systemId) {
/*  60 */     this.includedFrom = includedFrom;
/*  61 */     this.uri = uri;
/*  62 */     this.lineNumber = lineNumber;
/*  63 */     this.columnNumber = columnNumber;
/*  64 */     this.characterOffset = characterOffset;
/*  65 */     this.publicId = publicId;
/*  66 */     this.systemId = systemId;
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
/*     */   public XMLLocation(URI uri, int lineNumber, int columnNumber, int characterOffset, String publicId, String systemId) {
/*  80 */     this(null, uri, lineNumber, columnNumber, characterOffset, null, null);
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
/*     */   public XMLLocation(XMLLocation includedFrom, URI uri, int lineNumber, int columnNumber, int characterOffset) {
/*  93 */     this(includedFrom, uri, lineNumber, columnNumber, characterOffset, null, null);
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
/*     */   public XMLLocation(URI uri, int lineNumber, int columnNumber, int characterOffset) {
/* 105 */     this(uri, lineNumber, columnNumber, characterOffset, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XMLLocation(URI uri) {
/* 114 */     this(uri, -1, -1, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   XMLLocation(XMLLocation includedFrom, URI uri, Location original) {
/* 124 */     this(includedFrom, uri, original.getLineNumber(), original.getColumnNumber(), original.getCharacterOffset(), original.getPublicId(), original.getSystemId());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   XMLLocation(URI uri, Location original) {
/* 134 */     this(uri, original.getLineNumber(), original.getColumnNumber(), original.getCharacterOffset(), original.getPublicId(), original.getSystemId());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   XMLLocation(Location original) {
/* 143 */     this((original instanceof XMLLocation) ? ((XMLLocation)original).getUri() : null, original.getLineNumber(), original.getColumnNumber(), original.getCharacterOffset(), original.getPublicId(), original.getSystemId());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI getUri() {
/* 152 */     return this.uri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLineNumber() {
/* 161 */     return this.lineNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getColumnNumber() {
/* 170 */     return this.columnNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCharacterOffset() {
/* 179 */     return this.characterOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPublicId() {
/* 188 */     return this.publicId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSystemId() {
/* 197 */     return this.systemId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XMLLocation getIncludedFrom() {
/* 206 */     return this.includedFrom;
/*     */   }
/*     */   
/*     */   public static XMLLocation toXMLLocation(Location location) {
/* 210 */     return toXMLLocation((URI)null, location);
/*     */   }
/*     */   
/*     */   public static XMLLocation toXMLLocation(URI uri, Location location) {
/* 214 */     if (location instanceof XMLLocation)
/* 215 */       return (XMLLocation)location; 
/* 216 */     if (location == null) {
/* 217 */       return UNKNOWN;
/*     */     }
/* 219 */     return new XMLLocation(uri, location);
/*     */   }
/*     */ 
/*     */   
/*     */   public static XMLLocation toXMLLocation(XMLLocation includedFrom, Location location) {
/* 224 */     return toXMLLocation(includedFrom, null, location);
/*     */   }
/*     */   
/*     */   public static XMLLocation toXMLLocation(XMLLocation includedFrom, URI uri, Location location) {
/* 228 */     if (location instanceof XMLLocation)
/* 229 */       return (XMLLocation)location; 
/* 230 */     if (location == null) {
/* 231 */       return UNKNOWN;
/*     */     }
/* 233 */     return new XMLLocation(includedFrom, uri, location);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 238 */     int result = this.hashCode;
/* 239 */     if (result == 0) {
/* 240 */       if (this.includedFrom != null) result = this.includedFrom.hashCode; 
/* 241 */       result = 31 * result + ((this.uri != null) ? this.uri.hashCode() : 0);
/* 242 */       result = 31 * result + this.lineNumber;
/* 243 */       result = 31 * result + this.columnNumber;
/* 244 */       result = 31 * result + this.characterOffset;
/* 245 */       result = 31 * result + ((this.publicId != null) ? this.publicId.hashCode() : 0);
/* 246 */       result = 31 * result + ((this.systemId != null) ? this.systemId.hashCode() : 0);
/* 247 */       if (result == 0) result = -1; 
/* 248 */       this.hashCode = result;
/*     */     } 
/* 250 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 260 */     return (other instanceof XMLLocation && equals((XMLLocation)other));
/*     */   }
/*     */   
/*     */   private static boolean equals(Object a, Object b) {
/* 264 */     return (a == b || a == null) ? ((b == null)) : a.equals(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(XMLLocation other) {
/* 274 */     return (this == other || (other != null && equals(this.includedFrom, other.includedFrom) && equals(this.uri, other.uri) && this.lineNumber == other.lineNumber && this.columnNumber == other.columnNumber && this.characterOffset == other.characterOffset && equals(this.publicId, other.publicId) && equals(this.systemId, other.systemId)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 283 */     StringBuilder b = new StringBuilder();
/* 284 */     toString(b);
/* 285 */     return b.toString();
/*     */   }
/*     */   
/*     */   private void toString(StringBuilder b) {
/* 289 */     b.append("\n\tat ").append((this.uri == null) ? "<input>" : this.uri);
/* 290 */     if (this.lineNumber > 0) {
/* 291 */       b.append(':').append(this.lineNumber);
/* 292 */       if (this.columnNumber > 0) {
/* 293 */         b.append(':').append(this.columnNumber);
/*     */       }
/*     */     } 
/* 296 */     if (this.includedFrom != null) {
/* 297 */       this.includedFrom.toString(b);
/*     */     }
/*     */   }
/*     */   
/*     */   private int compareUri(URI a, URI b) {
/* 302 */     return (a == null) ? ((b == null) ? 0 : 1) : ((b == null) ? -1 : a.compareTo(b));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(XMLLocation o) {
/* 313 */     int c = compareUri(this.uri, o.uri);
/* 314 */     if (c == 0) {
/* 315 */       c = Integer.signum(this.lineNumber - o.lineNumber);
/* 316 */       if (c == 0) {
/* 317 */         c = Integer.signum(this.columnNumber - o.columnNumber);
/* 318 */         if (c == 0) {
/* 319 */           c = Integer.signum(this.characterOffset - o.characterOffset);
/*     */         }
/*     */       } 
/*     */     } 
/* 323 */     return c;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\XMLLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */