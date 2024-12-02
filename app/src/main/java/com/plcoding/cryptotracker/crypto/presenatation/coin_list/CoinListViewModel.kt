package com.plcoding.cryptotracker.crypto.presenatation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.presenatation.model.CoinUI
import com.plcoding.cryptotracker.crypto.presenatation.model.toCoinUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class CoinListViewModel(
    private val dataSource: CoinDataSource
): ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state = _state
        .onStart { loadCoin() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            CoinListState()
        )

    private val _event = Channel<CoinListEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: CoinLostAction) {
        when (action) {
            is CoinLostAction.OnCoinClick -> {
                selectCoin(action.coinUi)
            }
            CoinLostAction.OnRefresh -> {
                loadCoin()
            }
        }
    }

    private fun selectCoin(coinUi: CoinUI) {
        _state.update {
            it.copy(
                selectCoin = coinUi
            )
        }

        viewModelScope.launch {
            dataSource.getCoinHistory(
                coinId = coinUi.id,
                start = ZonedDateTime.now().minusDays(5),
                end = ZonedDateTime.now()
            ).onSuccess { history ->
                println(history)
            }.onError { error ->
                _event.send(CoinListEvent.Error(error))
            }
        }
    }

    private fun loadCoin() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            dataSource
                .getCoin()
                .onSuccess { coins ->
                    _state.update { it ->
                        it.copy(
                            isLoading = false,
                            coins = coins.map {it.toCoinUI()}
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    _event.send(CoinListEvent.Error(error))
                }
        }
    }
}