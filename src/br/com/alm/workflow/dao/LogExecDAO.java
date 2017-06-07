package br.com.alm.workflow.dao;

import java.util.List;
import br.com.alm.workflow.model.LogExec;
/**
 * @author David Makson do Nascimento Tavares
 */
public interface LogExecDAO {
	
	/**
	 * M�todo que insere um novo registro na tabela de log de execu��es
	 * @param logExec
	 */
	public void insere(LogExec logExec);
	
	/**
	 * M�todo que atualiza um registro na tabela de log de execu��es
	 * @param logExec
	 */
	public void atualiza(LogExec logExec);
	
	/**
	 * M�todo que atualiza o campo status_msg_upload com o erro
	 * @param logExec
	 */
	public void atualizaStatusMsg(int id_exec,String status_msg_upload);
	
	/**
	 * M�todo que apaga um registro na tabela de log de execu��es
	 * @param logExec
	 */	
	public void deleta(int idLogExec);
	
	public List<LogExec> listaTodasExecucoes();
	
	/**
	 * M�todo que retorna as ultimas execu��es que n�o possuem data de upload
	 * @return
	 */
	public List<LogExec> listaUltimasExecucoes();
	/**
	 * M�todo que procura um registro pelo campo ID_ALM
	 * @param idALM
	 * @return
	 */
	public LogExec findByIdExec(int idExec);

}
