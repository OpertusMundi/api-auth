package eu.opertusmundi.api_auth.model;

@lombok.Getter
@lombok.AllArgsConstructor(staticName = "of")
public class OwsServiceInfo
{
    final OwsServiceType serviceType;
    
    final String serviceVersion;
    
    /**
     * The service-specific request, e.g "GetMap" for a WMS request.
     */
    final String request;

    @Override
    public String toString()
    {
        return String.format("OwsServiceInfo [serviceType=%s, serviceVersion=%s, request=%s]", 
            serviceType, serviceVersion, request);
    }
}
