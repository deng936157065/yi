package com.leyou.item.pojo;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long parentId;
    private Boolean isParent; // 注意isParent生成的getter和setter方法需要手动加上Is
    private Integer sort;
}
