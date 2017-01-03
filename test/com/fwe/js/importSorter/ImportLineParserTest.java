package com.fwe.js.importSorter;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ImportLineParserTest {

    @Test
    public void testNonImportLine() {
        ImportLineParser line = new ImportLineParser("const carrot = 4");
        assertFalse(line.isImportLine());
    }

    @Test
    public void testImportLineWithDefaultMember() {
        ImportLineParser line = new ImportLineParser("import sinon from 'sinon';");
        assertTrue(line.isImportLine());
        assertTrue(line.hasDefaultMember());
        assertEquals(line.getModule(), "sinon");
        assertEquals(line.getDefaultMember(), "sinon");
        assertTrue(line.isNodeModule());
        assertNull(line.getMembers());
        assertEquals("import sinon from 'sinon';", line.toString());
    }

    @Test
    public void testImportLineWithoutDefaultMember() {
        ImportLineParser line = new ImportLineParser("import { Link } from 'react-router';");
        assertTrue(line.isImportLine());
        assertFalse(line.hasDefaultMember());
        assertEquals(line.getModule(), "react-router");
        assertTrue(line.isNodeModule());
        assertEquals(line.getMembers().length, 1);
        assertEquals(line.getMembers()[0], "Link");
        assertEquals("import { Link } from 'react-router';", line.toString());
    }

    @Test
    public void testImportLineWithMultipleMembers() {
        ImportLineParser line = new ImportLineParser("import { Field, reduxForm } from 'redux-form';");
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
        ImportLineParser line = new ImportLineParser("import React, { Component, PropTypes } from 'react';");
        assertTrue(line.isImportLine());
        assertTrue(line.hasDefaultMember());
        assertEquals(line.getDefaultMember(), "React");
        assertEquals(line.getModule(), "react");
        assertTrue(line.isNodeModule());
        assertEquals(line.getMembers().length, 2);
        assertEquals(line.getMembers()[0], "Component");
        assertEquals(line.getMembers()[1], "PropTypes");
        assertEquals("import React, { Component, PropTypes } from 'react';", line.toString());
    }

    @Test
    public void testImportLineWithLocalModule() {
        ImportLineParser line = new ImportLineParser("import controls from '../../controls';");
        assertTrue(line.isImportLine());
        assertTrue(line.hasDefaultMember());
        assertEquals(line.getDefaultMember(), "controls");
        assertEquals(line.getModule(), "../../controls");
        assertFalse(line.isNodeModule());
    }

    @Test
    public void testImportLineWithLocalModule2() {
        ImportLineParser line = new ImportLineParser("import controls from '/home/user/workspace/controls';");
        assertTrue(line.isImportLine());
        assertTrue(line.hasDefaultMember());
        assertEquals(line.getDefaultMember(), "controls");
        assertEquals(line.getModule(), "/home/user/workspace/controls");
        assertFalse(line.isNodeModule());
    }

    @Test
    public void testImportLineWithDoubleQuotesNoSpaces() {
        ImportLineParser line = new ImportLineParser("import React,{PropTypes,Component} from \"react\";");
        assertTrue(line.isImportLine());
        assertTrue(line.hasDefaultMember());
        assertEquals(line.getDefaultMember(), "React");
        assertEquals(line.getModule(), "react");
        assertTrue(line.isNodeModule());
        assertEquals(line.getMembers().length, 2);
        assertEquals(line.getMembers()[0], "Component");
        assertEquals(line.getMembers()[1], "PropTypes");
        assertEquals("import React, { Component, PropTypes } from 'react';", line.toString());
    }

    @Test
    public void testImportLineWithExtraWhiteSpace() {
        ImportLineParser line = new ImportLineParser("import   React  ,  {  Component  ,  PropTypes    }   from   'react'");
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
    public void testImportLineSortingMembers() {
        ImportLineParser line = new ImportLineParser("import {CardHeader, CardMedia, CardTitle, CardActions, Card, CardText} from 'material-ui/Card';");
        assertTrue(line.isImportLine());
        assertFalse(line.hasDefaultMember());
        assertEquals(line.getModule(), "material-ui/Card");
        assertTrue(line.isNodeModule());
        assertEquals(line.getMembers().length, 6);
        assertEquals(line.getMembers()[0], "Card");
        assertEquals(line.getMembers()[1], "CardActions");
        assertEquals(line.getMembers()[2], "CardHeader");
        assertEquals(line.getMembers()[3], "CardMedia");
        assertEquals(line.getMembers()[4], "CardText");
        assertEquals(line.getMembers()[5], "CardTitle");
        assertEquals("import { Card, CardActions, CardHeader, CardMedia, CardText, CardTitle } from 'material-ui/Card';", line.toString());
    }

    @Test
    public void testImportLineMultiLineMembers() {
        ImportLineParser line = new ImportLineParser("import {\n" +
                "    CardHeader,\n" +
                "    CardMedia,\n" +
                "    CardTitle,\n" +
                "    CardActions,\n" +
                "    Card,\n" +
                "    CardText\n" +
                "} from 'material-ui/Card';");
        assertTrue(line.isImportLine());
        assertFalse(line.hasDefaultMember());
        assertEquals(line.getModule(), "material-ui/Card");
        assertTrue(line.isNodeModule());
        assertEquals(line.getMembers().length, 6);
        assertEquals(line.getMembers()[0], "Card");
        assertEquals(line.getMembers()[1], "CardActions");
        assertEquals(line.getMembers()[2], "CardHeader");
        assertEquals(line.getMembers()[3], "CardMedia");
        assertEquals(line.getMembers()[4], "CardText");
        assertEquals(line.getMembers()[5], "CardTitle");
        assertEquals("import { Card, CardActions, CardHeader, CardMedia, CardText, CardTitle } from 'material-ui/Card';", line.toString());
    }

    @Test
    public void testImportAllAsName() {
        ImportLineParser line = new ImportLineParser("import * as API from './api';");
        assertTrue(line.isImportLine());
        assertFalse(line.hasDefaultMember());
        assertEquals(line.getModule(), "./api");
        assertFalse(line.isNodeModule());
        assertEquals("import * as API from './api';", line.toString());
    }

    @Test
    public void testImportWithSideffects() {
        ImportLineParser line = new ImportLineParser("import 'chai-as-promised';");
        assertTrue(line.isImportLine());
        assertFalse(line.hasDefaultMember());
        assertEquals(line.getModule(), "chai-as-promised");
        assertTrue(line.isNodeModule());
        assertEquals("import 'chai-as-promised';", line.toString());
    }

    @Test
    public void testComparator() {
        ImportLineParser line1 = new ImportLineParser("import React, { Component, PropTypes } from 'react';");
        ImportLineParser line2 = new ImportLineParser("import RaisedButton from 'material-ui/RaisedButton';");
        ImportLineParser line3 = new ImportLineParser("import FontIcon from 'material-ui/FontIcon';");
        ImportLineParser line4 = new ImportLineParser("import {CardHeader, CardMedia, CardTitle, CardActions, Card, CardText} from 'material-ui/Card';");

        List<ImportLineParser> list = asList(line1, line2, line3, line4);
        list.sort(ImportLineParser.ModuleComparator);

        assertEquals("material-ui/Card", list.get(0).getModule());
        assertEquals("material-ui/FontIcon", list.get(1).getModule());
        assertEquals("material-ui/RaisedButton", list.get(2).getModule());
        assertEquals("react", list.get(3).getModule());
    }
}