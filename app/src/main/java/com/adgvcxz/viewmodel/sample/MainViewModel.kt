package com.adgvcxz.viewmodel.sample

import com.adgvcxz.IState
import com.adgvcxz.ViewModel

/**
 * zhaowei
 * Created by zhaowei on 2017/5/1.
 */

class MainViewModel: ViewModel<MainViewModel.State>(State()) {

    class State: IState
}