package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.AndroidUtils
import ru.netology.nmedia.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.StringArg
import ru.netology.nmedia.databinding.FragmentNewPostBinding

class NewPostFragment : Fragment() {
    //пишем\получаем текст поста
    //(сам функционал вынесен в отдельный объект StringArg)
    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)

        //activity у нас одна, поэтому тут можно использовать ту же viewModel,
        //что и на основном списке постов
        val viewModel: PostViewModel by viewModels(
//            ownerProducer = ::requireParentFragment
            ownerProducer = { this.requireParentFragment() }
        )

        //получаем из navigate (которым нас послали в этот фрагмент) текст поста
//        arguments?.textArg.let(binding.newContent::setText())
        val text = arguments?.textArg ?: ""
        binding.newContent.setText(text)

        //меняем иконку на экране добавления поста
        //в зависимости от того, редактируем его или создаём
        if (text.isNotBlank()) {
            binding.okButton.setImageResource(R.drawable.ic_edit_24)
        } else {
            binding.okButton.setImageResource(R.drawable.ic_add_24)
        }

        //отправляем текст из поля ввода во viewmodel по нажатию кнопки
        //или ругаем пользователя, если он ничего не ввёл
        binding.okButton.setOnClickListener {
            val content = binding.newContent.text.toString()
            if (content.isNotBlank()) {
                viewModel.changeContent(content)
                viewModel.sendPost()
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            } else{
                Toast.makeText(requireContext(),R.string.empty_content_error,Toast.LENGTH_SHORT).show()
//                Snackbar.make(
//                    binding.root,
//                    R.string.empty_content_error,
//                    Snackbar.LENGTH_INDEFINITE
//                )
//                    .setAction(android.R.string.ok) { //действие по кнопке ОК
//                    }
//                    .show()
            }
        }
        return binding.root
    }
}