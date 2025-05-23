# JUnit Matcher for XSL Transformations

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](https://www.rultor.com/b/objectionary/xax)](https://www.rultor.com/p/objectionary/xax)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/objectionary/xax/actions/workflows/mvn.yml/badge.svg)](https://github.com/objectionary/xax/actions/workflows/mvn.yml)
[![PDD status](https://www.0pdd.com/svg?name=objectionary/xax)](https://www.0pdd.com/p?name=objectionary/xax)
[![Maven Central](https://img.shields.io/maven-central/v/org.eolang/xax.svg)](https://maven-badges.herokuapp.com/maven-central/org.eolang/xax)
[![Javadoc](https://www.javadoc.io/badge/org.eolang/xax.svg)](https://www.javadoc.io/doc/org.eolang/xax)
[![codecov](https://codecov.io/gh/objectionary/xax/branch/master/graph/badge.svg)](https://codecov.io/gh/objectionary/xax)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/xax)](https://hitsofcode.com/view/github/objectionary/xax)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/objectionary/xax/blob/master/LICENSE.txt)

This simple library can help you test your XSL stylesheets against different XML
document and assert the validity of transformations using XPath expressions.

You add this to your `pom.xml`:

```xml
<dependency>
  <groupId>org.eolang</groupId>
  <artifactId>xax</artifactId>
  <version>0.6.2</version>
</dependency>
```

Then, create this XSL file in `src/main/resources/simple.xsl`:

```xml
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="foo">
    <xsl:copy>
      <xsl:text>bye</xsl:text>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="node()|@*" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
```

Then, create this YAML file in `src/test/resources/simple.yaml`:

```yaml
sheets:
  - simple.xsl
document:
  <doc><foo>hello</foo></doc>
asserts:
  - /doc/foo[.='bye']
```

Finally, make a unit test (using
[JUnit5](https://github.com/junit-team/junit5),
[Hamcrest](https://github.com/hamcrest/JavaHamcrest),
and `@ClasspathSource` from [Jucs](https://github.com/objectionary/jucs)):

```java
import org.eolang.jucs.ClasspathSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;

final class MyTest {
    @ParameterizedTest
    @ClasspathSource(value = "", glob = "**.yaml")
    void itWorks(String yaml) {
        MatcherAssert.assertThat(
            new XaxStory(yaml),
            Matchers.is(true)
        );
    }
}
```

Should work.

## How to Contribute

Fork repository, make changes, send us a
[pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
mvn clean install -Pqulice
```

You will need Maven 3.3+ and Java 8+.
