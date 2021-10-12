# Extending Search Engines with Synonyms

## Table of Contents
* [Introduction](#introduction)
* [Dataset](#dataset)
* [Technologies](#technologies)
* [What You Need](#what-you-need)
* [Run the Application](#run-the-application)
* [License](#license)

## Introduction

## Dataset

## Technologies
**Programming Language**: Java <br>
**Frontend**: HTML, Thymeleaf, CSS <br>
**Application Framework**: Spring Boot <br>
**Search Engine**: Apache Lucene <br>
**Machine Learning/Deep Learning**: Deeplearning4j, WordNet <br>
**Other Libraries**: Apache Maven, Apache Commons

## What You Need
* JDK 11
* Apache Maven 3.2+
* wiki-news-300d-1M.vec (download from [here](https://fasttext.cc/docs/en/english-vectors.html), unzip the file and put the .vec file in ``` src\main\resources\fasttext-en ```)

## Run the Application
To run the application, run the following command in a terminal window (in the complete) directory:
```
mvnw spring-boot:run
```
Then, on the browser, visit ``` http://localhost:8080/ ``` to open the web page.

## License
Distributed under the MIT License. See [LICENSE.md](#LICENSE.md) for more information
