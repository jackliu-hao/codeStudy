package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APITypeMapper;

public interface WinCrypt {
   int CRYPTPROTECT_PROMPT_ON_UNPROTECT = 1;
   int CRYPTPROTECT_PROMPT_ON_PROTECT = 2;
   int CRYPTPROTECT_PROMPT_RESERVED = 4;
   int CRYPTPROTECT_PROMPT_STRONG = 8;
   int CRYPTPROTECT_PROMPT_REQUIRE_STRONG = 16;
   int CRYPTPROTECT_UI_FORBIDDEN = 1;
   int CRYPTPROTECT_LOCAL_MACHINE = 4;
   int CRYPTPROTECT_CRED_SYNC = 8;
   int CRYPTPROTECT_AUDIT = 16;
   int CRYPTPROTECT_NO_RECOVERY = 32;
   int CRYPTPROTECT_VERIFY_PROTECTION = 64;
   int CRYPTPROTECT_CRED_REGENERATE = 128;
   int CRYPT_E_ASN1_ERROR = -2146881280;
   int CRYPT_E_ASN1_INTERNAL = -2146881279;
   int CRYPT_E_ASN1_EOD = -2146881278;
   int CRYPT_E_ASN1_CORRUPT = -2146881277;
   int CRYPT_E_ASN1_LARGE = -2146881276;
   int CRYPT_E_ASN1_CONSTRAINT = -2146881275;
   int CRYPT_E_ASN1_MEMORY = -2146881274;
   int CRYPT_E_ASN1_OVERFLOW = -2146881273;
   int CRYPT_E_ASN1_BADPDU = -2146881272;
   int CRYPT_E_ASN1_BADARGS = -2146881271;
   int CRYPT_E_ASN1_BADREAL = -2146881270;
   int CRYPT_E_ASN1_BADTAG = -2146881269;
   int CRYPT_E_ASN1_CHOICE = -2146881268;
   int CRYPT_E_ASN1_RULE = -2146881267;
   int CRYPT_E_ASN1_UTF8 = -2146881266;
   int CRYPT_E_ASN1_PDU_TYPE = -2146881229;
   int CRYPT_E_ASN1_NYI = -2146881228;
   int CRYPT_E_ASN1_EXTENDED = -2146881023;
   int CRYPT_E_ASN1_NOEOD = -2146881022;
   int CRYPT_ASN_ENCODING = 1;
   int CRYPT_NDR_ENCODING = 2;
   int X509_ASN_ENCODING = 1;
   int X509_NDR_ENCODING = 2;
   int PKCS_7_ASN_ENCODING = 65536;
   int PKCS_7_NDR_ENCODING = 131072;
   int USAGE_MATCH_TYPE_AND = 0;
   int USAGE_MATCH_TYPE_OR = 1;
   int PP_CLIENT_HWND = 1;
   int CERT_SIMPLE_NAME_STR = 1;
   int CERT_OID_NAME_STR = 2;
   int CERT_X500_NAME_STR = 3;
   int CERT_XML_NAME_STR = 4;
   int CERT_CHAIN_POLICY_BASE = 1;
   String szOID_RSA_SHA1RSA = "1.2.840.113549.1.1.5";
   HCERTCHAINENGINE HCCE_CURRENT_USER = new HCERTCHAINENGINE(Pointer.createConstant(0));
   HCERTCHAINENGINE HCCE_LOCAL_MACHINE = new HCERTCHAINENGINE(Pointer.createConstant(1));
   HCERTCHAINENGINE HCCE_SERIAL_LOCAL_MACHINE = new HCERTCHAINENGINE(Pointer.createConstant(2));
   int CERT_COMPARE_SHIFT = 16;
   int CERT_COMPARE_NAME_STR_W = 8;
   int CERT_INFO_SUBJECT_FLAG = 7;
   int CERT_FIND_SUBJECT_STR_W = 524295;
   int CERT_FIND_SUBJECT_STR = 524295;
   int CRYPT_EXPORTABLE = 1;
   int CRYPT_USER_PROTECTED = 2;
   int CRYPT_MACHINE_KEYSET = 32;
   int CRYPT_USER_KEYSET = 4096;
   int PKCS12_PREFER_CNG_KSP = 256;
   int PKCS12_ALWAYS_CNG_KSP = 512;
   int PKCS12_ALLOW_OVERWRITE_KEY = 16384;
   int PKCS12_NO_PERSIST_KEY = 32768;
   int PKCS12_INCLUDE_EXTENDED_PROPERTIES = 16;
   int CERT_CLOSE_STORE_FORCE_FLAG = 1;
   int CERT_CLOSE_STORE_CHECK_FLAG = 2;

