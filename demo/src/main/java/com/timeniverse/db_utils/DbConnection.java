package com.timeniverse.db_utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.ConnectionString;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DbConnection {
    private static final Logger LOG = Logger.getLogger(DbConnection.class.getName());
    public static String SLASH = "/";
    private static MongoDatabase database = openConnection();
    private static MongoClient mongoClient;

    public enum DB_TABLE_TYPE {
        USERINFO("UserInfo"),
        FOLDERINFO("FolderInfo"),
        TASKINFO("TaskInfo");

        String dbName;

        DB_TABLE_TYPE(String dbName){
            this.dbName = dbName;
        }

        @Override
        public String toString(){
            return this.dbName;
        }

    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static void setMongoClient(MongoClient mongoClient) {
        DbConnection.mongoClient = mongoClient;
    }

    private static MongoDatabase openConnection() {
        try {
            Properties properties = new Properties();
            String path = System.getProperty("user.dir");
            if(!path.contains("demo")){
                path +=  SLASH + "demo";
            }
            Path propFile = Paths.get(path + SLASH + "src" + SLASH + "main"
                    + SLASH + "resources" + SLASH + "application.properties");
            properties.load(Files.newBufferedReader(propFile));
            ConnectionString connectionString = new ConnectionString(
                    "mongodb+srv://ar1516:" + properties.getProperty("mongodb.password")
                            + "@cluster0.uc6bhgt.mongodb.net/?retryWrites=true&w=majority");
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .build();
            MongoClient mongoClient = MongoClients.create(settings);
            setMongoClient(mongoClient);
            MongoDatabase database = mongoClient.getDatabase("Time-ni-verse");
            return database;
        } catch (Exception ex) {
            LOG.severe("Exception is ::: " + ex);
        }
        return null;
    }

    public static void closeConnection() {
        if(mongoClient != null){
            mongoClient.close();
        }
    }

    public static void insertUserInfo(String username, String password, String email) {
        MongoCollection<Document> collection = database.getCollection(DB_TABLE_TYPE.USERINFO.toString());
        Document document = new Document("id", getNextDataId(DB_TABLE_TYPE.USERINFO))
                .append("username", username)
                .append("password", password)
                .append("email", email);
        collection.insertOne(document);
    }

    private static Integer getNextDataId(DB_TABLE_TYPE dbTableType) {
        JSONArray dataInfo = new JSONArray();
        switch (dbTableType) {
            case USERINFO:
                dataInfo = getUserInfo();
                break;
            case FOLDERINFO:
                dataInfo = getFolderInfo();
                break;
            case TASKINFO:
                dataInfo = getTaskInfo();
                break;
            default:
                break;
        }
        Iterator<Object> dataInfoIter = dataInfo.iterator();
        Integer id = 0;
        while (dataInfoIter.hasNext()) {
            JSONObject data = (JSONObject) dataInfoIter.next();
            if (Integer.parseInt(data.get("id").toString()) >= id) {
                id = Integer.parseInt(data.get("id").toString());
            }
        }
        return id + 1;
    }

    public static JSONArray getUserInfo() {
        MongoCollection<Document> collection = database.getCollection(DB_TABLE_TYPE.USERINFO.toString());
        JSONArray data = new JSONArray();
        FindIterable<Document> iterDoc = collection.find();
        Iterator<Document> it = iterDoc.iterator();
        while (it.hasNext()) {
            Document document = it.next();
            JSONObject documentData = new JSONObject(document.toJson());
            documentData.remove("_id");
            data.put(documentData);
        }
        return data;
    }

    public static void insertFolderInfo(String foldername) {
        MongoCollection<Document> collection = database.getCollection(DB_TABLE_TYPE.FOLDERINFO.toString());
        Document document = new Document("id", getNextDataId(DB_TABLE_TYPE.FOLDERINFO))
                .append("name", foldername)
                .append("is_default", false)
                .append("order_number", getNextFolderOrderNo());
        collection.insertOne(document);
    }

    public static JSONArray getFolderInfo() {
        MongoCollection<Document> collection = database.getCollection(DB_TABLE_TYPE.FOLDERINFO.toString());
        JSONArray data = new JSONArray();
        FindIterable<Document> iterDoc = collection.find();
        Iterator<Document> it = iterDoc.iterator();
        while (it.hasNext()) {
            Document document = it.next();
            JSONObject documentData = new JSONObject(document.toJson());
            documentData.remove("_id");
            data.put(documentData);
        }
        return data;
    }

    private static Integer getNextFolderOrderNo() {
        JSONArray dataInfo = getFolderInfo();
        Iterator<Object> dataInfoIter = dataInfo.iterator();
        Integer orderNumber = 1;
        while (dataInfoIter.hasNext()) {
            JSONObject data = (JSONObject) dataInfoIter.next();
            if (Integer.parseInt(data.get("order_number").toString()) >= orderNumber) {
                orderNumber = Integer.parseInt(data.get("id").toString());
            }
        }
        return orderNumber + 1;
    }

    private static Boolean isFolderIdValid(Integer folderId){
        JSONArray dataInfo = getFolderInfo();
        Iterator<Object> dataInfoIter = dataInfo.iterator();
        while (dataInfoIter.hasNext()) {
            JSONObject data = (JSONObject) dataInfoIter.next();
            Integer id = Integer.parseInt(data.get("id").toString());
            if(id == folderId){
                return true;
            }
        }
        return false;
    }

    public static void insertTaskInfo(String taskname, String taskDescription, Integer folderId, Long timeToComplete, Long deadline, Boolean isPriority) throws Exception {
       if(!isFolderIdValid(folderId)){
        throw new Exception("Invalid Folder Id");
       }
        MongoCollection<Document> collection = database.getCollection(DB_TABLE_TYPE.TASKINFO.toString());
        Document document = new Document("id", getNextDataId(DB_TABLE_TYPE.TASKINFO))
                .append("folder_id", folderId)
                .append("name", taskname)
                .append("task_info", taskDescription)
                .append("time_to_complete", timeToComplete)
                .append("deadline_timestamp", deadline)
                .append("is_completed", false)
                .append("is_priority", isPriority);
        collection.insertOne(document);
    }

    public static JSONArray getTaskInfo() {
        MongoCollection<Document> collection = database.getCollection(DB_TABLE_TYPE.TASKINFO.toString());
        JSONArray data = new JSONArray();
        FindIterable<Document> iterDoc = collection.find();
        Iterator<Document> it = iterDoc.iterator();
        JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
        while (it.hasNext()) {
            Document document = it.next();
            JSONObject documentData = new JSONObject(document.toJson(relaxed));
            documentData.remove("_id");
            data.put(documentData);
        }
        return data;
    }   

}
