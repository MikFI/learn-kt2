package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = PostDemoSet.posts
    private var nextId = posts.size.toLong()
    private val data = MutableLiveData(posts)
    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id == id) {
                val newLikes = it.likes
                if (!it.likedByMe) {
                    it.copy(likedByMe = true, likes = newLikes + 1)
                } else {
                    it.copy(likedByMe = false, likes = newLikes - 1)
                }
            } else it
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id == id) {
                it.copy(shares = it.shares + 1)
            } else it
        }
        data.value = posts
    }

    //создаём/обновляем пост
    override fun save(post: Post) {
        //если id = 0, то создаём пост
        if (post.id == 0L) {
            posts = listOf(
                //меняем только id - остальные поля нам передали
                post.copy(
                    id = nextId++
                )
            ) + posts
            data.value = posts
            return
        }

        //если id нам тоже передали (!= 0), то обновляем пост
        posts = posts.map{
            if(it.id == post.id){
                it.copy(content = post.content)
            } else {
                it
            }
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }
}
