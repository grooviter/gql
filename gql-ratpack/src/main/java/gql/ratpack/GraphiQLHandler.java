package gql.ratpack;

import static ratpack.util.Exceptions.uncheck;

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
import ratpack.handling.Handler;
import ratpack.handling.Context;
import com.google.common.collect.ImmutableMap;

/**
 * GraphiQL endpoint. This handler will be exposing a given GraphiQL
 * client.
 *
 * By default the handler is disable, so in order to enable it you
 * should set the {@link GraphQLModule.Config}#activateGraphiQL to
 * true when enabling the module
 *
 * @since 0.2.0
 */
public class GraphiQLHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    GraphQLModule.Config config = ctx.get(GraphQLModule.Config.class);

    if (config.activateGraphiQL) {
      ctx.render(getGraphiQLFile());
    } else {
      ctx.getResponse().status(404);
      ctx.getResponse().send();
    }
  }

  private Path getGraphiQLFile() throws URISyntaxException {
    URL url = GraphiQLHandler.class.getResource("/static/index.html");
    Path path = toPath(url);

    return path;
  }

  private static Path toPath(URL resource) throws URISyntaxException {
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
