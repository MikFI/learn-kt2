package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import ru.netology.nmedia.databinding.ActivityMainBinding
import kotlin.math.floor

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likes: Int = 999,
    var shares: Int = 100,
    var views: Int = 100,
    var likedByMe: Boolean = false,
)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //создаём (генерируем) класс со списком привязок для элементов нашей вёрстки
        //через которые мы и будем их обрабатывать
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPost()
        initView()
        setListeners()
    }

    private fun initPost() {
        post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов " +
                    "по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике " +
                    "и управлению. Мы растём сами и помогаем расти студентам: от новичков до " +
                    "уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в " +
                    "каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать " +
                    "быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → " +
                    "http://netolo.gy/fyb",
            published = "21 мая в 18:36"

        )
    }

    private fun initView() {
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

    private fun setListeners() {
        binding.apply {
            postLikesIcon.setOnClickListener(toggleLike())
            postLikesCount.setOnClickListener(toggleLike())
            postSharesIcon.setOnClickListener(clickShare())
            postSharesCount.setOnClickListener(clickShare())
        }
    }

    private fun toggleLike(): View.OnClickListener {
        return View.OnClickListener {
            post.likedByMe = !post.likedByMe
            if (post.likedByMe) {
                post.likes++
                binding.postLikesIcon.setImageResource(R.drawable.ic_like_blue_24)
                binding.postLikesCount.text = parseCount(post.likes)
            } else {
                post.likes--
                binding.postLikesIcon.setImageResource(R.drawable.ic_like_gray_24)
                binding.postLikesCount.text = parseCount(post.likes)
            }
        }
    }

    private fun clickShare(): View.OnClickListener {
        return View.OnClickListener {
            post.shares++
            binding.postSharesCount.text = parseCount(post.shares)
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