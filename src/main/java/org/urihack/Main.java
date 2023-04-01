package org.urihack;

import org.urihack.pdf.PdfLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    // Adjust based on where the PDFs lie and where the index should be built/lies to be read.
    private static final String pdfFileDirectory = "C:\\dev\\HackUri\\data\\pdf";
    private static final String indexDirectoryPath = "C:\\dev\\HackUri\\data\\index";

    public static void main(String[] args) throws IOException {

        // To build the index set to true
        final boolean buildIndex = false;

        FileSystemIndex index = null;

        try {
            if (buildIndex) {
                index = buildIndex();
            } else {
                index = new FileSystemIndex(indexDirectoryPath, false);
            }

            var reader = new BufferedReader(new InputStreamReader(System.in));

            do {
                String searchString = reader.readLine();

                if (searchString.equals("quit")){
                    break;
                }
                var docHits = index.searchQuery(searchString);

                if (docHits.size() == 0) {
                    System.out.printf("No hits for search string '%s'%n", searchString);
                } else {
                    System.out.printf("Search string '%s' yielded %d results%n", searchString, docHits.size());
                    for (var hit : docHits) {
                        System.out.println(hit);
                    }
                }
            } while (true);

        } catch (Exception ex) {
            if (index != null){
                index.close();
            }

            ex.printStackTrace();
        }
    }

    private static FileSystemIndex buildIndex() throws IOException {
        var index = new FileSystemIndex(indexDirectoryPath, true);

        var pdfs = Files.find(Paths.get(pdfFileDirectory),
                Integer.MAX_VALUE,
                ((path, basicFileAttributes) -> basicFileAttributes.isRegularFile()))
                .map(Path::toFile)
                .toList();

        for (var pdf : pdfs) {
            var doc = PdfLoader.createDocument(pdf);
            index.addDocument(doc);
            System.out.println(doc.getField(Const.FILEPATH).stringValue());
        }

        return index;
    }
}
