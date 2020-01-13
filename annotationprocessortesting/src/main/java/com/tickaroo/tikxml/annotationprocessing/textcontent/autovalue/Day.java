package com.tickaroo.tikxml.annotationprocessing.textcontent.autovalue;

import com.google.auto.value.AutoValue;
import com.tickaroo.tikxml.annotation.GenericAdapter;
import com.tickaroo.tikxml.annotation.TextContent;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
@AutoValue
@GenericAdapter
public abstract class Day {

  @TextContent public abstract String name();
}
