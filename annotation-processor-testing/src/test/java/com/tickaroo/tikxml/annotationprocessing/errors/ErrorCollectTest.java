package com.tickaroo.tikxml.annotationprocessing.errors;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WeaponMan on 6/15/2017.
 */

public class ErrorCollectTest {
    @Test
    public void testAttributeMissing() throws IOException {
        TikXml xml = new TikXml.Builder()
                .exceptionOnUnreadXml(false)
                .build();
        List<String> errors = new ArrayList<>();

        String stringXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyTagList test=\"Test\" test2=\"Test3\" />";

        Error error =  xml.read(TestUtils.sourceFrom(stringXml), Error.class, errors);

        Assert.assertEquals(error.test, "Test");
        Assert.assertEquals(errors.size(), 1);
        Assert.assertThat(errors, CoreMatchers.hasItem("Could not map the xml attribute 'test2' at path /emptyTagList[@test2]"));
    }

    @Test
    public void testAttributeMultipleMissing() throws IOException {
        TikXml xml = new TikXml.Builder()
                .exceptionOnUnreadXml(false)
                .build();
        List<String> errors = new ArrayList<>();

        String stringXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyTagList test=\"Test\" test2=\"Test3\" test3=\"Test3\" />";

        Error error =  xml.read(TestUtils.sourceFrom(stringXml), Error.class, errors);

        Assert.assertEquals(error.test, "Test");
        Assert.assertEquals(errors.size(), 2);
        Assert.assertThat(errors, CoreMatchers.hasItems("Could not map the xml attribute 'test2' at path /emptyTagList[@test2]", "Could not map the xml attribute 'test3' at path /emptyTagList[@test3]"));
    }


    @Test
    public void testElementMissing() throws IOException {
        TikXml xml = new TikXml.Builder()
                .exceptionOnUnreadXml(false)
                .build();
        List<String> errors = new ArrayList<>();

        String stringXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyTagList test=\"Test\"><el></el></emptyTagList>";

        Error error =  xml.read(TestUtils.sourceFrom(stringXml), Error.class, errors);

        Assert.assertEquals(error.test, "Test");
        Assert.assertEquals(errors.size(), 1);
        Assert.assertThat(errors, CoreMatchers.hasItem("Could not map the xml element <el> at path /emptyTagList/el"));
    }

    @Test
    public void testElementMultipleMissing() throws IOException {
        TikXml xml = new TikXml.Builder()
                .exceptionOnUnreadXml(false)
                .build();
        List<String> errors = new ArrayList<>();

        String stringXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyTagList test=\"Test\"><el1/><el></el></emptyTagList>";

        Error error =  xml.read(TestUtils.sourceFrom(stringXml), Error.class, errors);

        Assert.assertEquals(error.test, "Test");
        Assert.assertEquals(errors.size(), 2);
        Assert.assertThat(errors, CoreMatchers.hasItems("Could not map the xml element <el1> at path /emptyTagList/el1", "Could not map the xml element <el> at path /emptyTagList/el"));
    }


    @Test
    public void testElementTextMissing() throws IOException {
        TikXml xml = new TikXml.Builder()
                .exceptionOnUnreadXml(false)
                .build();
        List<String> errors = new ArrayList<>();

        String stringXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyTagList>hello</emptyTagList>";

        Error error =  xml.read(TestUtils.sourceFrom(stringXml), Error.class, errors);
        Assert.assertNull(error.test);
        Assert.assertEquals(errors.size(), 1);
        Assert.assertThat(errors, CoreMatchers.hasItem("Could not map the xml element text content at path /emptyTagList/text()"));
    }


    @Test
    public void testElementAttributeAndTextCombined() throws IOException {
        TikXml xml = new TikXml.Builder()
                .exceptionOnUnreadXml(false)
                .build();
        List<String> errors = new ArrayList<>();

        String stringXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyTagList test545=\"test\">hello</emptyTagList>";

        Error error =  xml.read(TestUtils.sourceFrom(stringXml), Error.class, errors);
        Assert.assertNull(error.test);
        Assert.assertEquals(errors.size(), 2);
        Assert.assertThat(errors, CoreMatchers.hasItems("Could not map the xml attribute 'test545' at path /emptyTagList[@test545]",
                "Could not map the xml element text content at path /emptyTagList/text()"));
    }

    @Test
    public void testElementAttributeAndElementCombined() throws IOException {
        TikXml xml = new TikXml.Builder()
                .exceptionOnUnreadXml(false)
                .build();
        List<String> errors = new ArrayList<>();

        String stringXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><emptyTagList test545=\"test\"><sum/></emptyTagList>";

        Error error =  xml.read(TestUtils.sourceFrom(stringXml), Error.class, errors);
        Assert.assertNull(error.test);
        Assert.assertEquals(errors.size(), 2);
        Assert.assertThat(errors, CoreMatchers.hasItems("Could not map the xml attribute 'test545' at path /emptyTagList[@test545]",
                "Could not map the xml element <sum> at path /emptyTagList/sum"));
    }

}
