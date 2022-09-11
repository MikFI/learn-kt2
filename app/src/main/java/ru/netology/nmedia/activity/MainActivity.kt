package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.nmedia.AndroidUtils
import ru.netology.nmedia.Post
import ru.netology.nmedia.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //создаём (генерируем) класс со списком привязок для элементов нашей вёрстки
        //через которые мы и будем их обрабатывать
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //создаём viewModel, в которой будут производиться все действия с данными
        //здесь же, в activity, мы их только отображаем
        val viewModel: PostViewModel by viewModels()
        val adapter = PostAdapter(listener = PostInteraction(viewModel))

        var topElement = 0
        var lastElementCount = 0

        //получаем список постов и перематываем на самый верх в случае,
        //если количество постов увеличилось на 1 в сравнении с прошлым обновлением
        //(т.е. если только что добавили 1 пост)
        binding.postList.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
            topElement = posts.size
            if (topElement > lastElementCount){
                binding.postList.scrollToPosition(0)
            }
            lastElementCount = topElement
        }

        //id поста, находящегося в поле для редактирования
        //(для реализации костыля, реагирующего на удаление поста во время редактирования)
        var activePostId = 0L

        var inputFocused = false

        //следим за "транслятором" - если он изменился (выбрано "редактировать" у поста и,
        //соответственно, во "временном" посте появилось то, что нужно отредактировать),
        //то включаем поле ввода, клавиатуру и копируем туда содержимое "временного" поста
        //(при этом реагируем только на редактирование, а не на создание - т.е. id не должен быть передан)
        viewModel.tempPost.observe(this) {
            if (it.id == 0L) {
                return@observe
            }

            activePostId = it.id
            binding.apply {
                newContent.setText(it.content)
                actionText.text = it.content
                groupEditAction.visibility = View.VISIBLE
                actionTypeCreate.visibility = View.GONE
                inputFocused = true
                newContent.requestFocus()
                AndroidUtils.showKeyboard(newContent)
            }
        }

        //если пост удаляется во время редактирования,
        //то все поля очищаются и фокус ввода сбрасывается
        viewModel.deleted.observe(this) {
            if (it == activePostId) {
                binding.apply {
                    newContent.setText("")
                    inputFocused = false
                    newContent.clearFocus()
                    groupEditAction.visibility = View.GONE
                    AndroidUtils.hideKeyboard(newContent)
                }
                viewModel.cleanPostData()
            } else {
                return@observe
            }
        }

        binding.buttonSendPost.setOnClickListener {
            binding.apply {
                val text = newContent.text?.toString()?.trim()
                if (text.isNullOrBlank()) {
                    val emptyString = resources.getString(R.string.empty_content_error)
                    Toast.makeText(newContent.context, emptyString, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.changeContent(text)
                viewModel.sendPost()
                newContent.setText("")
                inputFocused = false
                newContent.clearFocus()
                groupEditAction.visibility = View.GONE
                AndroidUtils.hideKeyboard(newContent)
            }
        }

        binding.buttonCancelEdit.setOnClickListener {
            binding.apply {
                newContent.setText("")
                inputFocused = false
                newContent.clearFocus()
                groupEditAction.visibility = View.GONE
                AndroidUtils.hideKeyboard(newContent)
            }
            viewModel.cleanPostData()
        }

        binding.newContent.setOnFocusChangeListener { view, b ->
            if (inputFocused) {
                return@setOnFocusChangeListener
            }
            if (binding.newContent.isFocused) {
                binding.apply {
                    groupEditAction.visibility = View.VISIBLE
                    actionTypeEdit.visibility = View.GONE
                    actionText.visibility = View.GONE
                }
            }
        }
    }
}

class PostInteraction(viewModel: PostViewModel) : OnInteractionListener {
    private val v = viewModel

    override fun like(post: Post) {
        v.likeById(post.id)
    }

    override fun share(post: Post) {
        v.shareById(post.id)
    }

    override fun edit(post: Post) {
        v.edit(post)
    }

    override fun remove(post: Post) {
        v.removeById(post.id)
    }
}
