package ru.netology.nmedia.activity

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import kotlin.math.floor

//создаёт вьюшку для каждого поста из набора, для отображения на экране в виде списка
//по идее, можно было затолкать в один класс с адаптером, поскольку это, в некотором роде, его дочка
class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: (Post) -> Unit,
    private val onShareListener: (Post) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        updateView(post, binding)
        setListeners(binding, post)
    }

    //суём в разметку (через binding) данные, прилетевшие из post
    private fun updateView(post: Post, binding: CardPostBinding) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            postLikesCount.text = parseCount(post.likes)
            postSharesCount.text = parseCount(post.shares)
            postViewsCount.text = parseCount(post.views)
            postLikesIcon.setImageResource(
                if (post.likedByMe) R.drawable.ic_like_blue_24 else R.drawable.ic_like_gray_24
            )
        }
    }

    //назначаем обработчики на иконки (а, заодно, и на текст рядом с ними) лайка и шары
    private fun setListeners(binding: CardPostBinding, post: Post) {
        binding.apply {
            postLikesIcon.setOnClickListener { onLikeListener(post) }
            postLikesCount.setOnClickListener { onLikeListener(post) }
            postSharesIcon.setOnClickListener { onShareListener(post) }
            postSharesCount.setOnClickListener { onShareListener(post) }
        }
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