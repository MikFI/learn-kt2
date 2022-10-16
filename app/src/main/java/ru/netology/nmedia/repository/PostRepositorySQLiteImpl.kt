package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post
import ru.netology.nmedia.dao.PostDao

class PostRepositorySQLiteImpl(
    private val dao: PostDao,
    context: Context
) : PostRepository {
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)
    private val sharedPrefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)

    init {
        if (sharedPrefs.getBoolean("firstRun", true)){
            posts = PostDemoSet.posts.reversed()
            posts.forEach {
                dao.initSave(it)
            }
            sharedPrefs.edit().apply(){
                putBoolean("firstRun", false)
                apply()
            }
        }
        posts = dao.getAll()
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun save(post: Post) {
        val id = post.id
        val saved = dao.save(post)
        posts = if (id == 0L) {
            listOf(saved) + posts
        } else {
            posts.map {
                if (it.id != id) it else saved
            }
        }
        data.value = posts
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
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

    override fun removeById(id: Long) {
        dao.removeById(id)
        posts = posts.filter { it.id != id }
        data.value = posts
    }
}

