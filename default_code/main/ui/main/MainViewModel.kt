package cu.jaco.transito.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.jaco.transito.lifecycle.SingleLiveEvent
import cu.jaco.transito.repositories.database.AppDatabase
import cu.jaco.transito.repositories.retrofit.services.DefaultApiService
import cu.jaco.transito.repositories.retrofit.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private var defaultApi: DefaultApiService,
    private var database: AppDatabase
) : ViewModel() {

    private val _inventory: SingleLiveEvent<ResultWrapper> = SingleLiveEvent()

    fun inventory(): SingleLiveEvent<ResultWrapper> {
        viewModelScope.launch {
            when (val inventory = defaultApi.inventory()) {
                is ResultWrapper.Success<*> -> {
                    _inventory.postValue(inventory)
                }
                else -> {
                    _inventory.postValue(inventory)
                }
            }
        }

        return _inventory
    }

}