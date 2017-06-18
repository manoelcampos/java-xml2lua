#!/usr/bin/env lua

---Application which loads some lua files converted from XML files using the Java Xml2Lua tool.
--@author Manoel Campos da Silva Filho - http://manoelcampos.com
print "\nNOTE: Run the converter.sh script to generate the products.lua and highlights.lua from the respective XML files.\n"
local products = dofile("products.lua")
local highlights = dofile("highlights.lua")

local prod = {}
for i, highlight in pairs(highlights) do
    prod = products[highlight.id]
    print("Id: ", dest.id, "Brand:", prod.branc, "Price:", prod.price, "Description:", prod.description)
end
