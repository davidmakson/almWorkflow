package br.com.alm.workflow.model;

import java.util.Date;
/**
 * @author David Makson do Nascimento Tavares
 */
public class LogExec {

	private int idExec;
	private String actionRobo;
	private int ciclo;
	private Date dataHora;
	private Date dataUpload;
	private String evidenciaPath;
	private String frente;
	private int idAlm;
	private int idRobo;
	private String idUc;
	private String nuCt;
	private String statusExec;
	private String statusMsgUpload;
	private String statusUpload;
	private String tb;

	public LogExec() {
	}

	public int getIdExec() {
		return this.idExec;
	}

	public void setIdExec(int idExec) {
		this.idExec = idExec;
	}

	public String getActionRobo() {
		return this.actionRobo;
	}

	public void setActionRobo(String actionRobo) {
		this.actionRobo = actionRobo;
	}

	public int getCiclo() {
		return this.ciclo;
	}

	public void setCiclo(int ciclo) {
		this.ciclo = ciclo;
	}

	public Date getDataHora() {
		return this.dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public Date getDataUpload() {
		return this.dataUpload;
	}

	public void setDataUpload(Date dataUpload) {
		this.dataUpload = dataUpload;
	}

	public String getEvidenciaPath() {
		return this.evidenciaPath;
	}

	public void setEvidenciaPath(String evidenciaPath) {
		this.evidenciaPath = evidenciaPath;
	}

	public String getFrente() {
		return this.frente;
	}

	public void setFrente(String frente) {
		this.frente = frente;
	}

	public int getIdAlm() {
		return this.idAlm;
	}

	public void setIdAlm(int idAlm) {
		this.idAlm = idAlm;
	}

	public int getIdRobo() {
		return this.idRobo;
	}

	public void setIdRobo(int idRobo) {
		this.idRobo = idRobo;
	}

	public String getIdUc() {
		return this.idUc;
	}

	public void setIdUc(String idUc) {
		this.idUc = idUc;
	}

	public String getNuCt() {
		return this.nuCt;
	}

	public void setNuCt(String nuCt) {
		this.nuCt = nuCt;
	}

	public String getStatusExec() {
		return this.statusExec;
	}

	public void setStatusExec(String statusExec) {
		this.statusExec = statusExec;
	}

	public String getStatusMsgUpload() {
		return this.statusMsgUpload;
	}

	public void setStatusMsgUpload(String statusMsgUpload) {
		this.statusMsgUpload = statusMsgUpload;
	}

	public String getStatusUpload() {
		return this.statusUpload;
	}

	public void setStatusUpload(String statusUpload) {
		this.statusUpload = statusUpload;
	}

	public String getTb() {
		return this.tb;
	}

	public void setTb(String tb) {
		this.tb = tb;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ciclo;
		result = prime * result + idAlm;
		result = prime * result + idExec;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogExec other = (LogExec) obj;
		if (ciclo != other.ciclo)
			return false;
		if (idAlm != other.idAlm)
			return false;
		if (idExec != other.idExec)
			return false;
		return true;
	}

	public LogExec(int idExec) {
		this.idExec = idExec;
	}

	public LogExec(int idExec, String evidenciaPath, int idAlm,
			String statusExec) {
		this.idExec = idExec;
		this.evidenciaPath = evidenciaPath;
		this.idAlm = idAlm;
		this.statusExec = statusExec;
	}
}