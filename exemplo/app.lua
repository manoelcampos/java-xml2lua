#!/usr/bin/env lua
---Aplicação para carregar arquivos lua convertidos a partir de um XML e exibir os dados no terminal.
--@author Manoel Campos da Silva Filho - http://manoelcampos.com
local produtos = dofile("produtos.lua")
local destaques = dofile("destaques.lua")

local prod = {}
for i, dest in pairs(destaques) do
    prod = produtos[dest.id]
    print("Id: ", dest.id, "Marca:", prod.marca, "Preço:", prod.preco, "Descrição:", prod.descricao)
end
