package br.com.alm.workflow.util;

/**
 * Enum de Status de Defects (ALM IDs)
 * @author eyvevu
 * 
 */
public enum Frente {
	
//	PHM("blocked")
	ACR("ACR","Agenda Credenciadora"),
	AEC("AEC","Agenda Pagamentos EC"),
	AER("AER","Agenda Emissor"),
	AUT("AUT","Autorizacoes"),
	CAD("CAD","Cadastro"),
	CAD1("CAD1","Cadastro 1"),
	CAD2("CAD2","Cadastro 2"),
	GRD("GRD","Grade"),
	IC("IC","Integrador Contabil"),
	OUT("OUT","Outgoing"),
	PAG("PAG","Pagfor"),
	PHM("PHM","Pre-Homologacao"),
	STR("STR","STRATUS"),
	REGRESSIVO("Regressivo","Regressivo");
	
	/**
	 * Sigla da frente
	 */
	private final String sigla;
	
	/**
	 * Descricao da frente
	 */
	private final String descricao;
	
	/**
	 * Construtor
	 * @param descricao
	 */
	private Frente(String sigla, String descricao) {
		this.sigla = sigla;
		this.descricao = descricao;
	}

	/**
	 * get descricao
	 * @return
	 */
	public String getDescricao() {
		return descricao;
	}

	public String getSigla() {
		return sigla;
	}
}
