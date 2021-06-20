# Java Xml2Lua <a href="https://buymeacoff.ee/manoelcampos" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 30px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>
[![Build Status](https://github.com/manoelcampos/java-xml2lua/actions/workflows/maven.yml/badge.svg)](https://github.com/manoelcampos/java-xml2lua/actions/workflows/maven.yml)  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.manoelcampos/xml2lua/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.manoelcampos/xml2lua) [![Javadocs](https://www.javadoc.io/badge/com.manoelcampos/xml2lua.svg)](https://www.javadoc.io/doc/com.manoelcampos/xml2lua) [![GPL licensed](https://img.shields.io/badge/license-GPL-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

The Java Xml2Lua allows parseing a XML file and converting it to [Lua](http://lua.org) file, represented as a [table](https://www.lua.org/pil/2.5.html), the native data structure of the Lua language.

It gets a XML file such as the following as input:

```xml
<products>
	<product id="12">
	  <description>TV 32''</description>
	  <brand>Samsung</brand>
	  <price>1200</price>
	</product>
	<product id="150">
	  <description>Netbook</description>
	  <brand>Asus</brand>
	  <price>900</price>
	</product>
</products>
```

Then, it converts it to a Lua file, representing the XML data as a Lua table:

```lua
products =  {
  [12]={
    description = "TV 32''", brand = "Samsung", price = "1200", 
  },
  [150]={
    description = "Netbook", brand = "Asus", price = "900", 
  },
  [198]={
    description = "Laser Printer", brand = "Samsung", price = "399", 
  },
}
```

# Using it as a Maven dependency into your own project

The library can be added as a Maven dependency into your own project, by adding the following code to your pom.xml file:

```xml
<dependency>
    <groupId>com.manoelcampos</groupId>
    <artifactId>xml2lua</artifactId>
    <version>1.0.0</version>
</dependency>
```

Using the `Xml2Lua` class to convert a XML to a Lua file requires just few lines of code:

```java
Xml2Lua parser = new Xml2Lua(xmlFilePath);
parser.convert();
System.out.printf("Lua file generated at %s.\n", parser.getLuaFileName());
```

# Using it as a command line tool

You can use the available command tool to convert XML to Lua using the command line.
If you downloaded the project source code, when you build it using `mvn clean install`
or some IDE, a jar file will be created inside the target directory.

Alternatively, you can simply download the jar file from the [releases](https://github.com/manoelcampos/JavaXml2Lua/releases) page.

Once you have the jar file, you can run it as below:

```bash
java -jar xml2lua.jar XmlFilePath
```

The tool will generate a Lua file with the same name of the XML file, inside the directory of the XML file.

