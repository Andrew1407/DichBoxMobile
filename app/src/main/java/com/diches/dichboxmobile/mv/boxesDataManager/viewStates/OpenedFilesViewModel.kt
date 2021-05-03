package com.diches.dichboxmobile.mv.boxesDataManager.viewStates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.Cleanable

class OpenedFilesViewModel : ViewModel(), Cleanable {
    private val data = MutableLiveData<List<BoxesContainer.OpenedFile>>()
    val liveData: LiveData<List<BoxesContainer.OpenedFile>> = data

    fun addNewFile(file: BoxesContainer.OpenedFile) {
        val newList = data.value?.map { it.copy(opened = false) }?.toMutableList() ?: mutableListOf()
        newList.add(0, file)
        if (newList.size > 10) newList.removeLast()
        data.value = newList.toList()
    }

    fun openFile(index: Int) {
        data.value = data.value?.mapIndexed { i, file ->
            file.copy(opened = i == index)
        }
    }

    fun closeFile(index: Int) {
        if (data.value == null) return
        val openedFiles = data.value!!.size
        if (openedFiles == 1) {
            data.value = null
            return
        }
        val wasOpened = data.value!![index].opened
        val newList = data.value!!.toMutableList()
        newList.removeAt(index)
        data.value = if (!wasOpened) newList else {
            val nextOpened = if (index + 1 >= openedFiles) index - 1 else index
            newList.mapIndexed { i, openedFile ->
                if (i == nextOpened) openedFile.copy(opened = true) else openedFile
            }
        }
    }

    fun editFile(name: String, filePath: String, edited: String) {
        data.value = data.value?.map {
            if (it.name == name && it.filePath === filePath) it.copy(edited = edited) else it
        }
    }

    fun setViewMode() {
        data.value = data.value?.map {
            if (it.edited != null) it.copy(edited = null) else it
        }
    }

    fun writeFiles(openedOnly: Boolean = false) {
        data.value = data.value?.map {
            var editable = it.edited != null
            if (openedOnly) editable = editable && it.opened
            if (editable) it.copy(src = it.edited!!, edited = null) else it
        }
    }

    fun renameFile(index: Int, name: String) {
        data.value = data.value?.mapIndexed { i, file ->
            if (i == index) file.copy(name = name) else file
        }
    }

    fun renamePaths(oldPath: String, newPath: String) {
        if (data.value == null) return
        data.value = data.value?.map {
            val currentPath = it.filePath
            if (currentPath.startsWith(oldPath)) {
                val unchangedPart = if (oldPath.length >= currentPath.length) ""
                else currentPath.slice(oldPath.indices)
                val modifiedPath = newPath + unchangedPart
                it.copy(filePath = modifiedPath)
            } else it
        }
    }

    fun closeByPath(dirPath: String) {
        if (data.value == null) return
        val leftPath = if (dirPath.startsWith('/')) dirPath else "/$dirPath"
        val remained = data.value!!.filter {
            val fullPath = it.filePath + '/' + it.name
            !fullPath.startsWith(leftPath)
        }
        if (remained.isEmpty()) {
            data.value = null
            return
        }
        val remainedOpened = remained
            .map { it.opened }
            .reduce { acc, opened -> acc || opened }
        data.value = if (remainedOpened) remained
            else remained.mapIndexed { index, openedFile ->
                if (index == 0) openedFile.copy(opened = true) else openedFile
            }
    }

    override fun clear() {
        if (data.value != null) data.value = null
    }
}
