package com.tickaroo.tikxml.annotationprocessing.element.path;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Path;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Document {
  @Path("toSkip") @Element Image image;
}
