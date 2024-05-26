package com.akagiyui.drive.repository.cache

import com.akagiyui.drive.entity.cache.UploadTask
import org.springframework.data.repository.CrudRepository

/**
 *
 * @author AkagiYui
 */
interface UploadTaskRepository : CrudRepository<UploadTask, String> {
}
