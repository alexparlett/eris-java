package org.homonoia.eris.engine;

import com.google.gson.Gson;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.homonoia.eris.core.CommandLineArgs;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.EmptyStatistics;
import org.homonoia.eris.core.FileSystem;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.homonoia.eris.resources.types.json.JsonException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 06/03/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class LocaleTest {

    @Mock
    private CommandLineArgs commandLineArgs;

    @InjectMocks
    Context context;

    FileSystem fileSystem;
    ResourceCache resourceCache;
    Locale locale;

    @Before
    public void setup() {
        context.registerBean(new EmptyStatistics());
        context.registerBean(new Gson());

        fileSystem = new FileSystem(context);
        resourceCache = new ResourceCache(context, fileSystem);

        Path resourcePath = Paths.get("src/test/resources").toAbsolutePath();
        fileSystem.addPath(resourcePath);
        resourceCache.addDirectory(resourcePath);

        locale = new Locale(context, resourceCache);
    }

    @Test
    public void testLocalize_Successful() throws IOException, JsonException {
        locale.load("enGB");

        Assert.assertThat(locale.localize(0, 1), Is.is("NEW"));
        Assert.assertThat(locale.localize(0, 2), Is.is("LOAD"));
        Assert.assertThat(locale.localize(0, 3), Is.is("MODS"));
        Assert.assertThat(locale.localize(0, 4), Is.is("OPTIONS"));
        Assert.assertThat(locale.localize(0, 5), Is.is("EXIT"));
        Assert.assertThat(locale.localize(0, 6), Is.is("CONTINUE"));
        Assert.assertThat(locale.localize(0, 7), Is.is("SAVE"));
    }

    @Test
    public void testLocalize_LineDoesntExistSuccessful() throws IOException, JsonException {
        locale.load("enGB");

        Assert.assertThat(locale.localize(0, 8), Is.is(IsNull.nullValue()));
    }

    @Test
    public void testLocalize_PageDoesntExistSuccessful() throws IOException, JsonException {
        locale.load("enGB");

        Assert.assertThat(locale.localize(1, 8), Is.is(IsNull.nullValue()));
    }

    @Test(expected = IOException.class)
    public void testLocalize_LanguageDoesntExist() throws IOException, JsonException {
        locale.load("enUS");
    }

    @Test
    public void testReplace() {
        String replaced = Locale.replace("{0} to {1}", "foo", "bar");

        Assert.assertThat(replaced.startsWith("foo"), Is.is(true));
        Assert.assertThat(replaced.endsWith("bar"), Is.is(true));
    }
}
