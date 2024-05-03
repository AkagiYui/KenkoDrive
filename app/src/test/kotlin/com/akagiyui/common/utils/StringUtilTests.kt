package com.akagiyui.common.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * StringUtil 测试类
 * @author AkagiYui
 */
class StringUtilTests {

    @Test
    fun ellipsis() {
        Assertions.assertEquals("ab", "abc".ellipsis(2))
        Assertions.assertEquals("abc", "abc".ellipsis(5))
        Assertions.assertEquals("ab...", "abcdef".ellipsis(5))
    }

    @Test
    fun compressPackageName() {
        Assertions.assertEquals("abc", "abc".compressPackageName())
        Assertions.assertEquals("a.d.ghi", "abc.def.ghi".compressPackageName())
    }

    @Test
    fun hasText() {
        Assertions.assertTrue("abc".hasText())
        Assertions.assertFalse("".hasText())
        Assertions.assertFalse(" ".hasText())
        Assertions.assertFalse("\t".hasText())
        Assertions.assertFalse("\n".hasText())
        Assertions.assertFalse(null.hasText())
    }

    @Test
    fun toCamelCase() {
        Assertions.assertEquals("userName", "user_name".toCamelCase())
        Assertions.assertEquals("userName", "USER_NAME".toCamelCase())
    }

    @Test
    fun toUnderscoreCase() {
        Assertions.assertEquals("user_name", "userName".toUnderscoreCase())
        Assertions.assertEquals("user_name", "UserName".toUnderscoreCase())
        Assertions.assertEquals("USER_NAME", "userName".toUnderscoreCase(true))
    }
}
