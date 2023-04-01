# amtsblatt-lucene
Created for Data Hack Day 2023.
Our challenge: Making "Amtsblätter" searchable.

This project tries to achieve that through indexing and searching public "Amtsblatt" pdfs using Apache Lucene.

## Setup
* Download all relevant PDFs and point the path constant "Main.pdfFileDirectory" to the root directory.
* Point the path "Main.indexDirectoryPath" to a directory where Lucene can put its indices.
* For the first run of the program, change the boolean "buildIndex" to true. 
Subsequent runs should have "buildIndex" on false, otherwise the indices get built multiple times, resulting in duplicate matches in the search.
  * Building the indices will take a few minutes.

## How to use
Type in the words you'd like to search for followed by *ENTER*.
The program will tell you in which document the words are used.
If you use multiple words, then the program will match on every word.
Typing in "Herrengasse Altdorf" will match on both "Herrengasse" and "Altdorf".
If you want to ensure that both words are kept as a unit use parentheses: ""Herrengasse Altdorf""
Full documentation for search queries can be found at https://lucene.apache.org/core/9_5_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html.

