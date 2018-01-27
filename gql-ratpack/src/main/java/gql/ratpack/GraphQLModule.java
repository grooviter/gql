package gql.ratpack;

import com.google.inject.Scopes;
import ratpack.guice.ConfigurableModule;

/**
 * Configures GraphQL and GraphiQL handler instances
 *
 * import static ratpack.groovy.Groovy.ratpack
 *
 * import gql.ratpack.GraphQLHandler
 * import gql.ratpack.GraphiQLHandler
 *
 * ratpack {
 *   bindings {
 *     module(GraphQLModule)
 *   }
 *   handlers {
 *     prefix('graphql') {
 *       post(GraphQLHandler)
 *       prefix('browser') {
 *         get(GraphiQLHandler)
 *       }
 *     }
 *   }
 * }
 *
 * @since 0.2.0
 */
public class GraphQLModule extends ConfigurableModule<GraphQLModule.Config> {

  /**
   * GraphQL module configuration. You can set things like to
   * enable/disable GraphiQL
   *
   * @since 0.2.0
   */
  public static class Config {
    boolean activateGraphiQL = true;
  }

  @Override
  public void configure() {
    bind(GraphQLHandler.class).in(Scopes.SINGLETON);
    bind(GraphiQLHandler.class).in(Scopes.SINGLETON);
  }
}
