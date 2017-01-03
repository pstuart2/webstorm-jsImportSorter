package com.fwe.js.importSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportLineParser {
    private final static String IMPORT_REGEX = "^import\\s*(?<defMember>[-\\w]+){0,}\\s*(\\* as (?<member>[-\\w]+)){0,1},?\\s*((?<members>\\{[\\w\\s\\,]{0,}\\}))?[\\s*from\\s*]*('|\")(?<module>[\\.\\/\\-a-zA-Z0-9]+)('|\");?$";
    public static Comparator<ImportLineParser> ModuleComparator = new Comparator<ImportLineParser>() {

        public int compare(ImportLineParser line1, ImportLineParser line2) {

            String lineModule1 = line1.getModule();
            String lineModule2 = line2.getModule();

            return lineModule1.compareTo(lineModule2);
        }

    };
    private String defaultMember;
    private String allAsMember;
    private String[] members;
    private String module;
    private boolean hasDefaultMember;
    private boolean isNodeModule;

    public ImportLineParser(String line) {
        parseLine(line);
    }

    public String getDefaultMember() {
        return defaultMember;
    }

    public String getModule() {
        return module;
    }

    public boolean isImportLine() {
        return module != null;
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

    public boolean hasMember() {
        return defaultMember != null || members != null || allAsMember != null;
    }

    @Override
    public String toString() {
        String result = "import ";
        if (allAsMember != null) {
            result += "* as " + allAsMember;
        } else {
            if (hasDefaultMember()) {
                result += defaultMember;
            }

            if (members != null) {
                if (hasDefaultMember()) {
                    result += ", ";
                }

                result += "{ ";
                result += String.join(", ", members);
                result += " }";
            }
        }

        if (hasMember()) {
            result += " from '" + module + "';";
        } else {
            result += "'" + module + "';";
        }

        return result;
    }

    private void parseLine(String line) {
        if (line.startsWith("import")) {
            populate(line);
        }
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
        allAsMember = m.group("member");
        isNodeModule = !module.startsWith(".") && !module.startsWith("/");

        if (members != null) {
            Arrays.sort(members);
        }
    }

    private String[] getMembers(String members) {
        if (members == null) {
            return null;
        }

        return members.replace("{", "").replace("}", "").trim().replaceAll("\\s", "").split(",");
    }
}
