package org.homonoia.eris.engine;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.components.FileSystem;
import org.homonoia.eris.resources.cache.ResourceCache;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.lwjgl.system.Platform;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexp
 * @since 06/03/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class SettingsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    ApplicationContext applicationContext;

    @InjectMocks
    Context context;

    FileSystem fileSystem;
    ResourceCache resourceCache;
    Settings settings;


    @Before
    public void setup() {
        fileSystem = new FileSystem(context);
        resourceCache = new ResourceCache(context, fileSystem);

        Path resourcePath = Paths.get("src/test/resources").toAbsolutePath();
        fileSystem.addPath(resourcePath);
        fileSystem.addPath(Paths.get(folder.getRoot().getAbsolutePath()));
        resourceCache.addDirectory(resourcePath);

        settings = new Settings(context, resourceCache, fileSystem);
    }

    @Test
    public void testGetSetting_Successful() throws IOException {
        settings.load();

        assertThat(settings.getString("Section1", "Foo1").get(), is("Bar"));
        assertThat(settings.getInteger("Section1", "Foo2").get(), is(2));
        assertThat(settings.getBoolean("Section2", "Foo3").get(), is(true));
        assertThat(settings.getFloat("Section2", "Foo4").get(), is(2.0f));
    }

    @Test
    public void testGetSetting_SuccessfulPropertyNotFound() throws IOException {
        settings.load();

        assertThat(settings.getString("Section1", "Foo3").isPresent(), is(false));
    }

    @Test
    public void testGetSetting_SuccessfulSectionNotFound() throws IOException {
        settings.load();

        assertThat(settings.getString("Section3", "Foo3").isPresent(), is(false));
    }

    @Test
    public void testSetSetting_SuccessfulAlreadyExists() throws IOException {
        settings.load();

        settings.setInteger("Section1", "Foo1", 1);
        assertThat(settings.getInteger("Section1", "Foo1").get(), is(1));
    }

    @Test
    public void testSetSetting_SuccessfulSectionAlreadyExists() throws IOException {
        settings.load();

        settings.setInteger("Section1", "Foo3", 1);
        assertThat(settings.getInteger("Section1", "Foo3").get(), is(1));
    }

    @Test
    public void testSetSetting_SuccessfulDoesntExists() throws IOException {
        settings.load();

        settings.setInteger("Section3", "Foo5", 1);
        assertThat(settings.getInteger("Section3", "Foo5").get(), is(1));
    }

    @Test(expected = IOException.class)
    public void testLoad_NoFile() throws IOException {
        Path resourcePath = Paths.get("src/test/resources").toAbsolutePath();
        Path emptyResourcePath = Paths.get("src/test/resources/Empty").toAbsolutePath();
        fileSystem.removePath(resourcePath);
        resourceCache.removeDirectory(resourcePath);

        fileSystem.addPath(emptyResourcePath);
        resourceCache.addDirectory(emptyResourcePath);

        settings.load();
    }

    @Test
    public void testSave_Successful() throws Exception {
        if (Platform.get().equals(Platform.WINDOWS)) {
            Map<String, String> map = new HashMap<>(System.getenv());
            map.put("APPDATA", folder.getRoot().getAbsolutePath());
            set(map);
        } else {
            System.setProperty("user.home", folder.getRoot().getAbsolutePath());
        }

        settings.load();
        settings.save();

        String[] fileList = folder.getRoot().list();
        assertThat(Stream.of(fileList).findAny().isPresent(), is(true));
    }

    public static void set(Map<String, String> newenv) throws Exception {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            try {
                Class[] classes = Collections.class.getDeclaredClasses();
                Map<String, String> env = System.getenv();
                for (Class cl : classes) {
                    if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                        Field field = cl.getDeclaredField("m");
                        field.setAccessible(true);
                        Object obj = field.get(env);
                        Map<String, String> map = (Map<String, String>) obj;
                        map.clear();
                        map.putAll(newenv);
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}
