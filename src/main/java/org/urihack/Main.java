package org.urihack;

import org.urihack.pdf.PdfLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    // Adjust based on where the PDFs lie and where the index should be built.
    private static final String pdfFileDirectory = "C:\\dev\\HackUri\\data\\pdf";
    private static final String indexDirectoryPath = "C:\\dev\\HackUri\\data\\index";

    public static void main(String[] args) {

        // To build the index set to true
        final boolean buildIndex = false;

        try {
            FileSystemIndex index;
            if (buildIndex) {
                index = buildIndex();
            } else {
                index = new FileSystemIndex(indexDirectoryPath, false);
            }

            final String searchString = "Schächenbrücke";
            var docHits = index.searchQuery(searchString);

            if (docHits.size() == 0) {
                System.out.println(String.format("No hits for search string '%s'", searchString));
            } else {
                System.out.println(String.format("Search string '%s' yielded %d results", searchString, docHits.size()));
                for (var hit : docHits) {
                    System.out.println(hit);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static FileSystemIndex buildIndex() throws IOException {
        var index = new FileSystemIndex(indexDirectoryPath, true);

        var pdfs = Files.find(Paths.get(pdfFileDirectory),
                Integer.MAX_VALUE,
                ((path, basicFileAttributes) -> basicFileAttributes.isRegularFile()))
                .map(path -> path.toFile())
                .toList();

        for (var pdf : pdfs) {
            var doc = PdfLoader.createDocument(pdf);
            index.addDocument(doc);
            System.out.println(doc.getField("filepath").stringValue());
        }

        return index;
    }
}
