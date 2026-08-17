package com.vigil.analyzer.pdf;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
@Builder
public class PdfParseResult {
    private String extractedText;
    private List<String> extractedUrls;
    private Map<String, String> metadata;
    private int pageCount;
    private String fileName;
    private List<PdfExtractedLink> extractedLinks;
    private boolean hasJavaScript;
    private boolean hasOpenAction;
}
