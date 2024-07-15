package api.development.platform.service.impl;

import api.development.platform.model.entity.ProductOrder;
import api.development.platform.service.ProductOrderService;
import api.development.platform.mapper.ProductOrderMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author poise
* @description 针对表【product_order(商品订单)】的数据库操作Service实现
* @createDate 2024-07-15 15:45:07
*/
@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder>
    implements ProductOrderService {

}




