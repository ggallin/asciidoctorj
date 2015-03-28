package org.asciidoctor.test.arquillian;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class AsciidoctorExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(ResourceProvider.class, AsciidoctorResourceProvider.class);
        builder.observer(AsciidoctorTestObserver.class);
    }

}
