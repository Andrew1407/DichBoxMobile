package com.diches.dichboxmobile.view.signForms

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.userDataManager.UserStateViewModel
import com.diches.dichboxmobile.mv.verifiers.signVerifiers.SignInVerifier

class SignIn : Fragment(), FragmentCleaner {
    private lateinit var verifier: SignInVerifier
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var emailWarning: TextView
    private lateinit var passwordWarning: TextView
    private lateinit var submitBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.sign_in, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = view.findViewById(R.id.editEmailAddress)
        emailWarning = view.findViewById(R.id.emailWarning)
        password = view.findViewById(R.id.editPassword)
        passwordWarning = view.findViewById(R.id.passwdWarning)
        submitBtn = view.findViewById(R.id.submitSignIn)

        verifier = SignInVerifier(submitBtn)
                .checkEmail(email, emailWarning)
                .checkPassword(password, passwordWarning)

        verifier.setSaveHandler { str ->
            val viewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
            viewModel.setState(Pair(str, str))
            context?.openFileOutput("signed.txt", Context.MODE_PRIVATE).use {
                it?.write(str.toByteArray())
            }
        }

    }

    override fun cleanFieldsInput() {
        if (this::email.isInitialized)
            cleanInput(email)
        if (this::password.isInitialized)
            cleanInput(password)
        if (this::emailWarning.isInitialized && emailWarning.text.isNotEmpty())
            emailWarning.text = ""
        if (this::passwordWarning.isInitialized && passwordWarning.text.isNotEmpty())
            passwordWarning.text = ""
    }

}