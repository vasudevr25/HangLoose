package com.hangloose.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.hangloose.HanglooseApp.Companion.getApiService
import com.hangloose.HanglooseApp.Companion.subscribeScheduler
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerCreateRequest
import com.hangloose.model.ConsumerLoginRequest
import com.hangloose.utils.AUTH_TYPE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response

class ConsumerLoginViewModel : ViewModel() {

    private var compositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var consumerLoginRequest: ConsumerLoginRequest =
        ConsumerLoginRequest(AUTH_TYPE.MOBILE.name, null, null)
    private var consumerRegisterRequest: ConsumerCreateRequest? = null
    private var consumerAuthDetailResponse: MutableLiveData<Response<ConsumerAuthDetailResponse>>? = null
    private val TAG = "ConsumerLoginViewModel"

    val phoneWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(edit: Editable?) {
            consumerLoginRequest.id = edit.toString()
        }
    }

    val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(edit: Editable?) {
            consumerLoginRequest.token = edit.toString()
        }
    }

    fun onSignInClick(view: View) {
        Log.i(TAG, "onSignInClick")
        verifySignIn()
    }

    fun onFacebookSignInClick(fbLoginRequest: ConsumerLoginRequest) {
        Log.i(TAG, "onFacebookSignInClick")
        consumerLoginRequest = fbLoginRequest
        verifySignIn()
    }

    fun onGoogleSignInClick(googleLoginRequest: ConsumerLoginRequest) {
        Log.i(TAG, "onGoogleSignInClick")
        consumerLoginRequest = googleLoginRequest
        verifySignIn()
    }

    /**
     * method to call API to verify signIn credentials
     */
    private fun verifySignIn() {
        if (consumerLoginRequest.id != null && consumerLoginRequest.token != null) {
            val disposable = getApiService()!!.consumerLogin(consumerLoginRequest)
                .subscribeOn(subscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.i(TAG, "success login")
                    consumerAuthDetailResponse!!.value = response
                }, {
                    Log.i(TAG, "error login")
                })

            compositeDisposable!!.add(disposable)
        } else {
        }
    }

    fun loginResponse(): MutableLiveData<Response<ConsumerAuthDetailResponse>>? {
        return consumerAuthDetailResponse
    }

    private fun unSubscribeFromObservable() {
        if (compositeDisposable != null && !compositeDisposable!!.isDisposed) {
            compositeDisposable!!.dispose()
        }
    }

    fun reset() {
        unSubscribeFromObservable()
        compositeDisposable = null
    }
}