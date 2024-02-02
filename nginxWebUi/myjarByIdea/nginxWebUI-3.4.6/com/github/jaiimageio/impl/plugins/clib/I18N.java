package com.github.jaiimageio.impl.plugins.clib;

import com.github.jaiimageio.impl.common.I18NImpl;

final class I18N extends I18NImpl {
   static String getString(String key) {
      return getString("com.github.jaiimageio.impl.plugins.clib.I18N", key);
   }
}
