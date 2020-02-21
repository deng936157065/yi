package com.leyou.goods.service;

public interface IGoodsHtmlService {

    /**
     * 创建html页面
     *
     * @param spuId
     * @throws Exception
     */
    void createHtml(Long spuId);

    /**
     * 新建线程处理页面静态化
     * @param spuId
     */
    void asyncExcute(Long spuId);

    /**
     * 删除业面的方法
     * @param id
     */
    void deleteHtml(Long id);
}
