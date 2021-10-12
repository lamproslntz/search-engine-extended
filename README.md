# Extending Search Engines with Synonyms

## Table of Contents
* [About the Project](#about-the-project)
* [Dataset](#dataset)
* [Technologies](#technologies)
* [What You Need](#what-you-need)
* [Run the Application](#run-the-application)
* [Demo](#demo)
* [License](#license)

## About the Project
When we search for something on the web, we usually give keywords or a description of what we want to find. But that’s not always the case! There are times when we’re careless of the words we choose to use in our searches, ambiguous, or even indifferent of the vocabulary we use. Thus, the results we get aren't the ones we wanted and we're unsatisfied. 

This project's purpose is find a way to make search engines (or in general, Information Retrieval Systems) answer users’ questions as precisely as possible. The idea that is proposed, is that **we should examine users' queries from many perspectives**! This is achieved by using the technique of **extending the user query with synonymous terms**, so that it can express the information need of the user in different ways. There are two methods we've looked into to find synonyms:
* **WordNet**, which is a collection of synonyms and sometimes antonyms of words (thesaurus),
* **Word Embeddings**, which are vectors that encode the meaning of words such that words that are close in the vector space have similar meaning.

The two Information Retrieval Systems we have explored, are evaluated using the **trec_eval** evaluation tool and its metrics. Our focus is mainly on the behaviour of **precision**, **recall** and **mean average precision** on the top k (20, 30, 50) retrieved documents.

For the full presentation of the problem, our approach, the results, and the system's architecture, you can download and look into this [report](report/report.pptx) (powerpoint format, available into the report directory).

## Dataset
To build the search engine, **CISI dataset** has been used. This is a text-based dataset that can be used for Information Retrieval and it is publicly available from the [University of Glasgow](https://www.gla.ac.uk/schools/computing/research/researchsections/ida-section/informationretrieval/). You can download it from [Kaggle](https://www.kaggle.com/dmaso01dsta/cisi-a-dataset-for-information-retrieval).

The data consist of text data about **1,460 documents** and **112 queries**. Its purpose is to be used to build models of information retrieval where a given query will return a list of document IDs relevant to the query. The file **CISI.ALL** contains the documents each with a unique ID, title, author, abstract and list of cross-references to other documents. It is the dataset for training IR models when used in conjunction with the queries. The file **CISI.QRY** contains the queries each with a unique ID and query text. The file **CISI.REL** contains the correct list (ie. ground proof) of query-document matching and your model can be compared against this ground proof to see how it has performed.

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
* wiki-news-300d-1M.vec (download [here](https://fasttext.cc/docs/en/english-vectors.html), unzip the file and put the .vec file in ``` src\main\resources\fasttext-en ```)

## Run the Application
To run the application, run the following command in a terminal window (in the complete) directory:
```
mvnw spring-boot:run
```
Then, on the browser, visit ``` http://localhost:8080/ ``` to open the web page.

## Demo

## License
Distributed under the MIT License. See [LICENSE.md](LICENSE.md) for more information
