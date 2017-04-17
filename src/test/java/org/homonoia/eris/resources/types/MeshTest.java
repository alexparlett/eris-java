package org.homonoia.eris.resources.types;

import com.google.gson.Gson;
import org.homonoia.eris.core.CommandLineArgs;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.EmptyStatistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexparlett on 07/05/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MeshTest {

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
    public void testLoad_Successful() throws IOException {
        InputStream resource = ClassLoader.getSystemResourceAsStream("cube.obj");

        Mesh mesh = new Mesh(context);
        mesh.load(resource);
    }

}
