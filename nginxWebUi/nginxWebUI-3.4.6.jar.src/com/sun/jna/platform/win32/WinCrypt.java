/*      */ package com.sun.jna.platform.win32;
/*      */ 
/*      */ import com.sun.jna.Memory;
/*      */ import com.sun.jna.Native;
/*      */ import com.sun.jna.Pointer;
/*      */ import com.sun.jna.StringArray;
/*      */ import com.sun.jna.Structure;
/*      */ import com.sun.jna.Structure.FieldOrder;
/*      */ import com.sun.jna.Union;
/*      */ import com.sun.jna.win32.StdCallLibrary;
/*      */ import com.sun.jna.win32.W32APITypeMapper;
/*      */ 
/*      */ public interface WinCrypt
/*      */ {
/*      */   public static final int CRYPTPROTECT_PROMPT_ON_UNPROTECT = 1;
/*      */   public static final int CRYPTPROTECT_PROMPT_ON_PROTECT = 2;
/*      */   public static final int CRYPTPROTECT_PROMPT_RESERVED = 4;
/*      */   public static final int CRYPTPROTECT_PROMPT_STRONG = 8;
/*      */   public static final int CRYPTPROTECT_PROMPT_REQUIRE_STRONG = 16;
/*      */   public static final int CRYPTPROTECT_UI_FORBIDDEN = 1;
/*      */   public static final int CRYPTPROTECT_LOCAL_MACHINE = 4;
/*      */   public static final int CRYPTPROTECT_CRED_SYNC = 8;
/*      */   public static final int CRYPTPROTECT_AUDIT = 16;
/*      */   public static final int CRYPTPROTECT_NO_RECOVERY = 32;
/*      */   public static final int CRYPTPROTECT_VERIFY_PROTECTION = 64;
/*      */   public static final int CRYPTPROTECT_CRED_REGENERATE = 128;
/*      */   public static final int CRYPT_E_ASN1_ERROR = -2146881280;
/*      */   public static final int CRYPT_E_ASN1_INTERNAL = -2146881279;
/*      */   public static final int CRYPT_E_ASN1_EOD = -2146881278;
/*      */   public static final int CRYPT_E_ASN1_CORRUPT = -2146881277;
/*      */   public static final int CRYPT_E_ASN1_LARGE = -2146881276;
/*      */   public static final int CRYPT_E_ASN1_CONSTRAINT = -2146881275;
/*      */   public static final int CRYPT_E_ASN1_MEMORY = -2146881274;
/*      */   public static final int CRYPT_E_ASN1_OVERFLOW = -2146881273;
/*      */   public static final int CRYPT_E_ASN1_BADPDU = -2146881272;
/*      */   public static final int CRYPT_E_ASN1_BADARGS = -2146881271;
/*      */   public static final int CRYPT_E_ASN1_BADREAL = -2146881270;
/*      */   public static final int CRYPT_E_ASN1_BADTAG = -2146881269;
/*      */   public static final int CRYPT_E_ASN1_CHOICE = -2146881268;
/*      */   public static final int CRYPT_E_ASN1_RULE = -2146881267;
/*      */   public static final int CRYPT_E_ASN1_UTF8 = -2146881266;
/*      */   public static final int CRYPT_E_ASN1_PDU_TYPE = -2146881229;
/*      */   public static final int CRYPT_E_ASN1_NYI = -2146881228;
/*      */   public static final int CRYPT_E_ASN1_EXTENDED = -2146881023;
/*      */   public static final int CRYPT_E_ASN1_NOEOD = -2146881022;
/*      */   public static final int CRYPT_ASN_ENCODING = 1;
/*      */   public static final int CRYPT_NDR_ENCODING = 2;
/*      */   public static final int X509_ASN_ENCODING = 1;
/*      */   public static final int X509_NDR_ENCODING = 2;
/*      */   public static final int PKCS_7_ASN_ENCODING = 65536;
/*      */   public static final int PKCS_7_NDR_ENCODING = 131072;
/*      */   public static final int USAGE_MATCH_TYPE_AND = 0;
/*      */   public static final int USAGE_MATCH_TYPE_OR = 1;
/*      */   public static final int PP_CLIENT_HWND = 1;
/*      */   public static final int CERT_SIMPLE_NAME_STR = 1;
/*      */   public static final int CERT_OID_NAME_STR = 2;
/*      */   public static final int CERT_X500_NAME_STR = 3;
/*      */   public static final int CERT_XML_NAME_STR = 4;
/*      */   public static final int CERT_CHAIN_POLICY_BASE = 1;
/*      */   public static final String szOID_RSA_SHA1RSA = "1.2.840.113549.1.1.5";
/*      */   
/*      */   @FieldOrder({"cbData", "pbData"})
/*      */   public static class DATA_BLOB
/*      */     extends Structure
/*      */   {
/*      */     public int cbData;
/*      */     public Pointer pbData;
/*      */     
/*      */     public DATA_BLOB(Pointer memory) {
/*   70 */       super(memory);
/*   71 */       read();
/*      */     } public static class ByReference extends DATA_BLOB implements Structure.ByReference {}
/*      */     public DATA_BLOB() {}
/*      */     public DATA_BLOB(byte[] data) {
/*   75 */       this.pbData = (Pointer)new Memory(data.length);
/*   76 */       this.pbData.write(0L, data, 0, data.length);
/*   77 */       this.cbData = data.length;
/*   78 */       allocateMemory();
/*      */     }
/*      */     
/*      */     public DATA_BLOB(String s) {
/*   82 */       this(Native.toByteArray(s));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getData() {
/*   91 */       return (this.pbData == null) ? null : this.pbData.getByteArray(0L, this.cbData);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwErrorStatus", "dwInfoStatus"})
/*      */   public static class CERT_TRUST_STATUS
/*      */     extends Structure
/*      */   {
/*      */     public int dwErrorStatus;
/*      */     
/*      */     public int dwInfoStatus;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_TRUST_STATUS
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"SubjectIdentifier", "cAttribute", "rgAttribute"})
/*      */   public static class CTL_ENTRY
/*      */     extends Structure
/*      */   {
/*      */     public WinCrypt.DATA_BLOB SubjectIdentifier;
/*      */     
/*      */     public int cAttribute;
/*      */     
/*      */     public Pointer rgAttribute;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CTL_ENTRY
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public WinCrypt.CRYPT_ATTRIBUTE[] getRgAttribute() {
/*  129 */       if (this.cAttribute == 0) {
/*  130 */         return new WinCrypt.CRYPT_ATTRIBUTE[0];
/*      */       }
/*  132 */       return (WinCrypt.CRYPT_ATTRIBUTE[])((WinCrypt.CERT_EXTENSION)Structure.newInstance(WinCrypt.CERT_EXTENSION.class, this.rgAttribute))
/*      */ 
/*      */         
/*  135 */         .toArray(this.cAttribute);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "pBaseCRLContext", "pDeltaCRLContext", "pCrlEntry", "fDeltaCrlEntry"})
/*      */   public static class CERT_REVOCATION_CRL_INFO
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */     
/*      */     public WinCrypt.CRL_CONTEXT.ByReference pBaseCRLContext;
/*      */     
/*      */     public WinCrypt.CRL_CONTEXT.ByReference pDeltaCRLContext;
/*      */     
/*      */     public WinCrypt.CRL_ENTRY.ByReference pCrlEntry;
/*      */     
/*      */     public boolean fDeltaCrlEntry;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_REVOCATION_CRL_INFO
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public CERT_REVOCATION_CRL_INFO() {
/*  161 */       super(W32APITypeMapper.DEFAULT);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "dwRevocationResult", "pszRevocationOid", "pvOidSpecificInfo", "fHasFreshnessTime", "dwFreshnessTime", "pCrlInfo"})
/*      */   public static class CERT_REVOCATION_INFO
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */     
/*      */     public int dwRevocationResult;
/*      */     
/*      */     public String pszRevocationOid;
/*      */     
/*      */     public Pointer pvOidSpecificInfo;
/*      */     
/*      */     public boolean fHasFreshnessTime;
/*      */     public int dwFreshnessTime;
/*      */     public WinCrypt.CERT_REVOCATION_CRL_INFO.ByReference pCrlInfo;
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_REVOCATION_INFO
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public CERT_REVOCATION_INFO() {
/*  187 */       super(W32APITypeMapper.ASCII);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "pCertContext", "TrustStatus", "pRevocationInfo", "pIssuanceUsage", "pApplicationUsage", "pwszExtendedErrorInfo"})
/*      */   public static class CERT_CHAIN_ELEMENT
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */     
/*      */     public WinCrypt.CERT_CONTEXT.ByReference pCertContext;
/*      */     
/*      */     public WinCrypt.CERT_TRUST_STATUS TrustStatus;
/*      */     
/*      */     public WinCrypt.CERT_REVOCATION_INFO.ByReference pRevocationInfo;
/*      */     
/*      */     public WinCrypt.CTL_USAGE.ByReference pIssuanceUsage;
/*      */     
/*      */     public WinCrypt.CTL_USAGE.ByReference pApplicationUsage;
/*      */     
/*      */     public String pwszExtendedErrorInfo;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_CHAIN_ELEMENT
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public CERT_CHAIN_ELEMENT() {
/*  217 */       super(W32APITypeMapper.UNICODE);
/*      */     }
/*      */     
/*      */     public CERT_CHAIN_ELEMENT(Pointer p) {
/*  221 */       super(p, 0, W32APITypeMapper.UNICODE);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwVersion", "SubjectUsage", "ListIdentifier", "SequenceNumber", "ThisUpdate", "NextUpdate", "SubjectAlgorithm", "cCTLEntry", "rgCTLEntry", "cExtension", "rgExtension"})
/*      */   public static class CTL_INFO
/*      */     extends Structure
/*      */   {
/*      */     public int dwVersion;
/*      */     
/*      */     public WinCrypt.CTL_USAGE SubjectUsage;
/*      */     
/*      */     public WinCrypt.DATA_BLOB ListIdentifier;
/*      */     
/*      */     public WinCrypt.DATA_BLOB SequenceNumber;
/*      */     
/*      */     public WinBase.FILETIME ThisUpdate;
/*      */     
/*      */     public WinBase.FILETIME NextUpdate;
/*      */     public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER SubjectAlgorithm;
/*      */     public int cCTLEntry;
/*      */     public Pointer rgCTLEntry;
/*      */     public int cExtension;
/*      */     public Pointer rgExtension;
/*      */     
/*      */     public static class ByReference
/*      */       extends CTL_INFO
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public WinCrypt.CTL_ENTRY[] getRgExtension() {
/*  252 */       if (this.cCTLEntry == 0) {
/*  253 */         return new WinCrypt.CTL_ENTRY[0];
/*      */       }
/*  255 */       return (WinCrypt.CTL_ENTRY[])((WinCrypt.CTL_ENTRY)Structure.newInstance(WinCrypt.CTL_ENTRY.class, this.rgCTLEntry))
/*      */ 
/*      */         
/*  258 */         .toArray(this.cCTLEntry);
/*      */     }
/*      */ 
/*      */     
/*      */     public WinCrypt.CERT_EXTENSION[] getRgCTLEntry() {
/*  263 */       if (this.cExtension == 0) {
/*  264 */         return new WinCrypt.CERT_EXTENSION[0];
/*      */       }
/*  266 */       return (WinCrypt.CERT_EXTENSION[])((WinCrypt.CERT_EXTENSION)Structure.newInstance(WinCrypt.CERT_EXTENSION.class, this.rgExtension))
/*      */ 
/*      */         
/*  269 */         .toArray(this.cExtension);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwMsgAndCertEncodingType", "pbCtlEncoded", "cbCtlEncoded", "pCtlInfo", "hCertStore", "hCryptMsg", "pbCtlContent", "cbCtlContent"})
/*      */   public static class CTL_CONTEXT
/*      */     extends Structure
/*      */   {
/*      */     public int dwMsgAndCertEncodingType;
/*      */ 
/*      */     
/*      */     public Pointer pbCtlEncoded;
/*      */ 
/*      */     
/*      */     public int cbCtlEncoded;
/*      */ 
/*      */     
/*      */     public WinCrypt.CTL_INFO.ByReference pCtlInfo;
/*      */ 
/*      */     
/*      */     public WinCrypt.HCERTSTORE hCertStore;
/*      */     
/*      */     public WinCrypt.HCRYPTMSG hCryptMsg;
/*      */     
/*      */     public Pointer pbCtlContent;
/*      */     
/*      */     public int cbCtlContent;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CTL_CONTEXT
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "pCtlEntry", "pCtlContext"})
/*      */   public static class CERT_TRUST_LIST_INFO
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */     
/*      */     public WinCrypt.CTL_ENTRY.ByReference pCtlEntry;
/*      */     
/*      */     public WinCrypt.CTL_CONTEXT.ByReference pCtlContext;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_TRUST_LIST_INFO
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cUsageIdentifier", "rgpszUsageIdentifier"})
/*      */   public static class CTL_USAGE
/*      */     extends Structure
/*      */   {
/*      */     public int cUsageIdentifier;
/*      */     
/*      */     public Pointer rgpszUsageIdentifier;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CTL_USAGE
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public String[] getRgpszUsageIdentier() {
/*  338 */       if (this.cUsageIdentifier == 0) {
/*  339 */         return new String[0];
/*      */       }
/*  341 */       return this.rgpszUsageIdentifier.getStringArray(0L, this.cUsageIdentifier);
/*      */     }
/*      */ 
/*      */     
/*      */     public void setRgpszUsageIdentier(String[] array) {
/*  346 */       if (array == null || array.length == 0) {
/*  347 */         this.cUsageIdentifier = 0;
/*  348 */         this.rgpszUsageIdentifier = null;
/*      */       } else {
/*  350 */         this.cUsageIdentifier = array.length;
/*  351 */         this.rgpszUsageIdentifier = (Pointer)new StringArray(array);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwType", "Usage"})
/*      */   public static class CERT_USAGE_MATCH
/*      */     extends Structure
/*      */   {
/*      */     public int dwType;
/*      */     
/*      */     public WinCrypt.CTL_USAGE Usage;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_USAGE_MATCH
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "RequestedUsage", "RequestedIssuancePolicy", "dwUrlRetrievalTimeout", "fCheckRevocationFreshnessTime", "dwRevocationFreshnessTime", "pftCacheResync", "pStrongSignPara", "dwStrongSignFlags"})
/*      */   public static class CERT_CHAIN_PARA
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */     
/*      */     public WinCrypt.CERT_USAGE_MATCH RequestedUsage;
/*      */     
/*      */     public WinCrypt.CERT_USAGE_MATCH RequestedIssuancePolicy;
/*      */     
/*      */     public int dwUrlRetrievalTimeout;
/*      */     
/*      */     public boolean fCheckRevocationFreshnessTime;
/*      */     
/*      */     public int dwRevocationFreshnessTime;
/*      */     
/*      */     public WinBase.FILETIME.ByReference pftCacheResync;
/*      */     
/*      */     public WinCrypt.CERT_STRONG_SIGN_PARA.ByReference pStrongSignPara;
/*      */     public int dwStrongSignFlags;
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_CHAIN_PARA
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public CERT_CHAIN_PARA() {
/*  398 */       super(W32APITypeMapper.DEFAULT);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "dwInfoChoice", "DUMMYUNIONNAME"})
/*      */   public static class CERT_STRONG_SIGN_PARA
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */     
/*      */     public int dwInfoChoice;
/*      */     
/*      */     public DUMMYUNION DUMMYUNIONNAME;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends WinCrypt.CERT_CHAIN_PARA
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public class DUMMYUNION
/*      */       extends Union
/*      */     {
/*      */       Pointer pvInfo;
/*      */       
/*      */       WinCrypt.CERT_STRONG_SIGN_SERIALIZED_INFO.ByReference pSerializedInfo;
/*      */       
/*      */       WTypes.LPSTR pszOID;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwFlags", "pwszCNGSignHashAlgids", "pwszCNGPubKeyMinBitLengths"})
/*      */   public static class CERT_STRONG_SIGN_SERIALIZED_INFO
/*      */     extends Structure
/*      */   {
/*      */     public int dwFlags;
/*      */     
/*      */     public String pwszCNGSignHashAlgids;
/*      */     public String pwszCNGPubKeyMinBitLengths;
/*      */     
/*      */     public static class ByReference
/*      */       extends WinCrypt.CERT_CHAIN_PARA
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public CERT_STRONG_SIGN_SERIALIZED_INFO() {
/*  445 */       super(W32APITypeMapper.UNICODE);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "dwError", "lChainIndex", "lElementIndex", "pvExtraPolicyStatus"})
/*      */   public static class CERT_CHAIN_POLICY_STATUS
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */     
/*      */     public int dwError;
/*      */     
/*      */     public int lChainIndex;
/*      */     
/*      */     public int lElementIndex;
/*      */     
/*      */     public Pointer pvExtraPolicyStatus;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_CHAIN_POLICY_STATUS
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "TrustStatus", "cElement", "rgpElement", "pTrustListInfo", "fHasRevocationFreshnessTime", "dwRevocationFreshnessTime"})
/*      */   public static class CERT_SIMPLE_CHAIN
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */     
/*      */     public WinCrypt.CERT_TRUST_STATUS TrustStatus;
/*      */     
/*      */     public int cElement;
/*      */     
/*      */     public Pointer rgpElement;
/*      */     
/*      */     public WinCrypt.CERT_TRUST_LIST_INFO.ByReference pTrustListInfo;
/*      */     
/*      */     public boolean fHasRevocationFreshnessTime;
/*      */     public int dwRevocationFreshnessTime;
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_SIMPLE_CHAIN
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public CERT_SIMPLE_CHAIN() {
/*  493 */       super(W32APITypeMapper.DEFAULT);
/*      */     }
/*      */     
/*      */     public WinCrypt.CERT_CHAIN_ELEMENT[] getRgpElement() {
/*  497 */       WinCrypt.CERT_CHAIN_ELEMENT[] elements = new WinCrypt.CERT_CHAIN_ELEMENT[this.cElement];
/*  498 */       for (int i = 0; i < elements.length; i++) {
/*  499 */         elements[i] = (WinCrypt.CERT_CHAIN_ELEMENT)Structure.newInstance(WinCrypt.CERT_CHAIN_ELEMENT.class, this.rgpElement
/*      */             
/*  501 */             .getPointer((i * Native.POINTER_SIZE)));
/*  502 */         elements[i].read();
/*      */       } 
/*  504 */       return elements;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "dwFlags", "pvExtraPolicyPara"})
/*      */   public static class CERT_CHAIN_POLICY_PARA
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */     
/*      */     public int dwFlags;
/*      */     
/*      */     public Pointer pvExtraPolicyPara;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_CHAIN_POLICY_PARA
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "TrustStatus", "cChain", "rgpChain", "cLowerQualityChainContext", "rgpLowerQualityChainContext", "fHasRevocationFreshnessTime", "dwRevocationFreshnessTime", "dwCreateFlags", "ChainId"})
/*      */   public static class CERT_CHAIN_CONTEXT
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */     
/*      */     public WinCrypt.CERT_TRUST_STATUS TrustStatus;
/*      */     
/*      */     public int cChain;
/*      */     
/*      */     public Pointer rgpChain;
/*      */     
/*      */     public int cLowerQualityChainContext;
/*      */     
/*      */     public Pointer rgpLowerQualityChainContext;
/*      */     
/*      */     public boolean fHasRevocationFreshnessTime;
/*      */     
/*      */     public int dwRevocationFreshnessTime;
/*      */     
/*      */     public int dwCreateFlags;
/*      */     public Guid.GUID ChainId;
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_CHAIN_CONTEXT
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public WinCrypt.CERT_SIMPLE_CHAIN[] getRgpChain() {
/*  554 */       WinCrypt.CERT_SIMPLE_CHAIN[] elements = new WinCrypt.CERT_SIMPLE_CHAIN[this.cChain];
/*  555 */       for (int i = 0; i < elements.length; i++) {
/*  556 */         elements[i] = (WinCrypt.CERT_SIMPLE_CHAIN)Structure.newInstance(WinCrypt.CERT_SIMPLE_CHAIN.class, this.rgpChain
/*      */             
/*  558 */             .getPointer((i * Native.POINTER_SIZE)));
/*  559 */         elements[i].read();
/*      */       } 
/*  561 */       return elements;
/*      */     }
/*      */     
/*      */     public CERT_CHAIN_CONTEXT[] getRgpLowerQualityChainContext() {
/*  565 */       CERT_CHAIN_CONTEXT[] elements = new CERT_CHAIN_CONTEXT[this.cLowerQualityChainContext];
/*  566 */       for (int i = 0; i < elements.length; i++) {
/*  567 */         elements[i] = (CERT_CHAIN_CONTEXT)Structure.newInstance(CERT_CHAIN_CONTEXT.class, this.rgpLowerQualityChainContext
/*      */             
/*  569 */             .getPointer((i * Native.POINTER_SIZE)));
/*  570 */         elements[i].read();
/*      */       } 
/*  572 */       return elements;
/*      */     }
/*      */     
/*      */     public CERT_CHAIN_CONTEXT() {
/*  576 */       super(W32APITypeMapper.DEFAULT);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwCertEncodingType", "pbCertEncoded", "cbCertEncoded", "pCertInfo", "hCertStore"})
/*      */   public static class CERT_CONTEXT
/*      */     extends Structure
/*      */   {
/*      */     public int dwCertEncodingType;
/*      */ 
/*      */     
/*      */     public Pointer pbCertEncoded;
/*      */ 
/*      */     
/*      */     public int cbCertEncoded;
/*      */     
/*      */     public WinCrypt.CERT_INFO.ByReference pCertInfo;
/*      */     
/*      */     public WinCrypt.HCERTSTORE hCertStore;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_CONTEXT
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"pszObjId", "fCritical", "Value"})
/*      */   public static class CERT_EXTENSION
/*      */     extends Structure
/*      */   {
/*      */     public String pszObjId;
/*      */     
/*      */     public boolean fCritical;
/*      */     
/*      */     public WinCrypt.DATA_BLOB Value;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_EXTENSION
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public CERT_EXTENSION() {
/*  622 */       super(W32APITypeMapper.ASCII);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cExtension", "rgExtension"})
/*      */   public static class CERT_EXTENSIONS
/*      */     extends Structure
/*      */   {
/*      */     public int cExtension;
/*      */     
/*      */     public Pointer rgExtension;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_EXTENSIONS
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public WinCrypt.CERT_EXTENSION[] getRgExtension() {
/*  641 */       WinCrypt.CERT_EXTENSION[] elements = new WinCrypt.CERT_EXTENSION[this.cExtension];
/*  642 */       for (int i = 0; i < elements.length; i++) {
/*  643 */         elements[i] = (WinCrypt.CERT_EXTENSION)Structure.newInstance(WinCrypt.CERT_EXTENSION.class, this.rgExtension
/*      */             
/*  645 */             .getPointer((i * Native.POINTER_SIZE)));
/*  646 */         elements[i].read();
/*      */       } 
/*  648 */       return elements;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwVersion", "SerialNumber", "SignatureAlgorithm", "Issuer", "NotBefore", "NotAfter", "Subject", "SubjectPublicKeyInfo", "IssuerUniqueId", "SubjectUniqueId", "cExtension", "rgExtension"})
/*      */   public static class CERT_INFO
/*      */     extends Structure
/*      */   {
/*      */     public int dwVersion;
/*      */     
/*      */     public WinCrypt.DATA_BLOB SerialNumber;
/*      */     
/*      */     public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
/*      */     
/*      */     public WinCrypt.DATA_BLOB Issuer;
/*      */     
/*      */     public WinBase.FILETIME NotBefore;
/*      */     public WinBase.FILETIME NotAfter;
/*      */     public WinCrypt.DATA_BLOB Subject;
/*      */     public WinCrypt.CERT_PUBLIC_KEY_INFO SubjectPublicKeyInfo;
/*      */     public WinCrypt.CRYPT_BIT_BLOB IssuerUniqueId;
/*      */     public WinCrypt.CRYPT_BIT_BLOB SubjectUniqueId;
/*      */     public int cExtension;
/*      */     public Pointer rgExtension;
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_INFO
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public WinCrypt.CERT_EXTENSION[] getRgExtension() {
/*  679 */       WinCrypt.CERT_EXTENSION[] elements = new WinCrypt.CERT_EXTENSION[this.cExtension];
/*  680 */       for (int i = 0; i < elements.length; i++) {
/*  681 */         elements[i] = (WinCrypt.CERT_EXTENSION)Structure.newInstance(WinCrypt.CERT_EXTENSION.class, this.rgExtension
/*      */             
/*  683 */             .getPointer((i * Native.POINTER_SIZE)));
/*  684 */         elements[i].read();
/*      */       } 
/*  686 */       return elements;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"Algorithm", "PublicKey"})
/*      */   public static class CERT_PUBLIC_KEY_INFO
/*      */     extends Structure
/*      */   {
/*      */     public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER Algorithm;
/*      */     
/*      */     public WinCrypt.CRYPT_BIT_BLOB PublicKey;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CERT_PUBLIC_KEY_INFO
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwCertEncodingType", "pbCrlEncoded", "cbCrlEncoded", "pCrlInfo", "hCertStore"})
/*      */   public static class CRL_CONTEXT
/*      */     extends Structure
/*      */   {
/*      */     public int dwCertEncodingType;
/*      */     
/*      */     public Pointer pbCrlEncoded;
/*      */     
/*      */     public int cbCrlEncoded;
/*      */     
/*      */     public WinCrypt.CRL_INFO.ByReference pCrlInfo;
/*      */     
/*      */     public WinCrypt.HCERTSTORE hCertStore;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CRL_CONTEXT
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"SerialNumber", "RevocationDate", "cExtension", "rgExtension"})
/*      */   public static class CRL_ENTRY
/*      */     extends Structure
/*      */   {
/*      */     public WinCrypt.DATA_BLOB SerialNumber;
/*      */     
/*      */     public WinBase.FILETIME RevocationDate;
/*      */     
/*      */     public int cExtension;
/*      */     
/*      */     public Pointer rgExtension;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CRL_ENTRY
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public WinCrypt.CERT_EXTENSION[] getRgExtension() {
/*  746 */       WinCrypt.CERT_EXTENSION[] elements = new WinCrypt.CERT_EXTENSION[this.cExtension];
/*  747 */       for (int i = 0; i < elements.length; i++) {
/*  748 */         elements[i] = (WinCrypt.CERT_EXTENSION)Structure.newInstance(WinCrypt.CERT_EXTENSION.class, this.rgExtension
/*      */             
/*  750 */             .getPointer((i * Native.POINTER_SIZE)));
/*  751 */         elements[i].read();
/*      */       } 
/*  753 */       return elements;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwVersion", "SignatureAlgorithm", "Issuer", "ThisUpdate", "NextUpdate", "cCRLEntry", "rgCRLEntry", "cExtension", "rgExtension"})
/*      */   public static class CRL_INFO
/*      */     extends Structure
/*      */   {
/*      */     public int dwVersion;
/*      */     
/*      */     public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
/*      */     
/*      */     public WinCrypt.DATA_BLOB Issuer;
/*      */     
/*      */     public WinBase.FILETIME ThisUpdate;
/*      */     
/*      */     public WinBase.FILETIME NextUpdate;
/*      */     public int cCRLEntry;
/*      */     public Pointer rgCRLEntry;
/*      */     public int cExtension;
/*      */     public Pointer rgExtension;
/*      */     
/*      */     public static class ByReference
/*      */       extends CRL_INFO
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public WinCrypt.CRL_ENTRY[] getRgCRLEntry() {
/*  781 */       WinCrypt.CRL_ENTRY[] elements = new WinCrypt.CRL_ENTRY[this.cCRLEntry];
/*  782 */       for (int i = 0; i < elements.length; i++) {
/*  783 */         elements[i] = (WinCrypt.CRL_ENTRY)Structure.newInstance(WinCrypt.CRL_ENTRY.class, this.rgCRLEntry
/*      */             
/*  785 */             .getPointer((i * Native.POINTER_SIZE)));
/*  786 */         elements[i].read();
/*      */       } 
/*  788 */       return elements;
/*      */     }
/*      */     
/*      */     public WinCrypt.CERT_EXTENSION[] getRgExtension() {
/*  792 */       WinCrypt.CERT_EXTENSION[] elements = new WinCrypt.CERT_EXTENSION[this.cExtension];
/*  793 */       for (int i = 0; i < elements.length; i++) {
/*  794 */         elements[i] = (WinCrypt.CERT_EXTENSION)Structure.newInstance(WinCrypt.CERT_EXTENSION.class, this.rgExtension
/*      */             
/*  796 */             .getPointer((i * Native.POINTER_SIZE)));
/*  797 */         elements[i].read();
/*      */       } 
/*  799 */       return elements;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"pszObjId", "Parameters"})
/*      */   public static class CRYPT_ALGORITHM_IDENTIFIER
/*      */     extends Structure
/*      */   {
/*      */     public String pszObjId;
/*      */ 
/*      */     
/*      */     public WinCrypt.DATA_BLOB Parameters;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CRYPT_ALGORITHM_IDENTIFIER
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */     
/*      */     public CRYPT_ALGORITHM_IDENTIFIER() {
/*  821 */       super(W32APITypeMapper.ASCII);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"pszObjId", "cValue", "rgValue"})
/*      */   public static class CRYPT_ATTRIBUTE
/*      */     extends Structure
/*      */   {
/*      */     public String pszObjId;
/*      */     
/*      */     public int cValue;
/*      */     
/*      */     public WinCrypt.DATA_BLOB.ByReference rgValue;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CRYPT_ATTRIBUTE
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public WinCrypt.DATA_BLOB[] getRgValue() {
/*  842 */       return (WinCrypt.DATA_BLOB[])this.rgValue.toArray(this.cValue);
/*      */     }
/*      */     
/*      */     public CRYPT_ATTRIBUTE() {
/*  846 */       super(W32APITypeMapper.ASCII);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbData", "pbData", "cUnusedBits"})
/*      */   public static class CRYPT_BIT_BLOB
/*      */     extends Structure
/*      */   {
/*      */     public int cbData;
/*      */     
/*      */     public Pointer pbData;
/*      */     
/*      */     public int cUnusedBits;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CRYPT_BIT_BLOB
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"pwszContainerName", "pwszProvName", "dwProvType", "dwFlags", "cProvParam", "rgProvParam", "dwKeySpec"})
/*      */   public static class CRYPT_KEY_PROV_INFO
/*      */     extends Structure
/*      */   {
/*      */     public String pwszContainerName;
/*      */     
/*      */     public String pwszProvName;
/*      */     
/*      */     public int dwProvType;
/*      */     
/*      */     public int dwFlags;
/*      */     
/*      */     public int cProvParam;
/*      */     public Pointer rgProvParam;
/*      */     public int dwKeySpec;
/*      */     
/*      */     public static class ByReference
/*      */       extends CRYPT_KEY_PROV_INFO
/*      */       implements Structure.ByReference {}
/*      */     
/*      */     public CRYPT_KEY_PROV_INFO() {
/*  889 */       super(W32APITypeMapper.UNICODE);
/*      */     }
/*      */     
/*      */     public WinCrypt.CRYPT_KEY_PROV_PARAM[] getRgProvParam() {
/*  893 */       WinCrypt.CRYPT_KEY_PROV_PARAM[] elements = new WinCrypt.CRYPT_KEY_PROV_PARAM[this.cProvParam];
/*  894 */       for (int i = 0; i < elements.length; i++) {
/*  895 */         elements[i] = (WinCrypt.CRYPT_KEY_PROV_PARAM)Structure.newInstance(WinCrypt.CRYPT_KEY_PROV_PARAM.class, this.rgProvParam
/*      */             
/*  897 */             .getPointer((i * Native.POINTER_SIZE)));
/*  898 */         elements[i].read();
/*      */       } 
/*  900 */       return elements;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwParam", "pbData", "cbData", "dwFlags"})
/*      */   public static class CRYPT_KEY_PROV_PARAM
/*      */     extends Structure
/*      */   {
/*      */     public int dwParam;
/*      */     
/*      */     public Pointer pbData;
/*      */     
/*      */     public int cbData;
/*      */     
/*      */     public int dwFlags;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CRYPT_KEY_PROV_PARAM
/*      */       implements Structure.ByReference {}
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "dwMsgEncodingType", "pSigningCert", "HashAlgorithm", "pvHashAuxInfo", "cMsgCert", "rgpMsgCert", "cMsgCrl", "rgpMsgCrl", "cAuthAttr", "rgAuthAttr", "cUnauthAttr", "rgUnauthAttr", "dwFlags", "dwInnerContentType", "HashEncryptionAlgorithm", "pvHashEncryptionAuxInfo"})
/*      */   public static class CRYPT_SIGN_MESSAGE_PARA
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */     
/*      */     public int dwMsgEncodingType;
/*      */     
/*      */     public WinCrypt.CERT_CONTEXT.ByReference pSigningCert;
/*      */     
/*      */     public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER HashAlgorithm;
/*      */     
/*      */     public Pointer pvHashAuxInfo;
/*      */     
/*      */     public int cMsgCert;
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends CRYPT_SIGN_MESSAGE_PARA
/*      */       implements Structure.ByReference {}
/*      */     
/*  945 */     public Pointer rgpMsgCert = null;
/*      */     public int cMsgCrl;
/*  947 */     public Pointer rgpMsgCrl = null;
/*      */     public int cAuthAttr;
/*  949 */     public Pointer rgAuthAttr = null;
/*      */     public int cUnauthAttr;
/*  951 */     public Pointer rgUnauthAttr = null;
/*      */     public int dwFlags;
/*      */     public int dwInnerContentType;
/*      */     public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER HashEncryptionAlgorithm;
/*      */     public Pointer pvHashEncryptionAuxInfo;
/*      */     
/*      */     public WinCrypt.CERT_CONTEXT[] getRgpMsgCert() {
/*  958 */       WinCrypt.CERT_CONTEXT[] elements = new WinCrypt.CERT_CONTEXT[this.cMsgCrl];
/*  959 */       for (int i = 0; i < elements.length; i++) {
/*  960 */         elements[i] = (WinCrypt.CERT_CONTEXT)Structure.newInstance(WinCrypt.CERT_CONTEXT.class, this.rgpMsgCert
/*      */             
/*  962 */             .getPointer((i * Native.POINTER_SIZE)));
/*  963 */         elements[i].read();
/*      */       } 
/*  965 */       return elements;
/*      */     }
/*      */     
/*      */     public WinCrypt.CRL_CONTEXT[] getRgpMsgCrl() {
/*  969 */       WinCrypt.CRL_CONTEXT[] elements = new WinCrypt.CRL_CONTEXT[this.cMsgCrl];
/*  970 */       for (int i = 0; i < elements.length; i++) {
/*  971 */         elements[i] = (WinCrypt.CRL_CONTEXT)Structure.newInstance(WinCrypt.CRL_CONTEXT.class, this.rgpMsgCrl
/*      */             
/*  973 */             .getPointer((i * Native.POINTER_SIZE)));
/*  974 */         elements[i].read();
/*      */       } 
/*  976 */       return elements;
/*      */     }
/*      */     
/*      */     public WinCrypt.CRYPT_ATTRIBUTE[] getRgAuthAttr() {
/*  980 */       if (this.cAuthAttr == 0) {
/*  981 */         return new WinCrypt.CRYPT_ATTRIBUTE[0];
/*      */       }
/*  983 */       return (WinCrypt.CRYPT_ATTRIBUTE[])((WinCrypt.CRYPT_ATTRIBUTE)Structure.newInstance(WinCrypt.CRYPT_ATTRIBUTE.class, this.rgAuthAttr))
/*      */ 
/*      */         
/*  986 */         .toArray(this.cAuthAttr);
/*      */     }
/*      */ 
/*      */     
/*      */     public WinCrypt.CRYPT_ATTRIBUTE[] getRgUnauthAttr() {
/*  991 */       if (this.cUnauthAttr == 0) {
/*  992 */         return new WinCrypt.CRYPT_ATTRIBUTE[0];
/*      */       }
/*  994 */       return (WinCrypt.CRYPT_ATTRIBUTE[])((WinCrypt.CRYPT_ATTRIBUTE)Structure.newInstance(WinCrypt.CRYPT_ATTRIBUTE.class, this.rgUnauthAttr))
/*      */ 
/*      */         
/*  997 */         .toArray(this.cUnauthAttr);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface CryptGetSignerCertificateCallback
/*      */     extends StdCallLibrary.StdCallCallback
/*      */   {
/*      */     WinCrypt.CERT_CONTEXT.ByReference callback(Pointer param1Pointer, int param1Int, WinCrypt.CERT_INFO param1CERT_INFO, WinCrypt.HCERTSTORE param1HCERTSTORE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "dwMsgAndCertEncodingType", "hCryptProv", "pfnGetSignerCertificate", "pvGetArg", "pStrongSignPara"})
/*      */   public static class CRYPT_VERIFY_MESSAGE_PARA
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int dwMsgAndCertEncodingType;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinCrypt.HCRYPTPROV_LEGACY hCryptProv;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinCrypt.CryptGetSignerCertificateCallback pfnGetSignerCertificate;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Pointer pvGetArg;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinCrypt.CERT_STRONG_SIGN_PARA.ByReference pStrongSignPara;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends WinCrypt.CRYPT_SIGN_MESSAGE_PARA
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void write() {
/* 1062 */       this.cbSize = size();
/* 1063 */       super.write();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class HCERTCHAINENGINE
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HCERTCHAINENGINE() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HCERTCHAINENGINE(Pointer p) {
/* 1085 */       super(p);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class HCERTSTORE
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HCERTSTORE() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HCERTSTORE(Pointer p) {
/* 1108 */       super(p);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class HCRYPTMSG
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public HCRYPTMSG() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HCRYPTMSG(Pointer p) {
/* 1131 */       super(p);
/*      */     }
/*      */   }
/*      */   
/*      */   public static class HCRYPTPROV_LEGACY
/*      */     extends BaseTSD.ULONG_PTR
/*      */   {
/*      */     public HCRYPTPROV_LEGACY() {}
/*      */     
/*      */     public HCRYPTPROV_LEGACY(long value) {
/* 1141 */       super(value);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"cbSize", "dwPromptFlags", "hwndApp", "szPrompt"})
/*      */   public static class CRYPTPROTECT_PROMPTSTRUCT
/*      */     extends Structure
/*      */   {
/*      */     public int cbSize;
/*      */ 
/*      */ 
/*      */     
/*      */     public int dwPromptFlags;
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.HWND hwndApp;
/*      */ 
/*      */ 
/*      */     
/*      */     public String szPrompt;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public CRYPTPROTECT_PROMPTSTRUCT() {
/* 1170 */       super(W32APITypeMapper.DEFAULT);
/*      */     }
/*      */     
/*      */     public CRYPTPROTECT_PROMPTSTRUCT(Pointer memory) {
/* 1174 */       super(memory, 0, W32APITypeMapper.DEFAULT);
/* 1175 */       read();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1466 */   public static final HCERTCHAINENGINE HCCE_CURRENT_USER = new HCERTCHAINENGINE(Pointer.createConstant(0));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1474 */   public static final HCERTCHAINENGINE HCCE_LOCAL_MACHINE = new HCERTCHAINENGINE(Pointer.createConstant(1));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1482 */   public static final HCERTCHAINENGINE HCCE_SERIAL_LOCAL_MACHINE = new HCERTCHAINENGINE(Pointer.createConstant(2));
/*      */   public static final int CERT_COMPARE_SHIFT = 16;
/*      */   public static final int CERT_COMPARE_NAME_STR_W = 8;
/*      */   public static final int CERT_INFO_SUBJECT_FLAG = 7;
/*      */   public static final int CERT_FIND_SUBJECT_STR_W = 524295;
/*      */   public static final int CERT_FIND_SUBJECT_STR = 524295;
/*      */   public static final int CRYPT_EXPORTABLE = 1;
/*      */   public static final int CRYPT_USER_PROTECTED = 2;
/*      */   public static final int CRYPT_MACHINE_KEYSET = 32;
/*      */   public static final int CRYPT_USER_KEYSET = 4096;
/*      */   public static final int PKCS12_PREFER_CNG_KSP = 256;
/*      */   public static final int PKCS12_ALWAYS_CNG_KSP = 512;
/*      */   public static final int PKCS12_ALLOW_OVERWRITE_KEY = 16384;
/*      */   public static final int PKCS12_NO_PERSIST_KEY = 32768;
/*      */   public static final int PKCS12_INCLUDE_EXTENDED_PROPERTIES = 16;
/*      */   public static final int CERT_CLOSE_STORE_FORCE_FLAG = 1;
/*      */   public static final int CERT_CLOSE_STORE_CHECK_FLAG = 2;
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\WinCrypt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */