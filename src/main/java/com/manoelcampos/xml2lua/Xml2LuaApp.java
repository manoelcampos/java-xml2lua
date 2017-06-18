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

/**
 * Command line application to convert XML files to <a href="http://lua.org">Lua</a> files.
 *
 * @author Manoel Campos da Silva Filho
 */
public class Xml2LuaApp {

    /**
     * Starts the applications.
     *
     * @param args args Command line arguments. The first argument must be the path of the XML file to be read.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("\n\nUsage: java com.manoelcampos.Xml2LuaApp XmlFilePath\n\n");
            System.exit(-1);
        }

        final Xml2Lua parser = new Xml2Lua(args[0]);
        parser.convert();
        System.out.printf("\nLua file %s generated successfully from the %s.\n\n", parser.getLuaFileName(), parser.getXmlFilePath());
    }
}
