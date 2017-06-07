package br.com.alm.workflow.util;

/**
 * Enum de Status de Defects (ALM IDs)
 * @author eyvevu
 * 
 */
public enum Status {
	
	BLOCKED("blocked"),
	DEFFERED("deffered"),
	FAILED("failed"),
	NA("na"),
	NO_RUN("noRun"),
	NOT_COMPLETED("notCompleted"),
	PASSED("passed");
	
	/**
	 * Descricao de status
	 */
	private final String descricao;
	
	/**
	 * Construtor
	 * @param descricao
	 */
	private Status(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * get descricao
	 * @return
	 */
	public String getDescricao() {
		return descricao;
	}
}
