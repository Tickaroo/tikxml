package com.tickaroo.tikxml.autovalue;

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;

/**
 * This is the auto-value extension for TikXml
 * @author Hannes Dorfmann
 */
@AutoService(AutoValueExtension.class)
public class TikXmlAutoValueExtension  extends AutoValueExtension {

  @Override public boolean applicable(Context context) {
    return
  }

  @Override
  public String generateClass(Context context, String s, String s1, boolean b) {
    // return JavaFile.builder(context.packageName(), subclass).build().toString();

    return null;
  }
}
