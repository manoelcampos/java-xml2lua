/*
 * Xml2Lua: Converts XML files to Lua format.
 *     Copyright (C) 2011-2016  Manoel Campos da Silva Filho (http://manoelcampos.com)
 *
 *     This file is part of Xml2Lua.
 *
 *     Xml2Lua is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Xml2Lua is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Xml2Lua. If not, see <http://www.gnu.org/licenses/>.
 */
package com.manoelcampos.xml2lua;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Converts XML files to <a href="http://lua.org">Lua</a> files.
 *
 * @author Manoel Campos da Silva Filho
 */
public class Xml2Lua {

    /**
     * Instantiates the class to parse an XML file, generating a Lua file
     * with the same name.
     *
     * @param xmlFilePath Path to the XML file to be parsed
     * @param printLuaCode if the generated Lua code must be printed or not
     * @param useFirstXmlTagAsLuaTableName if true, indicates the name of the first
     *                                     tag into the XML file will be used as the name of the
     *                                     Lua table to be generated.
     *                                     If false, the name of this first tag will be ignored
     *                                     and a lua "return" command will be used instead.
     *                                     This way, the Lua developer who will use the generated Lua file
     *                                     can define the name he/she wants to use for the generated Lua table.
     */
    public Xml2Lua(final String xmlFilePath, final boolean printLuaCode, final boolean useFirstXmlTagAsLuaTableName) {
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document doc = docBuilder.parse(new File(xmlFilePath));

            //Normalizes text representation
            doc.getDocumentElement().normalize();
            final String luaTableName = getLuaTableName(useFirstXmlTagAsLuaTableName, doc);
            final StringBuilder luaCode = new StringBuilder(luaTableName + " {\n");

            //Gets the attributes of the XML root node.
            final Element child = (Element) doc.getDocumentElement();
            luaCode.append(convertXmlNodeAttributesToLua(child));

            final NodeList nodes = doc.getDocumentElement().getChildNodes();
            luaCode.append(convertXmlChildNodesToLua(nodes, ""));
            luaCode.append("}");

            final String luaFileName = xmlFilePath.replaceFirst("\\.xml", ".lua");
            final FileWriter file = new FileWriter(luaFileName);
            try (BufferedWriter out = new BufferedWriter(file)) {
                out.write(luaCode.toString());
            }

            if (printLuaCode) {
                System.out.println(luaCode.toString());
            }
            System.out.printf("\nLua file %s generated successfully from the %s.\n\n", luaFileName, xmlFilePath);
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line "
                    + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getLuaTableName(boolean useFirstXmlNodeAsLuaTableName, Document doc) {
        if (useFirstXmlNodeAsLuaTableName) {
            return doc.getDocumentElement().getNodeName() + " = ";
        }

        return "return ";
    }

    /**
     * Converts the child elements from a given XML node to the corresponding Lua code.
     *
     * @param nodes nodes child nodes from a given XML node
     * @param space a string containing the amount of spaces to be used to indent the generated Lua code
     * @return a StringBuilder containing the Lua code generated for the child elements of the given XML node
     */
    private static StringBuilder convertXmlChildNodesToLua(final NodeList nodes, String space) {
        final StringBuilder sb = new StringBuilder();
        space += " ";
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            sb.append(convertXmlNodeToLua(space, node));
        }

        return sb;
    }

    /**
     * Converts a XML node to the corresponding Lua code.
     *
     * @param node the XML node to convert to Lua code
     * @param space a string containing the amount of spaces to be used to indent the generated Lua code
     * @return a StringBuilder containing the Lua code generated for the given XML node
     */
    private static StringBuilder convertXmlNodeToLua(final String space, final Node node) {
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            return new StringBuilder();
        }

        if (node.getChildNodes().getLength() > 1) {
            return convertXmlNodeWithChildrenToLua((Element) node, space);
        }

