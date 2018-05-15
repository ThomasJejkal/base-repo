/*
 * Copyright 2017 Karlsruhe Institute of Technology.
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
package edu.kit.datamanager.repo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.kit.datamanager.annotations.Searchable;
import edu.kit.datamanager.annotations.SecureUpdate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Data;

/**
 *
 * @author jejkal
 */
@Entity
@Data
@ApiModel(description = "A subject of a resource, which can either be free text or a value URI.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Subject{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @SecureUpdate({"FORBIDDEN"})
  @Searchable
  private Long id;
  @ApiModelProperty(value = "keyword", dataType = "String", required = false)
  private String value;
  @ApiModelProperty(required = false)
  @OneToOne(cascade = javax.persistence.CascadeType.ALL, orphanRemoval = true)
  private Scheme scheme;
  @ApiModelProperty(value = "http://udcdata.info/037278", dataType = "String", required = false)
  private String valueUri;
  @ApiModelProperty(value = "en", dataType = "String", required = false)
  private String lang;
}
