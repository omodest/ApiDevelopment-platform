package api.development.platform.model.dto.InterfaceInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
//
//{
//        "id": "26",
//        "requestParams":
//            "[\n    " +
//                "{\n      \"fieldName\": \"aries\",\n      \"value\": \"aries\"\n    },\n    " +
//                "{\n      \"fieldName\": \"time\",\n      \"value\": \"today\"\n    }\n  ]"
//}

//{
//        "id": "24"
//}

/**
 * 更新请求
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 接口id
     */
    private Long id;
    /**
     * 请求参数
     */
//    private String requestParams;

    /**
     * 请求参数
     */
    private List<InterfaceInfoInvokeRequest.Field> requestParams;

    @Data
    public static class Field {

        private String fieldName;

        private String value;
    }

    private static final long serialVersionUID = 1L;


}