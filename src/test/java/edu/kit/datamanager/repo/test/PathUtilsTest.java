/*
 * Copyright 2018 Karlsruhe Institute of Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.kit.datamanager.repo.test;

import edu.kit.datamanager.exceptions.CustomInternalServerError;
import edu.kit.datamanager.repo.configuration.ApplicationProperties;
import edu.kit.datamanager.repo.domain.DataResource;
import edu.kit.datamanager.repo.util.PathUtils;
import java.net.URL;
import java.net.URLEncoder;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jejkal
 */
public class PathUtilsTest{

  @Test
  public void testGetDatUri() throws Exception{
    DataResource resource = DataResource.factoryNewDataResource("test123");
    ApplicationProperties props = new ApplicationProperties();
    //test with trailing slash
    props.setBasepath(new URL("file:///tmp/"));
    System.out.println(PathUtils.getDataUri(resource, "folder/file.txt", props));
    System.out.println(PathUtils.getDataUri(resource, "folder/file.txt", props).toString().startsWith("file:/tmp/2018/test123/folder/file.txt_"));

    Assert.assertTrue(PathUtils.getDataUri(resource, "folder/file.txt", props).toString().startsWith("file:/tmp/2018/test123/folder/file.txt_"));
    //test w/o trailing slash
    props.setBasepath(new URL("file:///tmp"));
    Assert.assertTrue(PathUtils.getDataUri(resource, "folder/file.txt", props).toString().startsWith("file:/tmp/2018/test123/folder/file.txt_"));
    //test with UTF-8 chars
    props.setBasepath(new URL("file:///fôldęr/"));
    Assert.assertTrue(PathUtils.getDataUri(resource, "folder/file.txt", props).toString().startsWith("file:/" + URLEncoder.encode("fôldęr", "UTF-8") + "/2018/test123/folder/file.txt_"));
  }

  @Test(expected = CustomInternalServerError.class)
  public void testInvalidBasePath() throws Exception{
    DataResource resource = DataResource.factoryNewDataResource("test123");
    ApplicationProperties props = new ApplicationProperties();
    props.setBasepath(new URL("file:///fold?<>:er/"));
    Assert.fail("Creating the following path should not work: " + PathUtils.getDataUri(resource, "folder/file.txt", props));
  }

  @Test(expected = CustomInternalServerError.class)
  public void testNoInternalIdentifier() throws Exception{
    DataResource resource = DataResource.factoryNewDataResource("test123");
    resource.getAlternateIdentifiers().clear();
    ApplicationProperties props = new ApplicationProperties();
    props.setBasepath(new URL("file:///folder/"));
    Assert.fail("Creating the following path should not work: " + PathUtils.getDataUri(resource, "folder/file.txt", props));
  }

}
