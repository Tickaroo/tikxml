package com.tickaroo.tikxml.annotationprocessing.errors;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * Created by WeaponMan on 6/15/2017.
 */
@Xml
public class Error {

    @Attribute(name = "test")
    public String test;
}
