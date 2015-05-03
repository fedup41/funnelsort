package com.obdobion.funnel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringBufferInputStream;

import org.junit.Assert;
import org.junit.Test;

import com.obdobion.Helper;
import com.obdobion.funnel.parameters.FunnelContext;
import com.obdobion.funnel.provider.InputCache;

/**
 * @author Chris DeGreef
 * 
 */
@SuppressWarnings("deprecation")
public class InputCacheTests
{
    static private FunnelContext createDummyContext (
        final InputStream in,
        final PrintStream out)
        throws Exception
    {
        if (in == null)
        {
            final InputStream inputStream = new StringBufferInputStream("");
            System.setIn(inputStream);
        } else
            System.setIn(in);
        if (out == null)
        {
            final File file = Helper.outFileWhenInIsSysin();
            final PrintStream outputStream = new PrintStream(new FileOutputStream(file));
            System.setOut(outputStream);
        } else
            System.setOut(out);
        return new FunnelContext("");
    }

    @Test
    public void bufferPositioningLargeNumbers ()
        throws Throwable
    {
        Helper.initializeFor("TEST bufferPositioningPow2");

        final long[] bufStarts =
        {
            0, 32768
        };

        Assert.assertEquals(0, InputCache.findBufferIndexForPosition(32767, bufStarts));
        Assert.assertEquals(1, InputCache.findBufferIndexForPosition(32768, bufStarts));
        Assert.assertEquals(1, InputCache.findBufferIndexForPosition(32846, bufStarts));
    }

    @Test
    public void bufferPositioningNotPow2 ()
        throws Throwable
    {
        Helper.initializeFor("TEST bufferPositioningNotPow2");

        final long[] bufStarts =
        {
            0, 10, 20, 30
        };

        Assert.assertEquals(0, InputCache.findBufferIndexForPosition(0, bufStarts));
        Assert.assertEquals(0, InputCache.findBufferIndexForPosition(9, bufStarts));
        Assert.assertEquals(1, InputCache.findBufferIndexForPosition(10, bufStarts));
        Assert.assertEquals(1, InputCache.findBufferIndexForPosition(19, bufStarts));
        Assert.assertEquals(2, InputCache.findBufferIndexForPosition(20, bufStarts));
        Assert.assertEquals(2, InputCache.findBufferIndexForPosition(29, bufStarts));
        Assert.assertEquals(3, InputCache.findBufferIndexForPosition(30, bufStarts));
    }

    @Test
    public void bufferPositioningPow2 ()
        throws Throwable
    {
        Helper.initializeFor("TEST bufferPositioningPow2");

        final long[] bufStarts =
        {
            0, 10
        };

        Assert.assertEquals(0, InputCache.findBufferIndexForPosition(0, bufStarts));
        Assert.assertEquals(0, InputCache.findBufferIndexForPosition(9, bufStarts));
        Assert.assertEquals(1, InputCache.findBufferIndexForPosition(10, bufStarts));
        Assert.assertEquals(1, InputCache.findBufferIndexForPosition(19, bufStarts));
        Assert.assertEquals(1, InputCache.findBufferIndexForPosition(20, bufStarts));
        Assert.assertEquals(1, InputCache.findBufferIndexForPosition(29, bufStarts));
        Assert.assertEquals(1, InputCache.findBufferIndexForPosition(30, bufStarts));
    }

    @Test
    public void inputStreamWith2BuffersByArray ()
        throws Throwable
    {
        Helper.initializeFor("TEST inputStreamWith2BuffersByArray");
        final int minRow = 100000;
        final int maxRows = 104106;

        final StringBuilder sb = new StringBuilder();
        for (long num = minRow; num < maxRows; num++)
        {
            sb.append(num).append(System.getProperty("line.separator"));
        }
        final InputStream testStream = new StringBufferInputStream(sb.toString());
        final FunnelContext context = createDummyContext(testStream, null);

        final byte[] testBytes = new byte[8];
        for (long num = minRow; num < maxRows; num++)
        {
            context.inputCache.read(context.inputFileIndex(), testBytes, (num - minRow) * 8, 8);
            Assert.assertEquals("" + num, new String(testBytes).trim());
        }
    }

    @Test
    public void inputStreamWith2BuffersByByte ()
        throws Throwable
    {
        Helper.initializeFor("TEST inputStreamWith2BuffersByByte");
        final int minRow = 100000;
        final int maxRows = 104106;

        final StringBuilder sb = new StringBuilder();
        for (long num = minRow; num < maxRows; num++)
        {
            sb.append(num).append(System.getProperty("line.separator"));
        }
        final InputStream testStream = new StringBufferInputStream(sb.toString());
        final FunnelContext context = createDummyContext(testStream, null);

        final byte[] testBytes = new byte[8];
        for (long num = minRow; num < maxRows; num++)
        {
            for (int b = 0; b < 8; b++)
                testBytes[b] = context.inputCache.readNextByte();
            Assert.assertEquals("" + num, new String(testBytes).trim());
        }
    }

    @Test
    public void sortWith2Buffers ()
        throws Throwable
    {
        Helper.initializeFor("TEST sortWith2Buffers");
        final int minRow = 100000;
        final int maxRows = 104106;

        final StringBuilder sb = new StringBuilder();
        for (long num = maxRows - 1; num >= minRow; num--)
        {
            sb.append(num).append(System.getProperty("line.separator"));
        }
        System.setIn(new StringBufferInputStream(sb.toString()));
        final File file = Helper.outFileWhenInIsSysin();
        final PrintStream outputStream = new PrintStream(new FileOutputStream(file));
        System.setOut(outputStream);

        Funnel.sort("--cacheInput --cacheWork");

        outputStream.flush();
        outputStream.close();

        final BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        for (long num = minRow; num < maxRows; num++)
        {
            line = br.readLine();
            Assert.assertEquals("" + num, line);
        }
        br.close();
    }
}
