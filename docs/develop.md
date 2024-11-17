# 开发笔记

## 添加 `系统设置` 的流程

在 `SettingKey` 类中添加一个新的枚举值，该枚举值的的名称将被存储于数据库中。
其拥有一个转换函数作为参数，用于将「设置值字符串」转换为对应的类型。
如果需要将字符串转换为 Int 类型，可以使用 `String.toInt()` 函数。

示例：

```kotlin
enum class SettingKey(val transform: (String) -> Any = { it }) {
    // ...
    NEW_SETTING({ it.toInt() }),
    // ...
}
```

在 `SettingService` 接口中添加一个可变的变量，用于在业务中获取和修改该设置的值，
该变量的名称可任意取，但建议与枚举值的名称保持一致（使用小驼峰命名）。

示例：

```kotlin
interface SettingService {
    // ...
    var newSetting: Int
    // ...
}
```

在 `SettingServiceImpl` 类中实现该变量的 getter 和 setter 方法。

示例：

```kotlin
@Service
class SettingServiceImpl(
    private val settingRepository: SettingRepository,
    cacheManager: CacheManager,
) : SettingService {
    // ...
    override var newSetting: Int
        get() = getSetting(SettingKey.NEW_SETTING, 0) // 默认值
        set(value) {
            saveSetting(SettingKey.NEW_SETTING, value)
        }
    // ...
}
```

如有「在获取全部设置的接口中包含该设置」的需要，可在该类的 `getSettings` 函数中添加该设置。

示例：

```kotlin
override fun getSettings(): Map<String, Any> {
    val rawMap = mutableMapOf(
        // ...
        SettingKey.NEW_SETTING.name to newSetting,
    )
    // ...
}
```