   @Structure.FieldOrder({"cbSize", "dwPromptFlags", "hwndApp", "szPrompt"})
   public static class CRYPTPROTECT_PROMPTSTRUCT extends Structure {
      public int cbSize;
      public int dwPromptFlags;
      public WinDef.HWND hwndApp;
      public String szPrompt;

      public CRYPTPROTECT_PROMPTSTRUCT() {
         super(W32APITypeMapper.DEFAULT);
      }

      public CRYPTPROTECT_PROMPTSTRUCT(Pointer memory) {
         super(memory, 0, W32APITypeMapper.DEFAULT);
         this.read();
      }
   }

   public static class HCRYPTPROV_LEGACY extends BaseTSD.ULONG_PTR {
      public HCRYPTPROV_LEGACY() {
      }

      public HCRYPTPROV_LEGACY(long value) {
         super(value);
      }
   }

   public static class HCRYPTMSG extends WinNT.HANDLE {
      public HCRYPTMSG() {
      }

      public HCRYPTMSG(Pointer p) {
         super(p);
      }
   }

   public static class HCERTSTORE extends WinNT.HANDLE {
      public HCERTSTORE() {
      }

      public HCERTSTORE(Pointer p) {
         super(p);
      }
   }

   public static class HCERTCHAINENGINE extends WinNT.HANDLE {
      public HCERTCHAINENGINE() {
      }

      public HCERTCHAINENGINE(Pointer p) {
         super(p);
      }
   }

   @Structure.FieldOrder({"cbSize", "dwMsgAndCertEncodingType", "hCryptProv", "pfnGetSignerCertificate", "pvGetArg", "pStrongSignPara"})
   public static class CRYPT_VERIFY_MESSAGE_PARA extends Structure {
      public int cbSize;
      public int dwMsgAndCertEncodingType;
      public HCRYPTPROV_LEGACY hCryptProv;
      public CryptGetSignerCertificateCallback pfnGetSignerCertificate;
      public Pointer pvGetArg;
      public CERT_STRONG_SIGN_PARA.ByReference pStrongSignPara;

      public void write() {
         this.cbSize = this.size();
         super.write();
      }

      public static class ByReference extends CRYPT_SIGN_MESSAGE_PARA implements Structure.ByReference {
      }
   }

   public interface CryptGetSignerCertificateCallback extends StdCallLibrary.StdCallCallback {
      CERT_CONTEXT.ByReference callback(Pointer var1, int var2, CERT_INFO var3, HCERTSTORE var4);
   }

   @Structure.FieldOrder({"cbSize", "dwMsgEncodingType", "pSigningCert", "HashAlgorithm", "pvHashAuxInfo", "cMsgCert", "rgpMsgCert", "cMsgCrl", "rgpMsgCrl", "cAuthAttr", "rgAuthAttr", "cUnauthAttr", "rgUnauthAttr", "dwFlags", "dwInnerContentType", "HashEncryptionAlgorithm", "pvHashEncryptionAuxInfo"})
   public static class CRYPT_SIGN_MESSAGE_PARA extends Structure {
      public int cbSize;
      public int dwMsgEncodingType;
      public CERT_CONTEXT.ByReference pSigningCert;
      public CRYPT_ALGORITHM_IDENTIFIER HashAlgorithm;
      public Pointer pvHashAuxInfo;
      public int cMsgCert;
      public Pointer rgpMsgCert = null;
      public int cMsgCrl;
      public Pointer rgpMsgCrl = null;
      public int cAuthAttr;
      public Pointer rgAuthAttr = null;
      public int cUnauthAttr;
      public Pointer rgUnauthAttr = null;
      public int dwFlags;
      public int dwInnerContentType;
      public CRYPT_ALGORITHM_IDENTIFIER HashEncryptionAlgorithm;
      public Pointer pvHashEncryptionAuxInfo;

