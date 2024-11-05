package com.plcoding.cryptotracker.crypto.presenatation.coin_list

import com.plcoding.cryptotracker.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(val error: NetworkError): CoinListEvent
}