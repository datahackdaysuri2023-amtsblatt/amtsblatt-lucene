package org.urihack.pdf;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.urihack.Const;

import java.io.*;

public class PdfLoader {


    public static Document createDocument(File pdfFile) throws IOException {
        var doc = new Document();
        String fileModifiedTimeStr = DateTools.timeToString(pdfFile.lastModified(), DateTools.Resolution.SECOND);
        String path = pdfFile.getPath();
        doc.add(new StringField(Const.FILEPATH, path, Store.YES));
        doc.add(new StringField(Const.LAST_MODIFIED, fileModifiedTimeStr, Store.YES));

        try (FileInputStream inputStream = new FileInputStream(pdfFile)) {
            parsePDFAndAddContentToDocument(doc, inputStream);
        }

        return doc;
    }

    private static void parsePDFAndAddContentToDocument(Document doc, FileInputStream inputStream) throws IOException {
        try (PDDocument pdfDocument = PDDocument.load(inputStream)) {
            var strWriter = new StringWriter();
            var txtStripper = new PDFTextStripper();
            txtStripper.writeText(pdfDocument, strWriter);
            var reader = new StringReader(strWriter.getBuffer().toString());
            doc.add(new TextField(Const.PDF_CONTENT, reader));
        }
    }
}
