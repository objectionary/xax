[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/objectionary/xax)](http://www.rultor.com/p/objectionary/xax)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/objectionary/xax/actions/workflows/mvn.yml/badge.svg)](https://github.com/objectionary/xax/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=objectionary/xax)](http://www.0pdd.com/p?name=objectionary/xax)
[![Maintainability](https://api.codeclimate.com/v1/badges/742bde48ea6fabdba1ce/maintainability)](https://codeclimate.com/github/objectionary/xax/maintainability)
[![Maven Central](https://img.shields.io/maven-central/v/com.objectionary/xax.svg)](https://maven-badges.herokuapp.com/maven-central/com.objectionary/xax)
[![Javadoc](http://www.javadoc.io/badge/com.objectionary/xax.svg)](http://www.javadoc.io/doc/com.objectionary/xax)
[![codecov](https://codecov.io/gh/objectionary/xax/branch/master/graph/badge.svg)](https://codecov.io/gh/objectionary/xax)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/xax)](https://hitsofcode.com/view/github/objectionary/xax)
![Lines of code](https://img.shields.io/tokei/lines/github/objectionary/xax)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/objectionary/xax/blob/master/LICENSE.txt)

This simple library can help you test your XSL stylesheets against different XML
document and assert the validity of transformations using XPath expressions.

You add this to your `pom.xml`:

```xml
<dependency>
  <groupId>org.eolang</groupId>
  <artifactId>xax</artifactId>
</dependency>
```

Then, do this:

```java
...
```

## How to Contribute

Fork repository, make changes, send us a [pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
$ mvn clean install -Pqulice
```

You will need Maven 3.3+ and Java 8+.
