package org.homonoia.eris.core.components;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.lwjgl.system.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 29/02/2016
 */
public class FileSystem extends Contextual {

    private final List<Path> allowedPaths = new ArrayList<>();

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
        Objects.requireNonNull(path);

        if (allowedPaths.isEmpty()) {
            return true;
        }

        Path fullPath;
        if (!path.isAbsolute()) {
            fullPath = getApplicationDataDirectory().resolve(path);
        } else {
            fullPath = path;
        }

        for (Path allowedPath : allowedPaths) {
            int compareTo = allowedPath.compareTo(fullPath);
            if (compareTo <= 0) {
                return true;
            }
        }

        return false;
    }

    public InputStream newInputStream(final Path path) throws IOException {
        if (isAccessible(path)) {
            return Files.newInputStream(path);
        }
        throw new IOException("Path not accessible");
    }

    public OutputStream newOutputStream(final Path path) throws IOException {
        if (isAccessible(path)) {
            if (!path.getParent().toFile().exists()) {
                Files.createDirectories(path.getParent());
            }
            return Files.newOutputStream(path);
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

    public static String readableFileSize(final long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
