package br.lucascarvalho.services;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.lucascarvalho.entidade.BancoDeDados;
import br.lucascarvalho.entidade.CSVUtils;
import br.lucascarvalho.entidade.Cidade;
import br.lucascarvalho.entidade.CidadeDeserializer;
import br.lucascarvalho.entidade.Estado;

@Path("/cidades")
public class CidadeService {

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
		BancoDeDados.listaCidades = CSVUtils.readlistaCidadesFromCSV("cidades.csv");

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode retorno = mapper.createObjectNode();
		retorno.put("status", "Arquivo carregado com sucesso!");
		return retorno.toString();
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
		List<Cidade> capitais = BancoDeDados.listaCidades.stream().filter(c -> c.isCapital())
				.sorted((c1, c2) -> c1.getNome().compareTo(c2.getNome())).collect(Collectors.toList());

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode retorno = mapper.createObjectNode();

		try {
			retorno.putPOJO("capitais", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(capitais));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return retorno.toString();
	}

	@GET
	@Path("/menor_maior_estado")
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Método responsável por encontrar os estados com maior e menor número de
	 * cidades
	 * 
	 * @return arquivo JSON contendo o nome do estado e a quantidade de cidades
	 */
	public String getMaiorEMenorEstado() {
		Map<Estado, Long> estadosAgrupados = BancoDeDados.listaCidades.stream()
				.collect(Collectors.groupingBy(Cidade::getUf, Collectors.counting()));

		Optional<Entry<Estado, Long>> maiorEstado = estadosAgrupados.entrySet().stream()
				.max(Comparator.comparing(Map.Entry::getValue));

		Optional<Entry<Estado, Long>> menorEstado = estadosAgrupados.entrySet().stream()
				.min(Comparator.comparing(Map.Entry::getValue));

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
	 * 
	 * @return arquivo JSON contendo o número de cidades de cada estado m
	 */
	public String getlistaCidadesPorEstado() {
		Map<Estado, Long> estadosAgrupados = BancoDeDados.listaCidades.stream()
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
	@Path("/cidades-por-id")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Método responsável por obter os dados de uma cidade a partir do seu id do
	 * IBGE
	 * 
	 * @param idIBGE código identificador da cidade
	 * @return arquivo JSON com os dados da cidade. Retorna null caso a cidade não
	 *         foi encontrada
	 */
	public String getCidadeById(String json) {
		ObjectNode node = null;
		try {
			node = new ObjectMapper().readValue(json, ObjectNode.class);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (node == null)
			return null;
		
		String idIBGE = node.has("ibge_id") ? node.get("ibge_id").textValue() : "";
		
		String retorno = null;
		Cidade cidade = BancoDeDados.listaCidades.stream().filter(c -> c.getIdIbge().equals(idIBGE)).findAny()
				.orElse(null);
		
		try {
			retorno = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(cidade);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return retorno;
	}

	@GET
	@Path("/cidades-por-estado-id")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Método responsável por encontrar as cidades de um estado especificado por
	 * parâmetro
	 * 
	 * @param uf sigla do estado
	 * @return arquivo JSON com a lista de cidades
	 */
	public String getlistaCidadesPorEstado(String json) {
		ObjectNode node = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			node = mapper.readValue(json, ObjectNode.class);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (node == null)
			return null;
		
		String uf = node.has("uf") ? node.get("uf").textValue() : "";
		
		List<String> cidades = BancoDeDados.listaCidades.stream().filter(c -> c.getUf().getSigla().equals(uf))
				.map(Cidade::getNome).collect(Collectors.toList());

		ArrayNode arrayNode = mapper.createArrayNode();
		ObjectNode nodeRetorno = mapper.createObjectNode();
		cidades.forEach(c -> arrayNode.add(c));
		nodeRetorno.set("cidades", arrayNode);

		return nodeRetorno.toString();
	}

	@PUT
	@Path("/inserir-cidade")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Método responsável por inserir uma nova cidade, recebendo um arquivo JSON com
	 * os dados da cidade
	 * 
	 * @param cidadeJson arquivo JSON com os dados da cidade
	 */
	public String inserirCidade(String cidadeJson) {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Cidade.class, new CidadeDeserializer());
		mapper.registerModule(module);

		Cidade novaCidade = null;

		try {
			novaCidade = mapper.readValue(cidadeJson, Cidade.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BancoDeDados.listaCidades.add(novaCidade);
		ObjectNode retorno = mapper.createObjectNode();
		retorno.put("status", "Cidade criada com sucesso!");
		return retorno.toString();
	}

	@DELETE
	@Path("/remover-cidade")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Método responsável por remover uma cidade da lista, dado o seu identificador
	 * @param idIBGE código identificador da cidade
	 */
	public String removerCidade(String json) {
		ObjectNode node = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			node = mapper.readValue(json, ObjectNode.class);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (node == null)
			return null;
		
		String idIBGE = node.has("ibge_id") ? node.get("ibge_id").textValue() : "";
		
		BancoDeDados.listaCidades.removeIf(c -> c.getIdIbge().equals(idIBGE));
		
		ObjectNode retorno = mapper.createObjectNode();
		retorno.put("status", "Cidade removida com sucesso!");
		return retorno.toString();
	}

	@GET
	@Path("/busca-por-coluna")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Método responsável por permitir uma consulta em uma coluna desejada, através de um filtro.
	 * Esse método utiliza-se de conceitos de Java Reflection.
	 * 
	 * @param json arquivo JSON contendo a coluna e filtro
	 */
	public String buscarPorColuna(String json) {
		ObjectNode node = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			node = mapper.readValue(json, ObjectNode.class);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (node == null)
			return null;
		
		String coluna = node.has("coluna") ? node.get("coluna").textValue() : "";
		String filtro = node.has("filtro") ? node.get("filtro").textValue() : "";
		
		// Inicia uma instância da classe Cidade
		Cidade cidadeFoo = new Cidade();

		// Busca todos os campos que a classe cidade possuí
		Field[] fields = cidadeFoo.getClass().getDeclaredFields();
		List<Cidade> listaBusca = null;
		
		/*
		 *  A lógica a seguir é a responsável por relacionar a coluna informada por parâmetro com o campo da classe Cidade.
		 *  Isso é possível devido as anotações criadas ao definir cada atributo da classe Cidade. Portanto, é necessário
		 *  que a coluna informada como parâmetro seja idêntica a coluna existente no arquivo cidades.csv
		 */
		for (Field f : fields) {
			// Encontra a propriedade Json definida para o campo
			JsonProperty jsonProperty = f.getDeclaredAnnotation(JsonProperty.class);
			
			// Caso a propriedade não seja nula e seja igual a coluna informada por parâmetro, prossegue
			if (jsonProperty != null && jsonProperty.value().equals(coluna)) {
				
				/*
				 * O trecho a seguir busca todos os métodos existentes na classe Cidade para encontrar o método
				 * do estilo get para o campo necessário. Essa lógica leva em consideração que os padrões de nomenclatura
				 * forma utilizados durante a definição da classe Cidade. Por exemplo:
				 * - Para o campo foo foi criado o método getFoo.
				 * 
				 * A única exceção a essa regra é o campo capital, que teve a nomenclatura do seu método get modificada para isCapital.
				 */
				Method[] allMethods = cidadeFoo.getClass().getDeclaredMethods();
				for (Method method : allMethods) {

					// Caso o método encontrado iniciar com get ou is e terminar com o nome do campo desejado, continua.
					if ((method.getName().toLowerCase().startsWith("get")
							|| method.getName().toLowerCase().startsWith("is"))
							&& method.getName().toLowerCase().endsWith(f.getName().toLowerCase())) {
						listaBusca = BancoDeDados.listaCidades.stream().filter(c -> {

							/*
							 * O trecho a seguir é responsável por identificar as comparações necessárias para cada tipo de campo.
							 * A classe cidade possui basicamente os tipos String, boolean e float.
							 */
							try {
								if (method.getGenericReturnType() == boolean.class) {
									return (boolean) method.invoke(c, null) == filtro.equals("true");
								} else if (method.getGenericReturnType() == float.class) {
									return (float) method.invoke(c, null) == Float.parseFloat(filtro);
								} else if (method.getGenericReturnType() == String.class) {
									return method.invoke(c, null).toString().contains(filtro);
								} else
									return false;

							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
								return false;
							}
						}).collect(Collectors.toList());

					}
				}
			}
		}

		String retorno = null;
		try {
			retorno = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(listaBusca);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return retorno;
	}

	@GET
	@Path("/nr-registros-por-coluna")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Método responsável por retornar o número de registros em um coluna desconsiderando itens repetidos
	 * 
	 * @param json arquivo JSON contendo a coluna que será utilizada na busca
	 */
	public String nrRegistrosPorColuna(String json) {
		ObjectNode node = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			node = mapper.readValue(json, ObjectNode.class);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (node == null)
			return null;
		
		String coluna = node.has("coluna") ? node.get("coluna").textValue() : "";
		
		/*
		 * A lógica utilizada nesse trecho de código é semelhante ao método buscarPorColuna(), porém nesse é realizado um 
		 * predicado para contar o número de registros únicos identificados
		 */
		Cidade cidadeFoo = new Cidade();
		Field[] fields = cidadeFoo.getClass().getDeclaredFields();
		long numeroRegistros = 0;
		for (Field f : fields) {
			
			JsonProperty jsonProperty = f.getDeclaredAnnotation(JsonProperty.class);
			
			if (jsonProperty != null && jsonProperty.value().equals(coluna)) {
				
				Method[] allMethods = cidadeFoo.getClass().getDeclaredMethods();
				for (Method method : allMethods) {
					
					if ((method.getName().toLowerCase().startsWith("get")
							|| method.getName().toLowerCase().startsWith("is"))
							&& method.getName().toLowerCase().endsWith(f.getName().toLowerCase())) {
						
						numeroRegistros = BancoDeDados.listaCidades.stream().filter(distinctByKey(c -> {
							try {
								return method.invoke(c, null);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
								return false;
							}
						})).collect(Collectors.counting());

					}
				}
			}
		}
		
		ObjectNode retorno = mapper.createObjectNode();
		retorno.put("nr_registros", numeroRegistros);
		
		return retorno.toString();
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@GET
	@Path("/nr-total-registros")
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Método responsável por retornar a quantidade total de registros na lista
	 * 
	 * @return arquivo JSON com o número de registros na lista
	 */
	public String nrTotalRegistros() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode retorno = mapper.createObjectNode();
		retorno.put("nr_registros", BancoDeDados.listaCidades.size());
		
		return retorno.toString();
	}

	/**
	 * Dentre todas as listaCidades, obter as duas listaCidades mais distantes uma
	 * da outra com base na localização (distância em KM em linha reta);
	 */
	private void buscaCidadesMaisDistantes() {
		// TODO: implementação
	}

}