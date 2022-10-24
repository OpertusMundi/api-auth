package eu.opertusmundi.api_auth.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NaturalId;

@lombok.Setter
@lombok.Getter
@Entity(name = "AssetResource")
@Immutable
@Table(schema = "file", name = "`asset_resource`")
public class AssetResourceEntity
{
    @Id
    @Column(name = "`id`", updatable = false)
    private Integer id;

    @NotNull
    @NaturalId
    @Column(name = "key", updatable = false, columnDefinition = "uuid")
    private UUID key;
    
    // Todo
}