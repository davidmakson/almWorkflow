package br.com.alm.workflow.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.alm.workflow.alm.AlmUtils;
import br.com.alm.workflow.domain.ConsolidadoEvolucaoCasosTeste;
import br.com.alm.workflow.infraestructure.Entities;
import br.com.alm.workflow.infraestructure.Entity;
import br.com.alm.workflow.infraestructure.Entity.Fields.Field;
import br.com.alm.workflow.infraestructure.RestConnector;
import br.com.alm.workflow.util.Constants;
import br.com.alm.workflow.util.FileHelper;
import br.com.alm.workflow.util.Frente;
import br.com.alm.workflow.util.NumberUtils;
import br.com.alm.workflow.util.TipoTeste;

/**
 * Classe de negócio para extração do relatorio 1: Consolidado de Evolucaode
 * Casos de Teste
 * 
 * @author EYVEVU - Fabio Escobar
 *
 */
public class ConsolidadoEvolucaoCasosTesteBO {

	/** LOG */
	static final Logger LOG = Logger.getLogger(ConsolidadoEvolucaoCasosTesteBO.class);

	/**
	 * Relatorio 1 (Consolidado Evolução dos Casos de Testes): Extrai detalhes
	 * de todas as instancias de teste de uma pasta de casos de teste
	 * especifica:
	 * 
	 * 1 - Obtem todos Casos de Teste abaixo da pasta informada; 2 - Obtem todos
	 * as Instancias de Teste de cada Caso de Teste; 3 - Obtem o produto de cada
	 * Instancia de Teste (somente a primeira de cada Caso de Teste) 4 - Obtem o
	 * Ciclo de Release (Frente/TB/Tipo) de cada Instancia de Teste (somente a
	 * primeira de cada Caso de Teste) 5 - Formata objeto de saída.
	 * 
	 *
	 * @author EYVEVU - Fabio Escobar
	 * @param conn
	 * @param folderName
	 * @param quantidadeDiasExtracao
	 * @param cargaMassiva
	 * 
	 * @return lista de registro para relatorio
	 */
	public static List<ConsolidadoEvolucaoCasosTeste> extrairConsolidadoEvolucaoCasosTeste(RestConnector conn,
			String folderName, Long quantidadeDiasExtracao, boolean cargaMassiva) {

		List<ConsolidadoEvolucaoCasosTeste> listaRelatorio = new ArrayList<>();

		try {
			// contabiliza quantidade de instancias de teste na pasta raiz
			int qtdTestSets = 0;
			int qtdTestInstances = 0;

			// procura pela pasta raiz e obtem o id hierarquico
			LOG.debug("Pesquisando pasta '" + folderName + "'");
			Entity testSetFolder = AlmUtils.getTestSetFolderByName(conn, folderName, "hierarchical-path");
			String hierarchicalPath = AlmUtils.getEntityField(testSetFolder, "hierarchical-path");

			// obtem todos os Casos de Teste desta pasta, por data de corte ou
			// massivamente
			Entities testSets;
			if (cargaMassiva) {
				testSets = AlmUtils.getTestSetByTestSetFolder(conn, hierarchicalPath, Constants.ID);
			} else {
				testSets = AlmUtils.getTestSetByTestSetFolder(conn, hierarchicalPath, Constants.ID,
						quantidadeDiasExtracao);
			}

			// para cada entidade (Caso de Teste)...
			for (Entity testSet : testSets.getEntity()) {
				qtdTestSets++;

				// obtem todas as suas Instancias de Teste, por data de corte ou
				// massivamente
				int testSetId = NumberUtils.parseInt(AlmUtils.getEntityField(testSet, Constants.ID));
				Entities testInstances;
				if (cargaMassiva) {
					testInstances = AlmUtils.getTestInstancesFromTestSet(conn, testSetId,
							"id,status,test-id,assign-rcyc");
				} else {
					testInstances = AlmUtils.getTestInstancesFromTestSet(conn, testSetId,
							"id,status,test-id,assign-rcyc", quantidadeDiasExtracao);
				}

				LOG.debug(String.format("Para o TestSet id: %1$d foram encontrados %2$d instancias de teste.",
						testSetId, testInstances.getTotalResults()));

				// para cada entidade (Instancia de Teste)...
				int testInstanceProduct = 0;
				String testInstanceTestSubType = "";
				String testInstanceReleaseCycle = "";
				boolean isExtraInfoSet = false;
				for (Entity testInstance : testInstances.getEntity()) {
					qtdTestInstances++;

					// Obtem produto e tipo de teste amarrado ao Teste, somente
					// para
					// a primeira Instancia do Caso de Teste
					if (!isExtraInfoSet) {
						int testId = NumberUtils.parseInt(AlmUtils.getEntityField(testInstance, "test-id"));
						Entity test = AlmUtils.getTestById(conn, testId);

						testInstanceProduct = NumberUtils.parseInt(AlmUtils.getEntityField(test, "user-template-53"));
						testInstanceTestSubType = AlmUtils.getEntityField(test, "subtype-id");
						testInstanceReleaseCycle = AlmUtils.getTestInstanceReleaseCycle(conn, testInstance);
						isExtraInfoSet = true;
					}

					// obtem seus campos e valores
					List<Field> fields = testInstance.getFields().getField();
					for (int i = 0; i < fields.size(); i++) {
						Field field = fields.get(i);

						// imprime id e status
						if (field.getName().equalsIgnoreCase(Constants.ID)
								|| field.getName().equalsIgnoreCase(Constants.STATUS)) {
							LOG.debug(field.getName() + " : " + field.getValue());
						}

						if (i == fields.size() - 1) { // last iteration
							LOG.debug(String.format("Produto : %1$d", testInstanceProduct));
							LOG.debug(String.format("Release : %1$s", testInstanceReleaseCycle));
							LOG.debug("\n"); // quebra linha
						}
					}

					listaRelatorio.add(popularRelatorio(testInstance, testInstanceProduct, testInstanceReleaseCycle,
							testInstanceTestSubType));
				}

				LOG.debug(String.format("Qtd. de instancias de teste atual: %1$d", qtdTestInstances));
			}

			LOG.debug(String.format("Total de Testsets em '%1$s: %2$d", folderName, qtdTestSets));
			LOG.debug(String.format("Qtd. de instancias de teste em '%1$s': %2$d", folderName, qtdTestInstances));

		} catch (Exception e) {
			LOG.error("Erro ao obter instancias de teste para uma pasta raiz: " + e.getMessage());
		}

		return listaRelatorio;
	}

