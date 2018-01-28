package gql.ratpack;

import static ratpack.util.Exceptions.uncheck;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Optional;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileSystemNotFoundException;

/**
 * Helps to resolve resources location
 *
 * @since 0.2.0
 */
public final class PathUtil {

  /**
   * Resolves a given resource location whether it's located in the
   * current classpath or in any of the available jars in the
   * application
   *
   * @param resource the {@link URL} of the resource
   * @return a full {@link Path} where the resource can be located
   * @since 0.2.0
   */
  public static Path toPath(URL resource) throws URISyntaxException {
    URI uri = resource.toURI();

    String scheme = uri.getScheme();
    if (scheme.equals("file")) {
      return Paths.get(uri);
    }

    if (!scheme.equals("jar")) {
      throw new IllegalStateException("Cannot deal with class path resource url: " + uri);
    }

    String s = uri.toString();
    int separator = s.indexOf("!/");
    String entryName = s.substring(separator + 2);
    URI fileURI = URI.create(s.substring(0, separator));
    FileSystem fs = null;
    try {
      // Check if there's an existing file system, since it's provider-dependent whether file systems with the same URI are allowed
      fs = FileSystems.getFileSystem(fileURI);
      if (!fs.isOpen()) { // It's provider-dependent whether to return closed file systems
                         fs = null; // Ignore it; closed file systems can't be used
      }
    } catch (FileSystemNotFoundException ignore) {
      // Continue to create the file system
    }
    if (fs == null) {
      try {
        fs = FileSystems.newFileSystem(fileURI, ImmutableMap.of());
      } catch (IOException e) {
        throw uncheck(e);
      }
    }
    return fs.getPath(entryName);
  }
}
