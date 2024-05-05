package com.akagiyui.drive.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpRange

/**
 * HttpRange 测试
 * @author AkagiYui
 */

class HttpRangeTests {

    @Test
    fun `have start and end`() {
        val ranges = HttpRange.parseRanges("bytes=0-3")
        val range: HttpRange? = ranges.firstOrNull()
        assertNotNull(range)
        range as HttpRange
        println(range)

        // 内容比范围小 [start, end, EOF]
        var mediaLength = 2L
        var start = range.getRangeStart(mediaLength)
        var end = range.getRangeEnd(mediaLength)
        var length = end - start + 1
        println("start: $start, end: $end, length: $length, size: $mediaLength")
        assertEquals(0, start)
        assertEquals(1, end)
        assertEquals(2, length)

        // 内容比范围大 [start, end, EOF]
        mediaLength = 10L
        start = range.getRangeStart(mediaLength)
        end = range.getRangeEnd(mediaLength)
        length = end - start + 1
        println("start: $start, end: $end, length: $length, size: $mediaLength")
        assertEquals(0, start)
        assertEquals(3, end)
        assertEquals(4, length)
    }

    @Test
    fun `have start and no end`() {
        val ranges = HttpRange.parseRanges("bytes=2-")
        val range: HttpRange? = ranges.firstOrNull()
        assertNotNull(range)
        range as HttpRange
        println(range)

        // start 后有内容 [start, end=EOF]
        var mediaLength = 10L
        var start = range.getRangeStart(mediaLength)
        var end = range.getRangeEnd(mediaLength)
        var length = end - start + 1
        println("start: $start, end: $end, length: $length, size: $mediaLength")
        assertEquals(2, start)
        assertEquals(9, end)
        assertEquals(8, length)

        // start 后无内容 [EOF, start, end]
        mediaLength = 2L
        start = range.getRangeStart(mediaLength)
        end = range.getRangeEnd(mediaLength)
        length = end - start + 1
        println("start: $start, end: $end, length: $length, size: $mediaLength")
        assertEquals(2, start)
        assertEquals(1, end)
        assertEquals(0, length)
    }

    @Test
    fun `no start and have end`() {
        val ranges = HttpRange.parseRanges("bytes=-3")
        val range: HttpRange? = ranges.firstOrNull()
        assertNotNull(range)
        range as HttpRange
        println(range)

        // 内容比范围小 [start, EOF, end]
        var mediaLength = 2L
        var start = range.getRangeStart(mediaLength)
        var end = range.getRangeEnd(mediaLength)
        var length = end - start + 1
        println("start: $start, end: $end, length: $length, size: $mediaLength")
        assertEquals(0, start)
        assertEquals(1, end)
        assertEquals(2, length)

        // 内容比范围大 [start, end, EOF]
        mediaLength = 10L
        start = range.getRangeStart(mediaLength)
        end = range.getRangeEnd(mediaLength)
        length = end - start + 1
        println("start: $start, end: $end, length: $length, size: $mediaLength")
        assertEquals(7, start)
        assertEquals(9, end)
        assertEquals(3, length)
    }
}
