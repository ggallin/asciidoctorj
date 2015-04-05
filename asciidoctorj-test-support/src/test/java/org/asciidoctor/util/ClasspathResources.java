package org.asciidoctor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Hels getting files from the classpath.
 */
public class ClasspathResources {

    private ClassLoader classloader;

    public ClasspathResources(Class<?> clazz) {
        this.classloader = clazz.getClassLoader();
    }

    /**
     * Gets a resourse in a similar way as {@link File#File(String)} 
     */
    public File getResource(String pathname) {
        try {
            URL resource = classloader.getResource(pathname);
            if (resource != null) {
                return new File(classloader.getResource(pathname).toURI());
            }
            else {
                throw new RuntimeException(new FileNotFoundException(pathname));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     *  Gets a resourse in a similar way as {@link File#File(String, String)} 
     */
    public File getResource(String parent, String child) {
        return new File(getResource(parent), child);
    }

}
