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
import com.diches.dichboxmobile.mv.SignUpVerifier
import com.diches.dichboxmobile.view.Profile
import com.diches.dichboxmobile.view.SignViewModel

class SignUp : Fragment(), FragmentCleaner {
    private lateinit var verifier: SignUpVerifier
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var nameWarning: TextView
    private lateinit var emailWarning: TextView
    private lateinit var passwordWarning: TextView
    private lateinit var submitBtn: Button

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.sign_up, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = view.findViewById(R.id.editPersonName)
        nameWarning = view.findViewById(R.id.usernameWarning)
        email = view.findViewById(R.id.editEmailAddress)
        emailWarning = view.findViewById(R.id.emailWarning)
        password = view.findViewById(R.id.editPassword)
        passwordWarning = view.findViewById(R.id.passwdWarning)
        submitBtn = view.findViewById(R.id.submitSignUp)

        verifier = SignUpVerifier(submitBtn)
                .checkUsername(username, nameWarning)
                .checkEmail(email, emailWarning)
                .checkPassword(password, passwordWarning)

        verifier.setSaveHandler { str ->
            context?.openFileOutput("signed.txt", Context.MODE_PRIVATE).use {
                it?.write(str.toByteArray())
            }

            ViewModelProvider(requireActivity())
                    .get(SignViewModel::class.java)
                    .setIsSigned(true)

            activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.user_container, Profile())
                    ?.commit()
        }
    }

    override fun cleanFieldsInput() {
        if (this::username.isInitialized)
            cleanInput(username)
        if (this::email.isInitialized)
            cleanInput(email)
        if (this::password.isInitialized)
            cleanInput(password)
        if (this::nameWarning.isInitialized && nameWarning.text.isNotEmpty())
            nameWarning.text = ""
        if (this::emailWarning.isInitialized && emailWarning.text.isNotEmpty())
            emailWarning.text = ""
        if (this::passwordWarning.isInitialized && passwordWarning.text.isNotEmpty())
            passwordWarning.text = ""
    }
}