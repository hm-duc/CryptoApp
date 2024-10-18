package com.plcoding.cryptotracker.crypto.presenatation.coin_list

import androidx.compose.runtime.Immutable
import com.plcoding.cryptotracker.crypto.presenatation.model.CoinUI

@Immutable
data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<CoinUI> = emptyList(),
    val selectCoin: CoinUI? = null
)