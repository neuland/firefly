package de.neuland.firefly.changes;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;


public class ChangeFileFilter implements FileFilter {
    private static final Logger LOG = Logger.getLogger(ChangeFileFilter.class);

    @Override public boolean accept(File pathname) {
        if (!pathname.canRead()) {
            LOG.warn("Unable to read potential change file " + pathname.getAbsolutePath());
            return false;
        }
        return pathname.isFile() &&
               pathname.getName().toUpperCase().endsWith(".XML");
    }
}
