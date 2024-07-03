package com.robypomper.smartvan.smart_van.android.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class SVSpecsTest {

    @Test
    public void accessibility() {
        assertNotNull(SVSpecs.SVBox);
        assertNotNull(SVSpecs.SVBox.Position);
        assertNotNull(SVSpecs.SVBox.Position.GNSS);
        assertNotNull(SVSpecs.SVBox.Position.GNSS.Latitude);
    }

    @Test
    public void path_generic() {
        assertEquals(SVSpecs.ROOT_NAME, SVSpecs.SVBox.getPath());
        assertEquals("Position", SVSpecs.SVBox.Position.getPath());
        assertEquals("Position > GNSS", SVSpecs.SVBox.Position.GNSS.getPath());
        assertEquals("Position > GNSS > Latitude", SVSpecs.SVBox.Position.GNSS.Latitude.getPath());
    }

    @Test
    public void fromPath() {
        // Multi level
        assertEquals(SVSpecs.SVBox, SVSpecs.fromPath(SVSpecs.ROOT_NAME));
        assertEquals(SVSpecs.SVBox.Position, SVSpecs.fromPath("Position"));
        assertEquals(SVSpecs.SVBox.Position.GNSS, SVSpecs.fromPath("Position > GNSS"));
        assertEquals(SVSpecs.SVBox.Position.GNSS.Latitude, SVSpecs.fromPath("Position > GNSS > Latitude"));

        // with extra/no spaces
        assertEquals(SVSpecs.SVBox.Position, SVSpecs.fromPath("   Position "));
        assertEquals(SVSpecs.SVBox.Position.GNSS, SVSpecs.fromPath("Position   >    GNSS"));
        assertEquals(SVSpecs.SVBox.Position.GNSS, SVSpecs.fromPath("Position>GNSS"));

        // Not existing
        assertNull(SVSpecs.fromPath("Not > Existing > Path"));
        assertNull(SVSpecs.fromPath("Position > Partial > Existing > Path"));
    }

}