package com.robypomper.smartvan.smart_van.android.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;


@RunWith(AndroidJUnit4.class)
public class SVSpecsGroupTest {

    @Test
    public void specs_list_from_init() {
        SVSpecGroup group;
        group = new SVSpecGroup("group", null);
        assertEquals(0, group.getSpecs().size());

        List<SVSpec> specsList = new java.util.ArrayList<>();
        group = new SVSpecGroup("group", specsList, null);
        assertEquals(0, group.getSpecs().size());

        group.addSpec(new SVSpec("spec1", null));
        group.addSpec(new SVSpec("spec2", null));
        assertEquals(2, group.getSpecs().size());
    }

    @Test
    public void specs_list_add_specs() {
        List<SVSpec> specsList = new java.util.ArrayList<>();
        SVSpecGroup group = new SVSpecGroup("group", specsList, null);
        assertEquals(0, group.getSpecs().size());

        group.addSpec(new SVSpec("spec1", null));
        group.addSpec(new SVSpec("spec2", null));
        assertEquals(2, group.getSpecs().size());
    }

    @Test
    public void specs_list_get_specs_immutable() {
        List<SVSpec> specsList = new java.util.ArrayList<>();
        SVSpecGroup group = new SVSpecGroup("group", specsList, null);

        group.addSpec(new SVSpec("spec1", null));
        group.addSpec(new SVSpec("spec2", null));
        assertEquals(2, group.getSpecs().size());

        assertThrows(UnsupportedOperationException.class, () -> {
            group.getSpecs().remove(0);
        });

    }

}