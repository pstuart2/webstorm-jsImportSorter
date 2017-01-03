package com.fwe.js.importSorter;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ImportSorterTest {
    private ImportSorter sorter;

    static String readFile(String resource) {
        try {
            Path path = Paths.get(ImportSorterTest.class.getClassLoader().getResource(resource).getPath());
            return String.join("\n", Files.readAllLines(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void setUp() {
        sorter = new ImportSorter();
    }

    @Test
    public void testNullText() {
        assertNull(sorter.tryParse(null));
    }

    @Test
    public void testEmptyText() {
        assertNull(sorter.tryParse(""));
    }

    @Test
    public void testNormal() {
        String fileText = readFile("test-files/normal.txt");
        String expectedReplacementText = readFile("test-files/normal.result");

        ImportSorterResult result = sorter.tryParse(fileText);
        assertEquals(0, result.getStart());
        assertEquals(476, result.getEnd());
        assertEquals(expectedReplacementText, result.getReplacement());
    }
}
