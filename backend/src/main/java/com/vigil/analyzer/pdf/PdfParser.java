package com.vigil.analyzer.pdf;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PdfParser {

    // Regex to extract URLs from plain text in the PDF
    private static final Pattern URL_PATTERN = Pattern.compile(
        "\\bhttps?://[a-zA-Z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]+",
        Pattern.CASE_INSENSITIVE
    );

    public PdfParseResult parse(InputStream inputStream, String fileName) throws IOException {
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(inputStream.readAllBytes())) {
            
            // Extract Text
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Extract URLs
            List<String> urls = new ArrayList<>();
            // 1. From embedded hyperlinks
            for (PDPage page : document.getPages()) {
                for (PDAnnotation annotation : page.getAnnotations()) {
                    if (annotation instanceof PDAnnotationLink) {
                        PDAnnotationLink link = (PDAnnotationLink) annotation;
                        PDAction action = link.getAction();
                        if (action instanceof PDActionURI) {
                            String uri = ((PDActionURI) action).getURI();
                            if (uri != null && (uri.startsWith("http://") || uri.startsWith("https://"))) {
                                urls.add(uri);
                            }
                        }
                    }
                }
            }
            // 2. From plain text
            Matcher matcher = URL_PATTERN.matcher(text);
            while (matcher.find()) {
                String matchedUrl = matcher.group();
                if (!urls.contains(matchedUrl)) {
                    urls.add(matchedUrl);
                }
            }

            // Extract Metadata
            Map<String, String> metadata = new HashMap<>();
            PDDocumentInformation info = document.getDocumentInformation();
            if (info != null) {
                if (info.getAuthor() != null) metadata.put("Author", info.getAuthor());
                if (info.getCreator() != null) metadata.put("Creator", info.getCreator());
                if (info.getProducer() != null) metadata.put("Producer", info.getProducer());
                if (info.getTitle() != null) metadata.put("Title", info.getTitle());
                if (info.getSubject() != null) metadata.put("Subject", info.getSubject());
            }

            return PdfParseResult.builder()
                    .extractedText(text)
                    .extractedUrls(urls)
                    .metadata(metadata)
                    .pageCount(document.getNumberOfPages())
                    .fileName(fileName)
                    .build();
        }
    }
}
