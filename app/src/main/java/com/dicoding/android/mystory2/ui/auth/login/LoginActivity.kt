package com.dicoding.android.mystory2.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import com.dicoding.android.mystory2.R
import com.dicoding.android.mystory2.data.response.UserSession
import com.dicoding.android.mystory2.databinding.ActivityLoginBinding
import com.dicoding.android.mystory2.ui.auth.register.RegisterActivity
import com.dicoding.android.mystory2.ui.main.MainActivity
import com.dicoding.android.mystory2.util.DataStoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModelViewModel by viewModels<LoginViewModel>()
    private val dataStoreViewModel by viewModels<DataStoreViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Login"

        setupViewModel()
        action()
        playAnimation()

    }

    private fun setupViewModel() {
        viewModelViewModel.isLoading.observe(this) { showLoading(it) }
        viewModelViewModel.toastMessage.observe(this) { toast(it) }
    }

    private fun action() {
        binding.btnLogin.setOnClickListener {
            login()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity as Activity).toBundle()
            )
        }
    }

    private fun login() {
        val email = binding.tvEmail.text.toString().trim()
        val password = binding.tvPassword.text.toString().trim()
        when {
            email.isEmpty() -> {
                binding.tilEmail.error = "Masukkan email"
            }
            password.isEmpty() -> {
                binding.textInputLayout.error = "Masukkan password"
            }
            else -> {
                viewModelViewModel.loginUser(email, password)

                viewModelViewModel.userLogin.observe(this) {
                    binding.progressBar.visibility = View.VISIBLE
                    if (it != null) {
                        AlertDialog.Builder(this).apply {
                            setTitle("berhasil login")
                            setMessage("Selamat Datang, ${it.name}!")
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent,
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity as Activity).toBundle()
                                )
                                finish()
                            }
                            create()
                            show()
                        }
                        savedSession(UserSession(it.name, it.token, it.userId, true))
                    }

                }


            }
        }
    }

    private fun savedSession(user: UserSession) {
        dataStoreViewModel.setSession(user)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(email)
            startDelay = 500
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}