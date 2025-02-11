package org.eclipse.slm.self_description_service.common.consul.model.acl

import com.fasterxml.jackson.annotation.JsonProperty

class NewAcl {
    @JsonProperty("Name")
    var name: String? = null

    @JsonProperty("Type")
    var type: AclType? = null

    @JsonProperty("Rules")
    var rules: String? = null

    public constructor() {}

    override fun toString(): String {
        return "NewAcl{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", rules='" + rules + '\'' +
                '}'
    }
}
