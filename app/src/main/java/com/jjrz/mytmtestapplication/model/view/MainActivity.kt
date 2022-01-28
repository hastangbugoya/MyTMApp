package com.jjrz.mytmtestapplication.model.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.jjrz.mytmtestapplication.databinding.ActivityMainBinding
import com.jjrz.mytmtestapplication.utility.DebugHelper.Companion.LogKitty
import com.jjrz.mytmtestapplication.viewmodel.MyVM

class MainActivity : AppCompatActivity() {
    private lateinit var myVM : MyVM
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        setContentView(view)
        val adapter = MyListAdapter()
        binding.mainRecyclerview.adapter = adapter
        myVM = ViewModelProvider(this).get(MyVM::class.java)

        myVM.getPostsData()
        myVM.getUserData()
        myVM.PostsComplete.observe(this) {
            LogKitty("posts change detected")
            if (myVM.UsersComplete.value ?: false) {
                myVM.consolidateLists()
            }
        }
        myVM.UsersComplete.observe(this) {
            LogKitty("users change detected")
            if (myVM.PostsComplete.value ?: false) {
                myVM.consolidateLists()
            }
        }
        myVM.summaryList.observe(this) {
            LogKitty("summary change detected")
            myVM.summaryList.value?.let { it1 -> adapter.updateList(it1) }
        }
        adapter.addSample()
        myVM.addSummmary()
    }
}