	/**
	 * Popula objeto (POJO) de relatorio conforme entidade
	 * 
	 * @param testInstance
	 * @param testInstanceProduct
	 * @param testInstanceReleaseCycle
	 * @param testInstanceTestSubType
	 * 
	 * @return dominio populado
	 */
	private static ConsolidadoEvolucaoCasosTeste popularRelatorio(Entity testInstance, int testInstanceProduct,
			String testInstanceReleaseCycle, String testInstanceTestSubType) {

		ConsolidadoEvolucaoCasosTeste relatorio = new ConsolidadoEvolucaoCasosTeste();

		relatorio.setFrente(obterNomeFrente(testInstanceReleaseCycle));
		relatorio.setTb(obterTb(testInstanceReleaseCycle));
		relatorio.setProduto(testInstanceProduct);
		relatorio.setTipoTeste(obterTipoTeste(testInstanceReleaseCycle, testInstanceTestSubType));

		switch (AlmUtils.getEntityField(testInstance, Constants.STATUS)) {

		case Constants.BLOCKED:
			relatorio.setBlocked(Constants.NUMERO_UM);
			break;

		case Constants.DEFERRED:
			relatorio.setDeffered(Constants.NUMERO_UM);
			break;

		case Constants.FAILED:
			relatorio.setFailed(Constants.NUMERO_UM);
			break;

		case Constants.NA:
			relatorio.setNa(Constants.NUMERO_UM);
			break;

		case Constants.NO_RUN:
			relatorio.setNoRun(Constants.NUMERO_UM);
			break;

		case Constants.NOT_COMPLETED:
			relatorio.setNotCompleted(Constants.NUMERO_UM);
			break;

		case Constants.PASSED:
			relatorio.setPassed(Constants.NUMERO_UM);
			break;

		default:
			break;
		}

		return relatorio;
	}

