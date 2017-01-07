package org.homonoia.eris.engine;

import com.google.gson.Gson;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.json.JsonException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 06/03/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class LocaleTest {

    @Mock
    ApplicationContext applicationContext;

    @InjectMocks
    Context context;

    FileSystem fileSystem;
    ResourceCache resourceCache;
    Locale locale;

    @Before
    public void setup() {
        fileSystem = new FileSystem(context);
        resourceCache = new ResourceCache(context, fileSystem);

        Path resourcePath = Paths.get("src/test/resources").toAbsolutePath();
        fileSystem.addPath(resourcePath);
        resourceCache.addDirectory(resourcePath);

        locale = new Locale(context, resourceCache);

        when(context.getApplicationContext().getBean(Gson.class)).thenReturn(new Gson());
    }

    @Test
    public void testLocalize_Successful() throws IOException, JsonException {
        locale.load("enGB");

        assertThat(locale.localize(0, 1), is("NEW"));
        assertThat(locale.localize(0, 2), is("LOAD"));
        assertThat(locale.localize(0, 3), is("MODS"));
        assertThat(locale.localize(0, 4), is("OPTIONS"));
        assertThat(locale.localize(0, 5), is("EXIT"));
        assertThat(locale.localize(0, 6), is("CONTINUE"));
        assertThat(locale.localize(0, 7), is("SAVE"));
    }

    @Test
    public void testLocalize_LineDoesntExistSuccessful() throws IOException, JsonException {
        locale.load("enGB");

        assertThat(locale.localize(0, 8), is(nullValue()));
    }

    @Test
    public void testLocalize_PageDoesntExistSuccessful() throws IOException, JsonException {
        locale.load("enGB");

        assertThat(locale.localize(1, 8), is(nullValue()));
    }

    @Test(expected = IOException.class)
    public void testLocalize_LanguageDoesntExist() throws IOException, JsonException {
        locale.load("enUS");
    }

    @Test
    public void testReplace() {
        String replaced = Locale.replace("{0} to {1}", "foo", "bar");

        assertThat(replaced.startsWith("foo"), is(true));
        assertThat(replaced.endsWith("bar"), is(true));
    }
}
