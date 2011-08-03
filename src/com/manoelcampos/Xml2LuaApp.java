package com.manoelcampos;

/**Aplicação de linha de comando para conversão de arquivo XML para Lua.
 * @author Manoel Campos da Silva Filho - http://manoelcampos.com
 * ***/
public class Xml2LuaApp {

	/**  
	 * @param String[] args Argumentos de linha de comando. Deve-se passar o nome do arquivo XML a ser feita a conversão
	 * para Lua 
	 * @throws Exception 
	 */
	public static void main(String[] args)  {
		if(args.length < 1) {
			System.out.println("\n\nUso: java com.manoelcampos.Xml2LuaApp NomeArquivoXML\n\n");
			System.exit(-1);
		}
		String xmlFileName = args[0];
		new Xml2Lua(xmlFileName, true, false);
	}




}