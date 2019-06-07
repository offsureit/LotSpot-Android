package com.oit.lotspot.mvp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.database.SharedPreferencesManager
import com.oit.lotspot.mvp.presenter.LoginPresenter
import com.oit.lotspot.retrofit.ApiClient
import com.oit.lotspot.retrofit.request.LoginRequestModel
import com.oit.lotspot.retrofit.response.ErrorResponse
import com.oit.lotspot.retrofit.response.LoginResponseModel
import com.oit.lotspot.activity.HomeActivity
import kotlinx.android.synthetic.main.activity_verification.*
import java.util.concurrent.TimeUnit

class VerificationActivity : BaseActivity(), LoginPresenter.ResponseCallBack {

    private var TAG = VerificationActivity::class.java.simpleName
    private lateinit var presenter: LoginPresenter
    private var loginRequestModel = LoginRequestModel()

    private var enteredOtpStringBuilder: StringBuilder? = null
    private var codeTextViews: Array<TextView>? = null
    private var contactNumber = ""
    private var device_Token = ""

    private var verificationId = ""
    private var resendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var verificationCode = ""
    private var mAuth: FirebaseAuth? = null

    private var runnable: Runnable = Runnable { setTimerToResendCode() }
    private var seconds = 0
    private var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        getIntentData()
        initUi()
        clickListener()
    }

    private fun initUi() {
        mAuth = FirebaseAuth.getInstance()
        presenter = LoginPresenter(this)
        getDeviceToken()
        enableDisableKeyDone(status = false)
        updateCodeSentDescText()
        initData()
    }

    /**
     * get Mobile number with CountryCode from intent
     */
    private fun getIntentData() {
        when (intent.hasExtra(Constants.App.CONTACT)) {
            true -> {
                contactNumber = intent.getStringExtra(Constants.App.CONTACT)
                if (contactNumber.isNotEmpty()) {
                    authenticatePhoneNumber()
                }
            }
        }
    }

    /**
     * get device token from fcm
     */
    private fun getDeviceToken() {
//        SharedPreferencesManager.with(this).edit()
//            .putString(Constants.SharedPref.PREF_DEVICE_TOKEN, FirebaseInstanceId.getInstance().token).apply()

        device_Token = "and454545"
//        device_Token = SharedPreferencesManager.with(this).getString(Constants.SharedPref.PREF_DEVICE_TOKEN, "")
    }

    /**
     * updating the different text colors to single text view
     */
    private fun updateCodeSentDescText() {
        val spanString = SpannableStringBuilder(getString(R.string.text_verify)).apply {
            val firstLength = this.length
            append(" $contactNumber")

            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this@VerificationActivity, R.color.colorAccent)),
                firstLength,
                this.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        tvCodeSentDesc.setText(spanString, TextView.BufferType.SPANNABLE)
    }

    private fun initData() {
        enteredOtpStringBuilder = StringBuilder()
        codeTextViews = arrayOf(etFirst, etSecond, etThird, etFourth, etFifth, etSixth)
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        ivBack.setOnClickListener(clickListener)
        ivCross.setOnClickListener(clickListener)
        btnOne.setOnClickListener(clickListener)
        btnTwo.setOnClickListener(clickListener)
        btnThree.setOnClickListener(clickListener)
        btnFour.setOnClickListener(clickListener)
        btnFive.setOnClickListener(clickListener)
        btnSix.setOnClickListener(clickListener)
        btnSeven.setOnClickListener(clickListener)
        btnEight.setOnClickListener(clickListener)
        btnNine.setOnClickListener(clickListener)
        btnZero.setOnClickListener(clickListener)
        tvResendCode.setOnClickListener(clickListener)
        ivKeyDone.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBack -> onBackPressed()

                R.id.ivCross -> clickedKeyBack()

                R.id.btnOne -> clickedKeyNumber(Constants.Number.ONE)

                R.id.btnTwo -> clickedKeyNumber(Constants.Number.TWO)

                R.id.btnThree -> clickedKeyNumber(Constants.Number.THREE)

                R.id.btnFour -> clickedKeyNumber(Constants.Number.FOUR)

                R.id.btnFive -> clickedKeyNumber(Constants.Number.FIVE)

                R.id.btnSix -> clickedKeyNumber(Constants.Number.SIX)

                R.id.btnSeven -> clickedKeyNumber(Constants.Number.SEVEN)

                R.id.btnEight -> clickedKeyNumber(Constants.Number.EIGHT)

                R.id.btnNine -> clickedKeyNumber(Constants.Number.NINE)

                R.id.btnZero -> clickedKeyNumber(Constants.Number.ZERO)

                R.id.tvResendCode -> {
                    resendOtp()
                }

                R.id.ivKeyDone -> {
                    when (validateVerificationCode()) {
                        true -> {
                            authenticateOtp()
                        }
                    }
                }

            }
    }

    /**
     * when user click on keypad's back key
     */
    private fun clickedKeyBack() {
        enteredOtpStringBuilder?.apply {
            if (isNotEmpty()) {
                val index = enteredOtpStringBuilder!!.length - 1

                deleteCharAt(index)

                setCodeTextView(
                    tvCode = codeTextViews!![index],
                    code = null,
                    status = false
                )

                enableDisableKeyDone(status = false)
            }
        }
    }

    /**
     * when user click on the keypad's number key i.e., 0-9.
     */
    private fun clickedKeyNumber(number: Int) {
        if (enteredOtpStringBuilder!!.length < Constants.DefaultValue.OtpSize) {
            enteredOtpStringBuilder!!.append(number)

            val index = enteredOtpStringBuilder!!.length - 1

            setCodeTextView(
                tvCode = codeTextViews!![index],
                code = enteredOtpStringBuilder!![index],
                status = true
            )

            if (enteredOtpStringBuilder!!.length == Constants.DefaultValue.OtpSize)
                enableDisableKeyDone(status = true)
        }
    }

    private fun setAutoDetectOtp() {
        enteredOtpStringBuilder!!.append(verificationCode)

        for (i in 0 until enteredOtpStringBuilder!!.length) {
            Log.d(TAG, "Code-- >${enteredOtpStringBuilder!![i]}")
            setCodeTextView(
                tvCode = codeTextViews!![i],
                code = enteredOtpStringBuilder!![i],
                status = true
            )
        }
        authenticateOtp()

    }

    /**
     * updating the Code by selecting the code views
     *
     * @param tvCode tvCode where code is shown
     * @param code setting code to tvCode
     * @param status status set to true or false w.r.t. values selected or unselected
     */
    private fun setCodeTextView(tvCode: TextView, code: Char?, status: Boolean) {
        tvCode.apply {
            text = code?.toString()
            isSelected = status
        }
    }

    /**
     * updating the KeyDone imageview status
     *
     * @param status status is set to true when all code fields are entered
     */
    private fun enableDisableKeyDone(status: Boolean) {
        ivKeyDone.isEnabled = status
    }

    private fun validateVerificationCode(): Boolean {
        if (enteredOtpStringBuilder.toString().isEmpty() || enteredOtpStringBuilder.toString().length < 6) {
            showToast(getString(R.string.text_enter_otp))
            return false
        }
        return true
    }

    private fun authenticateOtp() {
        showProgressView()
        val credential = PhoneAuthProvider.getCredential(verificationId, enteredOtpStringBuilder.toString())
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (null == mAuth) {
            mAuth = FirebaseAuth.getInstance()
        }
        mAuth!!.signInWithCredential(credential!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    hideProgressDialog()
                    // Sign in success, update UI with the signed-in user's information
                    showToast(getString(R.string.otp_verified_success_msg))
                    setResult(Activity.RESULT_OK)
                    apiHitForUserLogin()
                } else {
                    hideProgressDialog()
                    // Sign in failed, display a message and update the UI
                    showToast("You have entered invalid verification code")
                }
            }
    }

    private fun enableViewTimer() {
        tvTimer.visibility = View.VISIBLE
        tvResendCode.visibility = View.GONE
    }

    private fun enableViewResendCode() {
        tvResendCode.visibility = View.VISIBLE
        tvTimer.visibility = View.GONE
    }

    private fun startTimer() {
        seconds = 30
        runnable.run()
    }

    @SuppressLint("SetTextI18n")
    private fun setTimerToResendCode() {
        enableViewTimer()
        if (seconds == Constants.Duration.RESEND_TIMER_END) {
            stopTimer()
        } else {
            if (seconds.toString().length == 1) {
                tvTimer.text = "00:0$seconds"
            } else {
                tvTimer.text = "00:$seconds"
            }
            seconds--
            handler.postDelayed(runnable, Constants.Duration.RESEND_TIMER_start.toLong())
        }
    }

    private fun stopTimer() {
        enableViewResendCode()
        handler.removeCallbacks(runnable)
    }

    private fun authenticatePhoneNumber() {
        showProgressView()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            contactNumber, // Phone number to verify
            30, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            TaskExecutors.MAIN_THREAD, // Activity (for callback binding)
            firebaseCallback
        )
    }

    private fun resendOtp() {
        showProgressView()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            contactNumber, // Phone number to verify
            30, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            TaskExecutors.MAIN_THREAD, // Activity (for callback binding)
            firebaseCallback,
            resendingToken
        )
    }

    private val firebaseCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential?) {
            if (null == phoneAuthCredential) {
                apiHitForUserLogin()
            } else {
                val code = phoneAuthCredential.smsCode
                if (null != code) {
                    this@VerificationActivity.verificationCode = "${phoneAuthCredential.smsCode}"
                    setAutoDetectOtp()
                } else {
                    apiHitForUserLogin()
                }
            }
        }

        override fun onVerificationFailed(e: FirebaseException?) {
            hideProgressDialog()
            showToast("${e!!.message}")
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
            this@VerificationActivity.verificationId = "$verificationId"
            this@VerificationActivity.resendingToken = token
            hideProgressDialog()
            showToast(getString(R.string.the_sms_verificaton_code_has_been_sent_to_the_provided_phone_number))
            startTimer()
        }
    }

    /**
     * show alert dialog
     */
    internal fun showAlertForSignUp(message: String) {
        val alert = AlertDialog.Builder(this@VerificationActivity, R.style.MyDialogTheme)
        alert.setTitle(getString(R.string.app_name))
        alert.setMessage(message)
        alert.setPositiveButton(getString(R.string.text_sign_up)) { dialog, _ ->
            dialog.dismiss()
            clickedForSignUp()
        }
        alert.setNegativeButton(getString(R.string.text_cancel)) { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        alert.show()
    }

    private fun clickedForSignUp() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ApiClient.BASE_URL_SIGNUP)))
    }

    /**
     * Call for api to login
     */
    private fun apiHitForUserLogin() {
        showProgressView()
        Log.d(TAG, " Device Token::-- $device_Token")
        loginRequestModel.apply {
            contact = contactNumber
            deviceToken = device_Token
        }
        SharedPreferencesManager.with(this).edit()
            .putString(Constants.SharedPref.PREF_USER_CONTACT, loginRequestModel.contact)
            .apply()

        presenter.apiPostForLogin(loginRequestModel)
    }

    /**
     * when successful response or data retrieved from api login
     *
     * @param responseLogin success response
     */
    override fun onSuccess(responseLogin: LoginResponseModel.LoginResponseFirstModel) {
        hideProgressDialog()

        SharedPreferencesManager.with(this).edit()
            .putString(Constants.SharedPref.PREF_USER_PROFILE, Gson().toJson(responseLogin))
            .putBoolean(Constants.SharedPref.PREF_IS_USER_LOGIN, true)
            .putBoolean(Constants.SharedPref.PREF_MSG_LONG_PRESS, true)
            .apply()

        val model = Gson().fromJson(
            SharedPreferencesManager.with(this)
                .getString(Constants.SharedPref.PREF_USER_PROFILE, ""),
            LoginResponseModel.LoginResponseFirstModel::class.java
        )
        Log.d(TAG,"Token::--> ${model.token}")
        showToast(getString(R.string.login_success_msg))
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
    }

    /**
     * when error occurred in getting response from api login
     *
     * @param errorResponse of Error message
     */
    override fun onFailure(errorResponse: ErrorResponse) {
        hideProgressDialog()
        responseFailure(errorResponse)
    }

    override fun onBackPressed() = finish()
}
