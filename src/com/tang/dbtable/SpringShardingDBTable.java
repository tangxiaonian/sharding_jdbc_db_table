package com.tang.dbtable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Classname SpringShardingDBTable
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/1/11 10:06
 * @Created by ASUS
 */
@SpringBootApplication
@MapperScan("com.tang.dbtable.mapper")
public class SpringShardingDBTable {

    public static void main(String[] args) {

        SpringApplication.run(SpringShardingDBTable.class, args);

    }

}