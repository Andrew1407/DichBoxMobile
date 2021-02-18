package com.diches.dichboxmobile.view.signForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.SignInVerifier
import com.diches.dichboxmobile.mv.SignUpVerifier

class SignIn : Fragment(), FragmentCleaner {
    private lateinit var verifier: SignInVerifier
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.sign_in, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email = view.findViewById(R.id.editEmailAddress)
        password = view.findViewById(R.id.editPassword)
        verifier = SignInVerifier(email, password)
    }

    override fun cleanFieldsInput() {
        if (this::email.isInitialized && email.text.isNotEmpty())
            email.text.clear()
        if (this::password.isInitialized && password.text.isNotEmpty())
            password.text.clear()
    }
}