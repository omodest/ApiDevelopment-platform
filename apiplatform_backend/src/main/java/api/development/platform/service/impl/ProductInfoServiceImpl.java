package api.development.platform.service.impl;


import api.development.platform.model.entity.ProductInfo;
import api.development.platform.service.ProductInfoService;
import api.development.platform.mapper.ProductInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author poise
* @description 针对表【product_info(产品信息)】的数据库操作Service实现
* @createDate 2024-07-15 11:45:02
*/
@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo>
    implements ProductInfoService{

}




