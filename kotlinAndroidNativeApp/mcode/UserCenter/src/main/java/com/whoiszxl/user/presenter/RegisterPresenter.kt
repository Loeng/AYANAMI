package com.whoiszxl.user.presenter

import com.whoiszxl.base.ext.execute
import com.whoiszxl.base.presenter.BasePresenter
import com.whoiszxl.base.rx.BaseSubscriber
import com.whoiszxl.base.utils.NetWorkUtils
import com.whoiszxl.user.presenter.view.RegisterView
import com.whoiszxl.user.service.UserService
import es.dmoral.toasty.Toasty
import javax.inject.Inject


class RegisterPresenter @Inject constructor():BasePresenter<RegisterView>() {

    /**
     * 通过依赖注入的方式将service传递进来，和spring的那个操作一样一样
     */
    @Inject
    lateinit var userService: UserService

    fun register(mobile:String, pwd:String, verifyCode:String){

        if(!checkNetWork()){
            return
        }

        mView.showLoading()
        userService.register(mobile, pwd, verifyCode)
                .execute(object:BaseSubscriber<Boolean>(mView){
                    override fun onNext(t: Boolean) {
                        if(t){
                            mView.onRegisterResukt("注册成功")
                            mView.hideLoading()
                        }

                    }
                }, lifecycleProvider)

    }

    fun verifycode(mobile: String) {
        if(!checkNetWork()){
            return
        }

        userService.verifycode(mobile)
                .execute(object:BaseSubscriber<Boolean>(mView){
                    override fun onNext(t: Boolean) {
                        if(t) {
                            mView.onSendVerifyCodeResult("验证码发送成功")
                        }
                    }
                },lifecycleProvider)
    }
}