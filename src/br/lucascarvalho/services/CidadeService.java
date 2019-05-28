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

	// TODO: Criar instancia �nica no start do server (� recarregado a cada
	// requisi��o)
	List<Cidade> listaCidades;

	public CidadeService() {
		carregarArquivo();
	}

	@PUT
	@Path("/carregar-arquivo")
	@Produces(MediaType.TEXT_PLAIN)
	/**
	 * M�todo respons�vel por carregar o arquivo CSV das cidades para a lista de
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
	 * M�todo respons�vel por retornar as cidades que s�o capitais, ordenadas por
	 * nome em ordem alfab�tica
	 * 
	 * @return arquivo JSON com a lista das capitais
	 */
	public String getCapitais() {
		List<Cidade> capitais = listaCidades.stream().filter(c -> c.isCapital())
				.sorted((c1, c2) -> c1.getNome().compareTo(c2.getNome())).collect(Collectors.toList());

		ObjectMapper mapper = new ObjectMapper();

		// TODO: Corrigir JSON de retorno, a estrutura n�o est� correta
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
	 * M�todo respons�vel por encontrar os estados com maior e menor n�mero de
	 * cidades
	 * 
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
	 * M�todo respons�vel por calcular o n�mero de cidades de cada estado
	 * 
	 * @return arquivo JSON contendo o n�mero de cidades de cada estado
	 */
	public String getlistaCidadesPorEstado() {
		Map<Estado, Long> estadosAgrupados = listaCidades.stream()
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

	@GET
	@Path("/cidades-por-estado")
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * M�todo respons�vel por obter os dados de uma cidade a partir do seu id do
	 * IBGE
	 * 
	 * @param idIBGE c�digo identificador da cidade
	 * @return arquivo JSON com os dados da cidade. Retorna null caso a cidade n�o
	 *         foi encontrada
	 */
	// TODO: Corrigir parametro para JSON
	public String getCidadeById(String idIBGE) {
		String retorno = null;
		Cidade cidade = listaCidades
				.stream()
				.filter(c -> c.getIdIbge().equals(idIBGE))
				.findAny()
				.orElse(null);

		ObjectMapper mapper = new ObjectMapper();

		try {
			retorno = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cidade);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return retorno;
	}

	@GET
	@Path("/cidades-por-estado-id")
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * M�todo respons�vel por encontrar as cidades de um estado especificado por par�metro
	 * @param uf sigla do estado
	 * @return arquivo JSON com a lista de cidades
	 */
	// TODO: Corrigir parametro para JSON
	public String getlistaCidadesPorEstado(String uf) {
		List<String> cidades = listaCidades
				.stream()
				.filter(c -> c.getUf().getSigla().equals(uf))
				.map(Cidade::getNome)
				.collect(Collectors.toList());

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		ObjectNode node = mapper.createObjectNode();
		cidades.forEach(c -> arrayNode.add(c));
		node.set("cidades", arrayNode);

		return node.toString();
	}

}