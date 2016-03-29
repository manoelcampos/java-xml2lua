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
 * Converte um arquivo XML para um arquivo Lua contendo uma tabela representando
 * os dados do XML.
 *
 * @author Manoel Campos da Silva Filho - http://manoelcampos.com
 */
public class Xml2Lua {

    /**
     * Faz o parse de um arquivo XML e gera um arquivo lua com o mesmo nome.
     *
     * @param xmlFileName Nome do arquivo XML é ser processado.
     * @param printLuaCode Se true, imprime no terminal o código Lua gerado
     * @param useFirstXmlNodeAsLuaTableName Se true, o nome do primeiro nó no
     * arquivo XML será usado como o nome da tabela lua gerada. Se for false, o
     * nome deste 1o nó será ignorado e será usado um comando return de Lua para
     * retornar a tabela gerada, permitindo que o desenvolvedor Lua que for usar
     * tal arquivo Lua gerado, possa definir o nome que desejar para a tabela
     * Lua.
     */
    public Xml2Lua(String xmlFileName, boolean printLuaCode, boolean useFirstXmlNodeAsLuaTableName) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(xmlFileName));

            //Normalize text representation
            doc.getDocumentElement().normalize();
            String luaTableName;
            if (useFirstXmlNodeAsLuaTableName) {
                luaTableName = doc.getDocumentElement().getNodeName() + " = ";
            } else {
                luaTableName = "return ";
            }
            StringBuilder luacode = new StringBuilder(luaTableName + " {\n");

            //Obtém os atributos do nó raiz do XML
            Element child = (Element) doc.getDocumentElement();
            luacode.append(getAttributes(child.getAttributes()));

            NodeList nodes = doc.getDocumentElement().getChildNodes();
            luacode.append(getChildNodes(nodes, ""));
            luacode.append("}");

            String luaFileName = xmlFileName.replaceFirst("\\.xml", ".lua");
            FileWriter file = new FileWriter(luaFileName);
            try (BufferedWriter out = new BufferedWriter(file)) {
                out.write(luacode.toString());
            }

            if (printLuaCode) {
                System.out.println(luacode.toString());
            }
            System.out.println("Arquivo lua gerado com sucesso: " + luaFileName);
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line "
                    + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gera um StringBuilder contendo um trecho de código Lua para os elementos
     * filhos de um determinado nó XML.
     *
     * @param NodeList nodes Nós filhos de um determinado nó XML
     * @param String space String contendo os espaços a serem utilizados para
     * identar o trecho de código Lua gerado.
     * @return  Retorna um StringBuilder contendo a string com o
     * código lua gerado.
     */
    private static StringBuilder getChildNodes(NodeList nodes, String space) {
        StringBuilder sb = new StringBuilder();
        space += " ";
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element child = (Element) node;

                NodeList childNodes = child.getChildNodes();
                NamedNodeMap attrs = child.getAttributes();

                Node firstAttr = null;
                if (attrs.getLength() > 0) {
                    firstAttr = attrs.item(0);
                }

                if (childNodes.getLength() > 1) {
                    //sb.append(space+child.getNodeName() + " = {\n    ");
                    sb.append(startSubTable(space, firstAttr, true));
                    sb.append(getAttributes(attrs));
                    sb.append(getChildNodes(childNodes, space + " "));
                    sb.append("\n  },\n");
                } //se o nó XML não tem nós filhos
                else {
                    childNodes = child.getChildNodes();
                    Node childValue = (Node) childNodes.item(0);
                    String value = "";
                    if (childValue != null) {
                        value = childValue.getNodeValue();
                        if (value != null) {
                            value = value.trim();
                        } else {
                            value = "";
                        }
                    }
                    if (attrs.getLength() == 0) {
                        sb.append(child.getNodeName())
                          .append(" = \"").append(value).append("\", ");
                    } else {
                        sb.append(startSubTable(space, firstAttr, false));
                        sb.append(getAttributes(attrs));
                        sb.append("\n },\n");
                    }
                }
            }
        }
        return sb;
    }

    /**
     * Gera o código utilizado para abrir uma sub tabela dentro de uma tabela
     * lua (a partir de um nó XML)
     *
     * @param space String contendo espaços usados para identar o código gerado
     * para a tabela lua
     * @param firstAttr Primeiro atributo do nó XML (caso exista).
     * @param xmlNodeIdAttrAsLuaTableIndex Com o valor true, se o nó XML possui
     * um atributo id, tal atributo será usado como índice da sub-tabela lua. Se
     * o valor for false, o atributo id (caso exista) será incluído como um
     * atributo da sub-tabela lua.
     * @return Retorna uma string contendo o trecho de código lua gerado 
     */
    private static String startSubTable(String space, Node firstAttr, boolean xmlNodeIdAttrAsLuaTableIndex) {
        String strStartSubTable;
        //Se existe um atributo id, usa o mesmo como índice do nó XML na tabela Lua
        if (firstAttr != null && firstAttr.getNodeName().toLowerCase().equals("id")) {
            if (xmlNodeIdAttrAsLuaTableIndex) {
                strStartSubTable = space + " [" + firstAttr.getNodeValue() + "]={\n    ";
            } else {
                strStartSubTable = 
                        space + "{\n" + space + space + 
                        firstAttr.getNodeName() + "=" + firstAttr.getNodeValue();
            }
        } else {
            strStartSubTable = space + " {\n" + space;
        }
        return strStartSubTable;
    }

    /**
     * Faz o parse da lista de atributos de um Node XML.
     *
     * @param attrs Lista de atributso de um Node/Element XML.
     * @return StringBuffer Retorna um StringBuffer contendo os atributos no
     * formato "nome1 = valor1, nome2 = valor2, nomeN = valorN, "
     */
    private static StringBuffer getAttributes(NamedNodeMap attrs) {
        StringBuffer sbAttrs = new StringBuffer();
        for (int j = 0; j < attrs.getLength(); j++) {
            /*Se o atributo não tiver o nome de "id", então adiciona o mesmo 
            como um campo na (sub)tabela Lua sendo gerada, pois caso ela tenha 
            o nome de "id", o mesmo é usado como índice do elemento da 
            (sub) tabela Lua.*/
            if (!attrs.item(j).getNodeName().toLowerCase().equals("id")) {
                sbAttrs.append(attrs.item(j).getNodeName()).append(" = '")
                        .append(attrs.item(j).getNodeValue()).append("', ");
            }
        }
        return sbAttrs;
    }

}
