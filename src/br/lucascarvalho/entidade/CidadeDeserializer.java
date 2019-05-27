package br.lucascarvalho.entidade;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class CidadeDeserializer extends StdDeserializer<Cidade> {

	public CidadeDeserializer() { 
        this(null); 
    }
	
	protected CidadeDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Cidade deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
		String idIbge = node.get("ibge_id").asText();
		Estado uf = Estado.valueOf(node.get("uf").asText());
		String nome = node.get("name").asText();
		boolean capital = node.get("capital").asText().equals("TRUE") || node.get("capital").asText().equals("true");
		float longitude = (float) node.get("lon").asDouble();
		float latitude = (float) node.get("lat").asDouble();
		String nomeSemAcento = node.get("no_accents").asText();
		String nomeAlternativo = node.get("alternative_names").asText();
		String microrregiao = node.get("microregion").asText();
		String mesorregiao = node.get("mesoregion").asText();
		
        return new Cidade(idIbge, uf, nome, capital, longitude, latitude, nomeSemAcento, nomeAlternativo, microrregiao, mesorregiao);
	}
}