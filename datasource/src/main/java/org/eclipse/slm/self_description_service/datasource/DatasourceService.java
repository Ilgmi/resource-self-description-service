package org.eclipse.slm.self_description_service.datasource;

import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class DatasourceService {

    private final HashMap<String, Datasource> submodelToDatasourceMap = new HashMap<>();
    private final HashMap<String, Datasource> datasource = new HashMap<>();

    public DatasourceService(Template template, Docker docker) {

        this.datasource.put("Template", template);
        this.datasource.put("Docker", docker);

        for (Datasource datasource : this.datasource.values()) {
            var modelIds = datasource.getModelIds();
            for (String id : modelIds) {
                this.submodelToDatasourceMap.put(id, datasource);
            }
        }
    }

    public List<Datasource> getDatasources() {
        return new ArrayList<>(this.datasource.values());
    }

    public Optional<Datasource> getDatasourceForSubmodelId(String submodelId) {
        if (!this.submodelToDatasourceMap.containsKey(submodelId)) {
            for (Datasource datasource : datasource.values()) {
                try {
                    var submodel = datasource.getModelById(submodelId);
                    if (submodel.isPresent()) {
                        this.submodelToDatasourceMap.put(submodelId, datasource);
                        return Optional.of(datasource);
                    }
                } catch (IOException e) {
                    throw new ElementDoesNotExistException();
                }
            }
        } else {
            var datasource = this.submodelToDatasourceMap.get(submodelId);
            return Optional.of(datasource);
        }

        return Optional.empty();
    }


}
