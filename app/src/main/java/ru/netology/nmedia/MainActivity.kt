package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import kotlin.math.floor

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
        viewModel.data.observe(this) { post ->
            updateView(post)
        }

        setListeners(viewModel)
    }

    private fun updateView(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            postLikesCount.text = parseCount(post.likes)
            postSharesCount.text = parseCount(post.shares)
            postViewsCount.text = parseCount(post.views)
            if (post.likedByMe) {
                postLikesIcon.setImageResource(R.drawable.ic_like_blue_24)
            }
        }
    }

    private fun setListeners(viewModel: PostViewModel) {
        binding.apply {
            postLikesIcon.setOnClickListener { viewModel.like() }
            postLikesCount.setOnClickListener { viewModel.like() }
            postSharesIcon.setOnClickListener { viewModel.share() }
            postSharesCount.setOnClickListener { viewModel.share() }
        }
    }

    private fun parseCount(likes: Int): String {
        var result = likes.toDouble()
        if (likes > 999) {
            if (likes > 999_999) {
                result /= 1000_000
                return if (likes > 9_999_999) Math.round(result).toString() + "M"
                else (floor(result * 10.0) / 10.0).toString() + "M"
            }
            result /= 1000
            return if (likes > 9_999) Math.round(floor(result)).toString() + "K"
            else (floor(result * 10.0) / 10.0).toString() + "K"
        }
        return likes.toString()
    }
}