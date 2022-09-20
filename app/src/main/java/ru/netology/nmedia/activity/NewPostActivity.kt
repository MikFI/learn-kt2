package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val text = intent?.getStringExtra(Intent.EXTRA_TEXT)
        binding.newContent.setText(text)
        if (!text.isNullOrBlank()) {
            binding.okButton.setImageResource(R.drawable.ic_edit_24)
        } else {
            binding.okButton.setImageResource(R.drawable.ic_add_24)
        }

        //по нажатию кнопки текст из поля суётся в результат (setResult)
        //и NewPostActivity завершается (finish), передавая упраление
        //parseResult из контракта, который этот NewPostActivity и запускал
        binding.okButton.setOnClickListener {
            val content = binding.newContent.text.toString()
            if (content.isBlank()) {
                setResult(RESULT_CANCELED)
            } else {
                setResult(RESULT_OK, Intent().putExtra(Intent.EXTRA_TEXT, content))
            }
            finish()
        }
    }

    class Contract : ActivityResultContract<String?, String?>() {
        //формирование explicit интента на запуск NewPostActivity
        override fun createIntent(context: Context, input: String?): Intent =
            Intent(context, NewPostActivity::class.java).putExtra(Intent.EXTRA_TEXT, input)

        //обрабатывает результат, пришедший из setResult у NewPostActivity
        //(по нажатию кнопки ОК в окне ввода текста)
        //и возвращает строку туда, откуда был зарегистрирован контракт
        override fun parseResult(resultCode: Int, intent: Intent?) =
            if (resultCode == RESULT_OK) {
                intent?.getStringExtra(Intent.EXTRA_TEXT)
            } else {
                null
            }
    }
}