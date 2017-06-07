package br.com.alm.workflow.alm;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import br.com.alm.workflow.infraestructure.Entities;
import br.com.alm.workflow.infraestructure.Entity;
import br.com.alm.workflow.infraestructure.Entity.Fields;
import br.com.alm.workflow.infraestructure.Entity.Fields.Field;
import br.com.alm.workflow.infraestructure.EntityMarshallingUtils;
import br.com.alm.workflow.infraestructure.Response;
import br.com.alm.workflow.infraestructure.RestConnector;
import br.com.alm.workflow.util.Constants;
import br.com.alm.workflow.util.DateUtils;
import br.com.alm.workflow.util.NumberUtils;

/**
 * 
 * Alm Utils
 * 
 * Classe utilitária com métodos para leitura/escrita utilizando a API da ferramenta.
 * 
 * @author eyvevu - Fabio Escobar
 *
 */
public class AlmUtils {
	
	/** LOG */
	static final Logger LOG = Logger.getLogger(AlmUtils.class);
	
	/** ALM Host */
	private String host;

	/** ALM Domain */
	private String domain;

	/** ALM Project */
	private String project;

	/** ALM Username */
	private String username;

	/** ALM Password */
	private String password;	

	/**
	 * Obtem uma lista de instancia de testes de um test set especifico.
	 * 
	 * @param conn
	 * @param testSetId
	 * @param fields (separados por virgula) 
	 * @return
	 */
	public static Entities getTestInstancesFromTestSet(RestConnector conn, int testSetId, String fields) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("test-instance");
			String query = "?query={cycle-id[" + testSetId + "]}";
			entityUrl += query;
			
			if (fields != null) {
				entityUrl += "&fields=" + fields;
			}
			
			String pageSize = "&page-size=2000";
			entityUrl += pageSize;

