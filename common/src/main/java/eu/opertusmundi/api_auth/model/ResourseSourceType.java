package eu.opertusmundi.api_auth.model;

/**
 * Enumeration of a source type of an asset resource
 * 
 * @see https://github.com/OpertusMundi/java-commons/blob/master/src/main/java/eu/opertusmundi/common/model/asset/EnumResourceSource.java
 */
public enum ResourseSourceType 
{
    /**
     * Source not available
     */
    NONE,
    
    /**
     * Resource was copied from the parent data source.
     */
    PARENT_DATASOURCE,
    
    /**
     * Resource is copied from the user's file system
     */
    FILE_SYSTEM,
    
    /**
     * File was uploaded by the user
     */
    UPLOAD,
    ;
}