[![license](https://img.shields.io/github/license/grooviter/gql.svg)]() [![Bintray](https://api.bintray.com/packages/grooviter/maven/gql/images/download.svg)](https://bintray.com/grooviter/maven/gql) ![master](https://github.com/grooviter/gql/workflows/master/badge.svg) ![development](https://github.com/grooviter/gql/workflows/development/badge.svg)

**GQL** is a set of [Groovy](http://www.groovy-lang.org) DSLs and AST
transformations built on top
of [GraphQL-java](https://github.com/graphql-java/graphql-java) that
make it easier to build GraphQL schemas and execute **GraphQL**
queries without losing type safety.

In order to use `GQL` in your Groovy code you can find it in Bintray:

    repositories {
        jcenter()
    }

Then you can add the dependency to your project:

    compile 'com.github.grooviter:gql-core:0.4.0'

Current documentation is available at: http://grooviter.github.io/gql/
