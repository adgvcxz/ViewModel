package com.adgvcxz.viewmodel.sample

import com.adgvcxz.IAction
import com.adgvcxz.IMutation
import com.adgvcxz.IState
import com.adgvcxz.ViewModel
import io.reactivex.Observable

/**
 * zhaowei
 * Created by zhaowei on 2017/5/1.
 */

class MainViewModel: ViewModel<MainViewModel.State>(State()) {

    class State: IState

    override fun mutate(state: State, mutation: IMutation): State {
        return state
    }

    override fun action(action: IAction): Observable<IMutation> {
        return Observable.empty()
    }

}