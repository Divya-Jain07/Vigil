package com.vigil.analyzer.pdf;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PdfExtractedLink {
    private String displayText;
    private String actualUrl;
}