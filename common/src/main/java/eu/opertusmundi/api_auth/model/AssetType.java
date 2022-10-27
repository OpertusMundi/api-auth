package eu.opertusmundi.api_auth.model;

/**
 * Enumeration of relevant asset types.
 * 
 * <p>
 * <b>NOTE</b>: This is not a complete enumeration of supported asset types, as not all 
 * of them are relevant to auth-subrequest service.
 * 
 * @see https://github.com/OpertusMundi/java-commons/blob/master/src/main/java/eu/opertusmundi/common/model/catalogue/client/EnumAssetType.java
 * 
 */
public enum AssetType {
   
    SERVICE,
    
    TABULAR,
    
    VECTOR,
    
    RASTER;

}
