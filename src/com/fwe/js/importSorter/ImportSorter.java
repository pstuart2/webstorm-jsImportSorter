package com.fwe.js.importSorter;

import java.util.ArrayList;
import java.util.List;

class ImportSorter {
    private ImportLineParser reactLine;
    private List<ImportLineParser> nodeModuleWithDefaultList;
    private List<ImportLineParser> nodeModuleWithoutDefaultList;
    private List<ImportLineParser> customModuleWithDefaultsList;
    private List<ImportLineParser> customModuleWithoutDefaultsList;

    ImportSorterResult tryParse(String text) {
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
                ImportLineParser importLineParser = new ImportLineParser(cleanLine);
                if (importLineParser.isImportLine()) {
                    if (!haveFoundFirstImport) {
                        start = currentPos;
                        haveFoundFirstImport = true;
                    }

                    end = currentPos + line.length() + 1;

                    addToCorrectList(importLineParser);
                } else if (haveFoundFirstImport) {
                    break;
                }
            }

            currentPos += line.length() + 1; // Plus 1 of the \n we split on.
        }

        sortLists();
        String replacementText = getReplacementText();

        return new ImportSorterResult(start, end, replacementText);
    }

    private String[] getLines(String text) {
        return text.split("\n");
    }

    private void addToCorrectList(ImportLineParser importLineParser) {
        if (importLineParser.hasDefaultMember() && importLineParser.getDefaultMember().equals("React")) {
            reactLine = importLineParser;
            return;
        }

        if (importLineParser.isNodeModule()) {
            if (importLineParser.hasDefaultMember()) {
                nodeModuleWithDefaultList.add(importLineParser);
            } else {
                nodeModuleWithoutDefaultList.add(importLineParser);
            }
        } else {
            if (importLineParser.hasDefaultMember()) {
                customModuleWithDefaultsList.add(importLineParser);
            } else {
                customModuleWithoutDefaultsList.add(importLineParser);
            }
        }
    }

    private void sortLists() {
        nodeModuleWithDefaultList.sort(ImportLineParser.ModuleComparator);
        nodeModuleWithoutDefaultList.sort(ImportLineParser.ModuleComparator);
        customModuleWithDefaultsList.sort(ImportLineParser.ModuleComparator);
        customModuleWithoutDefaultsList.sort(ImportLineParser.ModuleComparator);
    }

    private String getReplacementText() {
        String replacementText = "";

        if (reactLine != null) {
            replacementText = reactLine + "\n";
        }

        for (ImportLineParser line : nodeModuleWithDefaultList) {
            replacementText += line + "\n";
        }

        for (ImportLineParser line : nodeModuleWithoutDefaultList) {
            replacementText += line + "\n";
        }

        for (ImportLineParser line : customModuleWithDefaultsList) {
            replacementText += line + "\n";
        }

        for (ImportLineParser line : customModuleWithoutDefaultsList) {
            replacementText += line + "\n";
        }

        return replacementText;
    }
}
