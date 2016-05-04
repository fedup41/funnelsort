package com.obdobion.funnel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.obdobion.Helper;
import com.obdobion.funnel.parameters.FunnelContext;

/**
 * @author Chris DeGreef
 * 
 */
public class StopWhenTest
{

    @Test
    public void stopAfter10 ()
            throws Throwable
    {
        Helper.initializeFor("TEST stopAfter10");

        final List<String> in1 = new ArrayList<>();
        for (int x = 10000; x <= 99999; x += 100)
        {
            in1.add("" + x);
        }

        final File file = Helper.createUnsortedFile("stopAfter10", in1);

        final FunnelContext context = Funnel.sort(file.getAbsolutePath()
                + " -r "
                + " --stopWhen 'recordNumber > 1'"
                + Helper.DEFAULT_OPTIONS);

        Assert.assertEquals("records", 1L, context.publisher.getWriteCount());
        final List<String> exp = new ArrayList<>();
        exp.add("10000");
        Helper.compare(file, exp);

        Assert.assertTrue(file.delete());
    }
}