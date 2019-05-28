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
	 * M�todo respons�vel por carregar o arquivo CSV das cidades para a lista de
	 * dados
	 * 
	 * @return arquivo JSON com resultado do processo
	 */
	public String carregarArquivo() {
		BancoDeDados.listaCidades = CSVUtils.readlistaCidadesFromCSV("cidades.csv");
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
		List<Cidade> capitais = BancoDeDados.listaCidades.stream().filter(c -> c.isCapital())
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
		Map<Estado, Long> estadosAgrupados = BancoDeDados.listaCidades.stream()
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
	 * @return arquivo JSON contendo o n�mero de cidades de cada estado m
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
		Cidade cidade = BancoDeDados.listaCidades.stream().filter(c -> c.getIdIbge().equals(idIBGE)).findAny().orElse(null);

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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * M�todo respons�vel por encontrar as cidades de um estado especificado por
	 * par�metro
	 * 
	 * @param uf sigla do estado
	 * @return arquivo JSON com a lista de cidades
	 */
	// TODO: Corrigir parametro para JSON
	public String getlistaCidadesPorEstado(String uf) {
		List<String> cidades = BancoDeDados.listaCidades.stream().filter(c -> c.getUf().getSigla().equals(uf)).map(Cidade::getNome)
				.collect(Collectors.toList());

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		ObjectNode node = mapper.createObjectNode();
		cidades.forEach(c -> arrayNode.add(c));
		node.set("cidades", arrayNode);

		return node.toString();
	}

	@PUT
	@Path("/inserir-cidade")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * M�todo respons�vel por inserir uma nova cidade, recebendo um arquivo JSON com
	 * os dados da cidade
	 * 
	 * @param cidadeJson arquivo JSON com os dados da cidade
	 */
	public void inserirCidade(String cidadeJson) {
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
	}

	// TODO: Verificar path de eventos DELETE/PUT
	@DELETE
	@Path("/remover-cidade")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * 
	 * @param idIBGE c�digo identificador da cidade
	 */
	public void removerCidade(String idIBGE) {
		BancoDeDados.listaCidades.removeIf(c -> c.getIdIbge().equals(idIBGE));
	}

	/**
	 *  Permitir selecionar uma coluna (do CSV) e atrav�s dela entrar com uma
	 * string para filtrar. retornar assim todos os objetos que contenham tal
	 * string;
	 * @param coluna
	 * @param filtro
	 */
	public void buscarPorColuna(String coluna, String filtro) {
		// TODO: Documentar ...
		Cidade cidadeFoo = new Cidade();
		Field[] fields = cidadeFoo.getClass().getDeclaredFields();
		final Method methodFinal = null;
		List<Cidade> listaBusca = null;
		for (Field f : fields) {
			JsonProperty jsonProperty = f.getDeclaredAnnotation(JsonProperty.class);
			if (jsonProperty != null && jsonProperty.value().equals(coluna)) {
				Method[] allMethods = cidadeFoo.getClass().getDeclaredMethods();
				for (Method method : allMethods) {
					if ((method.getName().toLowerCase().startsWith("get")
							|| method.getName().toLowerCase().startsWith("is"))
							&& method.getName().toLowerCase().endsWith(f.getName().toLowerCase())) {
						listaBusca = BancoDeDados.listaCidades.stream().filter(c -> {
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

		// TODO: retorno
		if (listaBusca != null)
			listaBusca.forEach(c -> System.out.println(c));

	}

	/**
	 * Retornar a quantidade de registro baseado em uma coluna. N�o deve contar
	 * itens iguais
	 * @param coluna
	 */
	public void nrRegistrosPorColuna(String coluna) {
		// TODO: Documentar
		Cidade cidadeFoo = new Cidade();
		Field[] fields = cidadeFoo.getClass().getDeclaredFields();
		final Method methodFinal = null;
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

		System.out.println(numeroRegistros);
		// TODO: retorno
	}

	// TODO: Implementar local para predicado
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	/**
	 * M�todo respons�vel por retornar a quantidade total de registros na lista
	 * @return arquivo JSON com o n�mero de registros na lista
	 */
	public int nrTotalRegistros() {
		return BancoDeDados.listaCidades.size();
	}
	
	/**
	 * Dentre todas as listaCidades, obter as duas listaCidades mais distantes
	 * uma da outra com base na localiza��o (dist�ncia em KM em linha reta);
	 */
	private void buscaCidadesMaisDistantes() {
		//TODO: implementa��o
	}

}