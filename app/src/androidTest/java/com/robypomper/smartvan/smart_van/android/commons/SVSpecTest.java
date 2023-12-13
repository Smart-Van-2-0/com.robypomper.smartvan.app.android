package com.robypomper.smartvan.smart_van.android.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SVSpecTest {

    @Mock
    private JSLRemoteObject remoteObject;
    @Mock
    private ObjStruct objectStructure;

    @Test
    public void path_generation() {
        assertEquals("root", new SVSpec("root", null).getPath());
        assertEquals("1st level", new SVSpec("1st level", new SVSpecGroup("ROOT", null)).getPath());
        assertEquals("1st level > 2nd level", new SVSpec("2nd level", new SVSpecGroup("1st level", new SVSpecGroup("ROOT", null))).getPath());
    }

    @Test
    public void remote_object_check() {
        SVSpec specExist = new SVSpec("exist", new SVSpecGroup("ROOT", null));
        JSLBooleanState specExistComp = new JSLBooleanState(remoteObject, "Exist", "", "BooleanState", true);
        SVSpec specNotExist = new SVSpec("not exists", new SVSpecGroup("ROOT", null));

        // exist
        when(remoteObject.getStruct()).thenReturn(objectStructure);
        when(objectStructure.getComponent(specExist.getPath())).thenReturn(specExistComp);
        assertTrue(specExist.checkObject(remoteObject));

        // not exist
        assertFalse(specNotExist.checkObject(remoteObject));
    }

    @Test
    public void remote_object_check_errors() {
        SVSpec specTest = new SVSpec("exist", new SVSpecGroup("ROOT", null));

        // null remote object
        assertThrows(NullPointerException.class, () -> {
            //noinspection DataFlowIssue
            specTest.checkObject(null);
        });

        // structure not init
        when(remoteObject.getStruct()).thenReturn(null);
        assertThrows(NullPointerException.class, () -> {
            assertTrue(specTest.checkObject(remoteObject));
        });
    }

}