			return getMultipleEntities(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem uma lista de instancia de testes de um test set especifico,
	 * 	porém somente as entidades com modificação nos últimos minutos conforme
	 * 	parâmetro.
	 * 
	 * @param conn
	 * @param testSetId
	 * @param fields (separados por virgula) 
	 * @param lastDays
	 * @return
	 */
	public static Entities getTestInstancesFromTestSet(RestConnector conn, int testSetId, String fields, long lastDays) {

		try {
			LocalDate now = LocalDate.now();
			LocalDate lastDaysFromNow = now.minusDays(lastDays);
			String almLastDaysFromNow = DateUtils.convertLocalDateToAlmDate(lastDaysFromNow);
			
			String entityUrl = conn.buildEntityCollectionUrl("test-instance");
			String query = "?query={cycle-id[" + testSetId + "];last-modified[>=" + almLastDaysFromNow + "]}";
			
			entityUrl += query;
			
			if (fields != null) {
				entityUrl += "&fields=" + fields;
			}
			
			String pageSize = "&page-size=2000";
			entityUrl += pageSize;

			return getMultipleEntities(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem uma lista de testes de um test instance especifico.
	 * 
	 * @param conn
	 * @param testId
	 * @return
	 */
	public static Entities getTestsFromTestInstance(RestConnector conn, int testId) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("test");
			String query = "?query={realizes-test-instance.id[" + testId + "]}";
			entityUrl += query;

			return getMultipleEntities(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	public static Entities getTestInstanceByConfigurationId(RestConnector conn, int testId){
		
		try {
			String entityUrl = conn.buildEntityCollectionUrl("test-instance");
			String query = "?query={test-config-id[" + testId + "]}";
			entityUrl += query;

			return getMultipleEntities(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
		
	}
	
	public static Entities getRunByConfigurationId(RestConnector conn, int testId){
		
		try {
			String entityUrl = conn.buildEntityCollectionUrl("run");
			String query = "?query={test-config-id[" + testId + "]}";
			entityUrl += query;

			return getMultipleEntities(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
		
	}	
	
	/**
	 * Obtem um teste por id.
	 * 
	 * @param conn
	 * @param testId
	 * @return
	 */
	public static Entity getTestById(RestConnector conn, int testId) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("test");
			String query = "/" + testId;
			entityUrl += query;
			
			return getSingleEntity(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	private static Entity getSingleEntity(RestConnector conn, String entityUrl) throws Exception, JAXBException {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/xml");

		conn.getQCSession();
		Response res = conn.httpGet(entityUrl, null, requestHeaders);

		// xml -> class instance
		String postedEntityReturnedXml = res.toString();
		
		try {
			return EntityMarshallingUtils.unmarshal(Entity.class, postedEntityReturnedXml);
		} catch (JAXBException e) {
			LOG.error("Erro: " + postedEntityReturnedXml + "\n" + e.getMessage());
			return new Entity();
		}
	}
	
	/**
	 * Obtem uma lista de pastas de releases.
	 * 
	 * @param conn
	 * @param testSetId
	 * @return
	 */
	public static Entities getReleaseFolders(RestConnector conn) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("release-folder");

			return getMultipleEntities(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem uma lista de releases.
	 * 
	 * @param conn
	 * @param testSetId
	 * @return
	 */
	public static Entities getReleases(RestConnector conn) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("release");

			return getMultipleEntities(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	/**
	 * Obtem instancia de teste por id
	 * 
	 * @param conn
	 * @param testInstanceId
	 * @return
	 */
	public static Entity getTestInstancesById(RestConnector conn, int testInstanceId) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("test-instance");
			String query = "/" + testInstanceId;
			entityUrl += query;

			return getSingleEntity(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem ciclo de release por id
	 * 
	 * @param conn
	 * @param releaseCycleId
	 * @return
	 */
	public static Entity getReleaseCycleById(RestConnector conn, int releaseCycleId) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("release-cycle");
			String query = "/" + releaseCycleId;
			entityUrl += query;

			return getSingleEntity(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem caso de teste por id
	 * 
	 * @param conn
	 * @param testSetId
	 * @return
	 */
	public static Entity getTestSetById(RestConnector conn, int testSetId) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("test-set");
			String query = "/" + testSetId;
			entityUrl += query;

			return getSingleEntity(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem pasta de casos de teste por id
	 * 
	 * @param conn
	 * @param testSetFolderId
	 * @return
	 */
	public static Entity getTestSetFolderById(RestConnector conn, int testSetFolderId) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("test-set-folder");
			String query = "/" + testSetFolderId;
			entityUrl += query;

			return getSingleEntity(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem todas as pastas de casos de teste
	 * 
	 * @param conn
	 * @param testSetFolderId
	 * @return
	 */
	public static Entities getTestSetFolders(RestConnector conn) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("test-set-folder");

			return getMultipleEntities(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem todas as pastas de casos de teste (por uma pasta especifica)
	 * 
	 * @param conn
	 * @param folderName
	 * @return
	 */
	public static Entities getTestSetFolders(RestConnector conn, String folderName) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("test-set-folder");
			String query = "?query="+ convertQueryUrlEncode("{name['" + folderName + "']}");
			entityUrl += query;

			return getMultipleEntities(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem todas as pastas de casos de teste (por uma pasta especifica)
	 * RETORNA APENAS 1 - garantir nome único ao usar este método
	 * 
	 * @param conn
	 * @param folderName
	 * @param fields (separados por virgula)
	 * @return
	 */
	public static Entity getTestSetFolderByName(RestConnector conn, String folderName, String fields) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("test-set-folder");
			String query = "?query="+ convertQueryUrlEncode("{name['" + folderName + "']}");
			entityUrl += query;
			
			if (fields != null) {
				entityUrl += "&fields=" + fields;
			}
			
			return getMultipleEntities(conn, entityUrl).getEntity().get(0);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem todas os casos de teste de uma pasta de casos de teste especifica, filtrando por data de corte
	 * 
	 * @param conn
	 * @param folderName
	 * @param fields (separados por virgula)
	 * @param lastDays
	 * @return lista de entidades
	 */
	public static Entities getTestSetByTestSetFolder(RestConnector conn, String folderName, String fields, long lastDays) {

		try {
			LocalDate now = LocalDate.now();
			LocalDate lastDaysFromNow = now.minusDays(lastDays);
			String almLastDaysFromNow = DateUtils.convertLocalDateToAlmDate(lastDaysFromNow);
			
			String entityUrl = conn.buildEntityCollectionUrl("test-set");
			String query = "?query="+ convertQueryUrlEncode("{test-set-folder.hierarchical-path[" + folderName + "*]"
					+ ";contained-in-test-instance.last-modified[>=" + almLastDaysFromNow + "]}");
			entityUrl += query;
			
			if (fields != null) {
				entityUrl += "&fields=" + fields;
			}
			
			String pageSize = "&page-size=2000";
			entityUrl += pageSize;

			return getMultipleEntities(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem todas os casos de teste de uma pasta de casos de teste especifica (sem data de corte)
	 * 
	 * @param conn
	 * @param folderName
	 * @param fields (separados por virgula)
	 * @return lista de entidades
	 */
	public static Entities getTestSetByTestSetFolder(RestConnector conn, String folderName, String fields) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("test-set");
			String query = "?query="+ convertQueryUrlEncode("{test-set-folder.hierarchical-path[" + folderName + "*]}");
			entityUrl += query;
			
			if (fields != null) {
				entityUrl += "&fields=" + fields;
			}
			
			String pageSize = "&page-size=2000";
			entityUrl += pageSize;

			return getMultipleEntities(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Obtem todos os relacionamentos do projeto
	 * 
	 * @param conn
	 * @return
	 */
	public static Entities getRelations(RestConnector conn) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("customization/relation");

			return getMultipleEntities(conn, entityUrl);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	private static Entities getMultipleEntities(RestConnector conn, String entityUrl) throws Exception, JAXBException {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/xml");

		conn.getQCSession();
		Response res = conn.httpGet(entityUrl, null, requestHeaders);

		// xml -> class instance
		String postedEntityReturnedXml = res.toString();
		
		try {
			return EntityMarshallingUtils.unmarshal(Entities.class, postedEntityReturnedXml);
		} catch (JAXBException e) {
			LOG.error("Erro: " + postedEntityReturnedXml + "\n" + e.getMessage());
			return new Entities();
		}
	}
	
	/**
	 * Obtem bug por id
	 * 
	 * @param conn
	 * @param defectId
	 * @return
	 */
	public static Entity getDefectById(RestConnector conn, int defectId) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("defect");
			String query = "/" + defectId;
			entityUrl += query;

			return getSingleEntity(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Iterate instance para obter cycle-id
	 * 
	 * @param entity
	 * @param fieldName
	 * @return id
	 */
	public static String getEntityField(Entity entity, String fieldName) {
		String valueStr = "";

		try {
			List<Field> fields = entity.getFields().getField();
			for (Field field : fields) {
				if (field.getName().equalsIgnoreCase(fieldName)) {
					valueStr = field.getValue().toString();
				}
			}

			return valueStr.replace("[", "").replace("]", "");

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return "0";
		}
	}
	
	private static String convertQueryUrlEncode(String originalQuery) {
		try {
			 return URLEncoder.encode(originalQuery, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage());
		}
		
		return originalQuery;
	}
	
	/**
	 * Retorna o produto de uma instancia de teste (a partir de um id).
	 * 
	 * @param conn
	 * @param mockTestInstanceId
	 * @return
	 */
	public static int getTestInstanceProduct(RestConnector conn, int mockTestInstanceId) {

		// get all test instances by test set
		Entity testInstance = AlmUtils.getTestInstancesById(conn, mockTestInstanceId);

		// get test by test-id attribute of test-instance
		int testId = NumberUtils.parseInt(AlmUtils.getEntityField(testInstance, "test-id"));
		Entity test = AlmUtils.getTestById(conn, testId);

		try {
			return NumberUtils.parseInt(AlmUtils.getEntityField(test, "user-template-53"));
		} catch (Exception e) {
			LOG.error(String.format("Erro ao obter produto para Instancia de Teste %1$d: %2$s", mockTestInstanceId,
					e.getMessage()));
		}

		return 0;
	}
	
	/**
	 * Retorna o produto de uma instancia de teste (a partir de uma instancia).
	 * 
	 * @param conn
	 * @param testInstance
	 * @return
	 */
	public static int getTestInstanceProduct(RestConnector conn, Entity testInstance) {

		try {
			int testId = NumberUtils.parseInt(AlmUtils.getEntityField(testInstance, "test-id"));
			Entity test = AlmUtils.getTestById(conn, testId);

			return NumberUtils.parseInt(AlmUtils.getEntityField(test, "user-template-53"));
		} catch (Exception e) {
			LOG.error(String.format("Erro ao obter produto para Instancia de Teste %1$s: %2$s",
					AlmUtils.getEntityField(testInstance, Constants.ID), e.getMessage()));
		}

		return 0;
	}
	
	/**
	 * Obtem Ciclo de Release de uma Instancia de Teste especifica
	 * 
	 * @param conn
	 * @param testInstance
	 * @return nome do Ciclo de Release
	 */
	public static String getTestInstanceReleaseCycle(RestConnector conn, Entity testInstance) {

		try {
			int releaseCycleId = NumberUtils.parseInt(AlmUtils.getEntityField(testInstance, "assign-rcyc"));
			Entity releaseCycle = AlmUtils.getReleaseCycleById(conn, releaseCycleId);

			return AlmUtils.getEntityField(releaseCycle, Constants.NAME);
		} catch (Exception e) {
			LOG.error(String.format("Erro ao obter Ciclo de Release para Instancia de Teste %1$s: %2$s",
					AlmUtils.getEntityField(testInstance, Constants.ID), e.getMessage()));
		}

		return "";
	}
	
	/**
	 * Loga o produto de um caso de teste.
	 * 
	 * @param conn
	 * @param testSetId
	 * @return
	 */
	public static void printTestSetProduct(RestConnector conn, int mockTestSetId) {

		Entities testInstances = AlmUtils.getTestInstancesFromTestSet(conn, mockTestSetId, null);
		Entity testInstance = testInstances.getEntity().get(0);

		// get test by test-id attribute of test-instance
		int testId = NumberUtils.parseInt(AlmUtils.getEntityField(testInstance, "test-id"));
		Entity test = AlmUtils.getTestById(conn, testId);

		List<Field> fields = test.getFields().getField();
		for (Field field : fields) {
			LOG.debug(field.getName() + " : " + field.getValue());
		}
	}

	/**
	 * Loga os detalhes das instancias de testes.
	 * 
	 * @param conn
	 * @param testSetId
	 * @return
	 */
	public static List<Entity> printDetailedInstances(RestConnector conn, int testSetId) {

		List<Entity> entitiesList = new ArrayList<>();
		Entities entities = AlmUtils.getTestInstancesFromTestSet(conn, testSetId, null);

		for (Entity entity : entities.getEntity()) {
			entitiesList.add(entity);
			List<Field> fields = entity.getFields().getField();

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				LOG.debug(field.getName() + " : " + field.getValue());

				if (i == fields.size() - 1) { // last iteration
					LOG.debug("\n"); // quebra linha
				}
			}
		}

		return entitiesList;
	}

	/**
	 * Loga os detalhes das pastas de Release.
	 * 
	 * @param conn
	 * @param testSetId
	 * @return
	 */
	public static List<Entity> printDetailedReleaseFolders(RestConnector conn) {

		List<Entity> entitiesList = new ArrayList<>();
		Entities entities = AlmUtils.getReleaseFolders(conn);

		for (Entity entity : entities.getEntity()) {
			entitiesList.add(entity);
			List<Field> fields = entity.getFields().getField();

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				LOG.debug(field.getName() + " : " + field.getValue());

				if (i == fields.size() - 1) { // last iteration
					LOG.debug("\n"); // quebra linha
				}
			}
		}

		return entitiesList;
	}

	/**
	 * Loga os detalhes de todas as releases .
	 * 
	 * @param conn
	 * @param testSetId
	 * @return
	 */
	public static List<Entity> printDetailedReleases(RestConnector conn) {

		List<Entity> entitiesList = new ArrayList<>();
		Entities entities = AlmUtils.getReleases(conn);

		for (Entity entity : entities.getEntity()) {
			entitiesList.add(entity);
			List<Field> fields = entity.getFields().getField();

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				LOG.debug(field.getName() + " : " + field.getValue());

				if (i == fields.size() - 1) { // last iteration
					LOG.debug("\n"); // quebra linha
				}
			}
		}

		return entitiesList;
	}

	/**
	 * Loga os detalhes de todas os relacionamentos do projeto .
	 * 
	 * @param conn
	 * @param testSetId
	 * @return
	 */
	public static List<Entity> printDetailedRelations(RestConnector conn) {

		List<Entity> entitiesList = new ArrayList<>();
		Entities entities = AlmUtils.getRelations(conn);

		for (Entity entity : entities.getEntity()) {
			entitiesList.add(entity);
			List<Field> fields = entity.getFields().getField();

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				LOG.debug(field.getName() + " : " + field.getValue());

				if (i == fields.size() - 1) { // last iteration
					LOG.debug("\n"); // quebra linha
				}
			}
		}

		return entitiesList;
	}

	/**
	 * Loga detalhes da instancia de teste por id
	 * 
	 * @param conn
	 * @param testInstanceId
	 * @return
	 */
	public static Entity printDetailedTestInstanceById(RestConnector conn, int testInstanceId) {
		Entity entity = AlmUtils.getTestInstancesById(conn, testInstanceId);

		List<Field> fields = entity.getFields().getField();
		for (Field field : fields) {
			LOG.debug(field.getName() + " : " + field.getValue());
		}

		return entity;
	}

	/**
	 * Loga detalhes da instancia de teste por id
	 * 
	 * @param conn
	 * @param releaseCycleId
	 * @return
	 */
	public static Entity printDetailedReleaseCycleById(RestConnector conn, int releaseCycleId) {
		Entity entity = AlmUtils.getReleaseCycleById(conn, releaseCycleId);

		List<Field> fields = entity.getFields().getField();
		for (Field field : fields) {
			LOG.debug(field.getName() + " : " + field.getValue());
		}

		return entity;
	}

	/**
	 * Loga detalhes do caso de teste por id
	 * 
	 * @param conn
	 * @param testInstanceId
	 * @return
	 */
	public static Entity printDetailedTestSetById(RestConnector conn, int testSetId) {
		Entity entity = AlmUtils.getTestSetById(conn, testSetId);

		List<Field> fields = entity.getFields().getField();
		for (Field field : fields) {
			LOG.debug(field.getName() + " : " + field.getValue());
		}

		return entity;
	}

	/**
	 * Loga detalhes da pasta de casos de teste por id
	 * 
	 * @param conn
	 * @param testInstanceId
	 * @return
	 */
	public static Entity printDetailedTestSetFolderById(RestConnector conn, int testSetFolderId) {
		Entity entity = AlmUtils.getTestSetFolderById(conn, testSetFolderId);

		List<Field> fields = entity.getFields().getField();
		for (Field field : fields) {
			LOG.debug(field.getName() + " : " + field.getValue());
		}

		return entity;
	}

	/**
	 * Loga detalhes de todas as pasta de casos de teste
	 * 
	 * @param conn
	 * @return
	 */
	public static List<Entity> printDetailedTestSetFolders(RestConnector conn) {

		List<Entity> entitiesList = new ArrayList<>();
		Entities entities = AlmUtils.getTestSetFolders(conn);

		for (Entity entity : entities.getEntity()) {
			entitiesList.add(entity);
			List<Field> fields = entity.getFields().getField();

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);

				LOG.debug(field.getName() + " : " + field.getValue());

				if (i == fields.size() - 1) { // last iteration
					LOG.debug("\n"); // quebra linha
				}
			}
		}

		return entitiesList;
	}

	/**
	 * Loga detalhes de todas os casos de teste de uma pasta de casos de teste
	 * especifica
	 * 
	 * @param conn
	 * @param folderName
	 * @return
	 */
	public static List<Entity> printDetailedTestSetByTestSetFolder(RestConnector conn, String folderName) {

		Entity testSetFolder = AlmUtils.getTestSetFolderByName(conn, folderName, "hierarchical-path");
		String hierarchicalPath = AlmUtils.getEntityField(testSetFolder, "hierarchical-path");

		List<Entity> entitiesList = new ArrayList<>();
		Entities entities = AlmUtils.getTestSetByTestSetFolder(conn, hierarchicalPath, null, new Long(Constants.NUMERO_UM));

		for (Entity entity : entities.getEntity()) {
			entitiesList.add(entity);
			List<Field> fields = entity.getFields().getField();

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);

				LOG.debug(field.getName() + " : " + field.getValue());

				if (i == fields.size() - 1) { // last iteration
					LOG.debug("\n"); // quebra linha
				}
			}
		}

		return entitiesList;
	}

	/**
	 * Loga detalhes de todas as pasta de casos de teste (por um nome
	 * especifico)
	 * 
	 * @param conn
	 * @return
	 */
	public static List<Entity> printDetailedTestSetFolders(RestConnector conn, String folderName) {

		List<Entity> entitiesList = new ArrayList<>();
		Entities entities = AlmUtils.getTestSetFolders(conn, folderName);

		for (Entity entity : entities.getEntity()) {
			entitiesList.add(entity);
			List<Field> fields = entity.getFields().getField();

			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);

				LOG.debug(field.getName() + " : " + field.getValue());

				if (i == fields.size() - 1) { // last iteration
					LOG.debug("\n"); // quebra linha
				}
			}
		}

		return entitiesList;
	}

	/**
	 * Loga detalhes do defeito por id
	 * 
	 * @param conn
	 * @param defectId
	 * @return
	 */
	public static Entity printDetailedDefectById(RestConnector conn, int defectId) {
		// Obtem todas as instances de um TestSet especifico
		Entity entity = AlmUtils.getDefectById(conn, defectId);

		List<Field> fields = entity.getFields().getField();
		for (Field field : fields) {
			LOG.debug(field.getName() + " : " + field.getValue());
		}

		return entity;
	}
	
	/**
	 * Set instance status and return updated model
	 * 
	 * @param entity
	 * @param fields
	 * @param status
	 */
	public static Entity setEntityStatus(Entity originalTestInstance, String status) {
		Entity entity = new Entity();
		entity.setType("test-instance");
		entity.setFields(new Fields());
		
		try {
			Field idField = new Field();
			idField.setName(Constants.ID);
			idField.setValue(String.valueOf(getEntityField(originalTestInstance, Constants.ID)));
			entity.getFields().getField().add(idField);

			Field statusField = new Field();
			statusField.setName(Constants.STATUS);
			statusField.setValue(status);
			entity.getFields().getField().add(statusField);
			
			Field testOrderField = new Field();
			testOrderField.setName("test-order");
			testOrderField.setValue(String.valueOf(getEntityField(originalTestInstance, "test-order")));
			entity.getFields().getField().add(testOrderField);
			
			Field testIdField = new Field();
			testIdField.setName("test-id");
			testIdField.setValue(String.valueOf(getEntityField(originalTestInstance, "test-id")));
			entity.getFields().getField().add(testIdField);
			
			Field cycleIdField = new Field();
			cycleIdField.setName("cycle-id");
			cycleIdField.setValue(String.valueOf(getEntityField(originalTestInstance, "cycle-id")));
			entity.getFields().getField().add(cycleIdField);

		} catch (Exception e) {
			LOG.error(e.getMessage());
		}

		return entity;
	}
	
	/**
	 * Atualiza o status de uma entidade RUN
	 * @param originalTestInstance
	 * @param status
	 * @return
	 */
	public static Entity setEntityRunStatus(Entity originalTestInstance, String status) {
		Entity entity = new Entity();
		entity.setType("run");
		entity.setFields(new Fields());
		
		try {
			Field idField = new Field();
			idField.setName(Constants.ID);
			idField.setValue(String.valueOf(getEntityField(originalTestInstance, Constants.ID)));
			entity.getFields().getField().add(idField);

			Field statusField = new Field();
			statusField.setName(Constants.STATUS);
			statusField.setValue(status);
			entity.getFields().getField().add(statusField);
			
//			Field testOrderField = new Field();
//			testOrderField.setName("test-order");
//			testOrderField.setValue(String.valueOf(getEntityField(originalTestInstance, "test-order")));
//			entity.getFields().getField().add(testOrderField);
			
			Field testIdField = new Field();
			testIdField.setName("test-id");
			testIdField.setValue(String.valueOf(getEntityField(originalTestInstance, "test-id")));
			entity.getFields().getField().add(testIdField);
			
//			Field cycleIdField = new Field();
//			cycleIdField.setName("cycle-id");
//			cycleIdField.setValue(String.valueOf(getEntityField(originalTestInstance, "cycle-id")));
//			entity.getFields().getField().add(cycleIdField);

		} catch (Exception e) {
			LOG.error(e.getMessage());
		}

		return entity;
	}	

	/**
	 * Update status
	 * 
	 * @param conn
	 * @param originalTestInstance
	 * @param status
	 */
	public static void updateTestInstanceStatus(RestConnector conn, Entity originalTestInstance, String status) {

		try {
			int testInstanceId = Integer.parseInt(getEntityField(originalTestInstance, Constants.ID));

			String entityUrl = conn.buildEntityCollectionUrl("test-instance");
			String query = "/" + testInstanceId;
			entityUrl += query;

			Map<String, String> requestHeaders = new HashMap<String, String>();
			requestHeaders.put("Content-Type", "application/xml");
			requestHeaders.put("Accept", "application/xml");

			Entity updateTestInstance = setEntityStatus(originalTestInstance, status);

			// class instance -> xml
			String postXml = EntityMarshallingUtils.marshal(Entity.class, updateTestInstance);

			conn.getQCSession();
			Response res = conn.httpPut(entityUrl, postXml.getBytes(), requestHeaders);

			if (res.getStatusCode() != HttpURLConnection.HTTP_OK) {
				throw new Exception(res.toString());
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
		}

	}
	
	/**
	 * Update run status
	 * 
	 * @param conn
	 * @param originalTestInstance
	 * @param status
	 */
	public static void updateRunInstanceStatus(RestConnector conn, Entity originalTestInstance, String status) {

		try {
			int testInstanceId = Integer.parseInt(getEntityField(originalTestInstance, Constants.ID));

			String entityUrl = conn.buildEntityCollectionUrl("run");
			String query = "/" + testInstanceId;
			entityUrl += query;

			Map<String, String> requestHeaders = new HashMap<String, String>();
			requestHeaders.put("Content-Type", "application/xml");
			requestHeaders.put("Accept", "application/xml");

			Entity updateTestInstance = setEntityRunStatus(originalTestInstance, status);

			// class instance -> xml
			String postXml = EntityMarshallingUtils.marshal(Entity.class, updateTestInstance);

			conn.getQCSession();
			Response res = conn.httpPut(entityUrl, postXml.getBytes(), requestHeaders);

			if (res.getStatusCode() != HttpURLConnection.HTTP_OK) {
				throw new Exception(res.toString());
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
		}

	}	
	
	/**
	 * 
	 * Sobe anexo em uma entity
	 * 
	 * @param conn
     * @param entityUrl url of entity to attach the file to
     * @param fileData content of file
     * @param filename to use on server side
     * @return
     */
    public static String attachWithOctetStream(RestConnector conn, 
    		String entityUrl, byte[] fileData, String filename) throws Exception {

        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("Slug", filename);
        requestHeaders.put("Content-Type", "application/octet-stream");

        conn.getQCSession();
        Response response =
                conn.httpPost(entityUrl + "/attachments", fileData, requestHeaders);

        if (response.getStatusCode() != HttpURLConnection.HTTP_CREATED) {
            throw new Exception(response.toString());
        }

        return response.getResponseHeaders().get("Location").iterator().next();
    }
    
    /**
     * Método que inicia os parâmetros para utilização
     */
	public void initParams() {
		Properties prop = new Properties();
		
		
		
//		//LOG
//		try (InputStream inputStream = new FileInputStream(new File("C:/workflowAlm/config/" + Constants.LOG_FILE))) {
//			prop.load(inputStream);
//			PropertyConfigurator.configure(prop);
//			
//		} catch (Exception e) {
//			LOG.error("Erro ao carregar arquivo properties de log: " + e.getMessage());
//		}

		//APP
		try (InputStream inputStream = new FileInputStream(new File("C:/Users/Public/ALM/" + Constants.APP_FILE))) {
			prop.load(inputStream);
		
			this.host = prop.getProperty("HOST");
			this.domain = prop.getProperty("DOMAIN");
			this.project = prop.getProperty("PROJECT");
			this.username = prop.getProperty("USERNAME");
			this.password = prop.getProperty("PASSWORD");
		
		} catch (Exception e) {
			LOG.error("Erro ao carregar arquivo properties: " + e.getMessage());
		}
		
		
	}   
	
	/**
	 * Retorna o número do ID do ALM (campo ID) a partir do configuration id(test-config-id)
	 * @param configurationID
	 * @throws Exception
	 */
	public int getInstanceByConfigurationId(final int configurationID) throws Exception {
		
		initParams();
		
		int retorno = 0;
		
		AlmConnector alm = new AlmConnector();
 
		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), this.host,
				this.domain, this.project);
 
		alm.login(this.username, this.password);
		
		Entities entity = AlmUtils.getTestInstanceByConfigurationId(conn, configurationID);
		
		for (Field field : entity.getEntity().get(0).getFields().getField()){
			//System.out.println(field.getName() + " - " + field.getValue());
			if (field.getName().equals("id")){
				//System.out.println(field.getValue());
				retorno = removeChaves(field.getValue().toString());
				
			} 
		}
		
		alm.logout();
		
		return retorno;
	}
	
	public int removeChaves(final String numero){
		
		String numeroSemChaves = numero.replace("[", "");
		numeroSemChaves = numeroSemChaves.replace("]", "");
		
		return  Integer.valueOf(numeroSemChaves);
		
		
	}
	
	/**
	 * Método que atualiza o status de uma test instance a partir do test instance ID
	 * @param Podemos passar os stauts Passed ou Failed
	 * @throws Exception
	 */
	public void atualizaStatusTestInstance(final int idALM,final String status) throws Exception {
		AlmConnector alm = new AlmConnector();
 
		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), this.host,
				this.domain, this.project);
 
		alm.login(this.username, this.password);
		
		Entity entity = AlmUtils.getTestInstancesById(conn, idALM);
		AlmUtils.updateTestInstanceStatus(conn, entity, status);
		
		alm.logout();
	}
	
	/**
	 * Método criada para carregar uma evidência em uma entidade
	 * @param Os tipos de entidades podem ser run (para execução de caso de teste) test-instance (para caso de teste) defect (para defeito)
	 * @throws Exception
	 */
	public void carregaEvidencia(final TipoEntidade tipoEntidade, final int entidadeID, final String caminhoArquivo, final String nomeArquivo) throws Exception {
		AlmConnector alm = new AlmConnector();
		 
		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), this.host, this.domain, this.project);
 
		alm.login(this.username, this.password);
		
		final String runsCollectionUrl = conn.buildEntityCollectionUrl(tipoEntidade.getValor());
		final String runUrl = runsCollectionUrl + "/" + entidadeID;

		Path path = Paths.get(caminhoArquivo + File.separator  + nomeArquivo);
		byte[] data = Files.readAllBytes(path);
		String octetStreamFileName = nomeArquivo;

		try {
			AlmUtils.attachWithOctetStream(conn, runUrl, data, octetStreamFileName);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		
		alm.logout();
	}
	
	public int getRunIDByConfigurationId(final int configurationID) throws Exception {
		System.out.println("---------getRunIDByConfigurationId---------");
		initParams();
		
		int retorno = 0;
		
		AlmConnector alm = new AlmConnector();
 
		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), this.host,
				this.domain, this.project);
 
		alm.login(this.username, this.password);
		
		Entities entity = AlmUtils.getRunByConfigurationId(conn, configurationID);
		
		for (Field field : entity.getEntity().get(entity.getTotalResults() - 1).getFields().getField()){
			//System.out.println(field.getName() + " - " + field.getValue());
			if (field.getName().equals("id")){
				//System.out.println(field.getValue());
				retorno = removeChaves(field.getValue().toString());
				
			} 
		}
		
		alm.logout();
		
		return retorno;
	}	
	
	/**
	 * Método que retorna o ID de uma instance RUN pelo configurarion id (ID_ALM)
	 * @throws Exception
	 */
	public void testGetRunInstanceByConfigurationId(final int configurationID) throws Exception {
		AlmConnector alm = new AlmConnector();
 
		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), this.host,
				this.domain, this.project);
 
		alm.login(this.username, this.password);
		
		Entities entity = AlmUtils.getRunByConfigurationId(conn, configurationID);
		
		for (Field field : entity.getEntity().get(0).getFields().getField()){
			//System.out.println(field.getName() + " - " + field.getValue());
			if (field.getName().equals("id")){//id é o campo que pega o id do RUN
				System.out.println(field.getValue());
			}
			
		}
		
		alm.logout();
	}
	
	/**
	 * Obtem instancia de teste por id
	 * 
	 * @param conn
	 * @param testInstanceId
	 * @return
	 */
	public static Entity getRunInstancesById(RestConnector conn, int testInstanceId) {

		try {
			String entityUrl = conn.buildEntityCollectionUrl("run");
			String query = "/" + testInstanceId;
			entityUrl += query;

			return getSingleEntity(conn, entityUrl);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}
	
	public void updateRunStatus(final int runID, final String status) throws Exception {
		AlmConnector alm = new AlmConnector();
 
		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), this.host,
				this.domain, this.project);
 
		alm.login(this.username, this.password);
		
		Entity entity = AlmUtils.getRunInstancesById(conn, runID);
		AlmUtils.updateRunInstanceStatus(conn, entity, status);
		
		alm.logout();
	}	

}
