package gql.ratpack;

import javax.inject.Provider;
import java.util.Optional;

import com.google.inject.Inject;
import graphql.GraphQL;
import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.GraphQLSchema;

/**
 * Creates an instance of {@link GraphQL} that can be reused among all
 * incoming requests.
 *
 * @since 0.3.2
 */
public class GraphQLProvider implements Provider<GraphQL> {

  private GraphQLSchema graphQLSchema;
  private Instrumentation instrumentation;

  /**
   * Sets the {@link GraphQLSchema} instance. This is a hard
   * dependency
   *
   * @since 0.3.2
   */
  @Inject
  public void setGraphQLSchema(GraphQLSchema schema) {
    this.graphQLSchema = schema;
  }

  /**
   * Sets the {@link Instrumentation} instance used. This is a soft
   * dependency
   *
   * @since 0.3.2
   */
  @Inject(optional = true)
  public void setInstrumentation(Instrumentation instrumentation) {
    this.instrumentation = instrumentation;
  }

  @Override
  public GraphQL get() {
    final GraphQL.Builder builder = GraphQL.newGraphQL(graphQLSchema);

    return Optional
      .ofNullable(instrumentation)
      .map(ins -> builder.instrumentation(ins).build())
      .orElse(builder.build());
  }
}
