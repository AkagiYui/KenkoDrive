package com.akagiyui.common.collection

/**
 * 循环缓冲区
 * @author AkagiYui
 */

class CircularBuffer<T>(private val capacity: Int) {
    private val buffer: Array<Any?> = Array(capacity) { null }
    private var head: Int = 0
    private var tail: Int = 0
    private var count: Int = 0

    fun append(item: T) {
        if (count == capacity) {
            head = (head + 1) % capacity
        } else {
            count++
        }
        buffer[tail] = item
        tail = (tail + 1) % capacity
    }

    fun getAll(): List<T> {
        return List(count) { i ->
            @Suppress("UNCHECKED_CAST")
            buffer[(head + i) % capacity] as T
        }
    }

    fun isFull(): Boolean = count == capacity

    fun isEmpty(): Boolean = count == 0
}
