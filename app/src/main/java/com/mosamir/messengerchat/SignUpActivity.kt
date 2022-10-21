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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mosamir.messengerchat.model.User
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(), TextWatcher {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth
        val db = Firebase.firestore
        et_name_signUp.addTextChangedListener(this)
        et_email_signUp.addTextChangedListener(this)
        et_password_signUp.addTextChangedListener(this)

        btn_signUp.setOnClickListener {
            val name = et_name_signUp.text.toString().trim()
            val email = et_email_signUp.text.toString().trim()
            val password = et_password_signUp.text.toString().trim()

            if(name.isEmpty()){
                et_name_signUp.error = "Name Required"
                et_name_signUp.requestFocus()
                return@setOnClickListener
            }

            if(email.isEmpty()){
                et_email_signUp.error = "Email Required"
                et_email_signUp.requestFocus()
                return@setOnClickListener
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                et_email_signUp.error = "Please Enter Your Email"
                et_email_signUp.requestFocus()
                return@setOnClickListener
            }

            if(password.length<6){
                et_password_signUp.error = "6 Char Required"
                et_password_signUp.requestFocus()
                return@setOnClickListener
            }

            progressBar_signUp.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        val user = auth.currentUser
                        val newUser = User(name,"",user?.uid.toString())
                        db.collection("users").document(user?.uid.toString()).set(newUser)
                        Toast.makeText(baseContext, user.toString(), Toast.LENGTH_SHORT).show()
                    } else {
                        progressBar_signUp.visibility = View.INVISIBLE
                        Toast.makeText(baseContext, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }


        }

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        btn_signUp.isEnabled = et_name_signUp.text.trim().isNotEmpty()
                && et_email_signUp.text.trim().isNotEmpty() && et_password_signUp.text.trim().isNotEmpty()
    }

    override fun afterTextChanged(p0: Editable?) {
    }
}