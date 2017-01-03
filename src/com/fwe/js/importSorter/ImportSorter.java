package com.fwe.js.importSorter;

/*
        1. React
        2. Node Modules with defaults (alphabetical)
        3. Node Modules without defaults (alphabetical)
        4. Custom Modules with defaults
        5. Custom Modules without defaults

 */

import java.util.ArrayList;
import java.util.List;

public class ImportSorter {
    private final static String IMPORTS_REGEX = "import [\\w\\W][^\\n]{0,};?";

    private ImportLine reactLine;
    private List<ImportLine> nodeModuleWithDefaultList;
    private List<ImportLine> nodeModuleWithoutDefaultList;
    private List<ImportLine> customModuleWithDefaultsList;
    private List<ImportLine> customModuleWithoutDefaultsList;

    public ImportSorterResult tryParse(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }

        reactLine = null;
        nodeModuleWithDefaultList = new ArrayList<>();
        nodeModuleWithoutDefaultList = new ArrayList<>();
        customModuleWithDefaultsList = new ArrayList<>();
        customModuleWithoutDefaultsList = new ArrayList<>();

        return getResult(text);
    }

    private ImportSorterResult getResult(String text) {
        String[] lines = getLines(text);
        if (lines.length == 0) {
            return null;
        }

        boolean haveFoundFirstImport = false;
        int currentPos = 0;
        int start = 0;
        int end = 0;

        for (String line : lines) {
            String cleanLine = line.trim();
            if (cleanLine.length() > 0) {
                ImportLine importLine = new ImportLine(cleanLine);
                if (importLine.isImportLine()) {
                    if (!haveFoundFirstImport) {
                        start = currentPos;
                        haveFoundFirstImport = true;
                    }

                    end = currentPos + line.length() + 1;

                    addToCorrectList(importLine);
                } else if (haveFoundFirstImport) {
                    break;
                }
            }

            currentPos += line.length() + 1; // Plus 1 of the \n we split on.
        }

        String replacementText = getReplacementText();

        return new ImportSorterResult(start, end, replacementText);
    }

    private String[] getLines(String text) {
        return text.split("\n");
    }

    private void addToCorrectList(ImportLine importLine) {
        if(importLine.hasDefaultMember() && importLine.getDefaultMember().equals("React")) {
            reactLine = importLine;
            return;
        }

        if(importLine.isNodeModule()) {
            if(importLine.hasDefaultMember()) {
                nodeModuleWithDefaultList.add(importLine);
            } else {
                nodeModuleWithoutDefaultList.add(importLine);
            }
        } else {
            if(importLine.hasDefaultMember()) {
                customModuleWithDefaultsList.add(importLine);
            } else {
                customModuleWithoutDefaultsList.add(importLine);
            }
        }
    }

    private String getReplacementText() {
        String replacementText = "";

        if(reactLine != null) {
            replacementText = reactLine.getLine() + "\n";
        }

        for(ImportLine line : nodeModuleWithDefaultList) {
            replacementText += line.getLine() + "\n";
        }

        for(ImportLine line : nodeModuleWithoutDefaultList) {
            replacementText += line.getLine() + "\n";
        }

        for(ImportLine line : customModuleWithDefaultsList) {
            replacementText += line.getLine() + "\n";
        }

        for(ImportLine line : customModuleWithoutDefaultsList) {
            replacementText += line.getLine() + "\n";
        }

        return replacementText;
    }
}
