package com.adgvcxz

import io.reactivex.Observable

/**
 * zhaowei
 * Created by zhaowei on 2017/5/4.
 */

interface IViewModel<S: IModel> {

    fun scan(state: S, mutation: IMutation): S {
        return state
    }

    fun mutate(action: IAction): Observable<IMutation> {
        return Observable.empty()
    }

    fun transform(mutation: Observable<IMutation>): Observable<IMutation> {
        return mutation
    }
}
