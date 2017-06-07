package br.com.alm.workflow.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import br.com.alm.workflow.dao.LogExecDAOimpl;
import br.com.alm.workflow.infraestructure.AlmUtils;
import br.com.alm.workflow.infraestructure.TipoEntidade;
import br.com.alm.workflow.model.LogExec;

/**
 * @author David Makson do Nascimento Tavares
 */
public class Main {

	private static File resourcePath;
	private static String status_exec;
	private static File[] listFilesInResource;
	private static File[] listFilesInBase;
	private static List<String> listNameFiles = new ArrayList<String>();
	private static String nomeFile;
	private static final String STATUS_OK = "0";

	public static void main(String[] args) {
		init();
	}

	/**
	 * Metodo para iniciar a sequencia de passos para o workflow
	 */
	public static void init() {
		System.out.println("INICIO DO PROCESSO WORKFLOW ALM!");
		findAttachmentsInResource(findIdsAndPathsInBase());
	}

	/**
	 * Metodo achar o caminho do arquivo
	 */
	public static void findAttachmentsInResource(List<LogExec> resultFromBase) {

		try {
			for (LogExec logExec : resultFromBase) {
				resourcePath = new File(logExec.getEvidenciaPath());
				status_exec = logExec.getStatusExec();
				listFilesInResource = resourcePath.listFiles();

				if (listFilesInResource != null && listFilesInResource.length > 0) {
						for (File f : listFilesInResource) {
							if (!f.isDirectory()) {
								if (status_exec.equalsIgnoreCase("PASSED")) {
									if (logExec.getIdAlm() == Integer.parseInt(f.getName().split("\\.")[0])) {
										alteraStatusTestInstance(logExec.getIdExec(), f.getName().split("\\.")[0],
												converteCamelCase(status_exec));
										carregaEvidencia(logExec.getIdExec(), f.getName().split("\\.")[0],
												logExec.getEvidenciaPath(), f.getName());
										changeStatusAlm(logExec.getIdExec(), f.getName().split("\\.")[0],
												converteCamelCase(status_exec));
										moveResource(logExec.getIdExec(), f.getName().split("\\.")[0],
												logExec.getEvidenciaPath(), f.getName(), status_exec);
										updateBaseDados(logExec);
									}
								} else {
									moveResource(logExec.getIdExec(), nomeFile.split("\\.")[0], logExec.getEvidenciaPath(),
											f.getName(), status_exec);
								}
							}
						}
				} else {
					String msgErro= "NÃO FORAM ENCONTRADOS ARQUIVOS NA PASTA " + resourcePath + " PARA ANEXAR!";
					System.out.println(msgErro);
					updateMensagem(logExec.getIdExec(), Integer.toString(logExec.getIdAlm()), msgErro);
				}
				System.out.println("-----------------------------------------------------------------");
			}
		} catch (Exception e) {
			System.out.println("ERRO REALIZANDO PROCESSO DO WORKFLOW ALM: " + e.getMessage());
		}
		System.out.println("FIM DO PROCESSO WORKFLOW ALM!");
		System.exit(0);
	}

	/**
	 * Metodo para alterar o status do testeInstance
	 */
	private static void alteraStatusTestInstance(int idExec, String id_alm, String status) {
		String msgErro = "ERRO AO MUDAR O STATUS DO TESTINSTANCE!";
		AlmUtils almUtils = new AlmUtils();
		try {
			int instanceID = almUtils.getInstanceByConfigurationId(Integer.parseInt(id_alm));
			almUtils.updateTestInstanceStatus(instanceID, status);
		} catch (Exception e) {
			updateMensagem(idExec, id_alm, msgErro);
		}

	}
	
	/**
	 * Metodo para mover o arquivo para a pasta de acordo com o status
	 */
	private static void moveResource(int id_exec, String id_alm, String origem, String nomeFile, String status) {
		String msgErro = "ERRO AO MOVER O ARQUIVO PARA A PASTA!";

		try {
			String sourcePath = origem + File.separator + nomeFile;
			String destinationPath = origem + File.separator + status + File.separator + nomeFile;

			File afile = new File(sourcePath);
			File bfile = new File(destinationPath);

			moveFile(afile, bfile);

		} catch (Exception e) {
			updateMensagem(id_exec, id_alm, msgErro);
		}
	}

