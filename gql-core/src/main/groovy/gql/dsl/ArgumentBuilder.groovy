package gql.dsl

import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLInputType

/**
 * This builder holds information to build a new argument, an
 * instance of {@link GraphQLArgument}
 *
 * @since 0.1.6
 */
class ArgumentBuilder {

  GraphQLArgument.Builder builderSource = GraphQLArgument.newArgument()

  /**
   * Sets the type of the argument. It has to be something of
   * type {@link GraphQLInputType}
   *
   * @param inputType an instance of type {@link GraphQLInputType}
   * @since 0.1.6
   */
  ArgumentBuilder(GraphQLInputType inputType) {
    builderSource.type(inputType)
  }

  /**
   * Sets the new of the argument
   *
   * @param name the name of the argument
   * @return the current builder instance
   * @since 0.1.6
   */
  ArgumentBuilder name(String name) {
    builderSource.name(name)
    return this
  }

  /**
   * Sets the description of the argument
   *
   * @param description the description of the argument
   * @return the current builder instance
   * @since 0.1.6
   */
  ArgumentBuilder description(String description) {
    builderSource.description(description)
    return this
  }

  /**
   * Returns the built instance of type {@link GraphQLArgument}
   *
   * @return the resulting {@link GraphQLArgument} instance
   * @since 0.1.6
   */
  GraphQLArgument build() {
    return builderSource.build()
  }
}
