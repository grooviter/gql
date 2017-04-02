package gql.test.util

import java.nio.file.Path
import java.nio.file.Paths

/**
 * This class has methods to retrieve system resources
 *
 * @since 0.1.0
 */
class SystemResources {

  /**
   * Resolves the given file classpath to the underlying file system
   * full path
   *
   * @param filePath path within the classpath of a given resource
   * @return a {@link Path} of the passed string
   * @since 0.1.0
   */
  static Path classpath(String filePath) {
    Paths.get(ClassLoader.getSystemResource(filePath).toURI())
  }

  /**
   * Resolves the given file classpath to the underlying file system
   * full path as an {@link URL}
   *
   * @param filePath path within the classpath of a given resource
   * @return a {@link URL} of the passed string path
   * @since 0.1.0
   */
  static URL classpathAsURL(String filePath) {
    Class.getResource(filePath)
  }
}
