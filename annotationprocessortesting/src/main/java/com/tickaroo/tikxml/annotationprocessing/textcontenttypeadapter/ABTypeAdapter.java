package com.tickaroo.tikxml.annotationprocessing.textcontenttypeadapter;

import com.tickaroo.tikxml.typeadapter.TextContentTypeAdapter;

public class ABTypeAdapter extends TextContentTypeAdapter<Ab> {

    public ABTypeAdapter(String xmlTagName) {
        super(xmlTagName);
    }

    @Override
    protected Ab read(String value) {
        System.out.println("Read "+value);
        return Ab.valueOf(value);
    }

    @Override
    protected String write(Ab value) {
        return value.toString();
    }
}
