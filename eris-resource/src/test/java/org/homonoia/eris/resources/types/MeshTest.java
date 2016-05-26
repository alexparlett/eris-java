package org.homonoia.eris.resources.types;

import org.homonoia.eris.core.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by alexparlett on 07/05/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MeshTest {

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private Context context;

    @Test
    public void testLoad_Successful() throws IOException {
        Resource resource = new PathMatchingResourcePatternResolver().getResource("cube.obj");

        Mesh mesh = new Mesh(context);
        mesh.load(resource.getInputStream());

        assertThat(mesh.getFaces().size(), is(12));
        assertThat(mesh.getGeometry().size(), is(8));
        assertThat(mesh.getTextureCoords().size(), is(4));
        assertThat(mesh.getNormals().size(), is(6));
    }

}
