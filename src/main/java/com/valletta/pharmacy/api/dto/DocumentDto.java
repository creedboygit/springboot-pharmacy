package com.valletta.pharmacy.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("y")
    private double latitude;

    @JsonProperty("x")
    private double longitude;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
