package org.homonoia.eris.resources.types;

import com.google.gson.Gson;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.homonoia.eris.core.CommandLineArgs;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.EmptyStatistics;
import org.homonoia.eris.resources.types.ini.IniSection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.not;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 29/02/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class IniTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    CommandLineArgs commandLineArgs;

    @InjectMocks
    Context context;

    @Before
    public void setup() {
        context.registerBean(new EmptyStatistics());
        context.registerBean(new Gson());
    }

    @Test
    public void testLoad_Successful() throws Exception {
        String ini = "[Section]\n" +
                "Foo=Bar\n" +
                ";Foo2=Bar2\n" +
                "Foo3=Bar3";

        InputStream inputStream = new ByteArrayInputStream(ini.getBytes());

        Ini iniFile = new Ini(context);
        iniFile.load(inputStream);

        Assert.assertThat(iniFile.isEmpty(), Is.is(false));

        IniSection section = iniFile.get("Section").orElseThrow(() -> new Exception());
        Assert.assertThat(section, Is.is(not(IsNull.nullValue())));
        Assert.assertThat(section.get("Foo").get(), Is.is("Bar"));
        Assert.assertThat(section.contains("Foo2"), Is.is(false));
        Assert.assertThat(section.get("Foo3").get(), Is.is("Bar3"));
    }

    @Test(expected = IOException.class)
    public void testLoad_Failure_BadIniNoSection() throws Exception {
        String ini = "Foo=Bar\n" +
                ";Foo2=Bar2\n" +
                "Foo3=Bar3";

        InputStream inputStream = new ByteArrayInputStream(ini.getBytes());

        Ini iniFile = new Ini(context);
        iniFile.load(inputStream);
    }

    @Test(expected = IOException.class)
    public void testLoad_Failure_BadIniNoValue() throws Exception {
        String ini = "[Section]\n" +
                "Foo\n";

        InputStream inputStream = new ByteArrayInputStream(ini.getBytes());

        Ini iniFile = new Ini(context);
        iniFile.load(inputStream);
    }

    @Test(expected = IOException.class)
    public void testLoad_Failure_BadIniNoSectionName() throws Exception {
        String ini = "[]\n" +
                "Foo=Bar\n";

        InputStream inputStream = new ByteArrayInputStream(ini.getBytes());

        Ini iniFile = new Ini(context);
        iniFile.load(inputStream);
    }
}
