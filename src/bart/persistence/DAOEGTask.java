package bart.persistence;

import bart.exceptions.DAOException;
import bart.model.EGTask;
import bart.model.EGTaskConfiguration;
import bart.persistence.parser.ParserMainMemoryDatabase;
import bart.persistence.parser.operators.ParseDependencies;
import bart.model.database.EmptyDB;
import bart.model.database.IDatabase;
import bart.model.database.dbms.DBMSDB;
import bart.persistence.relational.AccessConfiguration;
import bart.persistence.xml.DAOXmlUtility;
import bart.persistence.xml.operators.TransformFilePaths;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAOEGTask {

    ///////////////////// DATABASE
    private static final String DB_TYPE_MAINMEMORY = "XML";
    private static final String DB_TYPE_MAINMEMORY_GENERATE = "GENERATE";
    private static final String DB_TYPE_DBMS = "DBMS";
    ///////////////////// PARTIAL ORDER
    private static Logger logger = LoggerFactory.getLogger(DAOEGTask.class);
    private DAOXmlUtility daoUtility = new DAOXmlUtility();
    private DAOEGTaskConfiguration daoEGTaskConfiguration = new DAOEGTaskConfiguration();
    private TransformFilePaths filePathTransformator = new TransformFilePaths();
    private ParserMainMemoryDatabase parserMainMemoryDatabase = new ParserMainMemoryDatabase();
    private String fileTask;

    public EGTask loadTask(String fileTask) {
        this.fileTask = fileTask;
        try {
            EGTask task = new EGTask(fileTask);
            Document document = daoUtility.buildDOM(fileTask);
            Element rootElement = document.getRootElement();
            //CONFIGURATION
            Element configurationElement = rootElement.getChild("configuration");
            EGTaskConfiguration configuration = daoEGTaskConfiguration.loadConfiguration(configurationElement, fileTask);
            task.setConfiguration(configuration);
            //SOURCE
            Element sourceElement = rootElement.getChild("source");
            IDatabase sourceDatabase = loadDatabase(sourceElement, task);
            task.setSource(sourceDatabase);
            //TARGET
            Element targetElement = rootElement.getChild("target");
            IDatabase targetDatabase = loadDatabase(targetElement, task);
            task.setTarget(targetDatabase);
            //AUTHORITATIVE SOURCES
            Element authoritativeSourcesElement = rootElement.getChild("authoritativeSources");
            List<String> authoritativeSources = loadAuthoritativeSources(authoritativeSourcesElement);
            task.setAuthoritativeSources(authoritativeSources);
            //DEPENDENCIES
            Element dependenciesElement = rootElement.getChild("dependencies");
            loadDependecies(dependenciesElement, task);
            return task;
        } catch (Throwable ex) {
            logger.error(ex.getLocalizedMessage());
            ex.printStackTrace();
            String message = "Unable to load egtask from file " + fileTask;
            if (ex.getMessage() != null && !ex.getMessage().equals("NULL")) {
                message += "\n" + ex.getMessage();
            }
            throw new DAOException(message);
        }
    }

    private IDatabase loadDatabase(Element databaseElement, EGTask task) throws DAOException {
        if (databaseElement == null || databaseElement.getChildren().isEmpty()) {
            return new EmptyDB();
        }
        Element typeElement = databaseElement.getChild("type");
        if (typeElement == null) {
            throw new DAOException("Unable to load scenario from file " + fileTask + ". Missing tag <type>");
        }
        String databaseType = typeElement.getValue();
        if (DB_TYPE_MAINMEMORY.equalsIgnoreCase(databaseType)) {
            Element xmlElement = databaseElement.getChild("xml");
            if (xmlElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileTask + ". Missing tag <xml>");
            }
            String schemaRelativeFile = xmlElement.getChild("xml-schema").getValue();
            String schemaAbsoluteFile = filePathTransformator.expand(fileTask, schemaRelativeFile);
            String instanceRelativeFile = xmlElement.getChild("xml-instance").getValue();
            String instanceAbsoluteFile = null; //Optional field
            if (instanceRelativeFile != null && !instanceRelativeFile.trim().isEmpty()) {
                instanceAbsoluteFile = filePathTransformator.expand(fileTask, instanceRelativeFile);
            }
            return parserMainMemoryDatabase.loadXMLScenario(schemaAbsoluteFile, instanceAbsoluteFile);
        } else if (DB_TYPE_MAINMEMORY_GENERATE.equalsIgnoreCase(databaseType)) {
            Element xmlElement = databaseElement.getChild("generate");
            if (xmlElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileTask + ". Missing tag <generate>");
            }
            String plainInstance = xmlElement.getValue();
            return parserMainMemoryDatabase.loadPlainScenario(plainInstance);
        } else if (DB_TYPE_DBMS.equalsIgnoreCase(databaseType)) {
            Element dbmsElement = databaseElement.getChild("access-configuration");
            if (dbmsElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileTask + ". Missing tag <access-configuration>");
            }
            AccessConfiguration accessConfiguration = new AccessConfiguration();
            accessConfiguration.setDriver(dbmsElement.getChildText("driver").trim());
            accessConfiguration.setUri(dbmsElement.getChildText("uri").trim());
            accessConfiguration.setSchemaName(dbmsElement.getChildText("schema").trim());
            accessConfiguration.setLogin(dbmsElement.getChildText("login").trim());
            accessConfiguration.setPassword(dbmsElement.getChildText("password").trim());
            Element initDbElement = databaseElement.getChild("init-db");
            DBMSDB database = new DBMSDB(accessConfiguration, task);
            if (initDbElement != null) {
                database.getInitDBConfiguration().setInitDBScript(initDbElement.getValue());
            }
            Element importXmlElement = databaseElement.getChild("import-xml");
            if (importXmlElement != null) {
                Attribute createTableAttribute = importXmlElement.getAttribute("createTables");
                if (createTableAttribute != null) {
                    database.getInitDBConfiguration().setCreateTablesFromXML(Boolean.parseBoolean(createTableAttribute.getValue()));
                }
                for (Object inputFileObj : importXmlElement.getChildren("input")) {
                    Element inputFileElement = (Element) inputFileObj;
                    String xmlFile = inputFileElement.getText();
                    xmlFile = filePathTransformator.expand(fileTask, xmlFile);
                    database.getInitDBConfiguration().addXmlFileToImport(xmlFile);
                }
            }
            return database;
        } else {
            throw new DAOException("Unable to load scenario from file " + fileTask + ". Unknown database type " + databaseType);
        }
    }

    private void loadDependecies(Element dependenciesElement, EGTask task) throws DAOException {
        if (dependenciesElement == null) {
            return;
        }
        String dependenciesString = dependenciesElement.getValue().trim();
        ParseDependencies generator = new ParseDependencies();
        try {
            generator.generateDependencies(dependenciesString, task);
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> loadAuthoritativeSources(Element authoritativeSourcesElement) {
        if (authoritativeSourcesElement == null || authoritativeSourcesElement.getChildren().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<String> sources = new ArrayList<String>();
        List<Element> sourceElements = authoritativeSourcesElement.getChildren("source");
        for (Element sourceElement : sourceElements) {
            sources.add(sourceElement.getText());
        }
        return sources;
    }
}
