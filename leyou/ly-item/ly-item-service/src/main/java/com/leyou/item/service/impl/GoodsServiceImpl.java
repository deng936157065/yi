package com.leyou.item.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.dto.CartDto;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.item.service.ICategoryService;
import com.leyou.item.service.IGoodsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private AmqpTemplate amqpTemplate;
    private static final Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);


    public PageResult<Spu> querySpuByPageAndSort(Integer page, Integer rows, String key, Boolean saleable) {
        //查询SPU
        //分页,最多允许100条
        PageHelper.startPage(page, Math.min(rows, 100));
        // 创建查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //是否过滤上下架
        if (saleable != null) {
            criteria.orEqualTo("saleable", saleable);
        }
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        Page<Spu> pageInfo = (Page<Spu>) this.spuMapper.selectByExample(example);
        List<Spu> list = pageInfo.getResult().stream().map(spu -> {
            //把Spu变成SpuBo
            SpuBo spuBo = new SpuBo();
            //属性拷贝
            BeanUtils.copyProperties(spu, spuBo);
            // 3、查询spu的商品分类名称,要查三级分类
            List<String> names = categoryService.queryNameByIds(
                    Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            // 将分类名称拼接后存入
            spuBo.setCname(StringUtils.join(names, "/"));
            // 4、查询spu的品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;

        }).collect(Collectors.toList());
        ;
        return new PageResult<>(pageInfo.getTotal(), list);
    }


    public void save(SpuBo spu) {
        //保存spu
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        this.spuMapper.insert(spu);
        //保存spu详情
        spu.getSpuDetail().setSpuId(spu.getId());
        this.spuDetailMapper.insert(spu.getSpuDetail());
        //保存sku和库存信息
        // 保存sku和库存信息
        saveSkuAndStock(spu.getSkus(), spu.getId());
        //发送MQ消息
        sendMessage(spu.getId(), "insert");
    }

    public SpuDetail querySpuDetailBySpuId(Long id) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }
        return spuDetail;
    }

    public List<Sku> querySkuBySpuIds(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }

        //查询库存
        for (Sku sku1 : skuList) {
            sku1.setStock(stockMapper.selectByPrimaryKey(sku1.getId()).getStock());

        }
        return skuList;
    }

    public void update(SpuBo spu) {
        // 查询以前sku
        List<Sku> skus = this.querySkuBySpuIds(spu.getId());
        // 如果以前存在，则删除
        if (!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            // 删除以前库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);

            // 删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spu.getId());
            this.skuMapper.delete(record);

        }
        // 新增sku和库存
        saveSkuAndStock(spu.getSkus(), spu.getId());

        // 更新spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spu);

        // 更新spu详情
        this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        //发送MQ消息
        this.sendMessage(spu.getId(), "update");

    }

    public Spu querySpuBySpuId(Long id) {
        //根据spuId查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);

        //查询spuDetail
        SpuDetail detail = querySpuDetailBySpuId(id);

        //查询skus
        List<Sku> skus = querySkuBySpuIds(id);

        spu.setSpuDetail(detail);
        spu.setSkus(skus);

        return spu;

    }

    public void sendMessage(Long id, String type) {
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            logger.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }
    }

    public List<Sku> querySkusByIds(List<Long> ids) {
        List skus = skuMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //填充库存
        fillStock(ids, skus);
        return skus;
    }

    private void fillStock(List<Long> ids, List<Sku> skus) {
        //批量查询库存
        List<Stock> stocks = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stocks)) {
            throw new LyException(ExceptionEnum.STOCK_NOT_FOUND);
        }
        //首先将库存转换为map，key为sku的ID
        Map<Long, Integer> map = stocks.stream().collect(Collectors.toMap(s -> s.getSkuId(), s -> s.getStock()));

        //遍历skus，并填充库存
        for (Sku sku : skus) {
            sku.setStock(map.get(sku.getId()));
        }
    }

    public void decreaseStock(List<CartDto> cartDtos) {
        for (CartDto cartDto : cartDtos) {
            int count = stockMapper.decreaseStock(cartDto.getSkuId(), cartDto.getNum());
            if (count != 1) {
                throw new LyException(ExceptionEnum.STOCK_NOT_ENOUGH);
            }
        }
    }


    private void saveSkuAndStock(List<Sku> skus, Long spuId) {
        for (Sku sku : skus) {
            if (!sku.getEnable()) {
                continue;
            }
            //保存sku
            sku.setSpuId(spuId);
            //默认不参与任何促销
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            // 保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insert(stock);
        }
    }
}
