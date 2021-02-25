TextFileStemmer
=================================================

![Points](../../blob/badges/points.svg)

For this homework assignment, you will create a class that is able to clean and parse text into stemmed words. Use `UTF8` and try-with-resources when writing your files. Do not use the [`java.io.File`](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/io/File.html) class.

The `TextParser` class is already provided for you.

## Hints ##

Below are some hints that may help with this homework assignment:

  - You need to have use the third-party [Apache OpenNLP](http://opennlp.apache.org/) library. The library should be automatically setup in Eclipse by maven. For how to use this library, see the [Apache OpenNLP Tools Javadoc](https://opennlp.apache.org/docs/1.9.3/apidocs/opennlp-tools/index.html) for details.

  - When working with files, you should use try-with-resources, the `UTF-8` character encoding, and buffered readers and writers.

  - Look for opportunities to reduce duplicate code. For example, you could create a new helper method that is reused in multiple other methods.

These hints are *optional*. There may be multiple approaches to solving this homework.

## Requirements ##

See the Javadoc and `TODO` comments in the template code in the `src/main/java` directory for additional details. You must pass the tests provided in the `src/test/java` directory. Do not modify any of the files in the `src/test` directory.

See the [Homework Guides](https://usf-cs212-spring2021.github.io/guides/homework/) for additional details on homework requirements and submission.
