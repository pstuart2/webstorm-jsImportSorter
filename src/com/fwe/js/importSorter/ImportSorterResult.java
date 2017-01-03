package com.fwe.js.importSorter;

public class ImportSorterResult {
    private int start;
    private int end;
    private String replacement;

    public ImportSorterResult(int start, int end, String replacement) {
        this.start = start;
        this.end = end;
        this.replacement = replacement;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getReplacement() {
        return replacement;
    }

    @Override
    public String toString() {
        return "ImportSorterResult{" +
                "start=" + start +
                ", end=" + end +
                ", replacement='" + replacement + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportSorterResult that = (ImportSorterResult) o;

        if (start != that.start) return false;
        if (end != that.end) return false;
        return replacement != null ? replacement.equals(that.replacement) : that.replacement == null;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        result = 31 * result + (replacement != null ? replacement.hashCode() : 0);
        return result;
    }
}
