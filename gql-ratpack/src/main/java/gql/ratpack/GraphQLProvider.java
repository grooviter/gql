package gql.ratpack;

import javax.inject.Inject;
import javax.inject.Provider;

import graphql.GraphQL;
import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.GraphQLSchema;

public class GraphQLProvider implements Provider<GraphQL> {

  private final GraphQLSchema graphQLSchema;
  private final Instrumentation instrumentation;

  @Inject
  public GraphQLProvider(GraphQLSchema schema, Instrumentation instrumentation) {
    this.graphQLSchema = schema;
    this.instrumentation = instrumentation;
  }

  @Override
  public GraphQL get() {
    return GraphQL
      .newGraphQL(graphQLSchema)
      .instrumentation(instrumentation)
      .build();
  }
}
