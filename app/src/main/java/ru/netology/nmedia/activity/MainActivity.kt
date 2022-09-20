package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
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
        val adapter = PostAdapter(listener = PostInteraction(vm = viewModel, view = binding.root))

        //назначаем SharedPref для хранения наших постов
        //просто для проверки, смысла оно особого не несёт (сама функция ниже)
//        setReadWrite()

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

        //регистрация контракта на добавление/изменение поста
        val activityPostLauncher = registerForActivityResult(NewPostActivity.Contract()) { text ->
            if (text.isNullOrBlank()) {
                Toast.makeText(this, R.string.empty_content_error, Toast.LENGTH_LONG).show()
                return@registerForActivityResult
            }
            viewModel.changeContent(text)
            viewModel.sendPost()
        }

        //следим за "транслятором" - если он изменился (выбрано "редактировать" у поста и,
        //соответственно, во "временном" посте появилось то, что нужно отредактировать),
        //то запускаем контракт на создание/редактирование поста
        viewModel.tempPost.observe(this) {
            if (it.id == 0L) {
                return@observe
            }
            activityPostLauncher.launch(it.content)
        }

        //запускаем контракт по добавлению поста на нажатие кнопки
        //он запускает createIntent, который запускает NewPostActivity
        binding.addPostButton.setOnClickListener {
            activityPostLauncher.launch(null)
        }
    }

    fun setReadWrite() {
        run {
            val preferences = getPreferences(Context.MODE_PRIVATE)
            preferences.edit().apply {
                putString("key", "value")
                commit()
            }
        }
        run {
            getPreferences(Context.MODE_PRIVATE)
                .getString("key", "no value")?.let {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
        }
    }
}

class PostInteraction(private val vm: PostViewModel, private val view: View) :
    OnInteractionListener {
    override fun like(post: Post) {
        vm.likeById(post.id)
    }

    override fun share(post: Post) {
        vm.shareById(post.id)
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
        vm.edit(post)
    }

    override fun remove(post: Post) {
        vm.removeById(post.id)
    }

    override fun playVideo(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val playIntent =
            Intent.createChooser(intent, view.resources.getString(R.string.share_post))
        view.context.startActivity(playIntent)
    }
}