	/**
	 * Obtem o TB do teste através do nome do Ciclo de Relese informado
	 * 
	 * @param testInstanceReleaseCycle
	 * @return TB do teste
	 */
	private static String obterTb(String testInstanceReleaseCycle) {

		String tb = "N/A";
		String literalTb = "TB";

		if (testInstanceReleaseCycle != null && testInstanceReleaseCycle.trim() != ""
				&& testInstanceReleaseCycle.contains(literalTb)) {

			tb = testInstanceReleaseCycle.substring(testInstanceReleaseCycle.indexOf(literalTb) + Constants.NUMERO_DOIS,
					testInstanceReleaseCycle.indexOf(literalTb) + Constants.NUMERO_QUATRO);

		}

		return tb;
	}

	/**
	 * Obtem o tipo de teste através do nome do Ciclo de Relese informado.
	 * 
	 * obs.: Se não encontrado, efetua a mesma busca pelo tipo de teste no
	 * atributo 'subtype-id' do Caso de Teste (test-set)
	 * 
	 * @param testInstanceReleaseCycle
	 * @param tipoTesteInTestSet
	 * @return tipo de teste
	 */
	private static String obterTipoTeste(String testInstanceReleaseCycle, String tipoTesteInTestSet) {
		String tipoTeste = "N/A";

		if (testInstanceReleaseCycle != null && testInstanceReleaseCycle.trim() != "") {

			// usar tipo de teste do ciclo de release
			if (testInstanceReleaseCycle.toLowerCase().contains(TipoTeste.AUTOMACAO.getChaveBusca().toLowerCase())) {
				tipoTeste = TipoTeste.AUTOMACAO.getDescricao();
			} else if (testInstanceReleaseCycle.toLowerCase()
					.contains(TipoTeste.MANUAL.getChaveBusca().toLowerCase())) {
				tipoTeste = TipoTeste.MANUAL.getDescricao();
			} else {
				// utilizar tipo de teste do test-set
				if (tipoTesteInTestSet.toLowerCase().contains(TipoTeste.AUTOMACAO.getChaveBusca().toLowerCase())) {
					tipoTeste = TipoTeste.AUTOMACAO.getDescricao();
				} else if (tipoTesteInTestSet.toLowerCase().contains(TipoTeste.MANUAL.getChaveBusca().toLowerCase())) {
					tipoTeste = TipoTeste.MANUAL.getDescricao();
				}
			}
		}

		return tipoTeste;
	}

