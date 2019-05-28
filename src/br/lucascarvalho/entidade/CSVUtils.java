package br.lucascarvalho.entidade;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

	public static List<Cidade> readlistaCidadesFromCSV(String fileName) {
		List<Cidade> cidades = new ArrayList<>();
		
		ClassLoader cl = new CSVUtils().getClass().getClassLoader();
		File file = new File(cl.getResource(fileName).getFile());

		Path pathToFile = Paths.get(file.getAbsolutePath());
		
		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {

			String line = br.readLine();
			line = br.readLine();
			while (line != null) {

				String[] attributes = line.split(",");
				Cidade Cidade = criarCidade(attributes);

				cidades.add(Cidade);

				line = br.readLine();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return cidades;

	}
	
	private static Cidade criarCidade(String[] metadata) {
		String idIbge = metadata[0];
		Estado uf = Estado.valueOf(metadata[1]);
		String nome = metadata[2];
		boolean capital = metadata[3].toLowerCase().equals("true");
		float longitude = Float.parseFloat(metadata[4]);
		float latitude = Float.parseFloat(metadata[5]);
		String nomeSemAcento = metadata[6];
		String nomeAlternativo = metadata[7];
		String microrregiao = metadata[8];
		String mesorregiao = metadata[9];

		return new Cidade(idIbge, uf, nome, capital, longitude, latitude, nomeSemAcento, nomeAlternativo, microrregiao,
				mesorregiao);
	}
	
}