package br.com.alm.workflow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.alm.workflow.conf.ConnectionFactory;
import br.com.alm.workflow.infraestructure.DbUtil;
import br.com.alm.workflow.model.LogExec;

/**
 * @author David Makson do Nascimento Tavares
 */
public class LogExecDAOimpl implements LogExecDAO {

	public LogExecDAOimpl() {
	}
	private static final String STATUS_NOK = "1";

	private Connection con;
	private PreparedStatement pst;
	private Statement st;

	@Override
	public void insere(LogExec logExec) {
	}

	@Override
	public void atualiza(LogExec logExec) {
		
		String sql = "UPDATE Arquitetura_NewElo.dbo.log_exec SET "
				+ "status_exec = ? ," 
				+ "data_upload = ? ,"
				+ "status_upload = ? , " 
				+ "status_msg_upload = ? " 
				+ "WHERE id_exec = ?";
		
		ResultSet rs = null;

		LogExec log = findByIdExec(logExec.getIdExec());

		if(null!= log){
			if(null == log.getStatusMsgUpload() || null == log.getStatusUpload()){
					try {
						Calendar cal = Calendar.getInstance();
						java.sql.Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
						
						con = ConnectionFactory.getConnection();
						
						pst = con.prepareStatement(sql);
						pst.setString(1, logExec.getStatusExec());
						pst.setTimestamp(2, timestamp);
						pst.setString(3, logExec.getStatusUpload());
						pst.setString(4, logExec.getStatusMsgUpload());
						pst.setInt(5, logExec.getIdExec());
						pst.executeUpdate();
						
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						DbUtil.close(con);
						DbUtil.close(st);
						DbUtil.close(rs);
					}
			}
		}
	}

	@Override
	public void deleta(int idLogExec) {
	}

	@Override
	public List<LogExec> listaTodasExecucoes() {
		String sql = "select id_alm,evidenciaPath,status_exec from Arquitetura_NewElo.dbo.log_exec";
		ResultSet rs = null;
		List<LogExec> logExecList = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);

			while (rs.next()) {
				LogExec logExec = new LogExec();
				logExec.setIdAlm(rs.getInt(1));
				logExec.setEvidenciaPath(rs.getString("evidenciaPath"));
				logExec.setStatusExec(rs.getString("status_exec"));
				logExecList.add(logExec);
			}

		} catch (Exception e) {
			System.out.println("ERRO AO LISTAR TODAS AS EXECUÇÕES NA BASE : "+e.getMessage());
		} finally {
			DbUtil.close(con);
			DbUtil.close(st);
			DbUtil.close(rs);
		}
		return logExecList;
	}

	@Override
	public List<LogExec> listaUltimasExecucoes() {
		String sql = "select id_exec,id_alm,evidenciaPath,status_exec from Arquitetura_NewElo.dbo.log_exec "
				+ "where "
				+ "data_upload is null "
				+ "and evidenciaPath is not null";
		ResultSet rs = null;
		List<LogExec> logExecList = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);

			while (rs.next()) {
				LogExec logExec = new LogExec();
				logExec.setIdExec(rs.getInt(1));
				logExec.setIdAlm(rs.getInt(2));
				logExec.setEvidenciaPath(rs.getString("evidenciaPath"));
				logExec.setStatusExec(rs.getString("status_exec"));
				logExecList.add(logExec);
			}

		} catch (Exception e) {
			System.out.println("ERRO AO PROCURAR AS ULTIMAS EXECUÇÕES NA BASE : "+e.getMessage());
		} finally {
			DbUtil.close(con);
			DbUtil.close(st);
			DbUtil.close(rs);
		}
		return logExecList;
	}

	@Override
	public void atualizaStatusMsg(int id_exec,String status_msg_upload) {
		Calendar cal = Calendar.getInstance();
		java.sql.Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		
		String sql = "UPDATE Arquitetura_NewElo.dbo.log_exec "
				+ " SET status_msg_upload = ?,"
				+ " status_upload = ?,"
				+ " data_upload = ?"
				+ " WHERE id_exec = ?";
		ResultSet rs = null;

		try {
			con = ConnectionFactory.getConnection();

			pst = con.prepareStatement(sql);
			pst.setString(1, status_msg_upload);
			pst.setString(2, STATUS_NOK);
			pst.setTimestamp(3, timestamp);
			pst.setInt(4, id_exec);

			pst.executeUpdate();

		} catch (Exception e) {
			System.out.println("ERRO AO ATUALIZAR A MENSAGEM DE ERRO: "+e.getMessage());
		} finally {
			DbUtil.close(con);
			DbUtil.close(st);
			DbUtil.close(rs);
		}

	}

	@Override
	public LogExec findByIdExec(int idExec) {
		ResultSet rs = null;
		String sql = "SELECT * FROM Arquitetura_NewElo.dbo.log_exec WHERE id_exec = ?;";
		LogExec logExec = new LogExec();

		try {
			con = ConnectionFactory.getConnection();
			pst = con.prepareStatement(sql);
			pst.setInt(1, idExec);
			rs = pst.executeQuery();

			while (rs.next()) {
				logExec.setIdExec(rs.getInt(1));
				logExec.setIdRobo(rs.getInt(2));
				logExec.setIdAlm(rs.getInt(3));
				logExec.setFrente(rs.getString("frente"));
				logExec.setTb(rs.getString("tb"));
				logExec.setIdUc(rs.getString("id_uc"));
				logExec.setNuCt(rs.getString("nu_ct"));
				logExec.setDataHora(rs.getDate("data_hora"));
				logExec.setActionRobo(rs.getString("action_robo"));
				logExec.setStatusExec(rs.getString("status_exec"));
				logExec.setEvidenciaPath(rs.getString("evidenciaPath"));
				logExec.setCiclo(rs.getInt(12));
				logExec.setDataUpload(rs.getDate("data_upload"));
				logExec.setStatusMsgUpload(rs.getString("status_msg_upload"));
				logExec.setStatusUpload(rs.getString("status_upload"));
			}

		} catch (Exception e) {
			System.out.println("ERRO AO PROCURAR PELO ID_ALM : "+e.getMessage());
		} finally {
			DbUtil.close(con);
			DbUtil.close(st);
			DbUtil.close(rs);
		}
		return logExec;
	}

}
