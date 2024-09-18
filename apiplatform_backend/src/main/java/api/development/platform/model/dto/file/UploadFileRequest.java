package api.development.platform.model.dto.file;

import java.io.Serializable;
import lombok.Data;

/**
 * 文件上传请求
 */
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 业务(上传文件的用途)
     */
    private String biz;

    /**
     * 序列化id
     */
    private static final long serialVersionUID = 1L;
}