package com.tickaroo.tikxml.annotationprocessing.textcontenttypeadapter;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Path;
import com.tickaroo.tikxml.annotation.Xml;

import java.util.List;

@Xml
public class TextContentItem {

    @Element(compileTimeChecks = false)
    public Ab ab;

    @Path("items")
    @Element(compileTimeChecks = false)
    public List<Ab> items;
}
