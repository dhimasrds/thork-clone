package id.thork.app

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.repository.LoginRepository

class MainViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository
    ) : LiveCoroutinesViewModel() {
    val TAG = MainViewModel::class.java.name

    val quote: LiveData<String> get() = _quote
    val _quote: MutableLiveData<String> = MutableLiveData()

    fun changeRandomQuotes() {
        val rnd = (0 until 999).random()
        val rnd2 = (0 until 999).random()
        _quote.value = "Keep Learning $rnd DAN ATAU DENGAN $rnd2"
    }

}