        return convertXmlNodeWithNoChildToLua((Element) node, space);
    }

    /**
     * Converts a XML node with doesn't have any child element to the corresponding Lua code.
     *
     * @param node the XML node to convert to Lua code
     * @param space a string containing the amount of spaces to be used to indent the generated Lua code
     * @return a StringBuilder containing the Lua code generated for the given XML node
     */
    private static StringBuilder convertXmlNodeWithNoChildToLua(final Element node, final String space) {
        final StringBuilder sb = new StringBuilder();

        final String value = getValueOfFirstXmlChildNode(node);

        final NamedNodeMap attrs = node.getAttributes();
        if (attrs.getLength() == 0) {
            sb.append(node.getNodeName())
              .append(" = \"").append(value).append("\", ");
            return sb;
        }

        sb.append(generateLuaSubTableFromXmlNode(node, false, space));
        sb.append(convertXmlNodeAttributesToLua(node));
        sb.append("\n },\n");

        return sb;
    }

    /**
     * Gets the value of the first child element of a given XML node.
     *
     * @param node the node the get the value of its first child node
     * @return the value of the first child of the given node, or an empty string if the node
     * doesn't have any children or the first child doesn't have a value.
     */
    private static String getValueOfFirstXmlChildNode(final Element node) {
        final Node childValue = (Node) node.getChildNodes().item(0);
        if (childValue != null) {
            final String value = childValue.getNodeValue();
            return value == null ? "" : value.trim();
        }

        return "";
    }

    /**
     * Converts a XML node with has children elements to the corresponding Lua code.
     *
     * @param node the XML node to convert to Lua code
     * @param space a string containing the amount of spaces to be used to indent the generated Lua code
     * @return a StringBuilder containing the Lua code generated for the given XML node
     */
    private static StringBuilder convertXmlNodeWithChildrenToLua(final Element node, final String space) {
        final StringBuilder sb = new StringBuilder();

        NodeList childNodes = node.getChildNodes();
        sb.append(generateLuaSubTableFromXmlNode(node, true, space));
        sb.append(convertXmlNodeAttributesToLua(node));
        sb.append(convertXmlChildNodesToLua(childNodes, space + " "));
        sb.append("\n  },\n");

        return sb;
    }

    private static Node getFirstAttributeFromXmlNode(final Element node) {
        return node.getAttributes().getLength() > 0 ? node.getAttributes().item(0) : null;
    }

    /**
     * Generates the Lua code to start a Lua sub-table (a tabela which belongs to another table),
     * from a given XML node.
     *
     * @param node the XML node to generate the code to start a Lua sub-table
     * @param xmlNodeIdAttrAsLuaTableIndex if true and the XML node has an attribute
     *                                    named "id", such an attribute is used as the indexes
     *                                    of the elements of the Lua sub-table to generate.
     *                                    If it's false and there is an attribute named "id",
     *                                    it will be included as an attribute inside the Lua sub-table.
     * @param space a string containing the amount of spaces to be used to indent the generated Lua code
     * @return a String containing the Lua code for the beginning of a lua sub-table generated from the XML node
     */
    private static String generateLuaSubTableFromXmlNode(final Element node, final boolean xmlNodeIdAttrAsLuaTableIndex, final String space) {
        final Node firstAttr = getFirstAttributeFromXmlNode(node);

        if (firstAttr != null && firstAttr.getNodeName().toLowerCase().equals("id")) {
            if (xmlNodeIdAttrAsLuaTableIndex) {
                return space + " [" + firstAttr.getNodeValue() + "]={\n    ";
            }

            return space + "{\n" + space + space +
                   firstAttr.getNodeName() + "=" + firstAttr.getNodeValue();
        }

        return space + " {\n" + space;
    }

    /**
     * Converts the attributes of a XML node to the corresponding Lua code.
     *
     * @param node the XML node to generate its attributes to Lua code
     * @return a String containing the XML node attributes converted to Lua code, in the following format:
     * nome1 = valor1, nome2 = valor2, nomeN = valorN,
     */
    private static String convertXmlNodeAttributesToLua(final Element node) {
        final StringBuilder sb = new StringBuilder();
        final NamedNodeMap attrs = node.getAttributes();

        for (int i = 0; i < attrs.getLength(); i++) {
            /*
            If the attribute is not named "id", adds it as a field into the
            Lua (sub)table being generated. If it is named "id", it's used
            as the index of the Lua (sub)table.
            */
            if (!attrs.item(i).getNodeName().toLowerCase().equals("id")) {
                sb.append(attrs.item(i).getNodeName()).append(" = '")
                        .append(attrs.item(i).getNodeValue()).append("', ");
            }
        }
        
        return sb.toString();
    }
}
