package com.leyou.goods.service.impl;

import com.leyou.goods.service.IGoodsHtmlService;
import com.leyou.goods.service.IGoodsService;
import com.leyou.goods.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class GoodsHtmlServiceImpl implements IGoodsHtmlService {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private TemplateEngine templateEngine;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHtmlServiceImpl.class);

    /**
     * 创建html页面
     *
     * @param spuId
     * @throws Exception
     */
    public void createHtml(Long spuId) {
        PrintWriter writer = null;
        try {
            //获取页面数据
            Map<String, Object> spuMap = goodsService.loadModel(spuId);
            // 创建thymeleaf上下文对象
            Context context = new Context();
            // 把数据放入上下文对象
            context.setVariables(spuMap);
            // 创建输出流
            File file = new File("d:\\item\\" + spuId + ".html");
            writer = new PrintWriter(file);
            // 执行页面静态化方法
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            LOGGER.error("页面静态化出错：{}，"+ e, spuId);
        }finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(()->createHtml(spuId));
          /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }

    @Override
    public void deleteHtml(Long id) {
        File file = new File("D:\\item\\", id + ".html");
        file.deleteOnExit();
    }


}
