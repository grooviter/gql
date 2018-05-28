package gql.ratpack.pac4j;

import static gql.ratpack.GraphQLHandlerUtil.executeGraphQL;
import static gql.ratpack.GraphQLHandlerUtil.renderGraphQL;
import static gql.ratpack.GraphQLHandlerUtil.renderGraphQLError;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParseException;
import org.pac4j.core.profile.UserProfile;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.pac4j.RatpackPac4j;

/**
 * GraphQL endpoint making available Pac4j UserProfile in
 * an easy way through Ratpack's {@link Context} request
 *
 * @since 0.3.1
 */
public class GraphQLHandler implements Handler {

  @Override
  public void handle(Context ctx) {
    RatpackPac4j
      .userProfile(ctx)
      .flatMap((Optional<UserProfile> userProfile) -> {
        // making UserProfile available easily through
        // ctx.request.get(UserProfile)
        ctx.getRequest().add(userProfile);

        return ctx
          .parse(Map.class)
          .onError(JsonParseException.class, renderGraphQLError(ctx))
          .flatMap(executeGraphQL(ctx));
      }).then(renderGraphQL(ctx));
  }
}
