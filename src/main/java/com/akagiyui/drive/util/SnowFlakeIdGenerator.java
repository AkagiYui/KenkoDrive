package com.akagiyui.drive.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * 雪花算法ID生成器
 * @author AkagiYui
 */
@SuppressWarnings("unused")
public class SnowFlakeIdGenerator implements IdentifierGenerator {
    /**
     * 雪花算法生成器
     */
    static Snowflake snowflake = IdUtil.getSnowflake();

    /**
     * 生成ID
     * @param sharedSessionContractImplementor Hibernate实现
     * @param o 实体类
     * @return ID
     * @throws HibernateException Hibernate异常
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return snowflake.nextIdStr();
    }
}
