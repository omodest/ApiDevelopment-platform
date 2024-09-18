package api.development.platform.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import api.development.platform.config.CosClientConfig;
import java.io.File;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Cos 对象存储操作
 */
@Component
public class CosManager {

    /**
     * 腾讯云存储配置
     */
    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 腾讯云存储 操作对象
     */
    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     * @param key 唯一键
     * @param localFilePath 本地文件路径
     * @return 上传操作的结果
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        // 定义上传操作的详细信息，包括目标桶、对象键和本地文件。
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        // 调用 putObject 方法，并传入之前创建的 PutObjectRequest 对象来执行上传操作。
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     * @param key 唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        // 定义上传操作的详细信息，包括目标桶、对象键和本地文件。
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 调用 putObject 方法，并传入之前创建的 PutObjectRequest 对象来执行上传操作。
        return cosClient.putObject(putObjectRequest);
    }
}
