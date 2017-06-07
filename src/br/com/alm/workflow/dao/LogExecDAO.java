package br.com.alm.workflow.dao;

import java.util.List;
import br.com.alm.workflow.model.LogExec;
/**
 * @author David Makson do Nascimento Tavares
 */
public interface LogExecDAO {
	
	/**
	 * Método que insere um novo registro na tabela de log de execuções
	 * @param logExec
	 */
	public void insere(LogExec logExec);
	
	/**
	 * Método que atualiza um registro na tabela de log de execuções
	 * @param logExec
	 */
	public void atualiza(LogExec logExec);
	
	/**
	 * Método que atualiza o campo status_msg_upload com o erro
	 * @param logExec
	 */
	public void atualizaStatusMsg(int id_exec,String status_msg_upload);
	
	/**
	 * Método que apaga um registro na tabela de log de execuções
	 * @param logExec
	 */	
	public void deleta(int idLogExec);
	
	public List<LogExec> listaTodasExecucoes();
	
	/**
	 * Método que retorna as ultimas execuções que não possuem data de upload
	 * @return
	 */
	public List<LogExec> listaUltimasExecucoes();
	/**
	 * Método que procura um registro pelo campo ID_ALM
	 * @param idALM
	 * @return
	 */
	public LogExec findByIdExec(int idExec);

}
