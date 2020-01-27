package com.tickaroo.tikxml.typeadapter;

import com.tickaroo.tikxml.TikXmlConfig;
import com.tickaroo.tikxml.TypeAdapterNotFoundException;

public interface TypeAdapterRetriever<T> {

  TypeAdapter<T> getTypeAdapter(TikXmlConfig config) throws TypeAdapterNotFoundException;

}
