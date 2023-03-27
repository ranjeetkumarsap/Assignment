package com.example.assignment


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream


class SignUpActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etConfPass: EditText
    private lateinit var etPass: EditText
    private lateinit var etUsername: EditText
    private lateinit var etShortbio: EditText
    private lateinit var ivProfile: ImageView
    private lateinit var btnSignUp: Button
    lateinit var tvRedirectLogin: TextView
    val REQUEST_CODE = 200
    lateinit var imgUri: Uri

    // create Firebase authentication object
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        // View Bindings
        etEmail = findViewById(R.id.etSEmailAddress)
        etConfPass = findViewById(R.id.etSConfPassword)
        etPass = findViewById(R.id.etSPassword)
        etUsername = findViewById(R.id.etUsername)
        etShortbio = findViewById(R.id.etShortbio)
        ivProfile = findViewById(R.id.profile_circular_image)
        btnSignUp = findViewById(R.id.btnSSigned)
        tvRedirectLogin = findViewById(R.id.tvRedirectLogin)

        // Initialising auth object
        auth = Firebase.auth

        btnSignUp.setOnClickListener {
            signUpUser()
        }

        // switching from signUp Activity to Login Activity
        tvRedirectLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

       imgUri =  Uri.parse("")
        ivProfile.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this@SignUpActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) !==
                PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@SignUpActivity,
                        Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(this@SignUpActivity,
                        arrayOf(Manifest.permission.CAMERA), 1)
                } else {
                    ActivityCompat.requestPermissions(this@SignUpActivity,
                        arrayOf(Manifest.permission.CAMERA), 1)
                }
            }else{
                capturePhoto()

            }
        }



    }

    private fun signUpUser() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        val confirmPassword = etConfPass.text.toString()
        val username = etUsername.text.toString()
        val shortbio = etShortbio.text.toString()

        if (imgUri.path!!.isBlank()) {
            Toast.makeText(applicationContext, "Please Set Display Picture", Toast.LENGTH_SHORT).show()
            return
        }
        // check pass
        if (!isValidEmail(email)){
            Toast.makeText(applicationContext,"Please Entre Valid email.",Toast.LENGTH_SHORT).show()
            return
        }
        if (pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(applicationContext, "Please Enter your username", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(shortbio)) {
            Toast.makeText(applicationContext, "Please Enter your Short bio", Toast.LENGTH_SHORT).show()
            return
        }

            // If all credential are correct
            // We call createUserWithEmailAndPassword
            // using auth object and pass the
            // email and pass in it.
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username+"#"+shortbio+"#"+imgUri).build()
                    user!!.updateProfile(profileUpdates)

                    Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
                }
            }


    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@SignUpActivity,
                            Manifest.permission.CAMERA) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                        capturePhoto()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    fun capturePhoto() {
       imgUri=  Uri.parse("")
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){
           // logo = findViewById(R.id.iv)
            val bitmap:Bitmap=data.extras?.get("data") as Bitmap
            imgUri= getImageUri(this,bitmap)!!
            Log.d("getImageUri",""+imgUri)
            ivProfile.setImageBitmap(bitmap)
        }
    }
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

}