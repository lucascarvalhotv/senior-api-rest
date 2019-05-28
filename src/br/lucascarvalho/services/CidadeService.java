package br.lucascarvalho.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.lucascarvalho.entidade.CSVUtils;
import br.lucascarvalho.entidade.Cidade;

@Path("/cidades")
public class CidadeService {

	List<Cidade> listaCidades;

	public CidadeService() {
		carregarArquivo();
	}
	
	@PUT
	@Path("/carregar-arquivo")
	@Produces(MediaType.TEXT_PLAIN)
	/**
	 * Método responsável por carregar o arquivo CSV das cidades para a lista de dados
	 * @return arquivo JSON com resultado do processo
	 */
	public String carregarArquivo() {
		listaCidades = CSVUtils.readlistaCidadesFromCSV("cidades.csv");
		return "Arquivo carregado com sucesso!";
	}

	@GET
	@Path("/buscar-capitais")
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Método responsável por retornar as cidades que são capitais, ordenadas por nome
	 * em ordem alfabética
	 * @return arquivo JSON com a lista das capitais
	 */
	public String getCapitais() {
		List<Cidade> capitais = listaCidades
				.stream()
				.filter(c -> c.isCapital())
				.sorted((c1, c2) -> c1.getNome().compareTo(c2.getNome()))
				.collect(Collectors.toList());

		ObjectMapper mapper = new ObjectMapper();

		// TODO: Corrigir JSON de retorno, a estrutura não está correta
		return capitais
				.stream()
				.map(c1 -> {
					try {
						return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(c1);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				return null;
		}).collect(Collectors.joining());
	}

}