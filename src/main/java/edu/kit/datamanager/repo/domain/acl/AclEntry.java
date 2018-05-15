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
package edu.kit.datamanager.repo.domain.acl;

import edu.kit.datamanager.annotations.SecureUpdate;
import edu.kit.datamanager.entities.BaseEnum;
import edu.kit.datamanager.repo.domain.Title;
import edu.kit.datamanager.util.EnumUtils;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

/**
 *
 * @author jejkal
 */
@Entity
@Data
public class AclEntry{

  public enum PERMISSION implements BaseEnum{
    NONE("PERMISSION_NONE"),
    READ("PERMISSION_READ"),
    WRITE("PERMISSION_WRITE"),
    ADMINISTRATE("PERMISSION_ADMINISTRATE");

    private final String value;

    private PERMISSION(String value){
      this.value = value;
    }

    public String getValue(){
      return value;
    }

    @Override
    public String toString(){
      return value;
    }

    public boolean atLeast(PERMISSION permission){
      return this.ordinal() >= permission.ordinal();
    }

  }

  public AclEntry(){
  }

  public AclEntry(String identifier, PERMISSION permission){
    this();
    this.sid = identifier;
    this.permission = permission;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @SecureUpdate("FORBIDDEN")
  private Long id;
  @SecureUpdate("FORBIDDEN")
  private String sid;
  @SecureUpdate("ADMINISTRATE")
  @Enumerated(EnumType.STRING)
  private PERMISSION permission;

  @Override
  public int hashCode(){
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.id);
    hash = 89 * hash + Objects.hashCode(this.sid);
    hash = 89 * hash + EnumUtils.hashCode(this.permission);
    return hash;
  }

  @Override
  public boolean equals(Object obj){
    if(this == obj){
      return true;
    }
    if(obj == null){
      return false;
    }
    if(getClass() != obj.getClass()){
      return false;
    }
    final AclEntry other = (AclEntry) obj;
    if(!Objects.equals(this.id, other.id)){
      return false;
    }
    if(!Objects.equals(this.sid, other.sid)){
      return false;
    }
    return EnumUtils.equals(this.permission, other.permission);
  }

}
