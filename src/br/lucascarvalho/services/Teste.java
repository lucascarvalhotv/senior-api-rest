package br.lucascarvalho.services;

import java.io.File;

import br.lucascarvalho.entidade.CSVUtils;

public class Teste {

	public static void main(String[] args) {
		
		ClassLoader cl = new Teste().getClass().getClassLoader();

		File file = new File(cl.getResource("cidades.csv").getFile());
		System.out.println(file.exists());
		
	}
	
}
