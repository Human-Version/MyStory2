package com.dicoding.android.mystory2.ui.main

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.android.mystory2.R
import com.dicoding.android.mystory2.data.adapter.ListAdapter
import com.dicoding.android.mystory2.data.adapter.LoadingStateAdapter
import com.dicoding.android.mystory2.databinding.ActivityMainBinding
import com.dicoding.android.mystory2.ui.addstory.AddStoryActivity
import com.dicoding.android.mystory2.ui.auth.login.LoginActivity
import com.dicoding.android.mystory2.ui.maps.MapsActivity
import com.dicoding.android.mystory2.util.DataStoreViewModel
import com.uk.tastytoasty.TastyToasty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainVIewModel>()
    private lateinit var binding: ActivityMainBinding
    private val dataStoreViewModel by viewModels<DataStoreViewModel>()
    private lateinit var adapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Home"

        adapter = ListAdapter()

        setupViewModel()
        initSwipeToRefresh()
        setRecyclerView()
        action()
    }

    private fun action() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity as Activity).toBundle()
            )
        }
    }


    private fun setupViewModel() {
        dataStoreViewModel.getSession().observe(this) { userSession ->
            if (!userSession.isLogin) {
                TastyToasty.success(this, "Please Login First").show()
                Log.d("tag", userSession.token)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity as Activity).toBundle()
                )
                finish()
            } else {
                viewModel.story.observe(this) {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
        viewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }

        viewModel.isLoading.observe(this) { showLoading(it) }
    }

    private fun setRecyclerView() {
        binding.apply {
            rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.setHasFixedSize(true)
            rvStory.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
        }

        adapter.addLoadStateListener { loadState ->
            // SwipeRefresh status based on LoadState
            binding.swipeRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun initSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener { adapter.refresh() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.menu_logout -> {
                dataStoreViewModel.logout()
                Log.d("tag", "Logout Success")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.menu_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> {return super.onOptionsItemSelected(item)}
        }
    }
}