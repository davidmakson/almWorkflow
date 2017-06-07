package br.com.alm.workflow.util;

/**
 * Enum de Status de Defects (ALM IDs)
 * @author eyvevu
 * 
 */
public enum TipoTeste {
	
	MANUAL("Manual", "Manual"),
	AUTOMACAO("Automação", "Automa");
	
	/**
	 * Descricao de status
	 */
	private final String descricao;
	
	/**
	 * Chave de busca
	 */
	private final String chaveBusca;
	
	/**
	 * Construtor
	 * @param descricao
	 */
	private TipoTeste(String descricao, String chaveBusca) {
		this.descricao = descricao;
		this.chaveBusca = chaveBusca;
	}

	/**
	 * get descricao
	 * @return
	 */
	public String getDescricao() {
		return descricao;
	}

	public String getChaveBusca() {
		return chaveBusca;
	}
}
