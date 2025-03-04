package org.eclipse.slm.self_description_service.common.consul.model.acl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class Role {

    @JsonProperty("Name")
    var name: String = ""

    @JsonProperty("Description")
    var description: String? = null

    @JsonProperty("Policies", required = false)
    var policies: List<org.eclipse.slm.self_description_service.common.consul.model.acl.PolicyLink>? = ArrayList()

    @JsonProperty("ID", required = false)
    var id: String? = null

    @JsonProperty("Createindex", required = false)
    var createIndex: Long? = null

    @JsonProperty("ModiyIndex", required = false)
    var modifyIndex: Long? = null

    public constructor() {}

    constructor(
        name: String,
        description: String?,
        policies: List<org.eclipse.slm.self_description_service.common.consul.model.acl.PolicyLink>?
    ) {
        this.name = name
        this.description = description
        this.policies = policies
    }

}
