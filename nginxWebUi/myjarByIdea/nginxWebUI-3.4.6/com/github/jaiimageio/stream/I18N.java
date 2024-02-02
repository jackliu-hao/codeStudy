package com.github.jaiimageio.stream;

import com.github.jaiimageio.impl.common.I18NImpl;

final class I18N extends I18NImpl {
   static String getString(String key) {
      return getString("com.github.jaiimageio.stream.I18N", key);
   }
}
