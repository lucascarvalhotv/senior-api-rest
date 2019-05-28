package br.lucascarvalho.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
	// Ler o arquivo CSV das cidades para a base de dados;
    public String carregarArquivo() {
		listaCidades = CSVUtils.readlistaCidadesFromCSV("cidades.csv");
        return "Arquivo carregado com sucesso!";
    }
	
}