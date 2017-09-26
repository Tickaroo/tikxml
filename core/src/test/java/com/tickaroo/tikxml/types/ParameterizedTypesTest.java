package com.tickaroo.tikxml.types;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.Types;
import okio.Buffer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

public class ParameterizedTypesTest {


    @Test
    public void readAndWriteGenericList() throws IOException {

        ParameterizedType stringListType = Types.newParameterizedType(List.class, String.class);

        TikXml tikXml = new TikXml.Builder()
                .addTypeAdapter(stringListType, new StringListTypeAdapter())
                .build();

        //
        // Read xml
        //
        List<String> list = tikXml.read(TestUtils.sourceForFile("stringlist.xml"), stringListType);
        Assert.assertEquals(Arrays.asList("a", "b", "c"), list);

        //
        // write xml
        //
        Buffer buffer = new Buffer();
        tikXml.write(buffer, list, stringListType);
        String writtenXml = TestUtils.bufferToString(buffer);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><items><a>a</a><b>b</b><c>c</c></items>", writtenXml);

    }


}
