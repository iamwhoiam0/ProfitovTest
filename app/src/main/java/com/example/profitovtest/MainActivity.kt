package com.example.profitovtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.profitovtest.data.api.RetrofitService
import com.example.profitovtest.data.entities.Entry
import com.example.profitovtest.data.entities.HotListId
import com.example.profitovtest.data.repository.MainRepository
import com.example.profitovtest.databinding.ActivityMainBinding
import com.example.profitovtest.presentation.viewModel.MainViewModel
import com.example.profitovtest.presentation.viewModel.MainViewModelFactory
import com.example.profitovtest.utils.Constants.CURRENT_POSITION
import com.example.profitovtest.utils.Constants.POSTS_ID
import com.example.profitovtest.utils.NetworkResultHandler

class MainActivity : AppCompatActivity(), NetworkResultHandler {

    lateinit var viewModel: MainViewModel

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var currentPosition = 0
    private lateinit var postsId: HotListId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofitService = RetrofitService.getInstance()
        val mainRepository = MainRepository(retrofitService)

        viewModel = ViewModelProvider(this, MainViewModelFactory(mainRepository))[MainViewModel::class.java]

        viewModel.hotList.observe(this) { result ->
            handleCharactersResult(result)
        }

        viewModel.post.observe(this){ result ->
            handleCharactersResult(result)
        }

        if (viewModel.hotList.value == null){ // Избавляемся от повторных запросов на сервер при повороте экрана
            viewModel.getHotList()
        }


        binding.backBtn.setOnClickListener {
            onClickBack()
        }
        binding.nextBtn.setOnClickListener {
            onClickNext()
        }
    }

    override fun handleError(errorMessage: String?) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun <T> handleSuccess(data: T) {
        if (data is HotListId){
            postsId = data
            viewModel.getCurrentPost(postsId[0])
        }
        if(data is Entry){
            outputData(data)
        }
    }

    override fun showEmptyView() {
        Toast.makeText(this, "Data is empty", Toast.LENGTH_LONG).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        outState.putInt(CURRENT_POSITION, currentPosition)
        outState.putIntegerArrayList(POSTS_ID, postsId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        currentPosition = savedInstanceState.getInt(CURRENT_POSITION)
        postsId = savedInstanceState.getIntegerArrayList(POSTS_ID) as HotListId
    }

    private fun onClickBack(){
        if(currentPosition == 0){
            currentPosition = postsId.size - 1
        }else{
            currentPosition -= 1
        }
        viewModel.getCurrentPost(postsId[currentPosition])
    }

    private fun onClickNext(){
        if(currentPosition == postsId.size-1){
            currentPosition = 0
        }else{
            currentPosition += 1
        }
        viewModel.getCurrentPost(postsId[currentPosition])
    }
    private fun outputData(data: Entry){
        when(data.type){
            "text" ->{
                binding.pageWv.visibility = View.GONE
                binding.pageTv.visibility = View.VISIBLE
                binding.pageTv.text = data.payload.text
            }
            "webpage" ->{
                binding.pageWv.visibility = View.VISIBLE
                binding.pageTv.visibility = View.GONE
                data.payload.url?.let {
                    binding.pageWv.loadUrl(it)
                }
            }
        }
    }
}

