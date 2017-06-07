package br.com.alm.workflow.alm;

public enum TipoEntidade {
	
	EXECUCAO_TESTE("run"), INSTANCIA_TESTE("test-instance"), DEFEITO("defect");
	
	private String valor;
	
	TipoEntidade(String valorOpcao){
		valor = valorOpcao;
	}
	
	public String getValor(){
		return valor;
	}

}
