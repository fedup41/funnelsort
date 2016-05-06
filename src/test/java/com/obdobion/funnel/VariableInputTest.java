package com.obdobion.funnel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.obdobion.Helper;
import com.obdobion.funnel.parameters.FunnelContext;

public class VariableInputTest
{
    @Test
    public void variableUnterminatedLastLine ()
        throws Throwable
    {
        final String testName = Helper.testName();
        Helper.initializeFor(testName);

        final List<String> out = new ArrayList<>();
        out.add("line 1");
        out.add("line 2");
        out.add("unterminated");

        final File file = Helper.createUnsortedFile("variableInputTest", out, false);

        final FunnelContext context = Funnel.sort(Helper.config(), file.getAbsolutePath()
            + "--max 2 -c original --eol cr,lf -r "
            + Helper.DEFAULT_OPTIONS);

        Assert.assertEquals("records", 3L, context.provider.actualNumberOfRows());
        Helper.compare(file, out);
        Assert.assertTrue("delete " + file.getAbsolutePath(), file.delete());
    }

}
