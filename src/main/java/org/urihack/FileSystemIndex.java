package org.urihack;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Inspiration: https://thirumurthi.hashnode.dev/index-and-search-pdf-content-using-lucene-and-pdfbox-libraries

public class FileSystemIndex implements AutoCloseable {
    private final Directory directory;
    private final Analyzer analyzer;
    private final IndexWriter indexWriter;

    public FileSystemIndex(String pathToIndexDirectory, boolean createNew) throws IOException {
        directory = FSDirectory.open(Paths.get(pathToIndexDirectory));
        analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();

        if (createNew) {
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        } else {
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        }

        indexWriter = new IndexWriter(directory, indexWriterConfig);
    }

    public void addDocument(Document doc) throws IOException {
        indexWriter.addDocument(doc);
    }

    public List<String> searchQuery(String searchString) throws Exception {
        if (indexWriter.isOpen()) {
            indexWriter.close();
        }

        var indexReader = DirectoryReader.open(directory);
        var indexSearcher = new IndexSearcher(indexReader);

        var queryParser = new QueryParser(Const.PDF_CONTENT, analyzer);
        var query = queryParser.parse(searchString);

        var hits = indexSearcher.search(query, indexReader.numDocs()).scoreDocs;

        var paths = new ArrayList<String>();
        for (var hit : hits) {
            Document document = indexSearcher.doc(hit.doc);
            paths.add(document.get(Const.FILEPATH));
        }

        return paths;
    }

    @Override
    public void close() throws IOException {
        if (indexWriter != null && indexWriter.isOpen()) {
            indexWriter.close();
        }
    }
}
