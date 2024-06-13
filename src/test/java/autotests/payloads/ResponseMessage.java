package autotests.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class ResponseMessage {
    @JsonProperty
    private String timestamp;

    @JsonProperty
    private int status;

    @JsonProperty
    private String error;

    @JsonProperty
    private String message;

    @JsonProperty
    private String path;
}
