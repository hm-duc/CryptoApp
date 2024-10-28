package com.plcoding.cryptotracker.crypto.presenatation.coin_list

import com.plcoding.cryptotracker.crypto.presenatation.model.CoinUI

sealed interface CoinLostAction {
    data class OnCoinClick(val coinUi: CoinUI): CoinLostAction
    data object OnRefresh: CoinLostAction
}