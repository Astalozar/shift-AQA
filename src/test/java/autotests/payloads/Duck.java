package autotests.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Duck {
    @JsonProperty
    private String id;

    @JsonProperty
    private String color;

    @JsonProperty
    private double height;

    @JsonProperty
    private String material;

    @JsonProperty
    private String sound;

    @JsonProperty
    private WingsState wingsState;

    public enum WingsState {
        ACTIVE,
        FIXED,
        UNDEFINED
    }
}
