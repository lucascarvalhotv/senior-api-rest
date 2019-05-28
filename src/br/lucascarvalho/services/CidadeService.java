package br.lucascarvalho.services;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.lucascarvalho.entidade.CSVUtils;
import br.lucascarvalho.entidade.Cidade;
import br.lucascarvalho.entidade.Estado;

@Path("/cidades")
public class CidadeService {

	// TODO: Criar instancia única no start do server (é recarregado a cada requisição)
	List<Cidade> listaCidades;

	public CidadeService() {
		carregarArquivo();
	}

	@PUT
	@Path("/carregar-arquivo")
	@Produces(MediaType.TEXT_PLAIN)
	/**
	 * Método responsável por carregar o arquivo CSV das cidades para a lista de
	 * dados
	 * 
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
	 * Método responsável por retornar as cidades que são capitais, ordenadas por
	 * nome em ordem alfabética
	 * 
	 * @return arquivo JSON com a lista das capitais
	 */
	public String getCapitais() {
		List<Cidade> capitais = listaCidades.stream().filter(c -> c.isCapital())
				.sorted((c1, c2) -> c1.getNome().compareTo(c2.getNome())).collect(Collectors.toList());

		ObjectMapper mapper = new ObjectMapper();

		// TODO: Corrigir JSON de retorno, a estrutura não está correta
		return capitais.stream().map(c1 -> {
			try {
				return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(c1);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.joining());
	}

	@GET
	@Path("/menor_maior_estado")
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Método responsável por encontrar os estados com maior e menor número de cidades
	 * @return arquivo JSON contendo o nome do estado e a quantidade de cidades
	 */
	public String getMaiorEMenorEstado() {
		Map<Estado, Long> estadosAgrupados = listaCidades.stream()
				.collect(Collectors.groupingBy(Cidade::getUf, Collectors.counting()));

		Optional<Entry<Estado, Long>> maiorEstado = estadosAgrupados.entrySet().stream()
				.max(Comparator.comparing(Map.Entry::getValue));

		Optional<Entry<Estado, Long>> menorEstado = estadosAgrupados.entrySet().stream()
				.min(Comparator.comparing(Map.Entry::getValue));

		// TODO: verificar a possibilidade de criar ObjectNodeUtil
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();

		ObjectNode objectMaiorEstado = mapper.createObjectNode();
		objectMaiorEstado.put("estado", maiorEstado.get().getKey().getNome());
		objectMaiorEstado.put("nr_cidades", maiorEstado.get().getValue());

		ObjectNode objectMenorEstado = mapper.createObjectNode();
		objectMenorEstado.put("estado", menorEstado.get().getKey().getNome());
		objectMenorEstado.put("nr_cidades", menorEstado.get().getValue());

		arrayNode.add(objectMaiorEstado);
		arrayNode.add(objectMenorEstado);

		return arrayNode.toString();
	}
	
	@GET
	@Path("/cidades-por-estado")
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Método responsável por calcular o número de cidades de cada estado
	 * @return arquivo JSON contendo o número de cidades de cada estado
	 */
	public String getlistaCidadesPorEstado() {
		Map<Estado, Long> estadosAgrupados = listaCidades
				.stream()
				.collect(Collectors.groupingBy(Cidade::getUf, Collectors.counting()));

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();

		estadosAgrupados.forEach((estado, numerolistaCidades) -> {
			ObjectNode objectEstado = mapper.createObjectNode();
			objectEstado.put("estado", estado.getNome());
			objectEstado.put("nr_cidades", numerolistaCidades);
			arrayNode.add(objectEstado);
		});

		return arrayNode.toString();
	}

}