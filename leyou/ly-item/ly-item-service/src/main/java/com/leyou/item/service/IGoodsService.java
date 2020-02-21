package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.dto.CartDto;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuBo;
import com.leyou.item.pojo.SpuDetail;

import java.util.List;

public interface IGoodsService {

    PageResult<Spu> querySpuByPageAndSort(Integer page, Integer rows, String key, Boolean saleable);

    void save(SpuBo spu);

    SpuDetail querySpuDetailBySpuId(Long id);

    List<Sku> querySkuBySpuIds(Long id);

    void update(SpuBo spu);

    Spu querySpuBySpuId(Long id);

    /**
     * 封装消息
     * @param id
     * @param type
     */
    void sendMessage(Long id, String type);

    /**
     * 根据spuId查询spu及skus
     * @param ids
     * @return
     */
    List<Sku> querySkusByIds(List<Long> ids);

    /**
     * 减库存
     * @param cartDtos
     */
    void decreaseStock(List<CartDto> cartDtos);
}
