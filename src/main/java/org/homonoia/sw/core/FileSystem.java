package org.homonoia.eris.core;

import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import static com.jme3.system.Platform.Linux32;
import static com.jme3.system.Platform.Linux64;
import static com.jme3.system.Platform.Windows32;
import static com.jme3.system.Platform.Windows64;

/**
 * Copyright (c) 2015-2016 the Eris project.
 *
 * @author alexparlett
 * @since 29/02/2016
 */
public class FileSystem {
    public static Path getApplicationDataDirectory() {
        Platform platform = JmeSystem.getPlatform();
        if (platform.equals(Windows32) || platform.equals(Windows64)) {
            return Paths.get(System.getenv("APPDATA"), "Homonoia Studios", "Eris");
        } else if (platform.equals(Linux32) || platform.equals(Linux64)) {
            return Paths.get(System.getProperty("user.home"), "Homonoia Studios", "Eris");
        } else {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "Homonoia Studios", "Eris");
        }
    }

    public static String getApplicationDataDirectoryString() {
        return getApplicationDirectory().toString();
    }

    public static String getApplicationDirectory() {
        return System.getProperty("user.dir");
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