      public CERT_CONTEXT[] getRgpMsgCert() {
         CERT_CONTEXT[] elements = new CERT_CONTEXT[this.cMsgCrl];

         for(int i = 0; i < elements.length; ++i) {
            elements[i] = (CERT_CONTEXT)Structure.newInstance(CERT_CONTEXT.class, this.rgpMsgCert.getPointer((long)(i * Native.POINTER_SIZE)));
            elements[i].read();
         }

         return elements;
      }

      public CRL_CONTEXT[] getRgpMsgCrl() {
         CRL_CONTEXT[] elements = new CRL_CONTEXT[this.cMsgCrl];

         for(int i = 0; i < elements.length; ++i) {
            elements[i] = (CRL_CONTEXT)Structure.newInstance(CRL_CONTEXT.class, this.rgpMsgCrl.getPointer((long)(i * Native.POINTER_SIZE)));
            elements[i].read();
         }

         return elements;
      }

      public CRYPT_ATTRIBUTE[] getRgAuthAttr() {
         return this.cAuthAttr == 0 ? new CRYPT_ATTRIBUTE[0] : (CRYPT_ATTRIBUTE[])((CRYPT_ATTRIBUTE[])((CRYPT_ATTRIBUTE)Structure.newInstance(CRYPT_ATTRIBUTE.class, this.rgAuthAttr)).toArray(this.cAuthAttr));
      }

      public CRYPT_ATTRIBUTE[] getRgUnauthAttr() {
         return this.cUnauthAttr == 0 ? new CRYPT_ATTRIBUTE[0] : (CRYPT_ATTRIBUTE[])((CRYPT_ATTRIBUTE[])((CRYPT_ATTRIBUTE)Structure.newInstance(CRYPT_ATTRIBUTE.class, this.rgUnauthAttr)).toArray(this.cUnauthAttr));
      }

      public static class ByReference extends CRYPT_SIGN_MESSAGE_PARA implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwParam", "pbData", "cbData", "dwFlags"})
   public static class CRYPT_KEY_PROV_PARAM extends Structure {
      public int dwParam;
      public Pointer pbData;
      public int cbData;
      public int dwFlags;

      public static class ByReference extends CRYPT_KEY_PROV_PARAM implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"pwszContainerName", "pwszProvName", "dwProvType", "dwFlags", "cProvParam", "rgProvParam", "dwKeySpec"})
   public static class CRYPT_KEY_PROV_INFO extends Structure {
      public String pwszContainerName;
      public String pwszProvName;
      public int dwProvType;
      public int dwFlags;
      public int cProvParam;
      public Pointer rgProvParam;
      public int dwKeySpec;

      public CRYPT_KEY_PROV_INFO() {
         super(W32APITypeMapper.UNICODE);
      }

      public CRYPT_KEY_PROV_PARAM[] getRgProvParam() {
         CRYPT_KEY_PROV_PARAM[] elements = new CRYPT_KEY_PROV_PARAM[this.cProvParam];

         for(int i = 0; i < elements.length; ++i) {
            elements[i] = (CRYPT_KEY_PROV_PARAM)Structure.newInstance(CRYPT_KEY_PROV_PARAM.class, this.rgProvParam.getPointer((long)(i * Native.POINTER_SIZE)));
            elements[i].read();
         }

         return elements;
      }

      public static class ByReference extends CRYPT_KEY_PROV_INFO implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbData", "pbData", "cUnusedBits"})
   public static class CRYPT_BIT_BLOB extends Structure {
      public int cbData;
      public Pointer pbData;
      public int cUnusedBits;

