package eu.opertusmundi.api_auth.model;

public enum TopicCategory
{
    BIOTA("Biota"), 
    BOUNDARIES("Boundaries"), 
    CLIMA("Climatology / Meteorology / Atmosphere"), 
    ECONOMY("Economy"), 
    ELEVATION("Elevation"), 
    ENVIRONMENT("Environment"), 
    FARMING("Farming"), 
    GEO_SCIENTIFIC("Geoscientific Information"), 
    HEALTH("Health"), 
    IMAGERY("Imagery / Base Maps / Earth Cover"), 
    INLAND_WATERS("Inland Waters"), 
    INTELLIGENCE_MILITARY("Intelligence / Military"), 
    LOCATION("Location"), 
    OCEANS("Oceans"), 
    PLANNING_CADASTRE("Planning / Cadastre"), 
    SOCIETY("Society"), 
    STRUCTURE("Structure"), 
    TRANSPORTATION("Transportation"), 
    UTILITIES_COMMUNICATION("Utilities / Communication"),
    ;

    @lombok.Getter
    private final String value;

    private TopicCategory(String value) 
    {
        this.value = value;
    }

    public static TopicCategory fromString(String value) 
    {
        for (TopicCategory c: TopicCategory.values())
            if (c.value.equalsIgnoreCase(value))
                return c;
        return null;
    }
}
