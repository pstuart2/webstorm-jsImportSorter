package com.fwe.js.importSorter;

import org.junit.Test;

import static org.junit.Assert.*;

public class ImportLineTest {

    @Test
    public void testNonImportLine() {
        ImportLine line = new ImportLine("const carrot = 4");
        assertFalse(line.isImportLine());
    }

    @Test
    public void testImportLineWithDefaultMember() {
        ImportLine line = new ImportLine("import sinon from 'sinon';");
        assertTrue(line.isImportLine());
        assertTrue(line.hasDefaultMember());
        assertEquals(line.getModule(), "sinon");
        assertEquals(line.getDefaultMember(), "sinon");
        assertTrue(line.isNodeModule());
        assertNull(line.getMembers());
    }

    @Test
    public void testImportLineWithoutDefaultMember() {
        ImportLine line = new ImportLine("import { Link } from 'react-router';");
        assertTrue(line.isImportLine());
        assertFalse(line.hasDefaultMember());
        assertEquals(line.getModule(), "react-router");
        assertTrue(line.isNodeModule());
        assertEquals(line.getMembers().length, 1);
        assertEquals(line.getMembers()[0], "Link");
    }

    @Test
    public void testImportLineWithMultipleMembers() {
        ImportLine line = new ImportLine("import { Field, reduxForm } from 'redux-form';");
        assertTrue(line.isImportLine());
        assertFalse(line.hasDefaultMember());
        assertEquals(line.getModule(), "redux-form");
        assertTrue(line.isNodeModule());
        assertEquals(line.getMembers().length, 2);
        assertEquals(line.getMembers()[0], "Field");
        assertEquals(line.getMembers()[1], "reduxForm");
    }

    @Test
    public void testImportLineWithDefaultMemberMultipleMembers() {
        ImportLine line = new ImportLine("import React, { Component, PropTypes } from 'react';");
        assertTrue(line.isImportLine());
        assertTrue(line.hasDefaultMember());
        assertEquals(line.getDefaultMember(), "React");
        assertEquals(line.getModule(), "react");
        assertTrue(line.isNodeModule());
        assertEquals(line.getMembers().length, 2);
        assertEquals(line.getMembers()[0], "Component");
        assertEquals(line.getMembers()[1], "PropTypes");
    }

    @Test
    public void testImportLineWithLocalModule() {
        ImportLine line = new ImportLine("import controls from '../../controls';");
        assertTrue(line.isImportLine());
        assertTrue(line.hasDefaultMember());
        assertEquals(line.getDefaultMember(), "controls");
        assertEquals(line.getModule(), "../../controls");
        assertFalse(line.isNodeModule());
    }

    @Test
    public void testImportLineWithLocalModule2() {
        ImportLine line = new ImportLine("import controls from '/home/user/workspace/controls';");
        assertTrue(line.isImportLine());
        assertTrue(line.hasDefaultMember());
        assertEquals(line.getDefaultMember(), "controls");
        assertEquals(line.getModule(), "/home/user/workspace/controls");
        assertFalse(line.isNodeModule());
    }

    @Test
    public void testImportLineWithDoubleQuotesNoSpaces() {
        ImportLine line = new ImportLine("import React,{Component,PropTypes} from \"react\";");
        assertTrue(line.isImportLine());
        assertTrue(line.hasDefaultMember());
        assertEquals(line.getDefaultMember(), "React");
        assertEquals(line.getModule(), "react");
        assertTrue(line.isNodeModule());
        assertEquals(line.getMembers().length, 2);
        assertEquals(line.getMembers()[0], "Component");
        assertEquals(line.getMembers()[1], "PropTypes");
    }

    @Test
    public void testImportLineWithExtraWhiteSpace() {
        ImportLine line = new ImportLine("import   React  ,  {  Component  ,  PropTypes    }   from   'react'");
        assertTrue(line.isImportLine());
        assertTrue(line.hasDefaultMember());
        assertEquals(line.getDefaultMember(), "React");
        assertEquals(line.getModule(), "react");
        assertTrue(line.isNodeModule());
        assertEquals(line.getMembers().length, 2);
        assertEquals(line.getMembers()[0], "Component");
        assertEquals(line.getMembers()[1], "PropTypes");
    }

}