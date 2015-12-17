package com.tickaroo.tikxml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import okio.Buffer;
import okio.Okio;
import org.junit.Assert;

/**
 * @author Hannes Dorfmann
 */
public class TestUtils {

  private TestUtils() {

  }


  public static XmlReader readerFrom(String xml) {
    return XmlReader.of(new Buffer().writeUtf8(xml));
  }

  public static XmlReader readerFromFile(String filePath) throws IOException {
    return XmlReader.of(Okio.buffer(Okio.source(new File(getResourcePath(filePath)))));
  }


  /**
   * Get the resource path
   */
  private static String getResourcePath(String resPath) {
    URL resource = TestUtils.class.getClassLoader().getResource(resPath);
    Assert.assertNotNull("Could not open the resource " + resPath, resource);
    return resource.getFile();
  }


}
