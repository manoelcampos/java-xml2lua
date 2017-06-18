# Introducão [![Build Status](https://travis-ci.org/manoelcampos/JavaXml2Lua.png?branch=master)](https://travis-ci.org/manoelcampos/JavaXml2Lua) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.manoelcampos/xml2lua/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.manoelcampos/xml2lua) [![Javadocs](https://www.javadoc.io/badge/com.manoelcampos/xml2lua.svg)](https://www.javadoc.io/doc/com.manoelcampos/xml2lua) [![GPL licensed](https://img.shields.io/badge/license-GPL-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)


XML é um padrão internacional da W3C para intercâmbio de dados, amplamente utilizado e conhecido. Tal formato permite a troca de dados entre sistemas heterogêneos, garantindo a interoperabilidade entre os mesmos. No entanto, em aplicações interativas para o Sistema Brasileiro de TV Digital, desenvolvidas em NCL e Lua, tem-se uma alternativa bem mais simples que o uso de arquivos XML para a representação, armazenamento e troca de dados: os arquivos de dados em formato Lua, como já mostrei [neste post](http://manoelcampos.com/2010/06/08/aplicacao-de-tv-digital-usando-arquivos-de-dados-em-lua/).

O uso de tais arquivos traz vários benefícios como: tamanho menor (menos bytes), simplicidade de manipulação com uso dos recursos nativos da linguagem Lua (os dados armazenados nos arquivos são tratados como tabelas Lua, estrutura de dados disponibilizada pela linguagem, que funciona como vetores e structs), uso mínimo de processamento para manipular os dados (diferente dos parsers XML como o SAX, que demandam maior capacidade de processamento).

Por demandar uma capacidade mínima de processamento, o uso de arquivos de dados em formato Lua é ideal para ambientes de recursos de hardware restritos como os equipamentos de recepção de TV Digital, além de simplificar o código da aplicação para a manipulação de tais dados.


# O problema

O uso de arquivos XML em aplicações NCL/Lua para a TVD (enviadas via broadcast) é perfeitamente possível desde que tenha-se um parser escrito inteiramente em Lua, como o módulo homônimo [xml2lua](https://github.com/manoelcampos/xml2lua), escrito inteiramente em Lua. Tal parser converte um arquivo XML para uma tabela Lua, armazenando a mesma em RAM. Desta forma, a manipulação dos dados fica mais fácil. No link anterior existe uma versão do parser, adaptada por mim, que funciona com Lua 5 (a versão utilizada no subsistema Ginga-NCL do middleware Ginga).

No entanto, tal abordagem pode ser problemática quando se precisa carregar tal arquivo XML do disco, usando Lua. A norma ABNT NBR 15606-2 versão 3 (atualizada em 2009) na seção "10.1 Linguagem Lua - Funções removidas da biblioteca de Lua" mostra que nenhuma função do módulo IO de Lua (que permite a manipulação de arquivos em disco) deve estar disponível para aplicações de TVD. Uma atualização de 2011 da norma (ABNT NBR 15606-2 2011 Ed2) passou a incluir o módulo IO, no entanto, muitas das implementações de Ginga existentes no mercado são anteriores a esta revisão da norma. Assim, se os fabricantes seguiram a norma vigente na época, o módulo IO não deve estar disponível.

Apesar de ser possível a atualização do middleware, sabemos que nem todos os usuários se preocuparão com isso, e as formas de atualização podem variar de fabricante para fabricante, sendo que alguns podem requerer conexão da TV/conversor à Internet para tal atualização.

Desta forma, usar arquivos XML localmente não garante que a aplicação executará em qualquer implementação de Ginga.


# A solução: Java Xml2Lua

Para resolver tal problema, estou disponibilizando uma aplicação console em Java para converter um arquivo XML para o formato Lua. A aplicação utiliza o parser DOM (que se não estou errado é padrão no JDK e JRE) para percorrer os elementos do arquivo XML e assim poder gerar um arquivo Lua com os dados contidos no primeiro.

Para tal conversão, poderia ser utilizada qualquer linguagem de programação, até mesmo Lua, com uso do módulo homônimo [xml2lua](https://github.com/manoelcampos/xml2lua) (para fazer o parse do XML) e o [table.save](http://lua-users.org/wiki/SaveTableToFile) (para salvar a tabela Lua, gerada a partir do XML, em disco). No entanto, o arquivo lua gerado com o table.save não ficou muito organizado e incluiu muito lixo, dificultando a manipulação dos dados. Por este motivo, resolvi implementar a ferramenta em Java.

A implementação realizada está disponível no final do artigo, juntamente com toda a documentação e código fonte.  Ela possui uma ferramenta de linha de comando (para ser usada antes de enviar a aplicação NCL/Lua via broadcast) para fazer a conversão do XML para Lua. Além disto, existe também uma classe Java que pode ser usada em qualquer outra aplicação (Desktop ou Web), permitindo a integração de tal implementação em sistemas já existentes, para, por exemplo, automatizar a conversão dos arquivos XML para Lua, para assim poderem ser enviados pelo carrossel para transmissão em broadcast.

# Documentação

A documentação da API está [disponível online aqui](http://manoelcampos.github.io/Xml2Lua/apidocs).

# Usando a ferramenta

Para usar a ferramenta de linha de comando, basta executar em um terminal:

```bash
java -jar xml2lua.jar NomeArquivoXML
```

Note que deve-se informar o nome de um arquivo XML. A aplicação gerará um arquivo Lua de mesmo nome, dentro do diretório atual.

# Estudo de caso

Para exemplificar o uso da ferramenta, vamos imaginar que temos uma aplicação NCL/Lua de uma loja virtual, que precisa exibir uma lista de produtos na tela. As informações de tais produtos estão em um arquivo XML que será convertido para um arquivo Lua.

Para isto, usaremos um arquivo de nome produtos.xml, com o conteúdo a seguir. Todos os arquivos XML e o código do exemplo apresentado aqui está disponível na pasta [lua-sample-app](lua-sample-app).

```xml
<produtos>
	<produto id="12">
	  <descricao>TV 32''</descricao>
	  <marca>Samsung</marca>
	  <preco>1200</preco>
	</produto>
	<produto id="150">
	  <descricao>Netbook</descricao>
	  <marca>Asus</marca>
	  <preco>900</preco>
	</produto>
	<produto id="198">
	  <descricao>Impressora Laser</descricao>
	  <marca>Samsung</marca>
	  <preco>399</preco>
	</produto>	
	<produto id="201">
	  <descricao>Resma de Papel A4 (500 folhas)</descricao>
	  <marca>Office</marca>
	  <preco>9</preco>
	</produto>
	<produto id="17">
	  <descricao>Resma de Papel A4 (100 folhas)</descricao>
	  <marca>Office</marca>
	  <preco>3</preco>
	</produto>	
</produtos>
```

Para converter tal arquivo para Lua, entre no diretório bin por um terminal e execute:

```bash
java -jar xml2lua.jar produtos.xml
```

Isto considerando que o arquivo produtos.xml está no diretório bin.

Como pode ser visto no XML anterior, cada produto tem um atributo id na tag produto. Este id identifica unicamente cada produto.
Como as tabelas da linguagem Lua possuem a excelente característica de poderem funcionar como vetores e terem índices de qualquer tipo e valor, além de não terem a obrigatoriedade de os mesmos serem sequenciais, a ferramenta Xml2Lua desenvolvida usa tal atributo id do XML como o índice de cada produto na tabela lua (gerada a partir do XML).

Assim, o código Lua gerado no arquivo produtos.lua ficará como mostrado a seguir:

```lua
return  {
  [12]={
    descricao = "TV 32''", marca = "Samsung", preco = "1200",
  },
  [150]={
    descricao = "Netbook", marca = "Asus", preco = "900",
  },
  [198]={
    descricao = "Impressora Laser", marca = "Samsung", preco = "399",
  },
  [201]={
    descricao = "Resma de Papel A4 (500 folhas)", marca = "Office", preco = "9",
  },
  [17]={
    descricao = "Resma de Papel A4 (100 folhas)", marca = "Office", preco = "3",
  },
}
```

Os valores entre chaves são os índices da tabela. Desta forma, nossa tabela não tem índices sequenciais (1, 2, 3...) e sim 12, 150, 198, 201 e 17. O nome do atributo que será usado como índice da tabela Lua deve ser id, mas o código Java pode ser alterado para o nome de atributo que desejar (ao até mesmo alterado para passar tal informação por parâmetro para a aplicação).

Tal recurso é de fundamental importância, considerando agora o seguinte cenário: a aplicação de TVD em NCL/Lua precisa exibir na tela inicial apenas os produtos em destaque. Tais produtos estão relacionados em outro arquivo XML de nome destaques.xml, cujo conteúdo é apresentado a seguir (também disponível no pacote para download).

```xml
<destaques>
  <destaque id="150" />
  <destaque id="17" />  
  <destaque id="198" />  
</destaques>
```

No exemplo, de todos os produtos a serem exibidos na aplicação de TVD (no nosso exemplo são um total de 5), apenas 3 devem ser exibidos na tela inicial. Os outros produtos existentes poderiam ser encontrados pelo usuário por meio de um campo de busca na aplicação.

Com o requisito apresentado, o uso do id do produto como índice da tabela Lua (gerada a partir do XML de produtos) facilita a implementação de tal requisito. Se os índices da tabela produtos fossem sequenciais, para cada produto em destaque, seria preciso fazer um for dentro da tabela de produtos para encontrar tal produto e poder exibir seus dados na tela. Considerando nossos 3 produtos em destaque e 5 produtos cadastrados, teríamos um total de 15 iterações.

Usando o id dos produtos como índice da tabela produtos, para exibir os produtos em destaque, precisamos fazer um for apenas na tabela de destaques. Como o arquivo destaques.xml (apresentado anteriormente) não possui nenhum sub-elemento, tendo apenas o atributo id, tal atributo não é usado como índice da tabela destaques e sim como um campo normal. Assim, os índices da tabela destaque serão sequenciais. Desta forma, o código Lua gerado a partir do destaques.xml será como mostrado abaixo:

```lua
return  {
 {
  id=150
 },
 {
  id=17
 },
 {
  id=198
 },
}
```

A partir de um for em tal tabela, pegando-se o valor do campo id, pode-se acessar a tabela produtos diretamente (sem precisar fazer um for nela) na posição do id da tabela destaques. Assim, serão apenas 3 iterações.


## Código da aplicação Lua

Após terem sido convertidos os arquivos produtos.xml e destaques.xml para Lua, podemos ter uma aplicação Lua  para carregar tais arquivos Lua e exibir os dados, como pode ser visto no trecho de código a seguir. A aplicação não possui interface gráfica pois isto está fora do escopo do artigo. Ela apenas exibe os dados no terminal. Tal código está disponível no arquivo app.lua, no pacote para download.

```lua
local produtos = dofile("produtos.lua")
local destaques = dofile("destaques.lua")

local prod = {}
for i, dest in pairs(destaques) do
    prod = produtos[dest.id]
    print("Id: ", dest.id, "Marca:", prod.marca, "Preço:", prod.preco, "Descrição:", prod.descricao)
end
```

As duas primeiras linhas carregam os arquivos de dados Lua e armazenam a tabela contida neles em variáveis locais. Em seguida é feito um for na tabela de destaques, pegando o id de cada produto em destaque, e por meio dele, acessando diretamente os dados do produto na tabela produto, exibindo-os no terminal.

Como tal aplicação é apenas de exemplo e não usa nenhum recurso do Ginga-NCL, a mesma pode ser executada fora dele, em um terminal (obviamente tendo o interpretador Lua instalado) com o comando:

```bash
lua app.lua
```


# Conclusão

Como as aplicações de TVD (por exemplo, as de comércio eletrônico) podem ser apenas uma nova interface gráfica para sistemas já existentes que usam XML, WebServices e outras tecnologias como a base da arquitetura destes, implementar a geração de arquivos de dados Lua em tais arquiteturas pode ser algo trabalhoso.

Assim, com a implementação apresentada, pode-se utilizar os arquivos XML que por ventura já sejam gerados por sistemas existentes, e convertê-los para arquivos Lua para uso em uma aplicação para a TV Digital, sem precisar necessariamente alterar os sistemas existentes.

Com isto, o equipamento de TVD fica livre do overhead do parse do arquivo XML, por menor que este seja com o uso do módulo homônimo [xml2lua](https://github.com/manoelcampos/xml2lua).
