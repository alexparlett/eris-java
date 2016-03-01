package org.homonoia.eris.core.components;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.core.annotations.ContextualComponent;
import org.lwjgl.system.Platform;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by alexparlett on 29/02/2016.
 */
@ContextualComponent
public class FileSystem extends Contextual {

    private List<Path> allowedPaths = new ArrayList<>();

    @Autowired
    public FileSystem(final Context context) {
        super(context);
    }

    public void addPath(final Path path) {
        Objects.requireNonNull(path);
        allowedPaths.add(path);
    }

    public void removePath(final Path path) {
        Objects.requireNonNull(path);
        allowedPaths.remove(path);
    }

    public boolean isAccessible(final Path path) {
        return false;
    }

    public InputStream newInputStream(final Path path) throws IOException {
        if (isAccessible(path)) {
            return Files.newInputStream(path);
        }
        throw new IOException("Path not accessible");
    }

    public static Path getApplicationDataDirectory() {
        if (Platform.get().equals(Platform.WINDOWS)) {
            return Paths.get(System.getenv("APPDATA"), "Homonoia Studios", "Eris");
        } else if (Platform.get().equals(Platform.LINUX)) {
            return Paths.get(System.getProperty("user.home"), "Homonoia Studios", "Eris");
        } else {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "Homonoia Studios", "Eris");
        }
    }


    public static Path getApplicationDirectory() {
        return Paths.get(System.getProperty("user.dir"));
    }

    public static Path getTempDirectory() {
        return Paths.get(System.getProperty("java.io.tmpdir"));
    }

    public OutputStream newOutputStream(final Path path) throws IOException {
        if (isAccessible(path)) {
            return Files.newOutputStream(path);
        }
        throw new IOException("Path not accessible");
    }
}
