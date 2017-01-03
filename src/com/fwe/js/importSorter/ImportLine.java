package com.fwe.js.importSorter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportLine {
    private final static String IMPORT_REGEX = "^import\\s*(?<defMember>[-\\w\\.]+){0,}\\s*,?\\s*(\\{\\s*(?<members>.*)\\})?\\s*from\\s*('|\")(?<module>[\\.\\/\\-a-zA-Z0-9]+)('|\");?$";

    private String inputLine;

    private String defaultMember;
    private String[] members;
    private String module;

    private boolean isImportLine;
    private boolean hasDefaultMember;
    private boolean isNodeModule;

    public ImportLine(String line) {
        isImportLine = parseLine(line);
    }

    public String getDefaultMember() {
        return defaultMember;
    }

    public String getModule() {
        return module;
    }

    public boolean isImportLine() {
        return isImportLine;
    }

    public boolean hasDefaultMember() {
        return hasDefaultMember;
    }

    public boolean isNodeModule() {
        return isNodeModule;
    }

    public String[] getMembers() {
        return members;
    }

    public String getLine() { return inputLine; }

    private boolean parseLine(String line) {
        inputLine = line.trim();

        if (inputLine.startsWith("import")) {
            populate(inputLine);
            return true;
        }

        return false;
    }

    private void populate(String line) {
        Pattern pattern = Pattern.compile(IMPORT_REGEX);
        Matcher m = pattern.matcher(line);
        if (m.find()) {
            populateObject(m);
        }
    }

    private void populateObject(Matcher m) {
        defaultMember = m.group("defMember");
        hasDefaultMember = defaultMember != null;
        module = m.group("module");
        members = getMembers(m.group("members"));
        isNodeModule = !module.startsWith(".") && !module.startsWith("/");
    }

    private String[] getMembers(String members) {
        if (members == null) {
            return null;
        }

        return members.trim().replace(" ", "").split(",");
    }
}
