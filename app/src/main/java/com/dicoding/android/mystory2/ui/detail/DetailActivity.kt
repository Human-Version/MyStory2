package com.dicoding.android.mystory2.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.android.mystory2.databinding.ActivityDetailBinding
import com.dicoding.android.mystory2.util.EXTRA_DESCRIPTION
import com.dicoding.android.mystory2.util.EXTRA_IMAGE
import com.dicoding.android.mystory2.util.STORY_NAME

class DetailActivity : AppCompatActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = "Detail"
        setDeail()
    }

    private fun setDeail() {
        intent.apply {
            val name = getStringExtra(STORY_NAME)
            val desc = getStringExtra(EXTRA_DESCRIPTION)
            val image = getStringExtra(EXTRA_IMAGE)

            binding.apply {
                tvName.text = name
                tvDesc.text = desc
                Glide.with(this@DetailActivity)
                    .load(image)
                    .into(ivStory)
            }
        }
    }
}