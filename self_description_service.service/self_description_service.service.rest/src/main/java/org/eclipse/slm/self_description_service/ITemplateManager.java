package org.eclipse.slm.self_description_service;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Optional;

public interface ITemplateManager {

    Resource[] getTemplates() throws IOException;

    Optional<Resource> getTemplate(String name) throws IOException;
}
