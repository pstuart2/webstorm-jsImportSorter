package com.fwe.js.importSorter;

import java.util.ArrayList;
import java.util.List;

class ImportSorter {
    private ImportLineParser reactLine;
    private List<ImportLineParser> nodeModuleWithDefaultList;
    private List<ImportLineParser> nodeModuleWithoutDefaultList;
    private List<ImportLineParser> customModuleWithDefaultsList;
    private List<ImportLineParser> customModuleWithoutDefaultsList;
    private List<ImportLineParser> sideEffectModules;

    ImportSorterResult tryParse(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }

        reactLine = null;
        nodeModuleWithDefaultList = new ArrayList<>();
        nodeModuleWithoutDefaultList = new ArrayList<>();
        customModuleWithDefaultsList = new ArrayList<>();
        customModuleWithoutDefaultsList = new ArrayList<>();
        sideEffectModules = new ArrayList<>();

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

        String combinedLine = "";

        for (String line : lines) {
            combinedLine += line + "\n"; // Put the newline back on for position calculation
            if (combinedLine.trim().length() > 0) {
                ImportLineParser importLineParser = new ImportLineParser(combinedLine.trim());
                if (importLineParser.isImportLine()) {
                    if (!haveFoundFirstImport) {
                        start = currentPos;
                        haveFoundFirstImport = true;
                    }

                    end = currentPos + combinedLine.length();

                    addToCorrectList(importLineParser);
                } else if(combinedLine.startsWith("import")) {
                    continue;
                } else if (haveFoundFirstImport) {
                    break;
                }
            }

            currentPos += combinedLine.length();
            combinedLine = "";
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

        if (!importLineParser.hasMember()) {
            sideEffectModules.add(importLineParser);
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
        sideEffectModules.sort(ImportLineParser.ModuleComparator);
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

        for (ImportLineParser line : sideEffectModules) {
            replacementText += line + "\n";
        }

        return replacementText;
    }
}
