package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        //создаём ссылку на viewModel, в которой будут производиться все действия с данными
        //здесь же, в activity, мы их только отображаем
        val viewModel: PostViewModel by viewModels(
//            ownerProducer = ::requireParentFragment
            ownerProducer = { this.requireParentFragment() }
        )

        //setContentView во фрагменте не вызывается, вместо этого onCreateView
        //возвращает вьюшку (return binding.root в конце)
        //setContentView(binding.root)

        //втыкаем взаимодействие с постом (лайк, шара, редактирование) через класс PostInteraction,
        //в котором всё это описано
        val adapter = PostAdapter(listener = PostInteraction(vm = viewModel, view = binding.root))

        //получаем список постов и перематываем на самый верх в случае,
        //если количество постов увеличилось на 1 в сравнении с прошлым обновлением
        //(т.е. если только что добавили 1 пост)
        binding.postList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val newPost = posts.size > adapter.currentList.size
            adapter.submitList(posts) {
                if (newPost) {
                    binding.postList.scrollToPosition(0)
                }
            }
        }

        //переход между фрагментами по нажатию кнопки добавления поста
        binding.addPostButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root
    }
}