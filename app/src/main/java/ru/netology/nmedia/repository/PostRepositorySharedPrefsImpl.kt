package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.Post

class PostRepositorySharedPrefsImpl(
    context: Context,
) : PostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)

    //как мы будем получать данные из json
    //в данном случае на верхнем уровне у нас будет List, элементами которого будут Post
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "posts"
    private var nextId = 1L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)


    init {
        //заполняем sharedprefs демо-постами из прошлых занятий,
        //если в нём нет ключа firstRun
        val firstRun = prefs.getBoolean("firstRun", true)
        if (firstRun){
            posts = PostDemoSet.posts
            data.value = posts
            prefs.edit().apply(){
                putBoolean("firstRun", false)
                commit()
            }
            sync()
        }
        //тут получаем список постов из sharedprefs
        //(или выдаём пустой... если бы не было конструкции выше)
        prefs.getString(key, null)?.let {
            posts = gson.fromJson(it, type)
            data.value = posts
        }
    }

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
        sync()
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id == id) {
                it.copy(shares = it.shares + 1)
            } else it
        }
        data.value = posts
        sync()
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
            sync()
            return
        }

        //если id нам тоже передали (!= 0), то обновляем пост
        posts = posts.map {
            if (it.id == post.id) {
                it.copy(content = post.content)
            } else {
                it
            }
        }
        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }

    private fun sync() {
        with(prefs.edit()) {
            putString(key, gson.toJson(posts))
            apply()
        }
    }
}
