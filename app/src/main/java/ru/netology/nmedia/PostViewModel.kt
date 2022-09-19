package ru.netology.nmedia

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl


//шаблон для создания нового поста
//если ничего, кроме content не было передано - пост именно в таком виде и
//улетит в хранилище (которое уже само бует разбираться, чего с этим делать)
private val emptyPost: Post = Post(
    id = 0,
    author = "Me",
    content = "",
    published = "now"
)

//viewModel связывает UI с репозиторием(хранилищем) и отвечает за обработку того, что будет отрисовано в UI
//UI просит данные для отрисовки из хранилища через этот класс
class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)

    //tempPost - "несуществующий", временный пост, пользователь его не видит,
    //заодно выступает классом транслирующим изменения себя всем, кто на него подписан
    //подписчики (observe) видят изменения этой хтони и реагируют
    //в нашем случае mainactivity реагирует на редактирование
    val tempPost = MutableLiveData(emptyPost)

    //сбрасываем содержимое поста после нажатия кнопки закрытия редактирования
    //чтобы новое сообщение после этого не вставлялось непойми куда посреди ленты
    fun cleanPostData() {
        tempPost.value = emptyPost
    }

    //костыль для случая, когда кнопка "удалить" была нажата во время редактирования поста
    //данная переменная хранит id удаляемого поста и как только происходит удаление, на это реагирует
    //активити и зачищает поля ввода, если id совпало с тем, что находилось на редактировании
    val deleted = MutableLiveData<Long>()

    //суём во временный пост то, что нам отправили на редактирование
    //потом оно будет изменено через changeContent--sendPost
    fun edit(post: Post) {
        tempPost.value = post
    }

    fun removeById(id: Long) {
        deleted.value = id
        repository.removeById(id)
    }

    //создаём/обновляем пост (посылаем временный пост в репозиторий - он сам разберётся),
    fun sendPost() {
        tempPost.value?.let {
            repository.save(it)
        }
        //зануляем пост после сохранения, чтобы в следующий пост,
        //прошедший через эту функцию, не просочились поля из текущего поста
        tempPost.value = emptyPost
    }

    //обновляем текст нашего временного поста
    //если он отличается от того, что там лежит в данный момент
    fun changeContent(text: String) {
        val newText = text.trim()
        if (newText == tempPost.value?.content) {
            return
        }
        tempPost.value = tempPost.value?.copy(content = newText)
    }
}
