package com.mosamir.messengerchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(), TextWatcher {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = Firebase.auth
        et_email_signIn.addTextChangedListener(this)
        et_password_signIn.addTextChangedListener(this)

        btn_signIn.setOnClickListener {
            val email = et_email_signIn.text.trim().toString()
            val password = et_password_signIn.text.trim().toString()

            if(email.isEmpty()){
                et_email_signIn.error = "Email Required"
                et_email_signIn.requestFocus()
                return@setOnClickListener
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                et_email_signIn.error = "Please Enter Your Email"
                et_email_signIn.requestFocus()
                return@setOnClickListener
            }

            if(password.length<6){
                et_password_signIn.error = "Please Enter Your Password"
                et_password_signIn.requestFocus()
                return@setOnClickListener
            }

            progressBar_signIn.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        val user = auth.currentUser
                    } else {
                        progressBar_signIn.visibility = View.INVISIBLE
                        Toast.makeText(baseContext, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }


        }

        btn_create_account.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        btn_signIn.isEnabled = et_email_signIn.text.trim().isNotEmpty() && et_password_signIn.text.trim().isNotEmpty()
    }

    override fun afterTextChanged(p0: Editable?) {

    }
}