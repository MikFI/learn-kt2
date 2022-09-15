package ru.netology.nmedia.activity

import android.content.Intent
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

        //втыкаем взаимодействие с постом (лайк, шара, редактирование) через функцию,
        //которая обретается где-то ниже
        val adapter = PostAdapter(listener = PostInteraction(v = viewModel, view = binding.root))

        //получаем список постов и перематываем на самый верх в случае,
        //если количество постов увеличилось на 1 в сравнении с прошлым обновлением
        //(т.е. если только что добавили 1 пост)
        binding.postList.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val newPost = posts.size > adapter.currentList.size
            adapter.submitList(posts) {
                if (newPost) {
                    binding.postList.scrollToPosition(0)
                }
            }
        }

        //id поста, находящегося в поле для редактирования
        //(для реализации костыля, реагирующего на удаление поста во время редактирования)
        var activePostId = 0L

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
                    newContent.clearFocus()
                    groupEditAction.visibility = View.GONE
                    AndroidUtils.hideKeyboard(newContent)
                }
                viewModel.cleanPostData()
            } else {
                return@observe
            }
        }

        //обработчик нажатия кнопки "отправить", что рядом с полем ввода
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
                newContent.clearFocus()
                groupEditAction.visibility = View.GONE
                AndroidUtils.hideKeyboard(newContent)
            }
        }

        //обработчик кнопки "закрыть" в менюшке над полем ввода
        binding.buttonCancelEdit.setOnClickListener {
            binding.apply {
                newContent.setText("")
                newContent.clearFocus()
                groupEditAction.visibility = View.GONE
                AndroidUtils.hideKeyboard(newContent)
            }
            viewModel.cleanPostData()
        }

        //отображение корректного текста (создание/редактирование сообщения)
        //в менюшке над полем ввода
        binding.newContent.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.apply {
                    groupEditAction.visibility = View.VISIBLE
                    actionTypeEdit.visibility = View.GONE
                    actionText.visibility = View.GONE
                }
            }
        }
    }
}

class PostInteraction(private val v: PostViewModel, private val view: View) :
    OnInteractionListener {
    override fun like(post: Post) {
        v.likeById(post.id)
    }

    override fun share(post: Post) {
        v.shareById(post.id)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }
        val shareIntent =
            Intent.createChooser(intent, view.resources.getString(R.string.share_post))
        view.context.startActivity(shareIntent)
    }

    override fun edit(post: Post) {
        v.edit(post)
    }

    override fun remove(post: Post) {
        v.removeById(post.id)
    }
}
