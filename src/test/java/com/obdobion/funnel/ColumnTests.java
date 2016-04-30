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
public class ColumnTests
{
    @Test
    public void startingInFirstColumn ()
            throws Throwable
    {
        Helper.initializeFor("TEST startingInFirstColumn");

        final List<String> logFile = new ArrayList<>();
        logFile.add("FATAL");
        logFile.add("INFO ");
        logFile.add("WARN ");

        final File file = Helper.createUnsortedFile("startingInFirstColumn", logFile);

        final FunnelContext context = Funnel.sort(file.getAbsolutePath()
                + " --col(string -o0 -l5 -n level)"
                + " --where \"(level = 'FATAL')\""
                + " -r "
                + Helper.DEFAULT_OPTIONS);

        Assert.assertEquals("records", 1L, context.publisher.getWriteCount());

        Assert.assertTrue(file.delete());
    }

    @Test
    public void notStartingInFirstColumn ()
            throws Throwable
    {
        Helper.initializeFor("TEST notStartingInFirstColumn");

        final List<String> logFile = new ArrayList<>();
        logFile.add("  FATAL");
        logFile.add("  INFO ");
        logFile.add("  WARN ");

        final File file = Helper.createUnsortedFile("notStartingInFirstColumn", logFile);

        final FunnelContext context = Funnel.sort(file.getAbsolutePath()
                + " --col(string -o2 -l5 -n level)"
                + " --where \"(level = 'FATAL')\""
                + " -r "
                + Helper.DEFAULT_OPTIONS);

        Assert.assertEquals("records", 1L, context.publisher.getWriteCount());

        Assert.assertTrue(file.delete());
    }

    @Test
    public void shortValue()
            throws Throwable
    {
        Helper.initializeFor("TEST shortValue");

        final List<String> logFile = new ArrayList<>();
        logFile.add("  FATAL");
        logFile.add("  INFO ");
        logFile.add("  WARN ");

        final File file = Helper.createUnsortedFile("shortValue", logFile);

        final FunnelContext context = Funnel.sort(file.getAbsolutePath()
                + " --col(string -o2 -l5 -n level)"
                + " --where \"(level = 'INFO ')\""
                + " -r "
                + Helper.DEFAULT_OPTIONS);

        Assert.assertEquals("records", 1L, context.publisher.getWriteCount());

        Assert.assertTrue(file.delete());
    }
}