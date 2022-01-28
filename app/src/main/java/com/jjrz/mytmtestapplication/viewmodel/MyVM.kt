package com.jjrz.mytmtestapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jjrz.mytmtestapplication.model.Posts
import com.jjrz.mytmtestapplication.model.Summary
import com.jjrz.mytmtestapplication.model.Users
import com.jjrz.mytmtestapplication.model.usersItem
import com.jjrz.mytmtestapplication.utility.DebugHelper.Companion.LogKitty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MyVM : ViewModel() {
    var userList = MutableLiveData<Users?>().apply { value = Users() }
    var postsList = MutableLiveData<Posts?>().apply { value = Posts() }
    var summaryList = MutableLiveData<MutableList<Summary>>()
    var UsersComplete = MutableLiveData<Boolean>().apply { value = false }
    var PostsComplete = MutableLiveData<Boolean>().apply { value = false }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getUserData() {
        val service = retrofit.create(MyDataService::class.java)
        val call = service.getUsers()
        LogKitty("Retrofit1")
        call.enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                val temp = Users()
                if (response.code() == 200) {
                    LogKitty("Assigning value to userList")
                    LogKitty("Users : " + response.body()?.size.toString())
                    response.body()?.forEach {
                        userList.value?.add(it)
//                        LogKitty(it.toString())
                    }
                    UsersComplete.value = true
                    LogKitty("userList : " + userList.value?.size)
                }
            }
            override fun onFailure(call: Call<Users>, t: Throwable) {
                userList.postValue(null)
                LogKitty(t.toString())
            }
        })
    }

    fun getPostsData() {
        val service = retrofit.create(MyDataService::class.java)
        val call = service.getPosts()
        LogKitty("Retrofit2")
        call.enqueue(object : Callback<Posts> {
            override fun onResponse(call: Call<Posts>, response: Response<Posts>) {
                val temp = Posts()
                if (response.code() == 200) {
                    LogKitty("Assigning value to postsList")
//                    LogKitty("Posts : " + response.body()?.size.toString())
                    response.body()?.forEach {
                        postsList.value?.add(it)
                    }
                    PostsComplete.value = true
                    LogKitty("postsList : " + postsList.value?.size)
                }
            }
            override fun onFailure(call: Call<Posts>, t: Throwable) {
                postsList.postValue(null)
                LogKitty(t.toString())
            }
        })
    }

    interface MyDataService {
        @GET("users")
        fun getUsers(): Call<Users>

        @GET("posts")
        fun getPosts(): Call<Posts>
    }

    fun consolidateLists() {
        LogKitty("Consolidating List")
        userList.value?.forEach { usersItem ->
            val list = postsList.value?.filter {
                it.id == usersItem.id
            }
            list?.forEach {
                summaryList.value?.apply {
                    this.add(Summary(usersItem.company?.name,it.title,it.body))
                }
            }
        }
        LogKitty("summaryList : " + summaryList.value?.size)
    }

    fun addSummmary() {
        LogKitty("Adding to summaryList")
        summaryList.value?.apply {
            this.add(Summary("test","test","test"))
            this.add(Summary("test2","test2","test2"))
        }
        LogKitty("summaryList after add : " + summaryList.value?.size)
    }
}