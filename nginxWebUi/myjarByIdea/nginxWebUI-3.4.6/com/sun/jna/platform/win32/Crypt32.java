package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Crypt32 extends StdCallLibrary {
   Crypt32 INSTANCE = (Crypt32)Native.load("Crypt32", Crypt32.class, W32APIOptions.DEFAULT_OPTIONS);

   boolean CryptProtectData(WinCrypt.DATA_BLOB var1, String var2, WinCrypt.DATA_BLOB var3, Pointer var4, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT var5, int var6, WinCrypt.DATA_BLOB var7);

   boolean CryptUnprotectData(WinCrypt.DATA_BLOB var1, PointerByReference var2, WinCrypt.DATA_BLOB var3, Pointer var4, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT var5, int var6, WinCrypt.DATA_BLOB var7);

   boolean CertAddEncodedCertificateToSystemStore(String var1, Pointer var2, int var3);

   WinCrypt.HCERTSTORE CertOpenSystemStore(Pointer var1, String var2);

   boolean CryptSignMessage(WinCrypt.CRYPT_SIGN_MESSAGE_PARA var1, boolean var2, int var3, Pointer[] var4, int[] var5, Pointer var6, IntByReference var7);

   boolean CryptVerifyMessageSignature(WinCrypt.CRYPT_VERIFY_MESSAGE_PARA var1, int var2, Pointer var3, int var4, Pointer var5, IntByReference var6, PointerByReference var7);

   boolean CertGetCertificateChain(WinCrypt.HCERTCHAINENGINE var1, WinCrypt.CERT_CONTEXT var2, WinBase.FILETIME var3, WinCrypt.HCERTSTORE var4, WinCrypt.CERT_CHAIN_PARA var5, int var6, Pointer var7, PointerByReference var8);

   boolean CertFreeCertificateContext(WinCrypt.CERT_CONTEXT var1);

   void CertFreeCertificateChain(WinCrypt.CERT_CHAIN_CONTEXT var1);

   boolean CertCloseStore(WinCrypt.HCERTSTORE var1, int var2);

   int CertNameToStr(int var1, WinCrypt.DATA_BLOB var2, int var3, Pointer var4, int var5);

   boolean CertVerifyCertificateChainPolicy(WTypes.LPSTR var1, WinCrypt.CERT_CHAIN_CONTEXT var2, WinCrypt.CERT_CHAIN_POLICY_PARA var3, WinCrypt.CERT_CHAIN_POLICY_STATUS var4);

   WinCrypt.CERT_CONTEXT.ByReference CertFindCertificateInStore(WinCrypt.HCERTSTORE var1, int var2, int var3, int var4, Pointer var5, WinCrypt.CERT_CONTEXT var6);

   WinCrypt.HCERTSTORE PFXImportCertStore(WinCrypt.DATA_BLOB var1, WTypes.LPWSTR var2, int var3);
}
