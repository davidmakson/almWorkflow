package br.com.alm.workflow.alm;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import br.com.alm.workflow.business.ConsolidadoEvolucaoCasosTesteBO;
import br.com.alm.workflow.domain.ConsolidadoEvolucaoCasosTeste;
import br.com.alm.workflow.infraestructure.RestConnector;
import br.com.alm.workflow.util.Constants;
import br.com.alm.workflow.util.NumberUtils;
import br.com.alm.workflow.business.ConsolidadoEvolucaoCasosTesteBO;
/**
 * 
 * Alm Runner
 * 
 * Classe principal para conectar e interagir com a ferramenta HP ALM
 * 
 * @author EYVEVU - Fabio Escobar
 *
 */
public class AlmRunner {

	/** LOG */
	static final Logger LOG = Logger.getLogger(AlmRunner.class);

	/** ALM Host */
	private static String host;

	/** ALM Domain */
	private static String domain;

	/** ALM Project */
	private static String project;

	/** ALM Username */
	private static String username;

	/** ALM Password */
	private static String password;

	/** Pasta raiz relatorio */
	private static String pastaRaiz;
	
	/** Caminho saida relatorio */
	private static String caminhoSaida;
	
	/** Quantidade de dias para extracao */
	private static Long quantidadeDiasExtracao;
	
	/** Indicador de carga massiva */
	private static boolean cargaMassiva;
	
	/**
	 * Constructor
	 */
	private AlmRunner() {
	}

	/**
	 * Main Class
	 * 
	 * @param args
	 */
	/*public static void main(String[] args) {

		// Obtendo parametros iniciais
		int relatorio = NumberUtils.parseInt(args[0]);
		int pastaRaiz = NumberUtils.parseInt(args[1]);
		String configPath = args[2];

		// Obtendo parametros de conexao, insumos...
		AlmRunner.setLogValues(configPath);
		AlmRunner.setPropertiesValues(relatorio, pastaRaiz, configPath);
		
		LOG.info(String.format("Iniciando aplicação as %1$s", LocalDateTime.now().toString()));

		AlmConnector alm = new AlmConnector();
		RestConnector conn = RestConnector.getInstance();

		try {
			conn.init(new HashMap<String, String>(), AlmRunner.host, AlmRunner.domain, AlmRunner.project);
			alm.login(AlmRunner.username, AlmRunner.password);

			switch (relatorio) {
			case Constants.NUMERO_UM:

				// executar relatorio 1
				List<ConsolidadoEvolucaoCasosTeste> listaRelatorio = ConsolidadoEvolucaoCasosTesteBO
						.extrairConsolidadoEvolucaoCasosTeste(conn, AlmRunner.pastaRaiz,
								AlmRunner.quantidadeDiasExtracao, AlmRunner.cargaMassiva);
				
				ConsolidadoEvolucaoCasosTesteBO.gravarRelatorio(listaRelatorio, AlmRunner.caminhoSaida);
				
				break;

			default:
				break;
			}

			alm.logout();

		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {

			LOG.info(String.format("Fim da aplicação as %1$s", LocalDateTime.now().toString()));
		}
	}*/

	/**
	 * Set constant property settings
	 * 
	 * @param relatorio
	 * @param pastaRaiz
	 * @param configPath
	 */
	private static void setPropertiesValues(int relatorio, int pastaRaiz, String configPath) {
		Properties prop = new Properties();

		try (InputStream inputStream = new FileInputStream(new File(configPath + Constants.APP_FILE))) {
			prop.load(inputStream);

			AlmRunner.host = prop.getProperty("HOST");
			AlmRunner.domain = prop.getProperty("DOMAIN");
			AlmRunner.project = prop.getProperty("PROJECT");
			AlmRunner.username = prop.getProperty("USERNAME");
			AlmRunner.password = prop.getProperty("PASSWORD");
			AlmRunner.pastaRaiz = prop.getProperty("RELATORIO_" + relatorio + "_PASTA_" + pastaRaiz);
			AlmRunner.caminhoSaida = prop.getProperty("CAMINHO_SAIDA_RELATORIO_" + relatorio);
			AlmRunner.quantidadeDiasExtracao = Long.parseLong(prop.getProperty("QUANTIDADE_DIAS_EXTRACAO"));
			AlmRunner.cargaMassiva = prop.getProperty("CARGA_MASSIVA").equals("0") ? false : true;
			
		} catch (Exception e) {
			LOG.error("Erro ao carregar arquivo properties: " + e.getMessage());
		}
	}
	
	/**
	 * Set external log properties file
	 * 
	 * @param configPath
	 */
	private static void setLogValues(String configPath) {
		Properties prop = new Properties();
		
		try (InputStream inputStream = new FileInputStream(new File(configPath + Constants.LOG_FILE))) {
			prop.load(inputStream);
			PropertyConfigurator.configure(prop);
			
		} catch (Exception e) {
			LOG.error("Erro ao carregar arquivo properties de log: " + e.getMessage());
		}
	}

}
