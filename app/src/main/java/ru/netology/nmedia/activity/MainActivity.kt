package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.PostViewModel
import ru.netology.nmedia.R
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

        val adapter = PostAdapter(
            { post -> viewModel.likeById(post.id) },
            { post -> viewModel.shareById(post.id) }
        )

        binding.postList.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
    }

}