      public static class ByReference extends CRYPT_BIT_BLOB implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"pszObjId", "cValue", "rgValue"})
   public static class CRYPT_ATTRIBUTE extends Structure {
      public String pszObjId;
      public int cValue;
      public DATA_BLOB.ByReference rgValue;

      public DATA_BLOB[] getRgValue() {
         return (DATA_BLOB[])((DATA_BLOB[])this.rgValue.toArray(this.cValue));
      }

      public CRYPT_ATTRIBUTE() {
         super(W32APITypeMapper.ASCII);
      }

      public static class ByReference extends CRYPT_ATTRIBUTE implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"pszObjId", "Parameters"})
   public static class CRYPT_ALGORITHM_IDENTIFIER extends Structure {
      public String pszObjId;
      public DATA_BLOB Parameters;

      public CRYPT_ALGORITHM_IDENTIFIER() {
         super(W32APITypeMapper.ASCII);
      }

      public static class ByReference extends CRYPT_ALGORITHM_IDENTIFIER implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwVersion", "SignatureAlgorithm", "Issuer", "ThisUpdate", "NextUpdate", "cCRLEntry", "rgCRLEntry", "cExtension", "rgExtension"})
   public static class CRL_INFO extends Structure {
      public int dwVersion;
      public CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
      public DATA_BLOB Issuer;
      public WinBase.FILETIME ThisUpdate;
      public WinBase.FILETIME NextUpdate;
      public int cCRLEntry;
      public Pointer rgCRLEntry;
      public int cExtension;
      public Pointer rgExtension;

      public CRL_ENTRY[] getRgCRLEntry() {
         CRL_ENTRY[] elements = new CRL_ENTRY[this.cCRLEntry];

         for(int i = 0; i < elements.length; ++i) {
            elements[i] = (CRL_ENTRY)Structure.newInstance(CRL_ENTRY.class, this.rgCRLEntry.getPointer((long)(i * Native.POINTER_SIZE)));
            elements[i].read();
         }

         return elements;
      }

      public CERT_EXTENSION[] getRgExtension() {
         CERT_EXTENSION[] elements = new CERT_EXTENSION[this.cExtension];

         for(int i = 0; i < elements.length; ++i) {
            elements[i] = (CERT_EXTENSION)Structure.newInstance(CERT_EXTENSION.class, this.rgExtension.getPointer((long)(i * Native.POINTER_SIZE)));
            elements[i].read();
         }

         return elements;
      }

      public static class ByReference extends CRL_INFO implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"SerialNumber", "RevocationDate", "cExtension", "rgExtension"})
   public static class CRL_ENTRY extends Structure {
      public DATA_BLOB SerialNumber;
      public WinBase.FILETIME RevocationDate;
      public int cExtension;
      public Pointer rgExtension;

      public CERT_EXTENSION[] getRgExtension() {
         CERT_EXTENSION[] elements = new CERT_EXTENSION[this.cExtension];

         for(int i = 0; i < elements.length; ++i) {
            elements[i] = (CERT_EXTENSION)Structure.newInstance(CERT_EXTENSION.class, this.rgExtension.getPointer((long)(i * Native.POINTER_SIZE)));
            elements[i].read();
         }

         return elements;
      }

      public static class ByReference extends CRL_ENTRY implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwCertEncodingType", "pbCrlEncoded", "cbCrlEncoded", "pCrlInfo", "hCertStore"})
   public static class CRL_CONTEXT extends Structure {
      public int dwCertEncodingType;
      public Pointer pbCrlEncoded;
      public int cbCrlEncoded;
      public CRL_INFO.ByReference pCrlInfo;
      public HCERTSTORE hCertStore;

      public static class ByReference extends CRL_CONTEXT implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"Algorithm", "PublicKey"})
   public static class CERT_PUBLIC_KEY_INFO extends Structure {
      public CRYPT_ALGORITHM_IDENTIFIER Algorithm;
      public CRYPT_BIT_BLOB PublicKey;

      public static class ByReference extends CERT_PUBLIC_KEY_INFO implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwVersion", "SerialNumber", "SignatureAlgorithm", "Issuer", "NotBefore", "NotAfter", "Subject", "SubjectPublicKeyInfo", "IssuerUniqueId", "SubjectUniqueId", "cExtension", "rgExtension"})
   public static class CERT_INFO extends Structure {
      public int dwVersion;
      public DATA_BLOB SerialNumber;
      public CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
      public DATA_BLOB Issuer;
      public WinBase.FILETIME NotBefore;
      public WinBase.FILETIME NotAfter;
      public DATA_BLOB Subject;
      public CERT_PUBLIC_KEY_INFO SubjectPublicKeyInfo;
      public CRYPT_BIT_BLOB IssuerUniqueId;
      public CRYPT_BIT_BLOB SubjectUniqueId;
      public int cExtension;
      public Pointer rgExtension;

      public CERT_EXTENSION[] getRgExtension() {
         CERT_EXTENSION[] elements = new CERT_EXTENSION[this.cExtension];

         for(int i = 0; i < elements.length; ++i) {
            elements[i] = (CERT_EXTENSION)Structure.newInstance(CERT_EXTENSION.class, this.rgExtension.getPointer((long)(i * Native.POINTER_SIZE)));
            elements[i].read();
         }

         return elements;
      }

      public static class ByReference extends CERT_INFO implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cExtension", "rgExtension"})
   public static class CERT_EXTENSIONS extends Structure {
      public int cExtension;
      public Pointer rgExtension;

      public CERT_EXTENSION[] getRgExtension() {
         CERT_EXTENSION[] elements = new CERT_EXTENSION[this.cExtension];

         for(int i = 0; i < elements.length; ++i) {
            elements[i] = (CERT_EXTENSION)Structure.newInstance(CERT_EXTENSION.class, this.rgExtension.getPointer((long)(i * Native.POINTER_SIZE)));
            elements[i].read();
         }

         return elements;
      }

      public static class ByReference extends CERT_EXTENSIONS implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"pszObjId", "fCritical", "Value"})
   public static class CERT_EXTENSION extends Structure {
      public String pszObjId;
      public boolean fCritical;
      public DATA_BLOB Value;

      public CERT_EXTENSION() {
         super(W32APITypeMapper.ASCII);
      }

      public static class ByReference extends CERT_EXTENSION implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwCertEncodingType", "pbCertEncoded", "cbCertEncoded", "pCertInfo", "hCertStore"})
   public static class CERT_CONTEXT extends Structure {
      public int dwCertEncodingType;
      public Pointer pbCertEncoded;
      public int cbCertEncoded;
      public CERT_INFO.ByReference pCertInfo;
      public HCERTSTORE hCertStore;

      public static class ByReference extends CERT_CONTEXT implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbSize", "TrustStatus", "cChain", "rgpChain", "cLowerQualityChainContext", "rgpLowerQualityChainContext", "fHasRevocationFreshnessTime", "dwRevocationFreshnessTime", "dwCreateFlags", "ChainId"})
   public static class CERT_CHAIN_CONTEXT extends Structure {
      public int cbSize;
      public CERT_TRUST_STATUS TrustStatus;
      public int cChain;
      public Pointer rgpChain;
      public int cLowerQualityChainContext;
      public Pointer rgpLowerQualityChainContext;
      public boolean fHasRevocationFreshnessTime;
      public int dwRevocationFreshnessTime;
      public int dwCreateFlags;
      public Guid.GUID ChainId;

      public CERT_SIMPLE_CHAIN[] getRgpChain() {
         CERT_SIMPLE_CHAIN[] elements = new CERT_SIMPLE_CHAIN[this.cChain];

         for(int i = 0; i < elements.length; ++i) {
            elements[i] = (CERT_SIMPLE_CHAIN)Structure.newInstance(CERT_SIMPLE_CHAIN.class, this.rgpChain.getPointer((long)(i * Native.POINTER_SIZE)));
            elements[i].read();
         }

         return elements;
      }

      public CERT_CHAIN_CONTEXT[] getRgpLowerQualityChainContext() {
         CERT_CHAIN_CONTEXT[] elements = new CERT_CHAIN_CONTEXT[this.cLowerQualityChainContext];

         for(int i = 0; i < elements.length; ++i) {
            elements[i] = (CERT_CHAIN_CONTEXT)Structure.newInstance(CERT_CHAIN_CONTEXT.class, this.rgpLowerQualityChainContext.getPointer((long)(i * Native.POINTER_SIZE)));
            elements[i].read();
         }

         return elements;
      }

      public CERT_CHAIN_CONTEXT() {
         super(W32APITypeMapper.DEFAULT);
      }

      public static class ByReference extends CERT_CHAIN_CONTEXT implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbSize", "dwFlags", "pvExtraPolicyPara"})
   public static class CERT_CHAIN_POLICY_PARA extends Structure {
      public int cbSize;
      public int dwFlags;
      public Pointer pvExtraPolicyPara;

      public static class ByReference extends CERT_CHAIN_POLICY_PARA implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbSize", "TrustStatus", "cElement", "rgpElement", "pTrustListInfo", "fHasRevocationFreshnessTime", "dwRevocationFreshnessTime"})
   public static class CERT_SIMPLE_CHAIN extends Structure {
      public int cbSize;
      public CERT_TRUST_STATUS TrustStatus;
      public int cElement;
      public Pointer rgpElement;
      public CERT_TRUST_LIST_INFO.ByReference pTrustListInfo;
      public boolean fHasRevocationFreshnessTime;
      public int dwRevocationFreshnessTime;

      public CERT_SIMPLE_CHAIN() {
         super(W32APITypeMapper.DEFAULT);
      }

      public CERT_CHAIN_ELEMENT[] getRgpElement() {
         CERT_CHAIN_ELEMENT[] elements = new CERT_CHAIN_ELEMENT[this.cElement];

         for(int i = 0; i < elements.length; ++i) {
            elements[i] = (CERT_CHAIN_ELEMENT)Structure.newInstance(CERT_CHAIN_ELEMENT.class, this.rgpElement.getPointer((long)(i * Native.POINTER_SIZE)));
            elements[i].read();
         }

         return elements;
      }

      public static class ByReference extends CERT_SIMPLE_CHAIN implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbSize", "dwError", "lChainIndex", "lElementIndex", "pvExtraPolicyStatus"})
   public static class CERT_CHAIN_POLICY_STATUS extends Structure {
      public int cbSize;
      public int dwError;
      public int lChainIndex;
      public int lElementIndex;
      public Pointer pvExtraPolicyStatus;

      public static class ByReference extends CERT_CHAIN_POLICY_STATUS implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwFlags", "pwszCNGSignHashAlgids", "pwszCNGPubKeyMinBitLengths"})
   public static class CERT_STRONG_SIGN_SERIALIZED_INFO extends Structure {
      public int dwFlags;
      public String pwszCNGSignHashAlgids;
      public String pwszCNGPubKeyMinBitLengths;

      public CERT_STRONG_SIGN_SERIALIZED_INFO() {
         super(W32APITypeMapper.UNICODE);
      }

      public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbSize", "dwInfoChoice", "DUMMYUNIONNAME"})
   public static class CERT_STRONG_SIGN_PARA extends Structure {
      public int cbSize;
      public int dwInfoChoice;
      public DUMMYUNION DUMMYUNIONNAME;

      public class DUMMYUNION extends Union {
         Pointer pvInfo;
         CERT_STRONG_SIGN_SERIALIZED_INFO.ByReference pSerializedInfo;
         WTypes.LPSTR pszOID;
      }

      public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbSize", "RequestedUsage", "RequestedIssuancePolicy", "dwUrlRetrievalTimeout", "fCheckRevocationFreshnessTime", "dwRevocationFreshnessTime", "pftCacheResync", "pStrongSignPara", "dwStrongSignFlags"})
   public static class CERT_CHAIN_PARA extends Structure {
      public int cbSize;
      public CERT_USAGE_MATCH RequestedUsage;
      public CERT_USAGE_MATCH RequestedIssuancePolicy;
      public int dwUrlRetrievalTimeout;
      public boolean fCheckRevocationFreshnessTime;
      public int dwRevocationFreshnessTime;
      public WinBase.FILETIME.ByReference pftCacheResync;
      public CERT_STRONG_SIGN_PARA.ByReference pStrongSignPara;
      public int dwStrongSignFlags;

      public CERT_CHAIN_PARA() {
         super(W32APITypeMapper.DEFAULT);
      }

      public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwType", "Usage"})
   public static class CERT_USAGE_MATCH extends Structure {
      public int dwType;
      public CTL_USAGE Usage;

      public static class ByReference extends CERT_USAGE_MATCH implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cUsageIdentifier", "rgpszUsageIdentifier"})
   public static class CTL_USAGE extends Structure {
      public int cUsageIdentifier;
      public Pointer rgpszUsageIdentifier;

      public String[] getRgpszUsageIdentier() {
         return this.cUsageIdentifier == 0 ? new String[0] : this.rgpszUsageIdentifier.getStringArray(0L, this.cUsageIdentifier);
      }

      public void setRgpszUsageIdentier(String[] array) {
         if (array != null && array.length != 0) {
            this.cUsageIdentifier = array.length;
            this.rgpszUsageIdentifier = new StringArray(array);
         } else {
            this.cUsageIdentifier = 0;
            this.rgpszUsageIdentifier = null;
         }

      }

      public static class ByReference extends CTL_USAGE implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbSize", "pCtlEntry", "pCtlContext"})
   public static class CERT_TRUST_LIST_INFO extends Structure {
      public int cbSize;
      public CTL_ENTRY.ByReference pCtlEntry;
      public CTL_CONTEXT.ByReference pCtlContext;

      public static class ByReference extends CERT_TRUST_LIST_INFO implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwMsgAndCertEncodingType", "pbCtlEncoded", "cbCtlEncoded", "pCtlInfo", "hCertStore", "hCryptMsg", "pbCtlContent", "cbCtlContent"})
   public static class CTL_CONTEXT extends Structure {
      public int dwMsgAndCertEncodingType;
      public Pointer pbCtlEncoded;
      public int cbCtlEncoded;
      public CTL_INFO.ByReference pCtlInfo;
      public HCERTSTORE hCertStore;
      public HCRYPTMSG hCryptMsg;
      public Pointer pbCtlContent;
      public int cbCtlContent;

      public static class ByReference extends CTL_CONTEXT implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwVersion", "SubjectUsage", "ListIdentifier", "SequenceNumber", "ThisUpdate", "NextUpdate", "SubjectAlgorithm", "cCTLEntry", "rgCTLEntry", "cExtension", "rgExtension"})
   public static class CTL_INFO extends Structure {
      public int dwVersion;
      public CTL_USAGE SubjectUsage;
      public DATA_BLOB ListIdentifier;
      public DATA_BLOB SequenceNumber;
      public WinBase.FILETIME ThisUpdate;
      public WinBase.FILETIME NextUpdate;
      public CRYPT_ALGORITHM_IDENTIFIER SubjectAlgorithm;
      public int cCTLEntry;
      public Pointer rgCTLEntry;
      public int cExtension;
      public Pointer rgExtension;

      public CTL_ENTRY[] getRgExtension() {
         return this.cCTLEntry == 0 ? new CTL_ENTRY[0] : (CTL_ENTRY[])((CTL_ENTRY[])((CTL_ENTRY)Structure.newInstance(CTL_ENTRY.class, this.rgCTLEntry)).toArray(this.cCTLEntry));
      }

      public CERT_EXTENSION[] getRgCTLEntry() {
         return this.cExtension == 0 ? new CERT_EXTENSION[0] : (CERT_EXTENSION[])((CERT_EXTENSION[])((CERT_EXTENSION)Structure.newInstance(CERT_EXTENSION.class, this.rgExtension)).toArray(this.cExtension));
      }

      public static class ByReference extends CTL_INFO implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbSize", "pCertContext", "TrustStatus", "pRevocationInfo", "pIssuanceUsage", "pApplicationUsage", "pwszExtendedErrorInfo"})
   public static class CERT_CHAIN_ELEMENT extends Structure {
      public int cbSize;
      public CERT_CONTEXT.ByReference pCertContext;
      public CERT_TRUST_STATUS TrustStatus;
      public CERT_REVOCATION_INFO.ByReference pRevocationInfo;
      public CTL_USAGE.ByReference pIssuanceUsage;
      public CTL_USAGE.ByReference pApplicationUsage;
      public String pwszExtendedErrorInfo;

      public CERT_CHAIN_ELEMENT() {
         super(W32APITypeMapper.UNICODE);
      }

      public CERT_CHAIN_ELEMENT(Pointer p) {
         super(p, 0, W32APITypeMapper.UNICODE);
      }

      public static class ByReference extends CERT_CHAIN_ELEMENT implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbSize", "dwRevocationResult", "pszRevocationOid", "pvOidSpecificInfo", "fHasFreshnessTime", "dwFreshnessTime", "pCrlInfo"})
   public static class CERT_REVOCATION_INFO extends Structure {
      public int cbSize;
      public int dwRevocationResult;
      public String pszRevocationOid;
      public Pointer pvOidSpecificInfo;
      public boolean fHasFreshnessTime;
      public int dwFreshnessTime;
      public CERT_REVOCATION_CRL_INFO.ByReference pCrlInfo;

      public CERT_REVOCATION_INFO() {
         super(W32APITypeMapper.ASCII);
      }

      public static class ByReference extends CERT_REVOCATION_INFO implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbSize", "pBaseCRLContext", "pDeltaCRLContext", "pCrlEntry", "fDeltaCrlEntry"})
   public static class CERT_REVOCATION_CRL_INFO extends Structure {
      public int cbSize;
      public CRL_CONTEXT.ByReference pBaseCRLContext;
      public CRL_CONTEXT.ByReference pDeltaCRLContext;
      public CRL_ENTRY.ByReference pCrlEntry;
      public boolean fDeltaCrlEntry;

      public CERT_REVOCATION_CRL_INFO() {
         super(W32APITypeMapper.DEFAULT);
      }

      public static class ByReference extends CERT_REVOCATION_CRL_INFO implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"SubjectIdentifier", "cAttribute", "rgAttribute"})
   public static class CTL_ENTRY extends Structure {
      public DATA_BLOB SubjectIdentifier;
      public int cAttribute;
      public Pointer rgAttribute;

      public CRYPT_ATTRIBUTE[] getRgAttribute() {
         return this.cAttribute == 0 ? new CRYPT_ATTRIBUTE[0] : (CRYPT_ATTRIBUTE[])((CRYPT_ATTRIBUTE[])((CERT_EXTENSION)Structure.newInstance(CERT_EXTENSION.class, this.rgAttribute)).toArray(this.cAttribute));
      }

      public static class ByReference extends CTL_ENTRY implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwErrorStatus", "dwInfoStatus"})
   public static class CERT_TRUST_STATUS extends Structure {
      public int dwErrorStatus;
      public int dwInfoStatus;

      public static class ByReference extends CERT_TRUST_STATUS implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbData", "pbData"})
   public static class DATA_BLOB extends Structure {
      public int cbData;
      public Pointer pbData;

      public DATA_BLOB() {
      }

      public DATA_BLOB(Pointer memory) {
         super(memory);
         this.read();
      }

      public DATA_BLOB(byte[] data) {
         this.pbData = new Memory((long)data.length);
         this.pbData.write(0L, (byte[])data, 0, data.length);
         this.cbData = data.length;
         this.allocateMemory();
      }

      public DATA_BLOB(String s) {
         this(Native.toByteArray(s));
      }

      public byte[] getData() {
         return this.pbData == null ? null : this.pbData.getByteArray(0L, this.cbData);
      }

      public static class ByReference extends DATA_BLOB implements Structure.ByReference {
      }
   }
}
