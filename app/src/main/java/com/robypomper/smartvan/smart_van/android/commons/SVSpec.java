package com.robypomper.smartvan.smart_van.android.commons;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;

public class SVSpec {
    private final String name;
    private final String path;
    private final SVSpecGroup parent;

    public SVSpec(String name, SVSpecGroup parent) {
        this.name = name;
        this.path = parent == null || parent.getParent() == null
                ? name
                : parent.getPath() + SVSpecs.SEPARATOR_FORMATTED + name;
        this.parent = parent;
        if (parent != null)
            parent.addSpec(this);
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public SVSpecGroup getParent() {
        return parent;
    }

    public boolean checkObject(JSLRemoteObject remoteObj) {
        return checkStructure(remoteObj.getStruct());
    }

    public boolean checkStructure(ObjStruct remoteObjStruct) {
        return remoteObjStruct.getComponent(getPath()) != null;
    }

}
