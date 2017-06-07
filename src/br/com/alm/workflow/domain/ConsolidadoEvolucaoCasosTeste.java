package br.com.alm.workflow.domain;

/**
 * Classe POJO para JSON do relatorio 1 (Consolidado Evolução dos Casos de Testes)
 * Campos de status deveram ser 0 ou 1 de acordo com o status do registro obtido do ALM
 * 
 * @author EYVEVU - Fabio Escobar
 * 
 */
public class ConsolidadoEvolucaoCasosTeste {
	
	/** frente */
	private String frente;

	/** tb */
	private String tb;

	/** produto */
	private int produto;

	/** tipoTeste */
	private String tipoTeste;

	/** blocked */
	private int blocked;

	/** deffered */
	private int deffered;

	/** failed */
	private int failed;

	/** na */
	private int na;

	/** noRun */
	private int noRun;

	/** notCompleted */
	private int notCompleted;

	/** passed */
	private int passed;
	
	/**
	 * Construtor
	 */
	public ConsolidadoEvolucaoCasosTeste() {
		this.blocked = 0;
		this.deffered = 0;
		this.failed = 0;
		this.na = 0;
		this.noRun = 0;
		this.notCompleted = 0;
		this.passed = 0;
	}

	public String getFrente() {
		return frente;
	}

	public void setFrente(String frente) {
		this.frente = frente;
	}

	public String getTb() {
		return tb;
	}

	public void setTb(String tb) {
		this.tb = tb;
	}

	public int getProduto() {
		return produto;
	}

	public void setProduto(int produto) {
		this.produto = produto;
	}

	public String getTipoTeste() {
		return tipoTeste;
	}

	public void setTipoTeste(String tipoTeste) {
		this.tipoTeste = tipoTeste;
	}

	public int getBlocked() {
		return blocked;
	}

	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}

	public int getDeffered() {
		return deffered;
	}

	public void setDeffered(int deffered) {
		this.deffered = deffered;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public int getNa() {
		return na;
	}

	public void setNa(int na) {
		this.na = na;
	}

	public int getNoRun() {
		return noRun;
	}

	public void setNoRun(int noRun) {
		this.noRun = noRun;
	}

	public int getNotCompleted() {
		return notCompleted;
	}

	public void setNotCompleted(int notCompleted) {
		this.notCompleted = notCompleted;
	}

	public int getPassed() {
		return passed;
	}

	public void setPassed(int passed) {
		this.passed = passed;
	}

}
