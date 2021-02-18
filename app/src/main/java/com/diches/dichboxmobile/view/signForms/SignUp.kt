package com.diches.dichboxmobile.view.signForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.SignUpVerifier

class SignUp : Fragment(), FragmentCleaner {
    private lateinit var verifier: SignUpVerifier
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.sign_up, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = view.findViewById(R.id.editPersonName)
        email = view.findViewById(R.id.editEmailAddress)
        password = view.findViewById(R.id.editPassword)
        verifier = SignUpVerifier(username, email, password)
    }

    override fun cleanFieldsInput() {
        if (this::username.isInitialized && username.text.isNotEmpty())
            username.text.clear()
        if (this::email.isInitialized && email.text.isNotEmpty())
            email.text.clear()
        if (this::password.isInitialized && password.text.isNotEmpty())
            password.text.clear()
    }
}