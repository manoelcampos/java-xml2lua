# Sample Lua Application

This directory contains a sample lua application which prints the data inside the Lua files generated from the XML files,
using the Java Xml2Lua tool.

The following files are available:

- converter.sh: a script which converts the XML files into the current directory to Lua files, so that they can be used by the app.lua application.
- products.xml and highlights.xml: sample XML files to be converted to Lua files.
- app.lua: Lua application which prints the data inside the Lua files generated from the XML files.