	/**
	 * Obtem nome da frente de trabalho através do nome do Ciclo de Relese
	 * informado.
	 * 
	 * Padrões conhecidos: - OUT - TB03 - Manual - (OUT) Outgoing - TB03 -
	 * Manual - (OUT) Outgoing - Regressivo - PHM - Regressivo - PHM - Outgoing
	 * 
	 * obs.: Nao encontrado padrao para detecção de testes em Integrador
	 * Contábil!
	 * 
	 * @param testInstanceReleaseCycle
	 * @return nome da frente
	 */
	private static String obterNomeFrente(String testInstanceReleaseCycle) {

		String nomeFrente = "N/A";
		String prefixo = "";
		if (testInstanceReleaseCycle != null && testInstanceReleaseCycle.trim() != "") {

			// Verifica se tem prefixo de regressivo, homologacao ou stratus
			if (testInstanceReleaseCycle.contains(Frente.PHM.getSigla())) {
				prefixo = Frente.PHM.getDescricao() + " ";
			} else if (testInstanceReleaseCycle.contains(Frente.REGRESSIVO.getSigla())) {
				prefixo = Frente.REGRESSIVO.getDescricao() + " ";
			} else if (testInstanceReleaseCycle.contains(Frente.STR.getSigla())) {
				// stratus nao possui frente portanto retorna somente prefixo
				return Frente.STR.getDescricao();
			}

			if (prefixo != "") {
				nomeFrente = "";
			}

			// Verifica frente de trabalho pela sigla (OUT) ou descricao
			// (Outgoing)
			if (testInstanceReleaseCycle.contains(Frente.ACR.getSigla())
					|| testInstanceReleaseCycle.toLowerCase().contains(Frente.ACR.getDescricao().toLowerCase())) {
				nomeFrente = Frente.ACR.getDescricao();
			} else if (testInstanceReleaseCycle.contains(Frente.AEC.getSigla())
					|| testInstanceReleaseCycle.toLowerCase().contains(Frente.AEC.getDescricao().toLowerCase())) {
				nomeFrente = Frente.AEC.getDescricao();
			} else if (testInstanceReleaseCycle.contains(Frente.AER.getSigla())
					|| testInstanceReleaseCycle.toLowerCase().contains(Frente.AER.getDescricao().toLowerCase())) {
				nomeFrente = Frente.AER.getDescricao();
			} else if (testInstanceReleaseCycle.contains(Frente.AUT.getSigla())
					|| testInstanceReleaseCycle.toLowerCase().contains(Frente.AUT.getDescricao().toLowerCase())) {
				nomeFrente = Frente.AUT.getDescricao();
			} else if (testInstanceReleaseCycle.contains(Frente.CAD.getSigla())
					|| testInstanceReleaseCycle.toLowerCase().contains(Frente.CAD.getDescricao().toLowerCase())) {
				// verificar se é cad 1 ou 2 ou somente cad
				if (testInstanceReleaseCycle.contains(Frente.CAD1.getSigla())) {
					nomeFrente = Frente.CAD1.getDescricao();
				} else if (testInstanceReleaseCycle.contains(Frente.CAD2.getSigla())) {
					nomeFrente = Frente.CAD2.getDescricao();
				} else {
					nomeFrente = Frente.CAD.getDescricao();
				}
			} else if (testInstanceReleaseCycle.contains(Frente.GRD.getSigla())
					|| testInstanceReleaseCycle.toLowerCase().contains(Frente.GRD.getDescricao().toLowerCase())) {
				nomeFrente = Frente.GRD.getDescricao();
			} else if (testInstanceReleaseCycle.contains(Frente.OUT.getSigla())
					|| testInstanceReleaseCycle.toLowerCase().contains(Frente.OUT.getDescricao().toLowerCase())) {
				nomeFrente = Frente.OUT.getDescricao();
			} else if (testInstanceReleaseCycle.contains(Frente.PAG.getSigla())
					|| testInstanceReleaseCycle.toLowerCase().contains(Frente.PAG.getDescricao().toLowerCase())) {
				nomeFrente = Frente.PAG.getDescricao();
			}
		}

		return prefixo + nomeFrente;
	}

	/**
	 * Converte classe de dominio para objetos JSON e frava relatorio em arquivo
	 * 
	 * @param listaRelatorio
	 * @param caminhoSaida
	 * 
	 */
	public static void gravarRelatorio(List<ConsolidadoEvolucaoCasosTeste> listaRelatorio, String caminhoSaida) {

		try {
			// //Instancia JSON Builder
			// Gson gson = new GsonBuilder().setPrettyPrinting().create();

			// Instancia arquivo
			String filePath = caminhoSaida;
			File arquivo = new File(filePath);
			if (!arquivo.exists()) {
				arquivo.mkdirs();
			}

			String fileName = filePath + Constants.ARQUIVO_CONSOLIDADO_EVOLUCAO_CASOS_TESTE;
			List<String> linhasArquivo = new ArrayList<>();

			// Converte e grava em arquivo
			for (ConsolidadoEvolucaoCasosTeste relatorio : listaRelatorio) {
				// linhasArquivo.add(gson.toJson(relatorio));
				linhasArquivo.add(converteObjetoSaida(relatorio));
			}

			FileHelper.writeToFile(fileName, linhasArquivo, Constants.BUFFER_ARQUIVO);

		} catch (Exception e) {
			LOG.error("Erro ao gravar arquivo: " + e.getMessage());
		}
	}

	/**
	 * Converte dominio (POJO) para formato de saída para o LOGSTASH
	 * 
	 * @param relatorio
	 *            (POJO)
	 * @return string convertida
	 */
	private static String converteObjetoSaida(ConsolidadoEvolucaoCasosTeste relatorio) {

		String formato = "ALM cnsd_evlc_ct ::: \"%s\" ::: \"%s\" ::: \"%s\" ::: \"%s\" ::: %d ::: %d ::: %d ::: %d ::: %d ::: %d ::: %d";

		return String.format(formato, relatorio.getFrente(), relatorio.getTb(), relatorio.getProduto(),
				relatorio.getTipoTeste(), relatorio.getBlocked(), relatorio.getDeffered(), relatorio.getFailed(),
				relatorio.getNa(), relatorio.getNoRun(), relatorio.getNotCompleted(), relatorio.getPassed());

	}
}
