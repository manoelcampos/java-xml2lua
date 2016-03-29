#!/bin/bash
JAR="../target/xml2lua-1.0.jar"
if [ ! -f "$JAR" ];
then
   echo "Pacote $JAR não encontrado. Com o maven instalado, compile o projeto a partir da pasta raiz utilizando: mvn package" >&2
   exit -1
fi

java -jar $JAR destaques.xml
java -jar $JAR produtos.xml