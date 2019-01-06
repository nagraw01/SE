package com.example.mg156.smarttraveler

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText
import android.widget.Toast
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*


class MainActivity : AppCompatActivity(), RegisterFragment.OnFragmentInteractionListenerRegister, LoginFragment.OnFragmentInteractionListenerLogin, ForgotPasswordFragment.OnFragmentInteractionListenerForgotPassword {

    private val TAG = "EmailPassword"
    private val permissionTag = "PermissionSetup"
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 678
    private val CHOOSE_IMAGE: Int = 101
    private val CAMERA_IMAGE: Int = 201

    lateinit var recFragment: Fragment


    // [START declare_auth]
    private lateinit var mAuth: FirebaseAuth
    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isLoggedIn()
        setContentView(R.layout.activity_main)
        //supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)))

        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState == null) {
            recFragment = LoginFragment.newInstance(R.id.login_fragment_layout.toString(), "")
        }

        supportFragmentManager.beginTransaction().replace(R.id.main_acitivity_container,
                recFragment).commit()
    }

    private fun isLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid

        if(uid != null){
            val intent = Intent(this,HomeActivity::class.java )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun setupPermission() : Boolean{
        val permissionCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        val permissionExternalStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        var listPermissionsNeeded = ArrayList<String>()

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            Log.i(permissionTag, "Permission to camera denied")
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionExternalStorage != PackageManager.PERMISSION_GRANTED) {
            Log.i(permissionTag, "Permission to storage denied")
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(arrayOfNulls<String>(listPermissionsNeeded.size)) ,REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

    fun uploadImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                dialog.dismiss()
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_IMAGE)
            } else if (options[item] == "Choose From Gallery") {
                dialog.dismiss()
                var intent = Intent()
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_PICK)
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val profile_image = findViewById<ImageView>(R.id.register_page_image)
        if(requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null ){
            selectedPhotoUri = data.data
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            profile_image.setImageBitmap(bitmap)
        }
        else if(requestCode == CAMERA_IMAGE && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            var extra = data.extras
            var bitmap = extra.get("data") as Bitmap
            getImageUri(this,bitmap)
            profile_image.setImageBitmap(bitmap)
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap) {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null)
        selectedPhotoUri = Uri.parse(path)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                if (grantResults.size > 0) {
                    var permissionsDenied = ""
                    var i = 0
                    for (per in permissions) {
                        if (grantResults[i] === PackageManager.PERMISSION_DENIED) {
                            permissionsDenied += "\n" + per
                        }
                        i++
                    }

                    if(permissionsDenied == ""){
                        uploadImage()
                    }
                    else{
                        Toast.makeText(this,"Go to settings and enable permissions",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onFragmentInteractionRegister(v: View) {
        when (v.id) {

            R.id.register_page_image->{
                if(setupPermission()){
                    uploadImage()
                }
            }
            R.id.register_page_sign_up_button -> {
                val editFullName = findViewById<EditText>(R.id.register_page_full_name)
                val editEmail = findViewById<EditText>(R.id.register_page_email)
                val editPassword = findViewById<EditText>(R.id.register_page_password)
                val editConfirmPassword = findViewById<EditText>(R.id.register_page_confirm_password)

                var fullname = editFullName.text.toString()
                var email = editEmail.text.toString()
                var password = editPassword.text.toString()
                var confirmPassword = editConfirmPassword.text.toString()

                if (TextUtils.isEmpty(fullname)) {
                    editFullName.setError("Enter full name!")
                    editFullName.requestFocus()
                    return
                }

                if (TextUtils.isEmpty(email)) {
                    editEmail.setError("Enter email address!")
                    editEmail.requestFocus()
                    return
                }

                if (TextUtils.isEmpty(password)) {
                    editPassword.setError("Enter password!")
                    editPassword.requestFocus()
                    return
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    editConfirmPassword.setError("Enter confirm password!")
                    editConfirmPassword.requestFocus()
                    return
                }

                if (password.length < 6) {
                    editPassword.setError("Password too short, enter minimum 6 characters!")
                    editPassword.requestFocus()
                    return
                }

                if (password != confirmPassword) {
                    editConfirmPassword.setError("Password did not match!")
                    editConfirmPassword.requestFocus()
                    return
                }

                if(selectedPhotoUri == null){
                    Toast.makeText(baseContext, "Please select your profile image",
                            Toast.LENGTH_LONG).show()
                    return
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success")
                                Toast.makeText(baseContext, "Authentication Success.",
                                        Toast.LENGTH_LONG).show()
                                uploadImageToFirebaseStorage()

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(baseContext, "Authentication failed.",
                                        Toast.LENGTH_LONG).show()
                            }
                        }
            }
            R.id.register_page_sign_in_button -> {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.main_acitivity_container, LoginFragment.newInstance(R.id.login_fragment_layout.toString(), ""))
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            R.id.register_page_btn_reset_password -> {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.main_acitivity_container, ForgotPasswordFragment.newInstance(R.id.forgot_password_fragment_layout.toString(), ""))
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }
    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) return

        val userId = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseStorage.getInstance().getReference("/images/$userId")
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("SignUp", "Successfully uploaded image")
                    ref.downloadUrl.addOnSuccessListener {
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener{
                    Log.d(" SignUp ", "Failed to upload image to storage : ${it.message}")
                }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val editUserName = findViewById<EditText>(R.id.register_page_full_name)
        val editEmail = findViewById<EditText>(R.id.register_page_email)

        val user = Users(editUserName.text.toString(),profileImageUrl, editEmail.text.toString(),uid, "user")

        ref.setValue(user).addOnSuccessListener {
            val intent = Intent(applicationContext,HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onFragmentInteractionLogin(v: View) {
        when (v.id) {
            R.id.login_page_btn_signup -> {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.main_acitivity_container, RegisterFragment.newInstance(R.id.register_fragment_layout.toString(), ""))
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            R.id.login_page_btn_login -> {
                val editEmail = findViewById<EditText>(R.id.login_page_email)
                val editPassword = findViewById<EditText>(R.id.login_page_password)

                var email = editEmail.text.toString()
                var password = editPassword.text.toString()

                if (TextUtils.isEmpty(email)) {
                    editEmail.setError("Enter email address!")
                    editEmail.requestFocus()
                    return
                }

                if (TextUtils.isEmpty(password)) {
                    editPassword.setError("Enter password!")
                    editPassword.requestFocus()
                    return
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success")
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                if (password.length < 6) {
                                    editPassword.setError(getString(R.string.minimum_password))
                                    editPassword.requestFocus()
                                } else {
                                    Toast.makeText(applicationContext, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
            }
            R.id.login_page_btn_reset_password -> {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.main_acitivity_container, ForgotPasswordFragment.newInstance(R.id.forgot_password_fragment_layout.toString(), ""))
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }
    }

    override fun onFragmentInteractionForgotPassword(v: View) {
        when (v.id) {
            R.id.forgot_password_page_btn_back -> {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.main_acitivity_container, LoginFragment.newInstance(R.id.forgot_password_fragment_layout.toString(), ""))
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            R.id.forgot_password_page_btn_reset_password -> {
                val editEmail = findViewById<EditText>(R.id.forgot_password_page_email)

                var email = editEmail.text.toString()

                if (TextUtils.isEmpty(email)) {
                    editEmail.setError("Enter your registered email id!")
                    editEmail.requestFocus()
                    return
                }

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.")
                                Toast.makeText(applicationContext, "We have sent you instructions to reset your password!", Toast.LENGTH_LONG).show();
                                val fragmentTransaction = supportFragmentManager.beginTransaction()
                                fragmentTransaction.replace(R.id.main_acitivity_container, LoginFragment.newInstance(R.id.forgot_password_fragment_layout.toString(), ""))
                                fragmentTransaction.addToBackStack(null)
                                fragmentTransaction.commit()
                            } else {
                                Toast.makeText(applicationContext, "Failed to send reset email!", Toast.LENGTH_LONG).show();
                            }
                        }
            }
        }
    }

}
