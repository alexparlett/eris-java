package org.homonoia.eris.resources.types;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.resources.types.ini.IniSection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alexp on 29/02/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class IniTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private Context context;

    @Test
    public void testLoad_Successful() throws Exception {
        String ini = "[Section]\n" +
                "Foo=Bar\n" +
                ";Foo2=Bar2\n" +
                "Foo3=Bar3";

        InputStream inputStream = new ByteArrayInputStream(ini.getBytes());

        Ini iniFile = new Ini(context);
        iniFile.load(inputStream);

        assertThat(iniFile.isEmpty(), is(false));

        IniSection section = iniFile.get("Section").orElseThrow(()-> new Exception());
        assertThat(section, is(not(nullValue())));
        assertThat(section.get("Foo").get(), is("Bar"));
        assertThat(section.contains("Foo2"), is(false));
        assertThat(section.get("Foo3").get(), is("Bar3"));
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
