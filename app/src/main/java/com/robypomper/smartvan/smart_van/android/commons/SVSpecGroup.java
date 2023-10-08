package com.robypomper.smartvan.smart_van.android.commons;

import java.util.Collections;
import java.util.List;

public class SVSpecGroup extends SVSpec {
    private final List<SVSpec> specs;
    private List<SVSpec> specsImmutable;

    public SVSpecGroup(String name, SVSpecGroup parent) {
        this(name, null, parent);
    }

    public SVSpecGroup(String name, List<SVSpec> specs, SVSpecGroup parent) {
        super(name, parent);
        this.specs = specs == null ? new java.util.ArrayList<>() : specs;
    }

    public List<SVSpec> getSpecs() {
        if (specsImmutable == null || specsImmutable.size() != specs.size())
            specsImmutable = Collections.unmodifiableList(specs);
        return specsImmutable;
    }

    public void addSpec(SVSpec spec) {
        specs.add(spec);
    }

}
