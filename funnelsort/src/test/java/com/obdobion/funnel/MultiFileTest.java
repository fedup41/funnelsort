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
public class MultiFileTest
{

    @Test
    public void twoInputFilesMerged ()
        throws Throwable
    {
        Helper.initializeFor("TEST twoInputFilesMerged");

        final File output = new File("/tmp/twoInputFilesMerged");

        final List<String> expectedOutput = new ArrayList<>();

        final List<String> in1 = new ArrayList<>();
        in1.add("line 1");
        in1.add("line 3");

        final List<String> in2 = new ArrayList<>();
        in2.add("line 2");
        in2.add("line 4");

        expectedOutput.add(in1.get(0));
        expectedOutput.add(in2.get(0));
        expectedOutput.add(in1.get(1));
        expectedOutput.add(in2.get(1));

        final File file = Helper.createUnsortedFile(in1);
        final File file2 = Helper.createUnsortedFile(in2);

        final FunnelContext context = Funnel.sort(file.getAbsolutePath()
            + ","
            + file2.getAbsolutePath()
            + " -o "
            + output.getAbsolutePath()
            + " --max 4 --eol CR LF --eolOut LF"
            + Helper.DEFAULT_OPTIONS);

        Assert.assertEquals("records", 4L, context.publisher.getWriteCount());
        Helper.compare(output, expectedOutput);

        Assert.assertTrue(file.delete());
        Assert.assertTrue(file2.delete());
        Assert.assertTrue(output.delete());
    }

    @Test
    public void twoInputFilesWithReplace ()
        throws Throwable
    {
        Helper.initializeFor("TEST twoInputFiles");

        final List<String> out = new ArrayList<>();
        out.add("line 1");
        out.add("line 2");

        final File file = Helper.createUnsortedFile(out);
        final File file2 = Helper.createUnsortedFile(out);

        final FunnelContext context = Funnel.sort(file.getAbsolutePath()
            + ","
            + file2.getAbsolutePath()
            + " --replace --max 2 -c original --eol CR LF --eolOut LF"
            + Helper.DEFAULT_OPTIONS);

        Assert.assertEquals("records", 2L, context.provider.actualNumberOfRows());
        Helper.compare(file, out);
        // file.delete();
    }
}
