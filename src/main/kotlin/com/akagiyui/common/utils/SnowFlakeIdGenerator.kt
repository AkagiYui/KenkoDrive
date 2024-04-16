package com.akagiyui.common.utils

import cn.hutool.core.lang.Snowflake
import cn.hutool.core.util.IdUtil
import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.io.Serializable

/**
 * 雪花算法ID生成器
 *
 * @author AkagiYui
 */
class SnowFlakeIdGenerator : IdentifierGenerator {
    companion object {
        /**
         * 雪花算法生成器
         */
        private val SNOWFLAKE: Snowflake = IdUtil.getSnowflake()
    }

    /**
     * 生成ID
     *
     * @param sharedSessionContractImplementor Hibernate实现
     * @param o 实体类
     * @return ID
     * @throws HibernateException Hibernate异常
     */
    @Throws(HibernateException::class)
    override fun generate(sharedSessionContractImplementor: SharedSessionContractImplementor, o: Any): Serializable {
        return SNOWFLAKE.nextIdStr()
    }
}
