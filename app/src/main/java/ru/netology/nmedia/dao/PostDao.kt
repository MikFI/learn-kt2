package ru.netology.nmedia.dao

import ru.netology.nmedia.Post

interface PostDao {
    fun getAll(): List<Post>
    fun save(post: Post): Post
    fun initSave(post: Post)
    fun likeById(id: Long)
    fun removeById(id: Long)
}
