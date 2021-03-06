package com.whoiszxl.user.presenter

import com.whoiszxl.base.ext.execute
import com.whoiszxl.base.presenter.BasePresenter
import com.whoiszxl.base.rx.BaseSubscriber
import com.whoiszxl.user.data.protocol.UserInfo
import com.whoiszxl.user.presenter.view.LoginView
import com.whoiszxl.user.service.UserService
import javax.inject.Inject


class LoginPresenter @Inject constructor():BasePresenter<LoginView>() {

    /**
     * 通过依赖注入的方式将service传递进来，和spring的那个操作一样一样
     */
    @Inject
    lateinit var userService: UserService

    fun login(mobile:String, pwd:String, pushId:String){

        if(!checkNetWork()){
            return
        }

        mView.showLoading()
        userService.login(mobile, pwd, pushId)
                .execute(object:BaseSubscriber<UserInfo>(mView){
                    override fun onNext(t: UserInfo) {
                        mView.onLoginResult(t)
                    }
                }, lifecycleProvider)

    }
}