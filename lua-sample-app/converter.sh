#!/bin/bash
echo "Converts the XML files into the current directory to Lua files, so that they can be used by the app.lua application."
echo ""
JAR="../target/xml2lua-1.0.jar"
if [ ! -f "$JAR" ];
then
   echo "$JAR package not found. Considering you have maven installed, execute the following command at the upper dir: mvn package" >&2
   exit -1
fi

java -jar $JAR highlights.xml
java -jar $JAR products.xml