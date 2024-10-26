package gql.dsl

import groovy.transform.TupleConstructor

/**
 * This class represent a nested query depth. At which level we are operating. It's helpful
 * to render queries properly as {@link String}
 *
 * @since 1.1.0
 */
@TupleConstructor
class Leveled {
  /**
   * The current query level
   *
   * @since 1.1.0
   */
  Integer level = 1

  /**
   *  This method returns the number of tabs the render must use to render the query at this level properly
   *
   * @return the number of tabs corresponding to the current level
   * @since 1.1.0
   */
  String getLevelTabs() {
    return "\t" * (this.level - 1)
  }
}
