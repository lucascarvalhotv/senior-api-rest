package br.lucascarvalho.entidade;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cidade {

	@JsonProperty("ibge_id")
	private String idIbge;

	@JsonProperty("uf")
	private Estado uf;

	@JsonProperty("name")
	private String nome;

	@JsonProperty("capital")
	private boolean capital;

	@JsonProperty("lon")
	private float longitude;

	@JsonProperty("lat")
	private float latitude;

	@JsonProperty("no_accents")
	private String nomeSemAcento;

	@JsonProperty("alternative_names")
	private String nomeAlternativo;

	@JsonProperty("microregion")
	private String microrregiao;

	@JsonProperty("mesoregion")
	private String mesorregiao;

	public Cidade() {
	}

	public Cidade(String idIbge, Estado uf, String nome, boolean capital, float longitude, float latitude,
			String nomeSemAcento, String nomeAlternativo, String microrregiao, String mesorregiao) {
		super();
		this.idIbge = idIbge;
		this.uf = uf;
		this.nome = nome;
		this.capital = capital;
		this.longitude = longitude;
		this.latitude = latitude;
		this.nomeSemAcento = nomeSemAcento;
		this.nomeAlternativo = nomeAlternativo;
		this.microrregiao = microrregiao;
		this.mesorregiao = mesorregiao;
	}

	public String getIdIbge() {
		return idIbge;
	}

	public void setIdIbge(String idIbge) {
		this.idIbge = idIbge;
	}

	public Estado getUf() {
		return uf;
	}

	public void setUf(Estado uf) {
		this.uf = uf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isCapital() {
		return capital;
	}

	public void setCapital(boolean capital) {
		this.capital = capital;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public String getNomeSemAcento() {
		return nomeSemAcento;
	}

	public void setNomeSemAcento(String nomeSemAcento) {
		this.nomeSemAcento = nomeSemAcento;
	}

	public String getNomeAlternativo() {
		return nomeAlternativo;
	}

	public void setNomeAlternativo(String nomeAlternativo) {
		this.nomeAlternativo = nomeAlternativo;
	}

	public String getMicrorregiao() {
		return microrregiao;
	}

	public void setMicrorregiao(String microrregiao) {
		this.microrregiao = microrregiao;
	}

	public String getMesorregiao() {
		return mesorregiao;
	}

	public void setMesorregiao(String mesorregiao) {
		this.mesorregiao = mesorregiao;
	}

	@Override
	public String toString() {
		return "Cidade [ibge=" + this.idIbge + ", uf=" + uf + ", nome=" + nome + ", capital=" + capital + ", longitude="
				+ longitude + ", latitude=" + latitude + ", nomeSemAcento=" + nomeSemAcento + ", nomeAlternativo="
				+ nomeAlternativo + ", microrregiao=" + microrregiao + ", mesorregiao=" + mesorregiao + "]";
	}

}