	/**
	 * Metodo para mover o arquivo de uma pasta para outra
	 */
	private static void moveFile(File afile, File bfile) {
		InputStream inStream = null;
		OutputStream outStream = null;
		try {

			if (!bfile.exists()) {
				bfile.getParentFile().mkdirs();
			}
			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {

				outStream.write(buffer, 0, length);

			}

			inStream.close();
			outStream.close();

			// delete the original file
			afile.delete();

			System.out.println("O ARQUIVO FOI MOVIDO COM SUCESSO!");

		} catch (IOException e) {
			System.out.println("ERRO AO MOVER O ARQUIVO: " + e.getMessage());
		}
	}
	
	/**
	 * Metodo para atualizar a base de dados ao termino do processo
	 */
	private static void updateBaseDados(LogExec logExec) {
		LogExecDAOimpl logExecDAOimpl = new LogExecDAOimpl();
		logExec.setStatusUpload(STATUS_OK);
		logExec.setDataUpload(new Date());
		logExecDAOimpl.atualiza(logExec);
		System.out.println("O ALM " + logExec.getIdAlm() + " FOI ATUALIZADO NA BASE DE DADOS!");

	}

	/**
	 * Metodo para alterar o status da alm
	 */
	private static void changeStatusAlm(int id_exec, String id_alm, String status_exec) {
		String msgErro = "Run ID não encontrado para atualização.";
		AlmUtils almUtils = new AlmUtils();

		try {
			int runID = almUtils.getRunIDByConfigurationId(Integer.parseInt(id_alm));
			almUtils.updateRunStatus(runID, "Passed");
			System.out.println("O STATUS DA ALM " + id_alm + " FOI ATUALIZADO PARA " + status_exec);
		} catch (Exception e) {
			updateMensagem(id_exec, id_alm, msgErro);
		}

	}

	/**
	 * Metodo para carregar a evidencia
	 */
	private static void carregaEvidencia(int id_exec, String id_alm, String resourcePath, String nomeFile) {
		String msgErro = "Arquivo/Evidência não encontrado para upload.";
		AlmUtils almUtils = new AlmUtils();
		try {
			// Pega o INSTANCE ID baseado no CONFIGURATION ID
			int instanceID = almUtils.getInstanceByConfigurationId(Integer.parseInt(id_alm));
			int runTestID = almUtils.getRunIDByConfigurationId(Integer.parseInt(id_alm));
			almUtils.carregaEvidencia(TipoEntidade.EXECUCAO_TESTE, runTestID, resourcePath, nomeFile);
			System.out.println(
					"O ANEXO FOI ADICIONADO NA ALM ID =" + id_alm + " CAMINHO = " + resourcePath + "\\" + nomeFile);
		} catch (Exception e) {
			updateMensagem(id_exec, id_alm, msgErro);
		}
	}

	/**
	 * Metodo para fazer update da mensagem de erro na base de dados
	 */
	private static void updateMensagem(int id_exec, String id_alm, String msg) {
		LogExecDAOimpl logExecDAOimpl = new LogExecDAOimpl();
		logExecDAOimpl.atualizaStatusMsg(id_exec, msg);
		System.out.println("O ALM " + id_alm + " FOI ATUALIZADO COM A MENSAGEM: " + msg);

	}

	/**
	 * Metodo para encontrar na base de dados o caminho dos arquivos que devem ser anexados no alm
	 */
	private static List<LogExec> findIdsAndPathsInBase() {
		System.out.println("PROCURANDO RESULTADOS NA BASE DE DADOS...");

		List<LogExec> retorno = new ArrayList<LogExec>();

		LogExecDAOimpl logExecDAOimpl = new LogExecDAOimpl();
		retorno = logExecDAOimpl.listaUltimasExecucoes();

		if (retorno.isEmpty()) {
			System.out.println("NÃO FORAM ENCONTRADOS RESULTADOS NA BASE DE DADOS!");
		}
		return retorno;
	}

	public File getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(File resourcePath) {
		this.resourcePath = resourcePath;
	}

	public File[] getListFiles() {
		return listFilesInResource;
	}

	public void setListFiles(File[] listFiles) {
		this.listFilesInResource = listFiles;
	}

	public static File[] getListFilesInBase() {
		return listFilesInBase;
	}

	public static void setListFilesInBase(File[] listFilesInBase) {
		Main.listFilesInBase = listFilesInBase;
	}

	public static String converteCamelCase(final String str) {

		String strConvertida = str.toLowerCase();
		return strConvertida.substring(0, 1).toUpperCase() + strConvertida.substring(1);

	}

}
