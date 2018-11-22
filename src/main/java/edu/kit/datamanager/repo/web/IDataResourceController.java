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
package edu.kit.datamanager.repo.web;

import com.github.fge.jsonpatch.JsonPatch;
import edu.kit.datamanager.repo.domain.DataResource;
import edu.kit.datamanager.controller.IGenericResourceController;
import edu.kit.datamanager.repo.domain.ContentInformation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author jejkal
 */
public interface IDataResourceController extends IGenericResourceController<DataResource>{

  @ApiOperation(value = "Upload data for a data resource.", notes = "This endpoint allows to upload or assign data and content metadata related to the uploaded file to a resource identified by its id. "
          + "Uploaded data will be stored at the configured backend, typically the local hard disk. Furthermore, it is possible to register data stored elsewhere by providing only a content URI within the content metadata."
          + "In any other case, providing content metadata is optional. Parts of the content metadata, e.g. content type or checksum, may be generated or overwritten after a file upload if they not already exist or if "
          + "the configuration does not allow the user to provide particular content metadata entries, e.g. because a certain checksum digest is mandatory."
          + "All uploaded data can be virtually structured by providing the relative path where they should be accessible within the request URL. If a file at a given path already exists, there will be typically returned HTTP CONFLICT. "
          + "If desired, overwriting of existing content can be enforced by setting the request parameter 'force' to  true. In that case, the existing file will be marked for deletion and is deleted after the upload operation "
          + "has successfully finished. If the overwritten element only contains a reference URI, the entry is just replaced by the user provided entry.")
  @RequestMapping(path = "/{id}/data/**", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity handleFileUpload(@ApiParam(value = "The numeric resource identifier.", required = true) @PathVariable(value = "id") final String id,
          @ApiParam(value = "The file to upload. If no file is uploaded, a metadata document must be provided containing a reference URI to the externally hosted data.", required = false) @RequestPart(name = "file", required = false) MultipartFile file,
          @ApiParam(value = "Json representation of a content information metadata document. Providing this metadata document is optional unless no file is uploaded.", required = false) @RequestPart(name = "metadata", required = false) ContentInformation contentInformation,
          @ApiParam(value = "Flag to indicate, that existing content at the same location should be overwritten.", required = false) @RequestParam(name = "force", defaultValue = "false") boolean force,
          final WebRequest request,
          final HttpServletResponse response,
          final UriComponentsBuilder uriBuilder);

  @ApiOperation(value = "Access content information for single or multiple data elements.",
          notes = "List metadata of one or more content elements associated with a data resource in a paginated and/or sorted form. This endpoint is addressed if the caller provides content type "
          + "'application/vnd.datamanager.content-information+json' within the 'Accept' header. If this content type is not present, the content element is downloaded instead."
          + "The content path, defining whether one or more content element(s) is/are returned, is provided within the request URL. Everything after 'data/' is expected to be either a virtual folder or single content element. "
          + "If the provided content path ends with a slash, it is expected to represent a virtual collection which should be listed. If the content path does not end with a slash, it is expected to refer to a single element. "
          + "If not element with the exact content path exists, HTTP NOT_FOUND is returned. The user may provide custom sort criteria for ordering the returned elements. If no sort criteria is provided, the default sorting is "
          + "applied which returning all matching elements in ascending order by hierarchy depth and alphabetically by their relative path.")
  @ApiImplicitParams(value = {
    @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Results page you want to retrieve (0..N)")
    , @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", value = "Number of records per page.")
    , @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query", value = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.")
  })
  @RequestMapping(path = "/{id}/data/**", method = RequestMethod.GET, produces = "application/vnd.datamanager.content-information+json")
  @ResponseBody
  public ResponseEntity handleMetadataAccess(@ApiParam(value = "The numeric resource identifier.", required = true) @PathVariable(value = "id") final String id,
          @ApiParam(value = "A single tag assigned to certain content elements. Tags allow easy structuring and filtering of content associated to a resource.", required = false) @RequestParam(name = "tag", required = false) String tag,
          final Pageable pgbl,
          final WebRequest request,
          final HttpServletResponse response,
          final UriComponentsBuilder uriBuilder);

  @ApiOperation(value = "Download data located at the provided content path.",
          notes = "This endpoint allows to download the data associated with a data resource and located at a particular virtual part. The virtual path starts after 'data/' and should end with a filename. "
          + "Depending on the content located at the provided path, different response scenarios can occur. If the content is a locally stored, accessible file, the bitstream of the file is retured. If the file is (temporarily) not available, "
          + "HTTP 404 is returned. If the content referes to an externally stored resource accessible via http(s), the service will try if the resource is accessible. If this is the case, the service will return HTTP 303 (SEE_OTHER) together "
          + "with the resource URI in the 'Location' header. Depending on the client, the request is then redirected and the bitstream is returned. If the resource is not accessible or if the protocol is not http(s), the service "
          + "will either return the status received by accessing the resource URI, SERVICE_UNAVAILABLE if the request has failed or NO_CONTENT if not other status applies. In addition, the resource URI is returned in the 'Content-Location' header "
          + "in case the client wants to try to access the resource URI.")

  @RequestMapping(path = "/{id}/data/**", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity handleFileDownload(@ApiParam(value = "The numeric resource identifier.", required = true) @PathVariable(value = "id") final String id,
          final Pageable pgbl,
          final WebRequest request,
          final HttpServletResponse response,
          final UriComponentsBuilder uriBuilder);

  @ApiOperation(value = "Patch a single content information element.",
          notes = "This endpoint allows to patch single content information elements associated with a data resource. As most of the content information attributes are typically automatically generated their modification is restricted "
          + "to privileged users, e.g. user with role ADMINISTRATOR or permission ADMINISTRATE. Users having WRITE permissions to the associated resource are only allowed to modify contained metadata elements or tags assigned to the content element.")
  @RequestMapping(path = "/{id}/data/**", method = RequestMethod.PATCH, consumes = "application/json-patch+json")
  @ResponseBody
  public ResponseEntity patchMetadata(@ApiParam(value = "The numeric resource identifier.", required = true) @PathVariable(value = "id") final String id,
          @ApiParam(value = "Json representation of a json patch document. The document must comply with RFC 6902 specified by the IETF.", required = true) @RequestBody JsonPatch patch,
          final WebRequest request,
          final HttpServletResponse response);

  @ApiOperation(value = "Remove a single content information element.",
          notes = "This endpoint allows to remove single content information elements associated with a data resource. Removing content information elements including their content is restricted "
          + "to privileged users, e.g. user with role ADMINISTRATOR or permission ADMINISTRATE.")
  @RequestMapping(path = "/{id}/data/**", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity deleteContent(@ApiParam(value = "The numeric resource identifier.", required = true) @PathVariable(value = "id") final String id,
          final WebRequest request,
          final HttpServletResponse response);
}
