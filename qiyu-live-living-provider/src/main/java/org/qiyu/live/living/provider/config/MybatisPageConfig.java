package org.qiyu.live.living.provider.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPageConfig {

    /**
     * MyBatis-Plus 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {

        // MyBatis-Plus 总拦截器
        MybatisPlusInterceptor interceptor =
                new MybatisPlusInterceptor();

        // 分页内部拦截器，底层数据库是 MySQL
        PaginationInnerInterceptor paginationInterceptor =
                new PaginationInnerInterceptor(DbType.MYSQL);

        // 每页最多查询 500 条，避免一次查询过多数据
        paginationInterceptor.setMaxLimit(500L);

        // 优化带有 left join 的 count 查询
        paginationInterceptor.setOptimizeJoin(true);

        // 将分页拦截器加入 MyBatis-Plus 总拦截器
        interceptor.addInnerInterceptor(paginationInterceptor);

        return interceptor;
    }
}