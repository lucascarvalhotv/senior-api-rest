package br.lucascarvalho.entidade;

import java.util.List;
/**
 * Essa classe serve para representar o banco de dados, porém em uma estrutura mais simples para esse desafio
 * @author lucas carvalho
 *
 */
public class BancoDeDados {

	private static BancoDeDados instancia;
	public static List<Cidade> listaCidades;

	public BancoDeDados() {
		listaCidades = CSVUtils.readlistaCidadesFromCSV("cidades.csv");
	}
	
	public static synchronized BancoDeDados getInstance() {
		if (instancia == null)
			instancia = new BancoDeDados();
		return instancia;
	}
	
}