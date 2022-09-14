package ru.netology.nmedia.adapter

import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import kotlin.math.floor

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        updateView(post, binding)
        setListeners(binding, post)
    }

    private fun updateView(post: Post, binding: CardPostBinding) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            postLikesIcon.text = parseCount(post.likes)
            postSharesIcon.text = parseCount(post.shares)
            postViewsIcon.text = parseCount(post.views)
            postLikesIcon.isChecked = post.likedByMe
        }
    }

    private fun setListeners(binding: CardPostBinding, post: Post) {
        binding.apply {
            postLikesIcon.setOnClickListener { listener.like(post) }
            postSharesIcon.setOnClickListener { listener.share(post) }
            menuButton.setOnClickListener{
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove ->{
                                listener.remove(post)
                                true
                            }
                            R.id.edit ->{
                                listener.edit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
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