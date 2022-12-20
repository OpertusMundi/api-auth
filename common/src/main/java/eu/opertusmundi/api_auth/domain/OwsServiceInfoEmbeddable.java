package eu.opertusmundi.api_auth.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import eu.opertusmundi.api_auth.model.OwsServiceInfo;
import eu.opertusmundi.api_auth.model.OwsServiceType;

/**
 * @see {@link OwsServiceInfo}
 */
@Embeddable
@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class OwsServiceInfoEmbeddable
{
    @Enumerated(EnumType.STRING)
    @Column(name = "`ows_service_type`", columnDefinition = "ows_service_type_t")
    OwsServiceType serviceType;
    
    @Column(name = "`ows_service_version`")
    String serviceVersion;
    
   
    @Column(name = "`ows_request`")
    String request;
    
    public OwsServiceInfoEmbeddable(OwsServiceInfo serviceInfo)
    {
        this.serviceType = serviceInfo.getServiceType();
        this.serviceVersion = serviceInfo.getServiceVersion();
        this.request = serviceInfo.getRequest();
    }
}
