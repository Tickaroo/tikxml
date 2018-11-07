package com.tickaroo.tikxml.annotationprocessing.namespace;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Mariusz Saramak
 */
@Xml(writeNamespaces = { "http://www.w3.org/1998/Math/MathML" })
public class RootDefaultNamespace {
  @PropertyElement(name = "name") String name;
  @Element(name = "child") Child child;
}
