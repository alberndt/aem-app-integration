package com.alexanderberndt.appintegration;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;

import java.util.List;
import java.util.Set;

public class ExternalApplication {

    private ImmutableHierarchicalConfiguration configuration;

    private List<ExternalApplicationInstance> instanceList;

    private Set<ExternalStaticResource> resourceSet;

    public ExternalApplication(ImmutableHierarchicalConfiguration configuration) {
        this.configuration = configuration;
    }


}
